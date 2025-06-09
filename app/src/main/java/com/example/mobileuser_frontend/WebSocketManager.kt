package com.example.mobileuser_frontend

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URI
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class WebSocketManager(
    private val context: Context,
    private val voiceCommandHandler: VoiceCommandHandler
) {
    private val TAG = "WebSocketManager"
    private var webSocketClient: AudioWebSocketClient? = null
    private var audioRecord: AudioRecord? = null
    private val isRecording = AtomicBoolean(false)
    private val sampleRate = 16000
    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    ).let { minSize ->
        // Ensure buffer size is adequate
        if (minSize < 4096) 4096 else minSize
    }

    // Callback for recording state changes
    var onRecordingStateChanged: ((Boolean) -> Unit)? = null

    init {
        Log.d(TAG, "Initializing WebSocketManager with buffer size: $bufferSize")
        setupWebSocketClient()
    }

    private fun setupWebSocketClient() {
        try {
            val serverUri = URI("wss://7b9d-154-121-119-219.ngrok-free.app")
            Log.d(TAG, "Setting up WebSocket client for: $serverUri")

            val headers = HashMap<String, String>()
            headers["Connection"] = "Upgrade"
            headers["Upgrade"] = "websocket"
            headers["Sec-WebSocket-Version"] = "13"
            headers["Sec-WebSocket-Key"] = generateWebSocketKey()

            webSocketClient = AudioWebSocketClient(
                serverUri,
                headers
            ) { message ->
                Log.d(TAG, "Received message from server: $message")
                voiceCommandHandler.processWebSocketMessage(message)
            }

            Log.d(TAG, "Attempting to connect to WebSocket server...")
            webSocketClient?.connect()

            Handler(Looper.getMainLooper()).postDelayed({
                if (webSocketClient?.isOpen == true) {
                    Log.d(TAG, " WebSocket connection established successfully!")
                    // Send a test message to verify bidirectional communication
                    webSocketClient?.sendMessage("ANDROID_CLIENT_READY")
                } else {
                    Log.e(TAG, " Failed to establish WebSocket connection after timeout")
                    reconnectWebSocket()
                }
            }, 5000)

        } catch (e: Exception) {
            Log.e(TAG, "WebSocket setup error: ${e.message}", e)
        }
    }

    private fun reconnectWebSocket() {
        Log.d(TAG, "Attempting to reconnect WebSocket...")
        try {
            webSocketClient?.close()
            Handler(Looper.getMainLooper()).postDelayed({
                setupWebSocketClient()
            }, 2000)
        } catch (e: Exception) {
            Log.e(TAG, "Error during reconnection: ${e.message}", e)
        }
    }

    private fun generateWebSocketKey(): String {
        val key = ByteArray(16)
        Random().nextBytes(key)
        return Base64.encodeToString(key, Base64.NO_WRAP)
    }

    fun toggleRecording() {
        Log.d(TAG, "Toggle recording called. Current state: ${isRecording.get()}")
        if (isRecording.get()) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    fun startRecording() {
        Log.d(TAG, " Starting recording process...")

        if (!checkPermission()) {
            Log.e(TAG, " Audio recording permission not granted")
            return
        }

        if (isRecording.get()) {
            Log.d(TAG, "⚠ Already recording")
            return
        }

        if (webSocketClient?.isOpen != true) {
            Log.e(TAG, " WebSocket not connected, cannot start recording")
            reconnectWebSocket()
            return
        }

        try {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e(TAG, " Audio recording permission check failed")
                return
            }

            // Initialize AudioRecord
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                Log.e(TAG, " AudioRecord initialization failed. State: ${audioRecord?.state}")
                return
            }

            Log.d(TAG, " AudioRecord initialized successfully")
            Log.d(TAG, "Sample rate: $sampleRate, Buffer size: $bufferSize")

            audioRecord?.startRecording()
            isRecording.set(true)

            // Send start recording message to server
            webSocketClient?.startRecording()
            Log.d(TAG, " Sent START_RECORDING message to server")

            // Notify UI about recording state change
            onRecordingStateChanged?.invoke(true)

            Log.d(TAG, "🎙 Recording started, beginning audio stream...")

            // Start recording in a background thread
            CoroutineScope(Dispatchers.IO).launch {
                val buffer = ByteArray(bufferSize)
                var totalBytesSent = 0
                var packetCount = 0
                val startTime = System.currentTimeMillis()

                Log.d(TAG, " Audio streaming loop started")

                while (isRecording.get()) {
                    val readBytes = audioRecord?.read(buffer, 0, buffer.size) ?: 0

                    if (readBytes > 0) {
                        // Send the audio data
                        webSocketClient?.sendAudioData(buffer.copyOf(readBytes))
                        totalBytesSent += readBytes
                        packetCount++

                        // Log every 50 packets (roughly every second at 16kHz)
                        if (packetCount % 50 == 0) {
                            val elapsed = (System.currentTimeMillis() - startTime) / 1000.0
                            Log.d(TAG, " Audio Stats - Packets: $packetCount, Bytes: $totalBytesSent, Time: ${elapsed}s")
                        }
                    } else if (readBytes < 0) {
                        Log.w(TAG, "⚠ AudioRecord read error: $readBytes")
                        break
                    }
                }

                val totalTime = (System.currentTimeMillis() - startTime) / 1000.0
                Log.d(TAG, " Recording loop ended - Total: $packetCount packets, $totalBytesSent bytes in ${totalTime}s")
            }

        } catch (e: SecurityException) {
            Log.e(TAG, " Security exception during recording: ${e.message}")
            onRecordingStateChanged?.invoke(false)
        } catch (e: Exception) {
            Log.e(TAG, " Recording error: ${e.message}", e)
            onRecordingStateChanged?.invoke(false)
        }
    }

    fun stopRecording() {
        Log.d(TAG, " Stopping recording...")

        if (!isRecording.get()) {
            Log.d(TAG, " Not currently recording")
            return
        }

        isRecording.set(false)

        // Send stop recording message to server
        webSocketClient?.stopRecording()
        Log.d(TAG, " Sent STOP_RECORDING message to server")

        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null

        // Notify UI about recording state change
        onRecordingStateChanged?.invoke(false)

        Log.d(TAG, " Recording stopped successfully")
    }

    fun isCurrentlyRecording(): Boolean {
        return isRecording.get()
    }

    private fun checkPermission(): Boolean {
        val hasPermission = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        Log.d(TAG, "Audio permission check: $hasPermission")
        return hasPermission
    }

    fun cleanup() {
        Log.d(TAG, " Cleaning up WebSocketManager...")
        stopRecording()
        webSocketClient?.close()
    }

    fun isWebSocketConnected(): Boolean {
        val connected = webSocketClient?.isOpen == true
        Log.d(TAG, "WebSocket connection status: $connected")
        return connected
    }

    fun reconnect() {
        Log.d(TAG, " Manual reconnection requested")
        reconnectWebSocket()
    }

    // Test method to send a message and verify server response
    fun testConnection() {
        Log.d(TAG, " Testing WebSocket connection...")
        webSocketClient?.sendMessage("TEST_MESSAGE_FROM_ANDROID")
    }
}