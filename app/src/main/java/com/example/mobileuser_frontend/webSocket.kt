package com.example.mobileuser_frontend

import android.util.Log
import java.net.URI
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.concurrent.TimeUnit

/**
 * WebSocket client implementation for audio streaming
 */
class AudioWebSocketClient(
    private val serverUri: URI,
    private val headers: Map<String, String> = emptyMap(),
    private val onMessageCallback: (String) -> Unit = {}
) {
    private val TAG = "AudioWebSocketClient"
    private var webSocket: WebSocket? = null
    private var isRecording = false

    // Exposed flag to check connection state
    val isOpen: Boolean
        get() = webSocket != null

    init {
        Log.d(TAG, "Initializing WebSocketClient for $serverUri")
    }

    /**
     * Connect to the WebSocket server
     */
    fun connect() {
        try {
            Log.d(TAG, "Connecting to WebSocket server: $serverUri")

            // Create OkHttp client with reasonable timeouts
            val client = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            // Build request with headers
            val requestBuilder = Request.Builder()
                .url(serverUri.toString())

            // Add headers if provided
            headers.forEach { (key, value) ->
                requestBuilder.addHeader(key, value)
            }

            // Create and connect WebSocket
            webSocket = client.newWebSocket(requestBuilder.build(), object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    Log.d(TAG, "WebSocket connection established")
                    // Send a test message to verify connection
                    webSocket.send("CONNECTION_ESTABLISHED")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    Log.d(TAG, "Received text message: $text")
                    // Forward messages to callback handler
                    onMessageCallback(text)
                }

                override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                    Log.d(TAG, "Received binary message: ${bytes.size} bytes")
                    // We don't expect binary responses from the server
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    Log.d(TAG, "WebSocket closing: $code $reason")
                    webSocket.close(1000, null)
                    this@AudioWebSocketClient.webSocket = null
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    Log.d(TAG, "WebSocket closed: $code $reason")
                    this@AudioWebSocketClient.webSocket = null
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    Log.e(TAG, "WebSocket failure: ${t.message}", t)
                    this@AudioWebSocketClient.webSocket = null

                    // Attempt reconnection after failure (optional)
                    // reconnect()
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Error connecting to WebSocket: ${e.message}", e)
        }
    }

    /**
     * Send audio data over the WebSocket
     */
    fun sendAudioData(data: ByteArray) {
        if (isRecording && webSocket != null) {
            try {
                // Send binary audio data
                val byteString = ByteString.of(*data)
                webSocket?.send(byteString)
            } catch (e: Exception) {
                Log.e(TAG, "Error sending audio data: ${e.message}", e)
            }
        }
    }

    /**
     * Send a text message over the WebSocket
     */
    fun sendMessage(message: String) {
        try {
            Log.d(TAG, "Sending message: $message")
            webSocket?.send(message)
        } catch (e: Exception) {
            Log.e(TAG, "Error sending message: ${e.message}", e)
        }
    }

    /**
     * Set recording state to true
     */
    fun startRecording() {
        isRecording = true
        sendMessage("START_RECORDING")
    }

    /**
     * Set recording state to false
     */
    fun stopRecording() {
        isRecording = false
        sendMessage("STOP_RECORDING")
    }

    /**
     * Close the WebSocket connection
     */
    fun close() {
        try {
            isRecording = false
            webSocket?.close(1000, "Client closing connection")
            webSocket = null
            Log.d(TAG, "WebSocket connection closed")
        } catch (e: Exception) {
            Log.e(TAG, "Error closing WebSocket: ${e.message}", e)
        }
    }

    /**
     * Attempt to reconnect to the WebSocket server
     */
    fun reconnect() {
        close()
        connect()
    }
}