package com.example.mobileuser_frontend

import android.os.Handler
import android.os.Looper
import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean

class AudioWebSocketClient(
    serverUri: URI,
    headers: Map<String, String>? = null
) : WebSocketClient(serverUri, headers) {

    private val TAG = "AudioWebSocketClient"
    private var isRecording = false
    private var bytesSent = 0
    private var lastLogTime = 0L
    private val LOG_INTERVAL = 10000 // Log every second
    private val isConnecting = AtomicBoolean(false)
    private val reconnectHandler = Handler(Looper.getMainLooper())
    private var reconnectAttempts = 0
    private val MAX_RECONNECT_ATTEMPTS = 5
    private val RECONNECT_DELAY_MS = 2000L // 2 seconds

    init {
        this.connectionLostTimeout = 10 // Faster detection of connection loss
        Log.d(TAG, "AudioWebSocketClient initialized with URI: $serverUri")
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
        Log.d(TAG, "Connection opened successfully to $uri")
        reconnectAttempts = 0 // Reset on successful connection
        isConnecting.set(false)
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.d(TAG, "Connection closed: code=$code, reason=$reason, remote=$remote")
        Log.d(TAG, "Total bytes sent before disconnect: $bytesSent")

        // Try to reconnect if we were recording
        if (isRecording && !isConnecting.get()) {
            attemptReconnect()
        }
    }

    override fun onMessage(message: String?) {
        Log.d(TAG, "Received text message from server: $message")
    }

    override fun onMessage(bytes: ByteBuffer?) {
        val size = bytes?.remaining() ?: 0
        Log.d(TAG, "Received binary message of size: $size bytes")
    }

    override fun onError(ex: Exception?) {
        Log.e(TAG, "WebSocket error: ${ex?.message}")
        ex?.printStackTrace()

        // If connecting fails, try to reconnect
        if (!isOpen && isRecording && !isConnecting.get()) {
            attemptReconnect()
        }
    }

    private fun attemptReconnect() {
        if (reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
            Log.e(TAG, "Max reconnection attempts reached. Giving up.")
            return
        }

        if (isConnecting.compareAndSet(false, true)) {
            reconnectAttempts++
            Log.d(TAG, "Attempting to reconnect... (Attempt $reconnectAttempts)")

            reconnectHandler.postDelayed({
                try {
                    reconnect()
                    Log.d(TAG, "Reconnect attempt initiated")
                } catch (e: Exception) {
                    Log.e(TAG, "Reconnection failed: ${e.message}")
                    isConnecting.set(false)

                    // Schedule another attempt
                    if (isRecording) {
                        attemptReconnect()
                    }
                }
            }, RECONNECT_DELAY_MS)
        }
    }

    fun sendAudioData(audioData: ByteArray) {
        if (isOpen && isRecording) {
            try {
                send(audioData)
                bytesSent += audioData.size

                // Log progress periodically to avoid flooding
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastLogTime > LOG_INTERVAL) {
                    Log.d(TAG, "Sent ${audioData.size} bytes, total sent: $bytesSent bytes")
                    lastLogTime = currentTime
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error sending audio data: ${e.message}")
            }
        } else {
            if (!isOpen && isRecording && !isConnecting.get()) {
                Log.e(TAG, "WebSocket connection is not open, attempting to reconnect")
                attemptReconnect()
            }
        }
    }

    fun startRecording() {
        Log.d(TAG, "Starting recording")
        isRecording = true
        bytesSent = 0
        lastLogTime = System.currentTimeMillis()

        // Make sure we're connected when starting recording
        if (!isOpen && !isConnecting.get()) {
            attemptReconnect()
        }
    }

    fun stopRecording() {
        Log.d(TAG, "Stopping recording. Total bytes sent: $bytesSent")
        isRecording = false
    }

    override fun connect() {
        Log.d(TAG, "Connecting to WebSocket server: $uri")
        isConnecting.set(true)
        super.connect()
    }

    override fun reconnect() {
        Log.d(TAG, "Reconnecting to WebSocket server: $uri")
        isConnecting.set(true)
        super.reconnect()
    }
}