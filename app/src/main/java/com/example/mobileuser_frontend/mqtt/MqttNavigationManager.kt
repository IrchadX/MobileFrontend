package com.example.mobileuser_frontend.mqtt

import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.eclipse.paho.android.service.BuildConfig

// Data classes for MQTT messages
data class NavigationRequest(
    val destination: String,
    val destinationCoordinates: List<Double>,
    val timestamp: Long = System.currentTimeMillis()
)

data class NavigationInstruction(
    val instruction: String,
    val distance: String,
    val direction: String,
    val timestamp: Long
)

data class ObstacleAlert(
    val detected: Boolean,
    val description: String,
    val timestamp: Long
)

// POI data class matching GeoJSON structure
data class POI(
    val name: String,
    val description: String,
    val coordinates: List<Double>,
    val category: String = "",
    val categoryId: Int = 0
)

class MqttNavigationManager(private val context: Context) {
    private val brokerUrl = "mqtts://56e91a6cae9041d89eca972b135301fb.s1.eu.hivemq.cloud:8883"
    private val username = "hind_deh"
    private val password = "hindDehili2025"
    private val clientId = "AndroidNavClient_${System.currentTimeMillis()}"

    // MQTT Topics
    private val topicNavigationRequest = "navigation/request"
    private val topicNavigationInstructions = "navigation/instructions"
    private val topicObstacleAlert = "navigation/obstacle"

    private var mqttClient: MqttAndroidClient? = null
    private val gson = Gson()


    private val _navigationInstructions = MutableStateFlow<List<NavigationInstruction>>(emptyList())
    val navigationInstructions: StateFlow<List<NavigationInstruction>> = _navigationInstructions.asStateFlow()

    private val _obstacleAlert = MutableStateFlow<ObstacleAlert?>(null)
    val obstacleAlert: StateFlow<ObstacleAlert?> = _obstacleAlert.asStateFlow()

    private val _connectionStatus = MutableStateFlow(false)
    val connectionStatus: StateFlow<Boolean> = _connectionStatus.asStateFlow()

    private val _availablePOIs = MutableStateFlow<List<POI>>(emptyList())
    val availablePOIs: StateFlow<List<POI>> = _availablePOIs.asStateFlow()

    companion object {
        private const val TAG = "MqttNavigationManager"
    }

