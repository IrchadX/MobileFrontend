package com.example.mobileuser_frontend

import AddAidant
import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mobileuser_frontend.pages.Appareil
import com.example.mobileuser_frontend.pages.Appel
import com.example.mobileuser_frontend.pages.HomeScreen
import com.example.mobileuser_frontend.pages.Information
import com.example.mobileuser_frontend.pages.Navigation
import com.example.mobileuser_frontend.pages.Parametre
import com.example.mobileuser_frontend.pages.Preferences
import com.example.mobileuser_frontend.pages.Profil
import com.example.mobileuser_frontend.pages.SignInScreen
import com.example.mobileuser_frontend.pages.SignUpScreen
import com.example.mobileuser_frontend.repository.AuthRepository
import kotlinx.coroutines.flow.collectLatest

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun PageNavigation(navController: NavHostController, modifier: Modifier = Modifier, authRepository: AuthRepository, voiceCommandHandler: VoiceCommandHandler) {

    var isAuthenticated by remember { mutableStateOf(false) }

    // Check authentication state
    LaunchedEffect(Unit) {
        authRepository.getUserId().collectLatest { userId ->
            isAuthenticated = !userId.isNullOrEmpty()
        }
    }
    /*var startDestination by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // Run this block to check if the user is logged in
    LaunchedEffect(Unit) {
        val userId = context.authDataStore.data
            .map { preferences -> preferences[USER_ID] }
            .firstOrNull() // Get the user_id from DataStore

        startDestination = if (!userId.isNullOrBlank()) {
            // User is logged in
            Screens.MainScreen.route // Set to Home or Main Screen
        } else {
            // User is not logged in
            Screens.SignInScreen.route // Set to SignIn Screen
        }
    }

    // Ensure that startDestination is set before proceeding with the NavHost
    if (startDestination != null) {*/
        NavHost(
            navController = navController,
            startDestination = Screens.SignInScreen.route, // Default fallback
            modifier = modifier
        ) {
            composable(route = Screens.MainScreen.route) {
                if (isAuthenticated) {
                    HomeScreen(navController = navController)
                } else {
                    navController.navigate(Screens.SignInScreen.route) {
                        popUpTo(Screens.MainScreen.route) { inclusive = true }
                    }
                }
            }
            composable(route = Screens.SignInScreen.route) {
                SignInScreen(navController = navController)
            }
            composable(route = Screens.SignUpScreen.route) {
                SignUpScreen(navController = navController)
            }
            composable(route = Screens.NavigationScreen.route) {
                Navigation(navController = navController)
            }
            composable(route = Screens.Profil.route) {
                Profil(navController = navController)
            }
            composable(route = Screens.Parametre.route) {
                Parametre(navController = navController)
            }
            composable(route = Screens.Information.route) {
                Information()
            }
            composable(route = Screens.Appareil.route) {
                Appareil(navController = navController)
            }
            composable(route = Screens.AddAidant.route) {
                AddAidant(navController = navController)
            }
            composable(route = Screens.Preferences.route) {
                Preferences(navController = navController, voiceCommandHandler = voiceCommandHandler)
            }
            composable(route = Screens.Appel.route) {
                Appel(navController = navController)
            }
        }
   // }
}

