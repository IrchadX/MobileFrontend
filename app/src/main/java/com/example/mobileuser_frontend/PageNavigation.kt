package com.example.mobileuser_frontend

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mobileuser_frontend.pages.AddAidant
import com.example.mobileuser_frontend.pages.Appareil
import com.example.mobileuser_frontend.pages.Appel
import com.example.mobileuser_frontend.pages.HomeScreen
import com.example.mobileuser_frontend.pages.Information
import com.example.mobileuser_frontend.pages.Navigation
import com.example.mobileuser_frontend.pages.Parametre
import com.example.mobileuser_frontend.pages.Preferences
import com.example.mobileuser_frontend.pages.Profil

@Composable
fun PageNavigation(navController: NavHostController) {

    NavHost(navController = navController, startDestination = Screens.MainScreen.route){
        composable(route = Screens.MainScreen.route){
            HomeScreen(navController = navController)
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
            AddAidant(navController = navController)
        }
        composable(route = Screens.Preferences.route){
            Preferences(navController = navController)
        }
        composable(route = Screens.Appel.route){
            Appel(navController = navController)
        }
    }
}