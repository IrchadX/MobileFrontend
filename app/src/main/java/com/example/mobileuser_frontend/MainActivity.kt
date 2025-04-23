package com.example.mobileuser_frontend

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mobileuser_frontend.module.NavBarItem
import com.example.mobileuser_frontend.ui.theme.MobileUser_FrontendTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            MobileUser_FrontendTheme {
                val navController = rememberNavController()
                var dragAmount by remember { mutableStateOf(0f) }
                val density = LocalDensity.current
                val sensitivity = with(density) { 70.dp.toPx() } // Convert dp to pixels


                Scaffold(
                    modifier = Modifier.fillMaxSize()
                        .padding(WindowInsets.navigationBars.asPaddingValues())
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures { _, dragAmount ->
                                if (dragAmount < -40) {
                                    // Swipe right to go back to Home
                                    navController.navigate(Screens.Profil.route){popUpTo(0)}
                                }
                                if (dragAmount > 40) {
                                    // Swipe right to go back to Home
                                    navController.navigate(Screens.Parametre.route){popUpTo(0)}
                                }
                            }
                        }
                        ,

                    bottomBar = {
                            NavBar(navController)

                    }
                ) {
                    PageNavigation(navController)
                }
            }
        }
    }

}



@Composable
fun NavBar(navController: NavController) {
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
                .height(50.dp)
                .background(color = Color(0xffd1f1e6))
                .padding(horizontal = 40.dp)
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
            onClick = { /* TODO */ },
            shape = CircleShape,
            contentColor = Color(0xff3aafa9),
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



/*object RetrofitClient {
    private const val BASE_URL = "http://localhost:3000/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}*/