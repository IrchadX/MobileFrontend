package com.example.mobileuser_frontend.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.navigation.NavController
import com.example.mobileuser_frontend.R
import com.example.mobileuser_frontend.Screens
import com.example.mobileuser_frontend.module.fontSizeTitle

@Composable
fun HomeScreen(navController: NavController) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Box (
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    if (dragAmount < -100) {
                        navController.navigate(Screens.Profil.route)
                    } else if (dragAmount > 100) {
                        navController.navigate(Screens.Parametre.route)
                    }
                }
            }
    ){
        Column(
            modifier = Modifier
                .height(screenHeight - 130.dp)
                .background(color = Color(0xfffcfffe)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.vector),
                contentDescription = "Vector",
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(0.12f)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
            ) {
                Button(
                    onClick = { navController.navigate(Screens.Profil.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .padding(all = 10.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xffcc2222))
                ) {
                    Text(
                        text = "S.O.S",
                        color = Color.White,
                        lineHeight = 1.5.em,
                        style = TextStyle(fontSize = fontSizeTitle())
                    )
                }

                Button(
                    onClick = { navController.navigate(Screens.Appel.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .padding(all = 10.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xff17252a))
                ) {
                    Text(
                        text = "Assistance",
                        color = Color.White,
                        lineHeight = 1.5.em,
                        style = TextStyle(fontSize = fontSizeTitle())
                    )
                }

                Button(
                    onClick = { navController.navigate(Screens.NavigationScreen.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .padding(all = 10.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xff3aafa9))
                ) {
                    Text(
                        text = "Naviguer",
                        color = Color.White,
                        lineHeight = 1.5.em,
                        style = TextStyle(fontSize = fontSizeTitle())
                    )
                }
            }
        }
    }
}

@Preview(widthDp = 375, heightDp = 812)
@Composable
private fun HomeScreenPreview() {
    //HomeScreen()
}