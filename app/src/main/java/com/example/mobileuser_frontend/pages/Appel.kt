@file:OptIn(ExperimentalPermissionsApi::class)

package com.example.mobileuser_frontend.pages



import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mobileuser_frontend.R
import com.example.mobileuser_frontend.functions.fetchPhoneNumber
import com.example.mobileuser_frontend.functions.makePhoneCall
import com.example.mobileuser_frontend.module.DropDown
import com.example.mobileuser_frontend.viewmodel.EmergencyViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

/*@Preview
@Composable
private fun AppelPreview() {
    Appel()
}*/
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Appel(navController: NavController) {

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    Column(

        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight-130.dp)
            .verticalScroll(rememberScrollState())
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

        val context = LocalContext.current
        val callPermissionState = rememberPermissionState(android.Manifest.permission.CALL_PHONE)

        Button(
                onClick = {
                    when {
                        callPermissionState.status.isGranted -> {
                            // Permission already granted - make the call
                            fetchPhoneNumber("66") { phone ->
                                if (phone != null) {
                                    makePhoneCall(context,phone)
                                } else {
                                    Toast.makeText(context, "Error: Try again Please", Toast.LENGTH_LONG).show()
                                }
                            }

                        }
                        callPermissionState.status.shouldShowRationale -> {
                            Toast.makeText(
                                context,
                                "Phone call permission is required to make calls",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        else -> {
                            // Request permission
                            callPermissionState.launchPermissionRequest()
                        }
                    }
                },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
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



        EmergencyDropdown()

    }
}

@Composable
fun EmergencyDropdown(viewModel: EmergencyViewModel = viewModel()) {
    val dropdownState = remember { DropDown() }
    val items by viewModel.dropdownItems

    val context = LocalContext.current
    val callPermissionState = rememberPermissionState(android.Manifest.permission.CALL_PHONE)


    dropdownState.value = "Appel d'urgence"
    // Update items when they are ready
    LaunchedEffect(items) {
        dropdownState.items = items
    }
    val density = LocalDensity.current
    val backgroundColor = Color(0xFF2B7A78).copy(alpha = 0.05f) // 2B7A78 with 5% opacity
    val borderColor = Color(0xFF3AAFA9) // Your teal border color

    Box(modifier = Modifier.fillMaxWidth()) {
        // Custom styled TextField
        OutlinedTextField(
            value = dropdownState.value,
            onValueChange = {},
            readOnly = true,
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
                .heightIn(max = 200.dp) // enables scrolling if needed
        ) {
            Column() {
                dropdownState.items.forEach { item ->
                    DropdownMenuItem(onClick = {
                        dropdownState.onEnabled(false)
                        makePhoneCall(context, item.number)
                    }) {
                        Text(text = item.label)
                    }
                }
            }
        }
    }
}