    fun connect() {
        try {
            mqttClient = MqttAndroidClient(context, brokerUrl, clientId)

            val options = MqttConnectOptions().apply {
                isCleanSession = true
                connectionTimeout = 30
                keepAliveInterval = 60

                // Add authentication for HiveMQ Cloud
                userName = username
                password = this@MqttNavigationManager.password.toCharArray()

                // For secure connection (mqtts://)
                socketFactory = createSSLSocketFactory()
            }

            mqttClient?.setCallback(object : MqttCallbackExtended {
                override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                    Log.d(TAG, "Connected to MQTT broker: $serverURI")
                    _connectionStatus.value = true
                    subscribeToTopics()
                }

                override fun connectionLost(cause: Throwable?) {
                    Log.e(TAG, "Connection lost", cause)
                    _connectionStatus.value = false
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    handleIncomingMessage(topic, message)
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    Log.d(TAG, "Message delivered")
                }
            })

            mqttClient?.connect(options)

        } catch (e: Exception) {
            Log.e(TAG, "Error connecting to MQTT", e)
        }
    }

    // SSL Socket Factory for secure connection
    private fun createSSLSocketFactory(): javax.net.ssl.SSLSocketFactory {
        return try {
            val trustAllCerts = arrayOf<javax.net.ssl.TrustManager>(
                object : javax.net.ssl.X509TrustManager {
                    override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
                    override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
                    override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
                }
            )

            val sslContext = javax.net.ssl.SSLContext.getInstance("TLS")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
            sslContext.socketFactory
        } catch (e: Exception) {
            Log.e(TAG, "Error creating SSL socket factory", e)
            javax.net.ssl.SSLContext.getDefault().socketFactory
        }
    }

    private fun subscribeToTopics() {
        try {
            mqttClient?.apply {
                subscribe(topicNavigationInstructions, 1)
                subscribe(topicObstacleAlert, 1)
            }
            Log.d(TAG, "Subscribed to navigation topics")
        } catch (e: Exception) {
            Log.e(TAG, "Error subscribing to topics", e)
        }
    }

    private fun handleIncomingMessage(topic: String?, message: MqttMessage?) {
        try {
            val messageString = message?.toString() ?: return
            Log.d(TAG, "Received message on $topic: $messageString")

            when (topic) {
                topicNavigationInstructions -> {
                    val instruction = gson.fromJson(messageString, NavigationInstruction::class.java)
                    val currentInstructions = _navigationInstructions.value.toMutableList()
                    currentInstructions.add(instruction)
                    _navigationInstructions.value = currentInstructions
                }

                topicObstacleAlert -> {
                    val alert = gson.fromJson(messageString, ObstacleAlert::class.java)
                    _obstacleAlert.value = alert
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling incoming message", e)
        }
    }

    fun sendNavigationRequest(destination: String, coordinates: List<Double>) {
        try {
            val request = NavigationRequest(destination, coordinates)
            val message = gson.toJson(request)

            mqttClient?.publish(topicNavigationRequest, message.toByteArray(), 1, false)
            Log.d(TAG, "Navigation request sent: $message")

            // Clear previous instructions when new request is sent
            _navigationInstructions.value = emptyList()
            _obstacleAlert.value = null

        } catch (e: Exception) {
            Log.e(TAG, "Error sending navigation request", e)
        }
    }

    fun extractPOIsFromGeoJSON(geoJsonString: String) {
        try {
            val geoJsonType = object : TypeToken<Map<String, Any>>() {}.type
            val geoJson: Map<String, Any> = gson.fromJson(geoJsonString, geoJsonType)

            val features = geoJson["features"] as? List<Map<String, Any>> ?: emptyList()
            val pois = mutableListOf<POI>()

            for (feature in features) {
                val properties = feature["properties"] as? Map<String, Any> ?: continue
                val geometry = feature["geometry"] as? Map<String, Any> ?: continue

                val type = properties["type"] as? String
                val name = properties["name"] as? String ?: "Unknown POI"
                val description = properties["description"] as? String ?: ""
                val category = properties["categorie"] as? String ?: ""
                val categoryId = when (val id = properties["categoryId"]) {
                    is Double -> id.toInt()
                    is String -> id.toIntOrNull() ?: 0
                    else -> 0
                }

                // Extract coordinates based on geometry type
                val coordinates = when (geometry["type"] as? String) {
                    "Point" -> {
                        geometry["coordinates"] as? List<Double> ?: continue
                    }
                    "Polygon" -> {
                        val coords = geometry["coordinates"] as? List<List<List<Double>>> ?: continue
                        if (coords.isNotEmpty() && coords[0].isNotEmpty()) {
                            // Get centroid of polygon (simplified)
                            val firstRing = coords[0]
                            val avgLon = firstRing.map { it[0] }.average()
                            val avgLat = firstRing.map { it[1] }.average()
                            listOf(avgLon, avgLat)
                        } else continue
                    }
                    "LineString" -> {
                        val coords = geometry["coordinates"] as? List<List<Double>> ?: continue
                        if (coords.isNotEmpty()) {
                            coords[0] // First point of line
                        } else continue
                    }
                    else -> continue
                }

                // Only add if it's a POI or has a meaningful name
                if (type == "poi" || name != "Unknown POI") {
                    pois.add(POI(name, description, coordinates, category, categoryId))
                }
            }

            _availablePOIs.value = pois
            Log.d(TAG, "Extracted ${pois.size} POIs from GeoJSON")

        } catch (e: Exception) {
            Log.e(TAG, "Error extracting POIs from GeoJSON", e)
        }
    }

    fun clearObstacleAlert() {
        _obstacleAlert.value = null
    }

    fun disconnect() {
        try {
            mqttClient?.disconnect()
            _connectionStatus.value = false
            Log.d(TAG, "Disconnected from MQTT broker")
        } catch (e: Exception) {
            Log.e(TAG, "Error disconnecting from MQTT", e)
        }
    }
    fun reconnect() {
        if (_connectionStatus.value) {
            disconnect()
        }
        connect()
    }

    fun getCurrentConnectionStatus(): Boolean {
        return _connectionStatus.value
    }

    fun clearNavigationInstructions() {
        _navigationInstructions.value = emptyList()
    }
}