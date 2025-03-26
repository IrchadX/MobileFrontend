package com.example.mobileuser_frontend

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mobileuser_frontend.ui.theme.MobileUser_FrontendTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            MobileUser_FrontendTheme {
                val navController = rememberNavController()
                val currentRoute = currentRoute(navController)
                val hiddenBottomBarRoutes = setOf(
                    "AddAidant"
                )
                Scaffold(
                    bottomBar = {
                        if (currentRoute !in hiddenBottomBarRoutes) {
                            NavBar(navController)
                        }
                    }
                ) {
                    PageNavigation()
                }
            }
        }
    }
}

//Fonction pour récupérer la route actuelle
@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route

}


@Composable
fun NavBar(navController: NavController){
    val c = currentRoute(navController = navController)
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween, // Ensures proper spacing
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.BottomCenter) // Places it at the bottom
                .fillMaxWidth()
                .background(color = Color(0xffd1f1e6))
                .padding(horizontal = 40.dp)
        ) {
            // Home Button
            Button(
                onClick = {
                    println("Current Route: $c")
                    //navController.navigate(Screens.MainScreen.route)
                    },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent), // Transparent background
                elevation = null, // No shadow effect
                modifier = Modifier.size(70.dp) // Ensures button size fits image
            ) {
                Image(
                    painter = painterResource(id = R.drawable.home),
                    contentDescription = "Home",
                    modifier = Modifier.fillMaxSize() // Ensures it fits inside button
                )
            }

            Spacer(modifier = Modifier.weight(1f)) // Pushes items apart evenly

            // Profile Button
            Button(
                onClick = {navController.navigate(Screens.Profil.route)},
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                elevation = null,
                modifier = Modifier.size(70.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "Profile",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        FloatingActionButton(
            onClick = { /* TODO: Handle Microphone Click */ },
            shape = CircleShape,
            contentColor = Color(0xff3aafa9),
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
            modifier = Modifier
                .size(90.dp)
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