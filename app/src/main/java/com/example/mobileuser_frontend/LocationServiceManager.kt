package com.example.mobileuser_frontend

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.mobileuser_frontend.service.LocationForegroundService

class LocationServiceManager(private val context: Context) {

    companion object {
        private const val TAG = "LocationServiceManager"
    }

    /**
     * Starts real-time location tracking service
     */
    fun startLocationTracking(userId: String) {
        if (isServiceRunning()) {
            Log.d(TAG, "Location service already running, stopping first...")
            stopLocationTracking()
        }

        val intent = Intent(context, LocationForegroundService::class.java).apply {
            action = LocationForegroundService.ACTION_START_TRACKING
            putExtra(LocationForegroundService.EXTRA_USER_ID, userId)
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            Log.i(TAG, "Location tracking service started for user: $userId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start location tracking service", e)
        }
    }

    /**
     * Stops real-time location tracking service
     */
    fun stopLocationTracking() {
        val intent = Intent(context, LocationForegroundService::class.java).apply {
            action = LocationForegroundService.ACTION_STOP_TRACKING
        }

        try {
            context.startService(intent)
            Log.i(TAG, "Location tracking service stop requested")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop location tracking service", e)
        }
    }

    /**
     * Checks if the location tracking service is currently running
     */
    fun isServiceRunning(): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        @Suppress("DEPRECATION")
        val runningServices = activityManager.getRunningServices(Integer.MAX_VALUE)

        return runningServices.any { serviceInfo ->
            serviceInfo.service.className == LocationForegroundService::class.java.name
        }
    }

    /**
     * Gets the current status of location tracking
     */
    fun getTrackingStatus(): TrackingStatus {
        return if (isServiceRunning()) {
            TrackingStatus.ACTIVE
        } else {
            TrackingStatus.STOPPED
        }
    }

    enum class TrackingStatus {
        ACTIVE,
        STOPPED
    }
}