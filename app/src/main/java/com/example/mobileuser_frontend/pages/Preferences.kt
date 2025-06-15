package com.example.mobileuser_frontend.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobileuser_frontend.module.CustomDropdown


/*@Preview
@Composable
private fun PreferencesPreview() {
    Preferences()
}*/
@Composable
fun Preferences(navController: NavController) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight-10.dp)
            .background(color = Color(0xfffcfffe))
    ) {
        Text(
            text = "Préférences de Navigation",
            color = Color(0xff17252a),
            style = TextStyle(
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier.align(Alignment.Start)
                .padding(10.dp),
        )

        Text(
            text = "Guidage Vocal",
            color = Color(0xff17252a),
            style = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
            ),
            modifier = Modifier.align(Alignment.Start)
                .padding(start = 10.dp),
        )


        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()
        ) {

            Text(
                text = "Langue",
                color = Color(0xff17252a),
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier
                    .wrapContentHeight(align = Alignment.CenterVertically)
            )

            CustomDropdown(
                label = "",
                items = poiOptions,
                initialValue = "Choisir Votre Langue préférée",
                onItemSelected = {
                },
            )

            Text(
                text = "Genre de Voix",
                color = Color(0xff17252a),
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier
                    .wrapContentHeight(align = Alignment.CenterVertically)
            )
            CustomDropdown(
                label = "",
                items = poiOptions,
                initialValue = "Choisir Votre voix préférée",
                onItemSelected = {
                },
            )

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 2.dp,
                color = Color.Gray.copy(alpha = 0.3f)
            )
//Vibration Section
            Text(
                text = "Vibration",
                color = Color(0xff17252a),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.align(Alignment.Start),
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                Text(
                    text = "Sensibilité",
                    color = Color(0xff17252a),
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .wrapContentHeight(align = Alignment.CenterVertically)
                )

                CustomDropdown(
                    label = "",
                    items = poiOptions,
                    initialValue = "Choisir Votre niveau de sensibilité",
                    onItemSelected = {
                    },
                )
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 2.dp,
                    color = Color.Gray.copy(alpha = 0.3f)
                )

            }
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .semantics {
                        contentDescription = "Sauvegarder"
                    },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3AAFA9)),
                elevation = ButtonDefaults.elevatedButtonElevation(5.dp)
            ) {
                Text(
                    text = "Sauvegarder",
                    color = Color(0xfffcfffe),
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                )
            }

        }   }
}

