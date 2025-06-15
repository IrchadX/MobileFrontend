import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class LocationWebSocketClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private lateinit var webSocket: WebSocket
    private var isConnected = false
    private var isRegistered = false
    private var userId: String? = null

    private val serverUrl = "wss://websocket-production-1b56.up.railway.app/"

    // Callbacks for different events
    var onRegistrationSuccess: (() -> Unit)? = null        // No more subscriber count from DB
    var onLocationSent: ((Int) -> Unit)? = null            // Parameter: current subscriber count
    var onSubscriberUpdate: ((List<String>) -> Unit)? = null // New: list of current subscribers
    var onError: ((String) -> Unit)? = null
    var onConnectionStatusChanged: ((Boolean) -> Unit)? = null
    var onConnected: (() -> Unit)? = null
    private var pendingLocation: Pair<Double, Double>? = null


    fun connectWebSocket(
        userId: String,
    ) {
        this.userId = userId

        val request = Request.Builder()
            .url(serverUrl)
            .build()

        val listener = object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                super.onOpen(ws, response)
                webSocket = ws
                isConnected = true
                onConnectionStatusChanged?.invoke(true)
                onConnected?.invoke()
                println("WebSocket connected")

                // Automatically register as publisher after connection
               if(!isRegistered){
                registerAsPublisher(userId)}
            }

            override fun onMessage(ws: WebSocket, text: String) {
                super.onMessage(ws, text)
                handleServerMessage(text)
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                val errorMessage = "WebSocket failure: ${t.message}"
                println(errorMessage)
                isConnected = false
                isRegistered = false
                onConnectionStatusChanged?.invoke(false)
                onError?.invoke(errorMessage)
            }

            override fun onClosed(ws: WebSocket, code: Int, reason: String) {
                println("WebSocket closed: $reason")
                isConnected = false
                isRegistered = false
                onConnectionStatusChanged?.invoke(false)
            }
        }

        client.newWebSocket(request, listener)
    }

    private fun registerAsPublisher(userId: String) {
        if (!isConnected) {
            println("Cannot register: WebSocket not connected")
            return
        }

        val registrationJson = JSONObject().apply {
            put("type", "register")
            put("userId", userId)
            put("role", "publisher")
        }.toString()

        println("Registering as publisher: $registrationJson")
        webSocket.send(registrationJson)
        isRegistered = true
    }

    private fun handleServerMessage(message: String) {
        try {
            val json = JSONObject(message)
            val type = json.getString("type")

            when (type) {
                "registered" -> {
                    isRegistered = true
                    val message = json.getString("message")

                    println("Registration successful: $message")
                    pendingLocation?.let { (lat, lng) ->
                        sendLocation(lat, lng)
                        pendingLocation = null
                    }
                    onRegistrationSuccess?.invoke()
                }

                "location_sent" -> {
                    val subscriberCount = json.getInt("subscriberCount")
                    val message = json.getString("message")

                    println("Location sent: $message")
                    onLocationSent?.invoke(subscriberCount)
                }

                "subscribers" -> {
                    // New: Handle list of current subscribers
                    val subscribersArray = json.getJSONArray("subscribers")
                    val subscribersList = mutableListOf<String>()
                    for (i in 0 until subscribersArray.length()) {
                        subscribersList.add(subscribersArray.getString(i))
                    }

                    println("Current subscribers: $subscribersList")
                    onSubscriberUpdate?.invoke(subscribersList)
                }

                "error" -> {
                    val errorMessage = json.getString("message")
                    println("Server error: $errorMessage")
                    onError?.invoke("Server error: $errorMessage")

                    // If registration failed, try again
                    if (errorMessage.contains("not registered") && userId != null) {
                        println("Attempting to re-register...")
                        registerAsPublisher(userId!!)
                    }
                }

                else -> {
                    println("Unknown message type: $type")
                }
            }
        } catch (e: Exception) {
            println("Error parsing server message: ${e.message}")
            println("Raw message: $message")
        }
    }

    fun sendLocation(latitude: Double, longitude: Double) {
        if (!isConnected) {
            println("WebSocket is not connected. Cannot send location.")
            onError?.invoke("WebSocket not connected")
            return
        }

        if (!isRegistered) {
            println("Not registered as publisher. Attempting to register...")
            userId?.let { registerAsPublisher(it) }
            pendingLocation = latitude to longitude
            onError?.invoke("Not registered. Please wait for registration to complete.")
            return
        }

        val locationJson = JSONObject().apply {
            put("type", "location")
            put("coords", JSONObject().apply {
                put("latitude", latitude)
                put("longitude", longitude)
            })
        }.toString()

        println("Sending location: lat=$latitude, lng=$longitude")
        webSocket.send(locationJson)
    }

    fun getCurrentSubscribers() {
        if (!isConnected || !isRegistered) {
            println("Cannot get subscribers: not connected or not registered")
            return
        }

        val getSubscribersJson = JSONObject().apply {
            put("type", "get_subscriptions")
        }.toString()

        webSocket.send(getSubscribersJson)
    }



    fun getConnectionStatus(): ConnectionStatus {
        return when {
            !isConnected -> ConnectionStatus.DISCONNECTED
            !isRegistered -> ConnectionStatus.CONNECTING
            else -> ConnectionStatus.CONNECTED
        }
    }

    fun closeWebSocket() {
        if (isConnected) {
            webSocket.close(1000, "Normal closure")
            isConnected = false
            isRegistered = false
            onConnectionStatusChanged?.invoke(false)
        }
    }

    enum class ConnectionStatus {
        DISCONNECTED,
        CONNECTING,
        CONNECTED
    }
}