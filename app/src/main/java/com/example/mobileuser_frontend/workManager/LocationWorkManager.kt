package com.example.mobileuser_frontend.workManager

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.mobileuser_frontend.R
import com.example.mobileuser_frontend.repository.LocationRepository
import kotlinx.coroutines.delay

class LocationWorker(private val context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "LocationWorker"
        private const val NOTIFICATION_ID = 1001
        private const val NOTIFICATION_CHANNEL_ID = "location_services_channel"
        private const val LOCATION_RETRY_DELAY_MS = 15000L // 15 seconds
        private const val MAX_RETRY_ATTEMPTS = 3
    }

    private val repository = LocationRepository(context)

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun doWork(): Result {
        val userId = inputData.getString("userId")
            ?: return Result.failure(workDataOf("error" to "User ID is missing"))

        val retryAttempt = inputData.getInt("retry_attempt", 0)
        Log.d(TAG, "Starting location work for user $userId (attempt: ${retryAttempt + 1})")

        // Check permissions first
        if (!hasLocationPermissions()) {
            Log.w(TAG, "Location permissions not granted")
            return Result.failure(workDataOf("error" to "Location permissions not granted"))
        }

        // Check if location services are enabled
        if (!isLocationEnabled()) {
            Log.w(TAG, "Location services are disabled")

            // Show notification to the user
            promptEnableLocationServices()

            // If we haven't exceeded max retries, try again after delay
            if (retryAttempt < MAX_RETRY_ATTEMPTS) {
                delay(LOCATION_RETRY_DELAY_MS)
                return Result.retry()
            } else {
                Log.e(TAG, "Max retry attempts reached while waiting for location services")
                return Result.failure(workDataOf("error" to "Location services disabled after max retries"))
            }
        }

        // Now we can proceed with location fetch
        return try {
            val success = repository.fetchAndUpdateLocation(userId)


            if (success) {
                Log.i(TAG, "Location updated successfully for user $userId")
                Result.success()
            } else {
                Log.w(TAG, "Failed to update location for user $userId")
                Result.failure(workDataOf("error" to "Location update failed"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during location update: ${e.message}", e)

            if (isTransientError(e) && retryAttempt < MAX_RETRY_ATTEMPTS) {
                Log.d(TAG, "Transient error detected. Scheduling retry...")
                Result.retry()
            } else {
                Log.e(TAG, "Permanent error or max retries reached")
                Result.failure(workDataOf("error" to "Exception: ${e.message}"))
            }
        }
    }

    private fun hasLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun promptEnableLocationServices() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Location Services Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications related to location services status"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create intent to open location settings
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build and show notification
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Location Services Required")
            .setContentText("This app needs location services to function properly. Tap to enable.")
            .setSmallIcon(R.drawable.localisation)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun isTransientError(e: Exception): Boolean {
        return e is java.io.IOException ||
                e is java.net.SocketTimeoutException ||
                e.message?.contains("timeout", ignoreCase = true) == true ||
                e.message?.contains("connection", ignoreCase = true) == true
    }
}