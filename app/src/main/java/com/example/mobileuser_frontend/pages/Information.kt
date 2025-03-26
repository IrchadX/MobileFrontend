package com.example.mobileuser_frontend.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


/*@Preview
@Composable
private fun InformationPreview() {
    Information()
}*/
@Composable
fun Information(navController: NavController) {

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val text = remember { mutableStateOf("Omar El Farouk") }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight-10.dp)
            .background(color = Color(0xfffcfffe))
    ) {
        Text(
            text = "Informations Personnelles",
            color = Color(0xff17252a),
            style = TextStyle(
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier.align(Alignment.Start)
                .padding(10.dp),
        )



        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {

            Text(
                text = "Changer Nom",
                color = Color(0xff17252a),
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .wrapContentHeight(align = Alignment.CenterVertically)
            )


            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(color = Color(0xff2b7a78).copy(alpha = 0.05f))
                    .border(
                        border = BorderStroke(1.dp, Color(0xff17252a).copy(alpha = 0.12f)),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 15.dp),
            ) {

                TextField(
                    value = text.value, // String
                    onValueChange = { newText -> text.value = newText }, // (String) -> Unit
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(align = Alignment.CenterVertically),
                    textStyle = TextStyle(
                        fontSize = 15.sp,
                        color = Color(0xff17252a)
                    ),
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xff17252a)
                    )
                )
            }
            Divider(
                color = Color.Gray.copy(alpha = 0.3f),
                thickness = 2.dp,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Changer le mot de passe",
                color = Color(0xff17252a),
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .wrapContentHeight(align = Alignment.CenterVertically)
            )
            Text(
                text = "Mot de passe",
                color = Color(0xff17252a),
                style = TextStyle(
                    fontSize = 15.sp,
                ),
                modifier = Modifier
                    .wrapContentHeight(align = Alignment.CenterVertically)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(color = Color(0xff2b7a78).copy(alpha = 0.05f))
                    .border(
                        border = BorderStroke(1.dp, Color(0xff17252a).copy(alpha = 0.12f)),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 15.dp),
            ) {

                TextField(
                    value = text.value, // String
                    onValueChange = { newText -> text.value = newText }, // (String) -> Unit
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(align = Alignment.CenterVertically),
                    textStyle = TextStyle(
                        fontSize = 15.sp,
                        color = Color(0xff17252a)
                    ),
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xff17252a)
                    )
                )
            }


            Text(
                text = "Nouveau Mot de passe",
                color = Color(0xff17252a),
                style = TextStyle(
                    fontSize = 15.sp,
                ),
                modifier = Modifier
                    .wrapContentHeight(align = Alignment.CenterVertically)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(color = Color(0xff2b7a78).copy(alpha = 0.05f))
                    .border(
                        border = BorderStroke(1.dp, Color(0xff17252a).copy(alpha = 0.12f)),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 15.dp),
            ) {

                TextField(
                    value = text.value, // String
                    onValueChange = { newText -> text.value = newText }, // (String) -> Unit
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(align = Alignment.CenterVertically),
                    textStyle = TextStyle(
                        fontSize = 15.sp,
                        color = Color(0xff17252a)
                    ),
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xff17252a)
                    )
                )
            }
            Divider(
                color = Color.Gray.copy(alpha = 0.3f),
                thickness = 2.dp,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth().height(40.dp),
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
        }


    }
}

