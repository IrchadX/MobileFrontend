package com.example.mobileuser_frontend.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobileuser_frontend.R


/*@Preview
@Composable
private fun AppareilPreview() {
    Appareil()
}*/
@Composable
fun Appareil(navController: NavController) {

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight-10.dp)
            .background(color = Color(0xfffcfffe))
    ) {
        Text(
            text = "Appareil",
            color = Color(0xff17252a),
            lineHeight = 3.62.em,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,),
            modifier = Modifier
                .padding(10.dp),
        )
        Column(

            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()
        ) {

            Text(
                text = "État de la batterie",
                color = Color(0xff17252a),
                style = MaterialTheme.typography.h5)
            Box(
                modifier = Modifier
                    .requiredWidth(width = 280.dp)
                    .requiredHeight(height = 50.dp)
                    .clip(shape = RoundedCornerShape(6.dp))
                    .background(color = Color(0xff3aafa9))
            ) {
                Text(
                    text = "75 %",
                    color = Color.White,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold),
                    modifier = Modifier .padding(start= 5.dp)
                        .fillMaxWidth())
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "État de l'appareil",
                color = Color.Black,
                lineHeight = 6.51.em,
                style = TextStyle(
                    fontSize = 15.sp
                ),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Sans fil",
                    color = Color(0xff17252a),
                    lineHeight = 8.75.em,
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .requiredWidth(width = 281.dp)
                        .wrapContentHeight(align = Alignment.CenterVertically)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(8.dp))
                        .background(color = Color(0xff2b7a78).copy(alpha = 0.05f))
                        .border(
                            border = BorderStroke(1.dp, Color(0xff17252a).copy(alpha = 0.12f)),
                            shape = RoundedCornerShape(8.dp)
                        )

                ) {
                    Image(
                        painter = painterResource(id = R.drawable.wifisquare),
                        contentDescription = "connexion",
                        modifier = Modifier.padding(12.dp)
                            .requiredSize(size = 20.dp)
                    )
                    Text(
                        text = "Connecté",
                        color = Color(0xff17252a),
                        lineHeight = 10.em,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier
                            .wrapContentHeight(align = Alignment.CenterVertically)
                    )

                }
            }


            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Top),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Type",
                    color = Color(0xff17252a),
                    lineHeight = 8.75.em,
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
                        .clip(shape = RoundedCornerShape(8.dp))
                        .background(color = Color(0xff2b7a78).copy(alpha = 0.05f))
                        .border(
                            border = BorderStroke(
                                1.dp,
                                Color(0xff17252a).copy(alpha = 0.12f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(
                            horizontal = 16.dp,
                            vertical = 13.dp
                        )
                ) {
                    Text(
                        text = "Lunettes",
                        color = Color(0xff17252a),
                        lineHeight = 10.em,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier
                            .wrapContentHeight(align = Alignment.CenterVertically)
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Top),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "ID",
                    color = Color(0xff17252a),
                    lineHeight = 8.75.em,
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
                        .clip(shape = RoundedCornerShape(8.dp))
                        .background(color = Color(0xff2b7a78).copy(alpha = 0.05f))
                        .border(
                            border = BorderStroke(
                                1.dp,
                                Color(0xff17252a).copy(alpha = 0.12f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(
                            horizontal = 16.dp,
                            vertical = 13.dp
                        )
                ) {
                    Text(
                        text = "L-2023-002",
                        color = Color(0xff17252a),
                        lineHeight = 10.em,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier
                            .wrapContentHeight(align = Alignment.CenterVertically)
                    )
                }
            }
            Button(
                onClick = { /* TODO: Handle Click Action */ },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xffcc2222)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp) // Increased height for better UI
            ) {
                Text(
                    text = "Signaler un problème",
                    color = Color(0xfffcfffe),
                    style = TextStyle(fontSize = 18.sp), // Adjusted font size for better fit
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

        }

        }

}

