package com.example.mobileuser_frontend.repository

import LocationWebSocketClient
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.example.mobileuser_frontend.data.model.LocationRequest
import com.example.mobileuser_frontend.module.RetrofitClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.net.SocketException

class LocationRepository(private val context: Context) {
    private var wsClient: LocationWebSocketClient? = null
    private val apiService = RetrofitClient.instance
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // Track WebSocket connection state
    private var isWsConnected = false

    // Keep reference to the most recent location
    private var lastKnownLocation: Location? = null

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
        lastKnownLocation = suspendCancellableCoroutine<Location?> { cont ->
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
            initializeWebSocket()
        }

        // If WebSocket is already connected, send the location directly
        if (isWsConnected) {
            sendLocationViaWebSocket()
        }
        // Otherwise, location will be sent when the connection is established (in connectWebSocket)

        // Create the location request for REST API
        val request = LocationRequest(
            userId = userId,
            latitude = lastKnownLocation?.latitude.toString(),
            longitude = lastKnownLocation?.longitude.toString()
        )

        // Send the location update to the server via REST API
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

    private fun initializeWebSocket() {
        println("Initializing WebSocket client")

        // Create a new WebSocket client if needed
        if (wsClient == null) {
            wsClient = LocationWebSocketClient()
        }

        connectWebSocket()
    }

    private fun connectWebSocket() {
        println("Attempting to connect to WebSocket server...")

        wsClient?.connectWebSocket(
            onConnected = {
                println("WebSocket connected successfully!")
                isWsConnected = true

                // Send location once connected
                sendLocationViaWebSocket()
            },
            onFailure = { errorMessage ->
                println("WebSocket connection failed: $errorMessage")
                isWsConnected = false

                // Retry connection after delay
                Handler(Looper.getMainLooper()).postDelayed({
                    println("Retrying WebSocket connection...")
                    connectWebSocket()
                }, 5000) // Retry after 5 seconds
            }
        )
    }

    private fun sendLocationViaWebSocket() {
        lastKnownLocation?.let { location ->
            println("Sending location via WebSocket: ${location.latitude}, ${location.longitude}")
            wsClient?.sendLocation(location.latitude, location.longitude)
        } ?: println("⚠️ No location available to send via WebSocket")
    }

    fun closeWebSocket() {
        println("Closing WebSocket connection")
        wsClient?.closeWebSocket()
        isWsConnected = false
    }
}