package com.example.mobileuser_frontend


sealed class Screens (val route: String){
    object MainScreen : Screens("HomeScreen")
    object NavigationScreen : Screens("Navigation")
    object Profil : Screens("Profil")
    object Parametre : Screens("Parametre")
    object Information: Screens("Information")
    object Appareil : Screens("Appareil")
    object AddAidant : Screens("AddAidant")
    object Preferences : Screens("Preferences")
    object Appel : Screens("Appel")
}