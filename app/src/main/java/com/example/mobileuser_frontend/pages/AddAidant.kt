package com.example.mobileuser_frontend.pages

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobileuser_frontend.R

/*@Preview
@Composable
private fun AddAidantPreview() {
    AddAidant()
}*/
@Composable
fun AddAidant(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(color = Color(0xff191919))
    ) {
        Image(
            painter = painterResource(id = R.drawable.exit),
            contentDescription = "Icon",
            colorFilter = ColorFilter.tint(Color.White),
            modifier = Modifier.padding(12.dp)
                .requiredSize(size = 40.dp)
                .clickable { navController.popBackStack() })

        Column(
            verticalArrangement = Arrangement.spacedBy(1.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.qr),
                contentDescription = "QR",
                modifier = Modifier
                    .fillMaxSize(0.6f)
            )
            Text(
                text = "Placez le code à l'intérieur du cadre",
                color = Color.White,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 20.sp),
                modifier = Modifier
                    .fillMaxWidth())
        }
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxHeight().fillMaxWidth()
        ) {
            Button(
                onClick = { /* TODO: Handle Microphone Click */ },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xff3aafa9)),
                elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
                modifier = Modifier .padding(20.dp)
                    .align(Alignment.CenterHorizontally)
                    .size(100.dp) // Standardized size
                    .border(BorderStroke(4.dp, Color.White), CircleShape) // White border
            ) {
                Image(
                    painter = painterResource(id = R.drawable.microphone),
                    contentDescription = "Microphone",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
