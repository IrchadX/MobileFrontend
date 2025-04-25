package com.example.mobileuser_frontend

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mobileuser_frontend.API.ApiService
import com.example.mobileuser_frontend.module.NavBarItem
import com.example.mobileuser_frontend.ui.theme.MobileUser_FrontendTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URI
import java.util.concurrent.atomic.AtomicBoolean
import java.util.Random
import android.util.Base64


class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    private val PERMISSION_REQUEST_CODE = 200
    private lateinit var webSocketClient: AudioWebSocketClient
    private var audioRecord: AudioRecord? = null
    private val isRecording = AtomicBoolean(false)
    private val sampleRate = 16000 // Must match your Python script
    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )


    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup WebSocket client
        setupWebSocketClient()

        // Check for audio recording permission
        if (!checkPermission()) {
            requestPermission()
        }

        setContent {

            MobileUser_FrontendTheme {
                val navController = rememberNavController()
                val coroutineScope = rememberCoroutineScope()
                var isRecordingState by remember { mutableStateOf(false) }
                val density = LocalDensity.current

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(WindowInsets.navigationBars.asPaddingValues())
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures { _, dragAmount ->
                                if (dragAmount < -40) {
                                    navController.navigate(Screens.Profil.route){popUpTo(0)}
                                }
                                if (dragAmount > 40) {
                                    navController.navigate(Screens.Parametre.route){popUpTo(0)}
                                }
                            }
                        },
                    bottomBar = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .background(color = Color(0xffd1f1e6))
                                    .padding(horizontal = 40.dp)
                            ) {
                                // Home Button
                                NavBarItem(
                                    iconId = R.drawable.home,
                                    description = "Home",
                                    isSelected = false,
                                    onClick = { navController.navigate(Screens.MainScreen.route) }
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                // Profile Button
                                NavBarItem(
                                    iconId = R.drawable.profile,
                                    description = "Profile",
                                    isSelected = false,
                                    onClick = { navController.navigate(Screens.Profil.route) }
                                )
                            }

                            FloatingActionButton(
                                onClick = {
                                    isRecordingState = !isRecordingState
                                    if (isRecordingState) {
                                        coroutineScope.launch(Dispatchers.IO) {
                                            startRecording()
                                        }
                                    } else {
                                        stopRecording()
                                    }
                                },
                                shape = CircleShape,
                                backgroundColor = if (isRecordingState) Color.Red else Color(0xff3aafa9),
                                contentColor = Color.White,
                                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                                modifier = Modifier
                                    .size(90.dp)
                                    .padding(bottom = 2.dp)
                                    .offset(y = 5.dp)
                                    .border(BorderStroke(4.dp, Color.White), CircleShape)
                                    .align(Alignment.Center)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.microphone),
                                    contentDescription = "Microphone",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(10.dp)
                                )
                            }
                        }
                    }
                ) {
                    PageNavigation(navController)
                }
            }
        }

    }

    private fun setupWebSocketClient() {
        try {
            // IMPORTANT: Use the correct IP address here
            // For testing, if both devices are on the same network:
            val serverUri = URI("ws://172.20.10.2:8765") // Your Python server's IP

            // For local testing on the same device via ADB port forwarding:
            // val serverUri = URI("ws://10.0.2.2:8765")

            // For production deployment, use your server's domain or static IP

            // Create proper WebSocket headers
            val headers = HashMap<String, String>()
            headers["Connection"] = "Upgrade"
            headers["Upgrade"] = "websocket"
            headers["Sec-WebSocket-Version"] = "13"
            headers["Sec-WebSocket-Key"] = generateWebSocketKey() // Add this function

            // Initialize with headers
            webSocketClient = AudioWebSocketClient(serverUri, headers)

            // Set connection timeout
            webSocketClient.setConnectionLostTimeout(15) // 15 seconds

            // Connect with timeout
            Log.d(TAG, "Attempting to connect to WebSocket server...")
            webSocketClient.connect()

            // Add a timeout check to see if connection was successful
            Handler(Looper.getMainLooper()).postDelayed({
                if (webSocketClient.isOpen) {
                    Log.d(TAG, "WebSocket connection established successfully!")
                } else {
                    Log.e(TAG, "Failed to establish WebSocket connection after timeout")
                    // Add UI feedback here to inform the user
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

    private fun startRecording() {
        if (!checkPermission()) {
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
            webSocketClient.startRecording()

            val buffer = ByteArray(bufferSize)

            while (isRecording.get()) {
                val readBytes = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (readBytes > 0) {
                    webSocketClient.sendAudioData(buffer.copyOf(readBytes))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Recording error: ${e.message}")
        }
    }

    private fun stopRecording() {
        isRecording.set(false)
        webSocketClient.stopRecording()
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Audio recording permission granted")
            } else {
                Log.e(TAG, "Audio recording permission denied")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRecording()
        webSocketClient.close()
    }

}