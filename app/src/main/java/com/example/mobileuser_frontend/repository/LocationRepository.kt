package com.example.mobileuser_frontend.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.example.mobileuser_frontend.data.model.LocationRequest
import com.example.mobileuser_frontend.module.RetrofitClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LocationRepository(private val context: Context) {
    private val apiService = RetrofitClient.instance
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    suspend fun fetchAndUpdateLocation(userId: String): Boolean {


        // Check if location permissions are granted
        if (
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            println("Location permission not granted")
            return false
        }

        // Fetch the last known location
        val location = suspendCoroutine<Location?> { cont ->
            fusedLocationClient.lastLocation
                .addOnSuccessListener { cont.resume(it) }
                .addOnFailureListener { cont.resume(null) }
        }

        if (location == null) {
            println("Failed to fetch location")
            return false
        }

        // Map the location to LocationRequest
        val request = LocationRequest(
            userId = userId,
            latitude = location.latitude.toString(),
            longitude = location.longitude.toString()
        )

        // Send the location update to the server
        return try {
            println("Sending location update to the server")
            val response = withContext(Dispatchers.IO) {
                apiService.updateLocation(request)
            }
            response != null
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}