package com.example.mobileuser_frontend.workScheduler


import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.mobileuser_frontend.workManager.LocationWorker
import java.util.concurrent.TimeUnit

object LocationWorkScheduler {

    fun scheduleLocationUpdates(context: Context, userId: String) {
        val inputData = Data.Builder()
            .putString("userId", userId)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val locationWorkRequest = PeriodicWorkRequestBuilder<LocationWorker>(15, TimeUnit.MINUTES)
            .setInputData(inputData)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "LocationUpdateWork",
            ExistingPeriodicWorkPolicy.KEEP,
            locationWorkRequest
        )
    }

}