package com.example.mobileuser_frontend.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileuser_frontend.data.model.UserLocation
import com.example.mobileuser_frontend.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LocationViewModel(private val context: Context) : ViewModel() {

    private val repository = LocationRepository(context)

    private val _userLocation = MutableStateFlow<UserLocation?>(null)
    val userLocation: StateFlow<UserLocation?> get() = _userLocation

    fun fetchUserLocation(userId :String) {
        viewModelScope.launch {
            val success = repository.fetchAndUpdateLocation(userId)
            if (success) {
                println("Location sent successfully for user $userId")
            } else {
                println("Failed to get or send location for user $userId")
            }

        }
    }
}