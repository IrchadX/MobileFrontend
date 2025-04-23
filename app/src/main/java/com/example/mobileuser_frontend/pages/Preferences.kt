package com.example.mobileuser_frontend.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobileuser_frontend.module.DropDown


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
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(color = Color(0xff2b7a78).copy(alpha = 0.05f))
                    .border(
                        border = BorderStroke(1.dp, Color(0xff17252a).copy(alpha = 0.12f)),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(
                        horizontal = 15.dp,
                    ),
            ) {
                Text(
                    text = "Choisissez votre langue préférée",
                    color = Color(0xff17252a),
                    style = TextStyle(
                        fontSize = 15.sp,
                    ),
                    modifier = Modifier
                        .wrapContentHeight(align = Alignment.CenterVertically)
                )
                Icon(
                    imageVector = Icons.Rounded.ArrowDropDown,
                    contentDescription = "More",
                    tint = Color.Gray,
                    modifier = Modifier.size(55.dp),

                    )
            }
            val dropdownState = remember { DropDown() }

            //StableDropdown(dropdownState = dropdownState)
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
            /*Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(color = Color(0xff2b7a78).copy(alpha = 0.05f))
                    .border(
                        border = BorderStroke(1.dp, Color(0xff17252a).copy(alpha = 0.12f)),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(
                        horizontal = 15.dp,
                    ),
            ) {
                Text(
                    text = "Choisissez votre voix préférée",
                    color = Color(0xff17252a),
                    style = TextStyle(
                        fontSize = 15.sp,
                    ),
                    modifier = Modifier
                        .wrapContentHeight(align = Alignment.CenterVertically)
                )
                Icon(
                    imageVector = Icons.Rounded.ArrowDropDown,
                    contentDescription = "More",
                    tint = Color.Gray,
                    modifier = Modifier.size(55.dp),

                    )
            }*/

            //StableDropdown(dropdownState = dropdownState)

            Divider(
                color = Color.Gray.copy(alpha = 0.3f),
                thickness = 2.dp,
                modifier = Modifier.fillMaxWidth()
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
                /* Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth().height(60.dp)
                        .clip(shape = RoundedCornerShape(8.dp))
                        .background(color = Color(0xff2b7a78).copy(alpha = 0.05f))
                        .border(
                            border = BorderStroke(1.dp, Color(0xff17252a).copy(alpha = 0.12f)),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(
                            horizontal = 15.dp,
                        ),
                ) {
                    Text(
                        text = "Choisissez votre langue préférée",
                        color = Color(0xff17252a),
                        style = TextStyle(
                            fontSize = 15.sp,
                        ),
                        modifier = Modifier
                            .wrapContentHeight(align = Alignment.CenterVertically)
                    )
                    Icon(
                        imageVector = Icons.Rounded.ArrowDropDown,
                        contentDescription = "More",
                        tint = Color.Gray,
                        modifier = Modifier.size(55.dp),

                        )
                }


            }*/
                //StableDropdown(dropdownState = dropdownState)
                Divider(
                    color = Color.Gray.copy(alpha = 0.3f),
                    thickness = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                )

            }
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF3AAFA9)),
                elevation = ButtonDefaults.elevation(5.dp)
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

