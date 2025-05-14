package com.example.mobileuser_frontend.workScheduler


import android.content.Context
import androidx.work.Data
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.mobileuser_frontend.workManager.LocationWorker
import java.util.concurrent.TimeUnit

object LocationWorkScheduler {

    fun scheduleLocationUpdates(context: Context, userId: String) {
        // Pass userId to the worker
        val inputData = Data.Builder()
            .putString("userId", userId)
            .build()

        // Create the periodic work request
        val locationWorkRequest = PeriodicWorkRequestBuilder<LocationWorker>(1, TimeUnit.MINUTES)
            .setInputData(inputData)
            .build()

        // Enqueue the work
        WorkManager.getInstance(context).enqueue(locationWorkRequest)
    }
}