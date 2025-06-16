package com.example.mobileuser_frontend.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.mobileuser_frontend.MainActivity
import com.example.mobileuser_frontend.R
import com.example.mobileuser_frontend.repository.LocationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class LocationForegroundService : Service() {

    companion object {
        private const val TAG = "LocationForegroundService"
        const val NOTIFICATION_ID = 1002
        const val CHANNEL_ID = "location_tracking_channel"
        const val ACTION_START_TRACKING = "ACTION_START_TRACKING"
        const val ACTION_STOP_TRACKING = "ACTION_STOP_TRACKING"
        const val EXTRA_USER_ID = "EXTRA_USER_ID"
        private const val LOCATION_UPDATE_INTERVAL_MS = 30000L // 30 seconds
    }

    private var userId: String? = null
    private val handler = Handler(Looper.getMainLooper())
    private var locationRunnable: Runnable? = null
    private lateinit var repository: LocationRepository
    private var isTracking = false

    // Coroutine scope for the service
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    override fun onCreate() {
        super.onCreate()
        repository = LocationRepository(this)
        createNotificationChannel()
        Log.d(TAG, "LocationForegroundService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_TRACKING -> {
                userId = intent.getStringExtra(EXTRA_USER_ID)
                if (userId != null) {
                    startLocationTracking()
                } else {
                    Log.e(TAG, "User ID is null, cannot start tracking")
                    stopSelf()
                }
            }
            ACTION_STOP_TRACKING -> {
                stopLocationTracking()
            }
            else -> {
                // Handle legacy start without action
                userId = intent?.getStringExtra(EXTRA_USER_ID)
                if (userId != null && !isTracking) {
                    startLocationTracking()
                }
            }
        }

        return START_STICKY // Restart service if killed by system
    }

    private fun startLocationTracking() {
        if (isTracking) {
            Log.d(TAG, "Location tracking already active")
            return
        }

        if (!hasLocationPermissions()) {
            Log.e(TAG, "Location permissions not granted")
            stopSelf()
            return
        }

        if (!isLocationEnabled()) {
            Log.e(TAG, "Location services disabled")
            stopSelf()
            return
        }

        isTracking = true
        startForeground(NOTIFICATION_ID, createNotification())

        locationRunnable = object : Runnable {
            @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
            override fun run() {
                if (isTracking && userId != null) {
                    // Launch coroutine for location update
                    serviceScope.launch {
                        try {
                            val success = repository.fetchAndUpdateLocation(userId!!)
                            val message = if (success) {
                                "Location updated successfully"
                            } else {
                                "Failed to update location"
                            }
                            Log.d(TAG, "$message for user $userId")

                            // Update notification with last update info
                            updateNotification(success)

                        } catch (e: Exception) {
                            Log.e(TAG, "Error updating location for user $userId", e)
                        }
                    }

                    // Schedule next update
                    handler.postDelayed(this, LOCATION_UPDATE_INTERVAL_MS)
                } else {
                    Log.d(TAG, "Stopping location updates - tracking: $isTracking, userId: $userId")
                }
            }
        }

        // Start immediate first update
        handler.post(locationRunnable!!)
        Log.i(TAG, "Location tracking started for user $userId")
    }

    private fun stopLocationTracking() {
        isTracking = false
        locationRunnable?.let { handler.removeCallbacks(it) }
        locationRunnable = null

        Log.i(TAG, "Location tracking stopped")
        stopSelf()
    }

    private fun hasLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Location Tracking",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Ongoing location tracking for real-time updates"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, LocationForegroundService::class.java).apply {
            action = ACTION_STOP_TRACKING
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Location Tracking Active")
            .setContentText("Sharing location every 30 seconds")
            .setSmallIcon(R.drawable.localisation)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }

    private fun updateNotification(lastUpdateSuccess: Boolean) {
        val currentTime = System.currentTimeMillis()
        val timeString = android.text.format.DateFormat.format("HH:mm:ss", currentTime)

        val statusText = if (lastUpdateSuccess) {
            "Last updated: $timeString ✓"
        } else {
            "Last update failed: $timeString ✗"
        }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, LocationForegroundService::class.java).apply {
            action = ACTION_STOP_TRACKING
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Location Tracking Active")
            .setContentText(statusText)
            .setSmallIcon(R.drawable.localisation)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        isTracking = false
        locationRunnable?.let { handler.removeCallbacks(it) }
        serviceJob.cancel()
        Log.d(TAG, "LocationForegroundService destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}