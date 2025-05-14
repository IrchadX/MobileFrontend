package com.example.mobileuser_frontend.service

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
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.mobileuser_frontend.AudioWebSocketClient
import com.example.mobileuser_frontend.VoiceCommandHandler
import java.net.URI
import java.util.Random
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Service that handles audio recording and WebSocket communication
 */
class AudioService(private val context: Context) {
    private val TAG = "AudioService"
    private val PERMISSION_REQUEST_CODE = 200
    private lateinit var webSocketClient: AudioWebSocketClient
    private lateinit var voiceCommandHandler: VoiceCommandHandler
    private var audioRecord: AudioRecord? = null
    private val isRecording = AtomicBoolean(false)
    private val sampleRate = 16000 // Match the sample rate expected by the Python Vosk model
    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    init {
        // Initialize the voice command handler first
        voiceCommandHandler = VoiceCommandHandler(context)
        Log.d(TAG, "VoiceCommandHandler initialized")

        // Setup WebSocket client with callback to the voice command handler
        setupWebSocketClient()
    }

    /**
     * Set the navigation controller for the voice command handler
     */
    fun setNavController(navController: NavController) {
        if (::voiceCommandHandler.isInitialized) {
            voiceCommandHandler.setNavController(navController)
            Log.d(TAG, "NavController set in AudioService and passed to VoiceCommandHandler")
        } else {
            Log.e(TAG, "VoiceCommandHandler not initialized, cannot set NavController")
        }
    }

    private fun setupWebSocketClient() {
        try {
            // IMPORTANT: Use the correct IP address and protocol format
            // FIX: Added proper ws:// protocol prefix
            // NOTE: Make sure to change this IP address to match your server
            val serverUri = URI("ws://172.20.10.2:8765") // Your Python server's IP

            // For local testing on the same device via ADB port forwarding:
            // val serverUri = URI("ws://10.0.2.2:8765")

            // Create proper WebSocket headers
            val headers = HashMap<String, String>()
            headers["Connection"] = "Upgrade"
            headers["Upgrade"] = "websocket"
            headers["Sec-WebSocket-Version"] = "13"
            headers["Sec-WebSocket-Key"] = generateWebSocketKey()

            // Initialize with headers and message callback for voice commands
            webSocketClient = AudioWebSocketClient(
                serverUri,
                headers
            ) { message ->
                // Process received messages through the voice command handler
                Log.d(TAG, "Received WebSocket message, forwarding to VoiceCommandHandler: $message")
                voiceCommandHandler.processWebSocketMessage(message)
            }

            // Connect with timeout
            Log.d(TAG, "Attempting to connect to WebSocket server at $serverUri...")
            webSocketClient.connect()

            // Add a timeout check to see if connection was successful
            Handler(Looper.getMainLooper()).postDelayed({
                if (webSocketClient.isOpen) {
                    Log.d(TAG, "WebSocket connection established successfully!")
                    // Send a test message after connection is established
                    webSocketClient.sendMessage("ANDROID_CLIENT_CONNECTED")
                } else {
                    Log.e(TAG, "Failed to establish WebSocket connection after timeout")
                    // Try reconnecting once
                    webSocketClient.reconnect()
                }
            }, 5000) // 5 second timeout
        } catch (e: Exception) {
            Log.e(TAG, "WebSocket setup error: ${e.message}", e)
        }
    }

    // helper function
    private fun generateWebSocketKey(): String {
        val key = ByteArray(16)
        Random().nextBytes(key)
        return Base64.encodeToString(key, Base64.NO_WRAP)
    }

    fun startRecording() {
        if (!checkPermission()) {
            Log.e(TAG, "Missing audio recording permission")
            return
        }

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                Log.e(TAG, "AudioRecord initialization failed")
                return
            }

            audioRecord?.startRecording()
            isRecording.set(true)

            // Make sure WebSocket is open before starting
            if (!webSocketClient.isOpen) {
                Log.d(TAG, "WebSocket not connected, attempting to reconnect...")
                webSocketClient.reconnect()

                // Short delay to allow reconnection
                Handler(Looper.getMainLooper()).postDelayed({
                    webSocketClient.startRecording()
                }, 1000)
            } else {
                webSocketClient.startRecording()
            }

            Log.d(TAG, "Audio recording started with buffer size: $bufferSize")

            // Start a separate thread for reading audio data
            Thread {
                val buffer = ByteArray(bufferSize)

                while (isRecording.get()) {
                    val readBytes = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (readBytes > 0) {
                        Log.d(TAG, "Read $readBytes bytes of audio data")
                        webSocketClient.sendAudioData(buffer.copyOf(readBytes))
                    }
                }
            }.start()

        } catch (e: Exception) {
            Log.e(TAG, "Recording error: ${e.message}", e)
        }
    }

    fun stopRecording() {
        if (isRecording.get()) {
            Log.d(TAG, "Stopping audio recording")
            isRecording.set(false)
            webSocketClient.stopRecording()
            audioRecord?.stop()
            audioRecord?.release()
            audioRecord = null
        }
    }

    fun checkAndRequestPermissions() {
        if (!checkPermission()) {
            requestPermission()
        } else {
            Log.d(TAG, "Audio recording permission already granted")
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        if (context is android.app.Activity) {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                PERMISSION_REQUEST_CODE
            )
        } else {
            Log.e(TAG, "Context is not an activity, cannot request permissions")
        }
    }

    fun cleanup() {
        stopRecording()
        if (::webSocketClient.isInitialized) {
            webSocketClient.close()
        }
    }
}