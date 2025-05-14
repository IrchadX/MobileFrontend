package com.example.mobileuser_frontend

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mobileuser_frontend.module.NavBarItem
import com.example.mobileuser_frontend.repository.AuthRepository
import com.example.mobileuser_frontend.service.AudioService
import com.example.mobileuser_frontend.ui.theme.MobileUser_FrontendTheme
import com.example.mobileuser_frontend.viewmodel.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter",
        "UnusedMaterial3ScaffoldPaddingParameter"
    )
    private lateinit var authViewModel: AuthViewModel
    private lateinit var audioService: AudioService

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize AuthViewModel
        AuthViewModel.initialize(applicationContext)
        val authRepository = AuthRepository(applicationContext)
        authViewModel = ViewModelProvider(
            this,
            AuthViewModelFactory(authRepository)
        )[AuthViewModel::class.java]

        // Initialize AudioService
        audioService = AudioService(this)

        // Check for audio recording permission
        audioService.checkAndRequestPermissions()

        setContent {
            MobileUser_FrontendTheme {
                val navController = rememberNavController()

                // Set the NavController in the AudioService for voice navigation
                setNavControllerForVoiceCommands(navController)

                val coroutineScope = rememberCoroutineScope()
                var isRecordingState by remember { mutableStateOf(false) }
                val density = LocalDensity.current
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                        .padding(WindowInsets.navigationBars.asPaddingValues())
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures { _, dragAmount ->
                                if (dragAmount < -40) {
                                    // Swipe right to go back to Home
                                    navController.navigate(Screens.Profil.route) { popUpTo(0) }
                                }
                                if (dragAmount > 40) {
                                    // Swipe right to go back to Home
                                    navController.navigate(Screens.Parametre.route) { popUpTo(0) }
                                }
                            }
                        },

                    bottomBar = {
                        if (currentRoute !in listOf("SignInScreen", "SignUpScreen")) {
                            NavBar(
                                navController = navController,
                                isRecording = isRecordingState,
                                onRecordingStateChange = { newState ->
                                    isRecordingState = newState
                                    if (isRecordingState) {
                                        coroutineScope.launch(Dispatchers.IO) {
                                            audioService.startRecording()
                                        }
                                    } else {
                                        audioService.stopRecording()
                                    }
                                }
                            )
                        }
                    }
                ) { paddingValues ->
                    PageNavigation(navController, modifier = Modifier.padding(paddingValues))
                }
            }
        }
    }

    /**
     * Set the NavController in the AudioService for voice navigation
     */
    private fun setNavControllerForVoiceCommands(navController: NavController) {
        try {
            audioService.setNavController(navController)
            Log.d(TAG, "NavController successfully set for voice commands")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set NavController for voice commands: ${e.message}", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioService.cleanup()
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
    isRecording: Boolean = false,
    onRecordingStateChange: (Boolean) -> Unit = {}
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

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
            onClick = { onRecordingStateChange(!isRecording) },
            containerColor = if (isRecording) Color.Red else Color(0xff3aafa9),
            shape = CircleShape,
            contentColor = Color(0xFF2B7A78),
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
            modifier = Modifier
                .size(110.dp)
                .padding(bottom = 2.dp)
                .offset(y = -20.dp)
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