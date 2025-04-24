package com.example.mobileuser_frontend.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.mobileuser_frontend.module.CustomDropdown
import com.example.mobileuser_frontend.module.ListItems


/*@Preview
@Composable
private fun NavigationPreview() {
    Navigation()
*/

val poiOptions = listOf(
    ListItems(label = "Option1", number = ""),
    ListItems(label = "Option2", number = "")
)
@Composable
fun Navigation(navController: NavController) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight-130.dp)
            .background(color = Color(0xfffcfffe))
            .padding(top = 10.dp, start = 20.dp, end = 20.dp),
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
                        .requiredSize(size = 70.dp)
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Top),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Position actuelle",
                    color = Color(0xff17252a),
                    lineHeight = 5.43.em,
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
        Text(
            text = "Point de départ",
            color = Color.Black,
            lineHeight = 5.43.em,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier.padding(top = 20.dp, bottom = 20.dp),
        )
        //Point de Départ



        CustomDropdown(
            label = "Choisir point de départ",
            items = poiOptions,
            initialValue = "Choisir POI",
            onItemSelected = {
            },
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = "Point d'arrivée",
            color = Color.Black,
            lineHeight = 5.43.em,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier.padding(top = 30.dp, bottom = 20.dp),
        )
        //Point d'arrivée

        CustomDropdown(
            label = "Choisir Point de départ",
            items = poiOptions,
            initialValue = "Choisir POI",
            onItemSelected = {
            },
            modifier = Modifier.padding(16.dp)
        )
    }
}

