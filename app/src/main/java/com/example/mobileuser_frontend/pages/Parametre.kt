package com.example.mobileuser_frontend.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobileuser_frontend.Screens

/*@Preview
@Composable
private fun ParametrePreview() {
    Parametre()
}*/
@Composable
fun Parametre(navController: NavController) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    Column(
        modifier = Modifier
            .height(screenHeight - 130.dp)
            .background(color = Color(0xfffcfffe)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Bienvenue Omar El Farouk",
            color = Color(0xff17252a),
            style = TextStyle(
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier.align(Alignment.Start)
                .padding(23.dp),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
        ) {
            Button(
                onClick = { navController.navigate(Screens.Information.route)},
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .padding(all = 10.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xff3AAFA9))
            ) {
                Text(
                    text = "Informations Personnelles",
                    color = Color.White,
                    lineHeight = 1.5.em,
                    style = TextStyle(fontSize = 40.sp)
                )
            }

            Button(
                onClick = { navController.navigate(Screens.Preferences.route)},
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .padding(all = 10.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xff17252A))
            ) {
                Text(
                    text = "Préférences de Navigation",
                    color = Color.White,
                    lineHeight = 1.5.em,
                    style = TextStyle(fontSize = 40.sp)
                )
            }
        }
    }
}
