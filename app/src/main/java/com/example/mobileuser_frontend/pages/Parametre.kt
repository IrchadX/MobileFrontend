package com.example.mobileuser_frontend.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mobileuser_frontend.Screens
import com.example.mobileuser_frontend.functions.fetchUserInfo
import com.example.mobileuser_frontend.module.fontSizeSubTitle
import com.example.mobileuser_frontend.module.fontSizeTitle
import com.example.mobileuser_frontend.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/*@Preview
@Composable
private fun ParametrePreview() {
    Parametre()
}*/
@Composable
fun Parametre(navController: NavController, viewModel: AuthViewModel= viewModel()) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val coroutineScope = rememberCoroutineScope()

    var id by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        id = viewModel.authRepository.getUserId().firstOrNull() ?: ""
        // now you can use id
    }
    Box (
        modifier = Modifier.fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    if (dragAmount < 40) {
                        // Swipe right to go back to Home
                        navController.navigate(Screens.MainScreen.route){popUpTo(0)}
                    }
                }
            }
    ){
        Column(
            modifier = Modifier.fillMaxHeight()
                .background(color = Color(0xfffcfffe)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LaunchedEffect(id) {  // 'id' as key to restart when id changes
                fetchUserInfo(id) { name ->
                    value = name ?: "" // This will trigger recomposition
                }

                /*

                else {
                    Toast.makeText(context, "Error: Refresh Page", Toast.LENGTH_LONG).show()
                }
                 */
            }


            Text(
                text = "Bienvenue $value",
                color = Color(0xff17252a),
                style = TextStyle(
                    fontSize = fontSizeTitle(),
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(23.dp),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
            ) {
                Button(
                    onClick = { navController.navigate(Screens.Information.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(2.dp))
                        .padding(all = 10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xff3AAFA9))
                ) {
                    Text(
                        text = "Informations Personnelles",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 1.5.em,
                        style = TextStyle(fontSize = fontSizeSubTitle())
                    )
                }

                Button(
                    onClick = { navController.navigate(Screens.Preferences.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(2.dp))
                        .padding(all = 10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xff17252A))
                ) {
                    Text(
                        text = "Préférences de Navigation",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 1.5.em,
                        style = TextStyle(fontSize = fontSizeSubTitle())
                    )
                }
                Button(
                    onClick = {  /*when {
                        callPermissionState.status.isGranted -> {
                            // Permission already granted - make the call
                            fetchPhoneNumber("66") { phone ->
                                if (phone != null) {
                                    makePhoneCall(context,phone)
                                } else {
                                    Toast.makeText(context, "Error: Try again Please", Toast.LENGTH_LONG).show()
                                }
                            }

                        }
                        callPermissionState.status.shouldShowRationale -> {
                            Toast.makeText(
                                context,
                                "Phone call permission is required to make calls",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        else -> {
                            // Request permission
                            callPermissionState.launchPermissionRequest()
                        }
                    }*/
                        coroutineScope.launch {
                            viewModel.authRepository.clearAuthInfo()
                        }
                        navController.navigate(Screens.SignInScreen.route)

                    },
                    modifier = Modifier.semantics {
                        this.contentDescription = "Se Déconnecter"
                    }
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .padding(all = 10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xff3AAFA9))
                ) {
                    Text(
                        text = "Se Déconnecter",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 1.5.em,
                        style = TextStyle(fontSize = fontSizeSubTitle())
                    )
                }
            }
        }
    }
}
