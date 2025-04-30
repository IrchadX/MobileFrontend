package com.example.mobileuser_frontend.pages

import android.Manifest
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mobileuser_frontend.R
import com.example.mobileuser_frontend.Screens
import com.example.mobileuser_frontend.module.fontSizeTitle
import com.example.mobileuser_frontend.state.EmergencyUiState
import com.example.mobileuser_frontend.viewmodel.AuthViewModel
import com.example.mobileuser_frontend.viewmodel.CallViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel(), callViewModel: CallViewModel= viewModel()) {
    val configuration = LocalConfiguration.current
    val coroutineScope = rememberCoroutineScope()
    var id by remember { mutableStateOf("") }
    val context = LocalContext.current
    val stateFlow: StateFlow<EmergencyUiState> = callViewModel.uiState
    val uistate = stateFlow.collectAsState().value
    var call by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        id = viewModel.authRepository.getUserId().firstOrNull() ?: ""

    }
    when (uistate) {
        is EmergencyUiState.Loading -> {
            // Show loading indicator (no toast needed)
            CircularProgressIndicator()
        }

        is EmergencyUiState.PhoneNumberLoaded -> {
            // When phone number is loaded, make the call
            val phoneNumber = uistate.phoneNumber
            LaunchedEffect(key1 = phoneNumber) {
                if (call) {
                    callViewModel.makePhoneCall(context, phoneNumber)
                    Toast.makeText(
                        context,
                        "Calling $phoneNumber...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        is EmergencyUiState.Error -> {
            LaunchedEffect(key1 = uistate) {
                Toast.makeText(
                    context,
                    "Error: ${uistate.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        else -> {
            // Idle state
        }
    }

        Column(
            modifier = Modifier.fillMaxHeight()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        if (dragAmount < -100) {
                            navController.navigate(Screens.Profil.route)
                        } else if (dragAmount > 100) {
                            navController.navigate(Screens.Parametre.route)
                        }
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.vector),
                contentDescription = "Vector",
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(0.12f)
            )
            val context = LocalContext.current
            val callPermissionState = rememberPermissionState(Manifest.permission.CALL_PHONE)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
            ) {
                Button(
                    onClick = {  /*when {
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
                    }*/
                        call =true
                        callViewModel.fetchPhoneNumber(id)


                    },
                    modifier = Modifier.semantics {
                        this.contentDescription = "S.O.S"
                    }
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .padding(all = 10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xffcc2222))
                ) {
                    Text(
                        text = "S.O.S",
                        color = Color.White,
                        lineHeight = 1.5.em,
                        style = TextStyle(fontSize = fontSizeTitle())
                    )
                }

                Button(
                    onClick = { navController.navigate(Screens.Appel.route) },
                    modifier = Modifier.semantics {
                        this.contentDescription = "Assistance"
                    }
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .padding(all = 10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xff17252a))
                ) {
                    Text(
                        text = "Assistance",
                        color = Color.White,
                        lineHeight = 1.5.em,
                        style = TextStyle(fontSize = fontSizeTitle())
                    )
                }

                Button(
                    onClick = { navController.navigate(Screens.NavigationScreen.route) },
                    modifier = Modifier.semantics {
                        this.contentDescription = "Naviguer"
                    }
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .padding(all = 10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xff3aafa9))
                ) {
                    Text(
                        text = "Naviguer",
                        color = Color.White,
                        lineHeight = 1.5.em,
                        style = TextStyle(fontSize = fontSizeTitle())
                    )
                }
            }
        }
    }


@Preview(widthDp = 375, heightDp = 812)
@Composable
private fun HomeScreenPreview() {
    //HomeScreen()
}