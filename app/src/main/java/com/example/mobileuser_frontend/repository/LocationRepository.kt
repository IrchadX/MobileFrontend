package com.example.mobileuser_frontend.repository

import LocationWebSocketClient
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.core.app.ActivityCompat
import com.example.mobileuser_frontend.data.model.LocationRequest
import com.example.mobileuser_frontend.module.RetrofitClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

class LocationRepository(private val context: Context) {
    private var wsClient: LocationWebSocketClient? = null
    private val apiService = RetrofitClient.instance
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    val authRepository: AuthRepository = AuthRepository(context)

    // Track WebSocket connection state
    private var isWsConnected = false



    // Keep reference to the most recent location
    private var lastKnownLocation: Location? = null

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    suspend fun fetchAndUpdateLocation(providedUserId: String): Boolean {
        // Fetch userId from authRepository (on IO thread if needed)
        val userId = withContext(Dispatchers.IO) {
            authRepository.getUserId().firstOrNull() ?: ""
        }


        // Check location permissions
        if (
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            println("Location permission not granted")
            return false
        }

        // Fetch the last known location
        val lastKnownLocation = suspendCancellableCoroutine<Location?> { cont ->
            val cancellationTokenSource = CancellationTokenSource()
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { loc ->
                cont.resume(loc, null)
            }.addOnFailureListener {
                cont.resume(null, null)
            }

            cont.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        }

        if (lastKnownLocation == null) {
            println("Failed to fetch location")
            return false
        }

        // Initialize WebSocket if needed
        if (wsClient == null) {
            println("Connecting to websocket...")
            wsClient = LocationWebSocketClient()
            wsClient?.onConnected = {
                println("WebSocket confirmed connected — now sending location")
                isWsConnected = true
                wsClient?.sendLocation(lastKnownLocation.latitude, lastKnownLocation.longitude)
            }
            wsClient?.connectWebSocket(userId)
        } else if (isWsConnected) {
            println("WebSocket already connected — sending location")
            wsClient?.sendLocation(lastKnownLocation.latitude, lastKnownLocation.longitude)
        } else {
            println("WebSocket not connected yet")
        }
        // Create location request for REST API
        val request = LocationRequest(
            userId = userId,
            latitude = lastKnownLocation.latitude.toString(),
            longitude = lastKnownLocation.longitude.toString()
        )

        // Send the location update to the server
        return try {
            println("Sending location update to the server via REST API")
            val response = withContext(Dispatchers.IO) {
                apiService.updateLocation(request)
            }
            response != null
        } catch (e: Exception) {
            println("REST API error: ${e.message}")
            e.printStackTrace()
            false
        }
    }


    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    private fun initializeWebSocket(userId: String) {
        println("Initializing WebSocket client")

        // Create a new WebSocket client if needed
        if (wsClient == null) {
            wsClient = LocationWebSocketClient()
        }

        wsClient?.connectWebSocket(userId)
        isWsConnected = true
    }





    fun closeWebSocket() {
        println("Closing WebSocket connection")
        wsClient?.closeWebSocket()
        isWsConnected = false
    }
}