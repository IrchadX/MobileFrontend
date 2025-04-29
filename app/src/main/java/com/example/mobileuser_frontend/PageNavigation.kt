package com.example.mobileuser_frontend

import AddAidant
import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.runtime.Composable
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

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun PageNavigation(navController: NavHostController, modifier: Modifier = Modifier) {

    NavHost(navController = navController, startDestination = Screens.SignUpScreen.route,  modifier = modifier){
        composable(route = Screens.MainScreen.route){
            HomeScreen(navController = navController)
        }
        composable(route = Screens.SignInScreen.route){
            SignInScreen(navController = navController)
        }
        composable(route = Screens.SignUpScreen.route){
            SignUpScreen(navController = navController)
        }
        composable(route = Screens.NavigationScreen.route){
            Navigation(navController = navController)
        }
        composable(route = Screens.Profil.route){
            Profil(navController = navController)
        }
        composable(route = Screens.Parametre.route){
            Parametre(navController = navController)
        }
        composable(route = Screens.Information.route){
            Information()
        }
        composable(route = Screens.Appareil.route){
            Appareil(navController = navController)
        }
        composable(route = Screens.AddAidant.route){
            AddAidant(navController= navController)
        }
        composable(route = Screens.Preferences.route){
            Preferences(navController = navController)
        }
        composable(route = Screens.Appel.route){
            Appel(navController = navController)
        }
    }
}