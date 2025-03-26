package com.example.mobileuser_frontend.pages

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavController
import com.example.mobileuser_frontend.R
import com.example.mobileuser_frontend.module.DropDown

/*@Preview
@Composable
private fun AppelPreview() {
    Appel()
}*/
@Composable
fun Appel(navController: NavController) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    Column(
        verticalArrangement = Arrangement.spacedBy(40.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight-130.dp)
            .background(color = Color(0xfffcfffe)).padding(top = 20.dp, bottom = 20.dp, start = 5.dp, end = 5.dp)
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
                    text = "Partager Position",
                    color = Color(0xff17252a),
                    lineHeight = 8.em,
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {  }
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
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .padding(all = 5.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xff17252a))
        ) {
            Text(
                text = "Demande D'aide",
                color = Color.White,
                lineHeight = 1.5.em,
                style = TextStyle(fontSize = 30.sp)
            )
        }
        Text(
            text = "Appel Urgent",
            color = Color.Black,
            lineHeight = 5.43.em,
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier.padding(top = 6.dp, bottom = 6.dp),
        )
        val dropdownState = remember { DropDown() }

        StableDropdown(dropdownState = dropdownState)

    }
}
@Composable
fun StableDropdown(dropdownState: DropDown) {
    val density = LocalDensity.current
    val backgroundColor = Color(0xFF2B7A78).copy(alpha = 0.05f) // 2B7A78 with 5% opacity
    val borderColor = Color(0xFF3AAFA9) // Your teal border color

    Box(modifier = Modifier.fillMaxWidth()) {
        // Custom styled TextField
        OutlinedTextField(
            value = dropdownState.value,
            onValueChange = {},
            readOnly = true,
            placeholder = {
                Text(
                    "Choisir un service",
                    color = Color(0xFF17252A).copy(alpha = 0.6f)
                )
            },
            trailingIcon = {
                Icon(
                    painter = painterResource(id = dropdownState.icon),
                    contentDescription = "Menu déroulant",
                    modifier = Modifier.clickable {
                        dropdownState.onEnabled(!dropdownState.enabled)
                    },
                    tint = Color(0xFF17252A)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    dropdownState.onSize(coordinates.size.toSize())
                }
                .clip(RoundedCornerShape(8.dp))
                .background(backgroundColor)
                .border(
                    BorderStroke(1.dp, borderColor),
                    shape = RoundedCornerShape(8.dp)
                ),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = borderColor,
                textColor = Color(0xFF17252A),
                placeholderColor = Color(0xFF17252A).copy(alpha = 0.6f)
            ),
            textStyle = TextStyle(
                fontSize = 16.sp
            )
        )

        // Dropdown Menu
        DropdownMenu(
            expanded = dropdownState.enabled,
            onDismissRequest = { dropdownState.onEnabled(false) },
            modifier = Modifier
                .width(with(density) { dropdownState.size.width.toDp() })
                .offset(y = with(density) { dropdownState.size.height.toDp() })
                .background(backgroundColor)
                .border(
                    BorderStroke(1.dp, borderColor),
                    shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                )
        ) {
            dropdownState.items.forEachIndexed { index, item ->
                DropdownMenuItem(
                    onClick = {
                        dropdownState.onSelectedIndex(index)
                        dropdownState.onEnabled(false)
                    },
                    content = {
                        Text(
                            text = item,
                            color = Color(0xFF17252A),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                )
            }
        }
    }
}