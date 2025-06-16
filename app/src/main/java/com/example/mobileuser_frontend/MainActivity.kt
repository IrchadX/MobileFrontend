package com.example.mobileuser_frontend

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mobileuser_frontend.module.NavBarItem
import com.example.mobileuser_frontend.repository.AuthRepository
import com.example.mobileuser_frontend.ui.theme.MobileUser_FrontendTheme
import com.example.mobileuser_frontend.viewmodel.AuthViewModel
import com.example.mobileuser_frontend.viewmodel.LocationViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions: Map<String, Boolean> ->

            val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (granted) {
                startLocationTracking(userId.toString())
            }
        }


    private val TAG = "MainActivity"
    private val PERMISSION_REQUEST_CODE = 200
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private lateinit var locationServiceManager: LocationServiceManager

    // SOLUTION 1: Use local variables for smart cast
    private var voiceCommandHandler: VoiceCommandHandler? = null
    private var webSocketManager: WebSocketManager? = null
    private lateinit var authViewModel: AuthViewModel
    private lateinit var userId: String // Declare here but initialize late

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    @SuppressLint(
        "UnusedMaterialScaffoldPaddingParameter",
        "UnusedMaterial3ScaffoldPaddingParameter"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        // Now it's safe to access authViewModel
        userId = "136"
        //For localisation
        locationServiceManager = LocationServiceManager(this)

        // Make sure location permissions are granted!
        if (hasLocationPermissions()) {
            locationServiceManager.startLocationTracking(userId)
        } else {

            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

        Log.d(TAG, "=== ONCREATE START ===")

        try {
            // Initialize voice command handler first
            voiceCommandHandler = VoiceCommandHandler(this)
            Log.d(TAG, "✅ VoiceCommandHandler initialized")

            // SOLUTION 1: Use local variable for smart cast
            val voiceHandler = voiceCommandHandler!! // Safe because we just initialized it

            // Initialize WebSocket manager with the handler
            webSocketManager = WebSocketManager(this, voiceHandler)
            Log.d(TAG, "✅ WebSocketManager initialized")

            val wsManager = webSocketManager!! // Safe because we just initialized it

            // Connect them together
            voiceHandler.setWebSocketManager(wsManager)
            Log.d(TAG, "✅ Components connected")

            // Check for audio recording permission
            if (!checkPermission()) {
                requestPermission()
            }

            AuthViewModel.initialize(applicationContext)
            val authRepository = AuthRepository(applicationContext)
            authViewModel = ViewModelProvider(
                this,
                AuthViewModelFactory(authRepository)
            )[AuthViewModel::class.java]

            setContent {
                MobileUser_FrontendTheme {
                    val navController = rememberNavController()
                    var dragAmount by remember { mutableStateOf(0f) }
                    val density = LocalDensity.current
                    val sensitivity = with(density) { 70.dp.toPx() }
                    val currentRoute =
                        navController.currentBackStackEntryAsState().value?.destination?.route

                    var isAuthenticated by remember { mutableStateOf(false) }
                    val context = LocalContext.current
                    val viewModel = LocationViewModel(context)

                    // State for microphone recording
                    var isRecording by remember { mutableStateOf(false) }

                    // Set up the recording state callback
                    LaunchedEffect(Unit) {
                        // SOLUTION 1: Use local variable again
                        wsManager.onRecordingStateChanged = { recording ->
                            isRecording = recording
                        }

                        // Set the navigation controller for voice commands
                        voiceHandler.setNavController(navController)
                        Log.d(TAG, "✅ NavController set for voice commands")
                    }

                    // Check authentication state
                    LaunchedEffect(Unit) {
                        authRepository.getUserId().collectLatest { userId ->
                            isAuthenticated = !userId.isNullOrEmpty()
                            viewModel.startPeriodicLocationUpdates(userId.toString())
                        }
                    }

                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        LOCATION_PERMISSION_REQUEST_CODE
                    )

                    Scaffold(
                        modifier = Modifier.fillMaxSize()
                            .padding(WindowInsets.navigationBars.asPaddingValues())
                            .pointerInput(Unit) {
                                detectHorizontalDragGestures { _, dragAmount ->
                                    if (isAuthenticated) {
                                        if (dragAmount < -40) {
                                            navController.navigate(Screens.Profil.route) { popUpTo(0) }
                                        }
                                        if (dragAmount > 40) {
                                            navController.navigate(Screens.Parametre.route) { popUpTo(0) }
                                        }
                                    } else {
                                        navController.navigate(Screens.SignInScreen.route) {
                                            popUpTo(Screens.MainScreen.route) { inclusive = true }
                                        }
                                    }
                                }
                            },

                        bottomBar = {
                            if (currentRoute !in listOf("SignInScreen", "SignUpScreen")) {
                                NavBar(
                                    navController = navController,
                                    isRecording = isRecording,
                                    onMicrophoneClick = {
                                        wsManager.toggleRecording()
                                    }
                                )
                            }
                        }
                    ) { paddingValues ->
                        PageNavigation(
                            navController,
                            modifier = Modifier.padding(paddingValues),
                            authRepository,
                            voiceHandler // Pass the local variable
                        )
                    }
                }
            }

            Log.d(TAG, "=== ONCREATE SUCCESS ===")

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error in onCreate: ${e.message}", e)
            throw e // Re-throw to show in crash logs
        }
    }

    private fun hasLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
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
        permissions: Array<String>,
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

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "=== ONDESTROY START ===")
        webSocketManager?.cleanup()
        Log.d(TAG, "=== ONDESTROY COMPLETE ===")
    }

    // SOLUTION 1: Use local variable approach
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun onLanguageChangedFromUI(newLanguage: String) {
        Log.d(TAG, "=== LANGUAGE CHANGE FROM UI ===")
        Log.d(TAG, "Language change requested: $newLanguage")

        val handler = voiceCommandHandler // Create local variable
        if (handler != null) {
            handler.onLanguageChanged()
            Log.d(TAG, "✅ VoiceCommandHandler notified of language change")
        } else {
            Log.e(TAG, "❌ VoiceCommandHandler is null - cannot process language change")
        }

        Log.d(TAG, "=== LANGUAGE CHANGE FROM UI END ===")
    }
    private fun startLocationTracking(userId : String ) {
        locationServiceManager.startLocationTracking(userId)
    }
}

