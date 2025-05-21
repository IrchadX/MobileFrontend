import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

class LocationWebSocketClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)  // Longer timeout
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()
    private lateinit var webSocket: WebSocket
    private var isConnected = false


    private val serverUrl = "ws://192.168.39.120:9090/ws"

    fun connectWebSocket(onConnected: (() -> Unit)? = null, onFailure: ((String) -> Unit)? = null) {
        val request = Request.Builder()
            .url(serverUrl)
            .addHeader("Origin", "http://192.168.39.120")
            .build()

        val listener = object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                super.onOpen(ws, response)
                webSocket = ws
                isConnected = true
                println("WebSocket connected")
                onConnected?.invoke()
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                val errorMessage = "WebSocket failure: ${t.message}"
                println(errorMessage)
                isConnected = false
                onFailure?.invoke(errorMessage)
            }

            override fun onClosed(ws: WebSocket, code: Int, reason: String) {
                println("WebSocket closed: $reason")
                isConnected = false
            }
        }

        client.newWebSocket(request, listener)
    }

    fun sendLocation(latitude: Double, longitude: Double) {
        if (!isConnected) {
            println("WebSocket is not connected. Cannot send location.")
            return
        }

        val locationJson = """
            {
              "type": "location",
              "coords": {
                "latitude": $latitude,
                "longitude": $longitude
              }
            }
        """.trimIndent()

        webSocket.send(locationJson)
    }

    fun closeWebSocket() {
        if (isConnected) {
            webSocket.close(1000, "Normal closure")
            isConnected = false
        }
    }
}