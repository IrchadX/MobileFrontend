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
import java.util.concurrent.atomic.AtomicLong

class AudioWebSocketClient(
    private val serverUri: URI,
    private val headers: Map<String, String> = emptyMap(),
    private val onMessageCallback: (String) -> Unit = {}
) : WebSocketListener() {
    private val TAG = "AudioWebSocketClient"
    private var webSocket: WebSocket? = null
    private var isRecording = false
    private var isConnected = false
    private val audioDataSentCounter = AtomicLong(0)
    private val textMessageSentCounter = AtomicLong(0)

    val isOpen: Boolean
        get() = isConnected && webSocket != null

    init {
        Log.d(TAG, "🔧 Initializing AudioWebSocketClient for $serverUri")
    }

    fun connect() {
        try {
            Log.d(TAG, "🔗 Connecting to WebSocket server: $serverUri")

            val client = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .pingInterval(30, TimeUnit.SECONDS)
                .build()

            val requestBuilder = Request.Builder()
                .url(serverUri.toString())

            headers.forEach { (key, value) ->
                requestBuilder.addHeader(key, value)
                Log.d(TAG, "🏷️ Added header: $key = $value")
            }

            if (serverUri.host?.contains("ngrok") == true) {
                requestBuilder.addHeader("ngrok-skip-browser-warning", "true")
                Log.d(TAG, "🔧 Added ngrok skip header")
            }

            webSocket = client.newWebSocket(requestBuilder.build(), this)
            Log.d(TAG, "📡 WebSocket connection initiated...")

        } catch (e: Exception) {
            Log.e(TAG, "💥 Error connecting to WebSocket: ${e.message}", e)
            isConnected = false
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d(TAG, "✅ WebSocket connection opened!")
        Log.d(TAG, "📊 Response code: ${response.code}")
        Log.d(TAG, "🔒 Protocol: ${response.protocol}")
        Log.d(TAG, "🏷️ Headers: ${response.headers}")

        isConnected = true
        this.webSocket = webSocket

        // Send initial connection message
        webSocket.send("CONNECTION_ESTABLISHED")
        Log.d(TAG, "📤 Sent CONNECTION_ESTABLISHED message")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d(TAG, "📥 Received text message: '$text'")

        // Check if this is a command response
        if (text.contains("COMMAND:") || text.contains("Recognized:")) {
            Log.d(TAG, "🎯 Command/Recognition detected in message")
        }

        onMessageCallback(text)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        Log.d(TAG, "📥 Received binary message: ${bytes.size} bytes")
        // Handle binary responses if needed
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.d(TAG, "⚠️ WebSocket closing: code=$code, reason='$reason'")
        isConnected = false
        webSocket.close(1000, null)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.d(TAG, "❌ WebSocket closed: code=$code, reason='$reason'")
        isConnected = false
        this.webSocket = null
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e(TAG, "💥 WebSocket failure: ${t.message}", t)
        Log.e(TAG, "📊 Response: ${response?.toString()}")
        isConnected = false
        this.webSocket = null
    }

    fun sendAudioData(data: ByteArray) {
        if (!isRecording) {
            Log.w(TAG, "⚠️ Attempted to send audio data while not recording")
            return
        }

        if (!isConnected || webSocket == null) {
            Log.w(TAG, "⚠️ Cannot send audio data - not connected")
            return
        }

        try {
            val byteString = ByteString.of(*data)
            val success = webSocket?.send(byteString) ?: false

            if (success) {
                val count = audioDataSentCounter.incrementAndGet()

                // Log every 100th packet to avoid spam
                if (count % 100 == 0L) {
                    Log.d(TAG, "📊 Audio data sent - Packet #$count, Size: ${data.size} bytes")
                }
            } else {
                Log.e(TAG, "❌ Failed to send audio data - WebSocket send returned false")
            }
        } catch (e: Exception) {
            Log.e(TAG, "💥 Error sending audio data: ${e.message}", e)
        }
    }

    fun sendMessage(message: String) {
        try {
            Log.d(TAG, "📤 Sending text message: '$message'")

            if (webSocket != null && isConnected) {
                val success = webSocket?.send(message) ?: false

                if (success) {
                    val count = textMessageSentCounter.incrementAndGet()
                    Log.d(TAG, "✅ Text message sent successfully (count: $count)")
                } else {
                    Log.e(TAG, "❌ Failed to send text message - WebSocket send returned false")
                }
            } else {
                Log.w(TAG, "⚠️ Cannot send message - WebSocket not connected (connected: $isConnected, socket: ${webSocket != null})")
            }
        } catch (e: Exception) {
            Log.e(TAG, "💥 Error sending message: ${e.message}", e)
        }
    }

    fun startRecording() {
        isRecording = true
        Log.d(TAG, "🎙️ Recording state set to true")
        sendMessage("START_RECORDING")
    }

    fun stopRecording() {
        isRecording = false
        Log.d(TAG, "🛑 Recording state set to false")
        sendMessage("STOP_RECORDING")

        // Log final statistics
        Log.d(TAG, "📊 Final stats - Audio packets: ${audioDataSentCounter.get()}, Text messages: ${textMessageSentCounter.get()}")
    }

    fun close() {
        try {
            isRecording = false
            isConnected = false

            Log.d(TAG, "🔐 Closing WebSocket connection...")
            webSocket?.close(1000, "Client closing connection")
            webSocket = null

            Log.d(TAG, "✅ WebSocket connection closed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "💥 Error closing WebSocket: ${e.message}", e)
        }
    }

    fun reconnect() {
        Log.d(TAG, "🔄 Reconnecting WebSocket...")
        close()
        Thread.sleep(1000)
        connect()
    }

    // Debug method to get connection stats
    fun getConnectionStats(): String {
        return "Connected: $isConnected, Recording: $isRecording, Audio packets: ${audioDataSentCounter.get()}, Text messages: ${textMessageSentCounter.get()}"
    }
}