// ALTERNATIVE SOLUTION 2: Use lateinit (if you're sure it will be initialized)
/*
class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    private val PERMISSION_REQUEST_CODE = 200
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    // Use lateinit instead of nullable
    private lateinit var voiceCommandHandler: VoiceCommandHandler
    private lateinit var webSocketManager: WebSocketManager
    private lateinit var authViewModel: AuthViewModel

    // Then you can use them directly without null checks
    // voiceCommandHandler.setWebSocketManager(webSocketManager)
    // But be careful - accessing before initialization will crash
}
*/

// ALTERNATIVE SOLUTION 3: Use by lazy delegation
/*
class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    private val PERMISSION_REQUEST_CODE = 200
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    private val voiceCommandHandler by lazy { VoiceCommandHandler(this) }
    private val webSocketManager by lazy { WebSocketManager(this, voiceCommandHandler) }
    private lateinit var authViewModel: AuthViewModel

    // These will be initialized on first access
    // voiceCommandHandler.setWebSocketManager(webSocketManager)
}
*/


class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LocationViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class AuthViewModelFactory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun NavBar(
    navController: NavController,
    isRecording: Boolean,
    onMicrophoneClick: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(100.dp)
                .background(color = Color(0xffd1f1e6))
                .padding(horizontal = 30.dp)
        ) {
            // Home Button
            NavBarItem(
                iconId = R.drawable.home,
                description = "Home",
                isSelected = currentRoute == Screens.MainScreen.route,
                onClick = { navController.navigate(Screens.MainScreen.route) }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Profile Button
            NavBarItem(
                iconId = R.drawable.profile,
                description = "Profile",
                isSelected = currentRoute == Screens.Profil.route,
                onClick = { navController.navigate(Screens.Profil.route) }
            )
        }

        FloatingActionButton(
            onClick = onMicrophoneClick,
            containerColor = if (isRecording) Color(0xFFFF4444) else Color(0xff3aafa9),
            shape = CircleShape,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
            modifier = Modifier
                .size(130.dp)
                .padding(bottom = 2.dp)
                .offset(y = -10.dp)
                .border(
                    BorderStroke(4.dp, if (isRecording) Color(0xFFFF6666) else Color.White),
                    CircleShape
                )
                .align(Alignment.Center)
        ) {
            Image(
                painter = painterResource(id = R.drawable.microphone),
                contentDescription = if (isRecording) "Stop Recording" else "Start Recording",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            )
        }
    }
}