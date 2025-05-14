package com.example.mobileuser_frontend.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.mobileuser_frontend.workScheduler.LocationWorkScheduler

class LocationViewModel(private val context: Context) : ViewModel() {

    fun startPeriodicLocationUpdates(userId: String) {
        LocationWorkScheduler.scheduleLocationUpdates(context, userId)
    }
}