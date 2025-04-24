package com.example.mobileuser_frontend.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.mobileuser_frontend.R


@Preview
@Composable
private fun NavigationGuidePreview() {
    NavigationGuide()
}
@Composable
fun NavigationGuide() {
    Column(
        verticalArrangement = Arrangement.spacedBy(40.dp),
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(color = Color(0xfffcfffe))
            .padding(top = 106.dp, start = 20.dp, end = 20.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(13.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically

        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .requiredSize(size = 116.dp)
                    .clip(shape = RoundedCornerShape(14.dp))
                    .background(color = Color(0xff3aafa9))
                    .padding(all = 10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.directright),
                    contentDescription = "vuesax/bold/direct-right",
                    modifier = Modifier
                        .requiredSize(size = 67.dp)
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Position actuelle",
                    color = Color(0xff17252a),
                    lineHeight = 8.em,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Text(
                    text = "Cyberespace",
                    color = Color(0xff17252a),
                    lineHeight = 6.51.em,
                    style = TextStyle(
                        fontSize = 20.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(color = Color(0xff17252a))

            ) {
                Text(
                    text = "Marchez tout droit 2 mètres",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 5.83.em,
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .wrapContentHeight(align = Alignment.CenterVertically)
                )
                Text(
                    text = "Puis tournez à droite dans le couloir",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 7.78.em,
                    style = TextStyle(
                        fontSize = 18.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .wrapContentHeight(align = Alignment.CenterVertically)
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(color = Color(0xff17252a))

            ) {
                Text(
                    text = "2 mètres jusqu'à ",
                    color = Color.White,
                    lineHeight = 5.83.em,
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .wrapContentHeight(align = Alignment.CenterVertically)
                )
                Text(
                    text = "tourner à droite dans le couloir",
                    color = Color.White,
                    lineHeight = 7.78.em,
                    style = TextStyle(
                        fontSize = 18.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .wrapContentHeight(align = Alignment.CenterVertically)
                )
            }
        }

    }
}


