package com.example.mobileuser_frontend

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobileuser_frontend.pages.*

@Composable
fun PageNavigation() {
    val navController = rememberNavController()
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
            Information(navController = navController)
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