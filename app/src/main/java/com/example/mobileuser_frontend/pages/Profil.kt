package com.example.mobileuser_frontend.pages


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.navigation.NavController
import com.example.mobileuser_frontend.Screens
import com.example.mobileuser_frontend.functions.fetchUserInfo
import com.example.mobileuser_frontend.module.fontSizeSubTitle
import com.example.mobileuser_frontend.module.fontSizeTitle
import com.example.mobileuser_frontend.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.firstOrNull

/*@Preview
@Composable
private fun ProfilPreview() {
    Profil()
}*/
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Profil(navController: NavController, viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
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
                    if (dragAmount < -60) {
                        // Swipe right to go back to Home
                        navController.navigate(Screens.MainScreen.route){popUpTo(0)}
                    }
                }
            }
    ){
        Column(
            modifier = Modifier
                .fillMaxHeight()
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
                    onClick = { navController.navigate(Screens.Parametre.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .height(50.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .padding(all = 15.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xff3AAFA9))
                ) {
                    Text(
                        text = "Paramètres",
                        color = Color.White,
                        lineHeight = 1.5.em,
                        style = TextStyle(fontSize = fontSizeSubTitle())
                    )
                }

                Button(
                    onClick = { navController.navigate(Screens.AddAidant.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .height(50.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .padding(all = 15.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xff17252A))
                ) {
                    Text(
                        text = "Appairage",
                        color = Color.White,
                        lineHeight = 1.5.em,
                        style = TextStyle(fontSize = fontSizeSubTitle())
                    )
                }

                Button(
                    onClick = { navController.navigate(Screens.Appareil.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .height(50.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .padding(all = 15.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xff3AAFA9))
                ) {
                    Text(
                        text = "Appareil",
                        color = Color.White,
                        lineHeight = 1.5.em,
                        style = TextStyle(fontSize = fontSizeSubTitle())
                    )
                }
            }
        }
    }
}
