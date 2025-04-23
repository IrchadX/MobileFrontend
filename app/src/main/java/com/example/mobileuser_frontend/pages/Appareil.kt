package com.example.mobileuser_frontend.pages

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.navigation.NavController
import com.example.mobileuser_frontend.API.DeviceData
import com.example.mobileuser_frontend.R
import com.example.mobileuser_frontend.functions.fetchDeviceInfo
import com.example.mobileuser_frontend.module.fontSizeSmallText
import com.example.mobileuser_frontend.module.fontSizeSubTitle
import com.example.mobileuser_frontend.module.fontSizeText


/*@Preview
@Composable
private fun AppareilPreview() {
    Appareil()
}*/

@Composable
fun Appareil(navController: NavController) {
    val id = "66"
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    var data by remember { mutableStateOf<DeviceData?>(null) }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        fetchDeviceInfo(id) { info ->
            data = info
            if (data == null){
                Toast.makeText(context, "Server Error" , Toast.LENGTH_LONG).show()}
        }

    }

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
                fontSize = fontSizeSubTitle(),
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
                fontSize = fontSizeSmallText(),
                color = Color(0xff17252a),
                style = MaterialTheme.typography.h5)
            Box(
                modifier = Modifier
                    .requiredWidth(width = 280.dp)
                    .requiredHeight(height = 40.dp)
                    .clip(shape = RoundedCornerShape(6.dp))
                    .background(color = Color(0xff3aafa9))
            ) {
                Text(
                    text = "75 %",
                    color = Color.White,
                    style = TextStyle(
                        fontSize = fontSizeText(),
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
                    fontSize = fontSizeSmallText()
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
                        fontSize = fontSizeSmallText(),
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
                            text = data?.state ?: "",
                            color = Color(0xff17252a),
                            lineHeight = 10.em,
                            style = TextStyle(
                                fontSize = fontSizeSmallText(),
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
                        fontSize = fontSizeSmallText(),
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
                            text = data?.type ?: "",
                            color = Color(0xff17252a),
                            lineHeight = 10.em,
                            style = TextStyle(
                                fontSize = fontSizeSmallText(),
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
                    text = "Adresse",
                    color = Color(0xff17252a),
                    lineHeight = 8.75.em,
                    style = TextStyle(
                        fontSize = fontSizeSmallText(),
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .requiredWidth(width = 281.dp)
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
                        text = data?.mac_address ?: "",
                        color = Color(0xff17252a),
                        lineHeight = 10.em,
                        style = TextStyle(
                            fontSize = fontSizeSmallText(),
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
                    style = TextStyle(fontSize = fontSizeText()), // Adjusted font size for better fit
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

        }

        }

}

