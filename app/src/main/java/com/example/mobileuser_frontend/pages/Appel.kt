@file:OptIn(ExperimentalPermissionsApi::class)

package com.example.mobileuser_frontend.pages



import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mobileuser_frontend.R
import com.example.mobileuser_frontend.functions.makePhoneCall
import com.example.mobileuser_frontend.module.DropDown
import com.example.mobileuser_frontend.module.ListItems
import com.example.mobileuser_frontend.state.EmergencyUiState
import com.example.mobileuser_frontend.viewmodel.AuthViewModel
import com.example.mobileuser_frontend.viewmodel.CallViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Appel(navController: NavController, viewModel: CallViewModel = viewModel(), authviewModel: AuthViewModel= viewModel()) {

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val scrollvertical = rememberScrollState()
    val context = LocalContext.current
    val stateFlow: StateFlow<EmergencyUiState> = viewModel.uiState
    val uistate = stateFlow.collectAsState().value
    val emergencyStateFlow: StateFlow<EmergencyUiState> = viewModel.uiEmergencyState
    val uiEmergencyState =emergencyStateFlow.collectAsState().value

    var id by remember { mutableStateOf("") }
    // Permission state for CALL_PHONE
    val callPermissionState = rememberPermissionState(Manifest.permission.CALL_PHONE)

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "Permission granted. You can now make calls.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permission denied. Cannot make calls.", Toast.LENGTH_SHORT).show()
        }
    }


    LaunchedEffect(Unit) {
        id = authviewModel.authRepository.getUserId().firstOrNull() ?: ""

    }


    LaunchedEffect(key1 = true) {
        viewModel.fetchEmergencyList()
    }
    when (uistate) {
        is EmergencyUiState.Loading -> {
            // Show loading indicator (no toast needed)
            CircularProgressIndicator()
        }


        is EmergencyUiState.PhoneNumberLoaded -> {
            val phoneNumber = uistate.phoneNumber
            LaunchedEffect(key1 = phoneNumber) {
                if (callPermissionState.status.isGranted) {
                    viewModel.makePhoneCall(context, phoneNumber,requestPermissionLauncher)
                    Toast.makeText(
                        context,
                        "Calling $phoneNumber...",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    callPermissionState.launchPermissionRequest()
                    Toast.makeText(
                        context,
                        "Permission required to make a call.",
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

        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(scrollvertical)
            .background(color = Color(0xfffcfffe))
            .padding(top = 10.dp, bottom = 20.dp, start = 5.dp, end = 5.dp)
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
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                )
                Text(
                    text = "Cyberespace",
                    color = Color(0xff17252a),
                    style = TextStyle(
                        fontSize = 20.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
        Spacer(modifier = Modifier.padding(top = 40.dp))
        val context = LocalContext.current

        Button(
            onClick = {viewModel.fetchPhoneNumber(id)
            },


            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
                .height(120.dp)
                .clip(RoundedCornerShape(8.dp))
                .padding(all = 5.dp)
                .semantics {
                    contentDescription = "Demande d'aide"
                },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xff17252a))
        ) {
            Text(
                text = "Demande D'aide",
                color = Color.White,
                lineHeight = 1.5.em,
                style = TextStyle(fontSize = 30.sp)
            )
        }
        Spacer(modifier = Modifier.padding(top = 40.dp))
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
        Spacer(modifier = Modifier.padding(top = 20.dp))

        //Case of the Emergency List
        when (val state = uiEmergencyState) {
            is EmergencyUiState.Loading -> {
                // Show loading indicator (no toast needed)
                CircularProgressIndicator()
            }

            is EmergencyUiState.EmergencyListLoaded -> {
                EmergencyDropdown(state.list)
            }

            is EmergencyUiState.Error -> {
                LaunchedEffect(key1 = uiEmergencyState) {
                    Toast.makeText(
                        context,
                        "Error: ${state.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            else -> {
                // Idle state
            }
        }


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyDropdown( items: List<ListItems>){
    val dropdownState = remember { DropDown() }


    val context = LocalContext.current
    val callPermissionState = rememberPermissionState(Manifest.permission.CALL_PHONE)


    dropdownState.value = "Appel d'urgence"
    // Update items when they are ready
    LaunchedEffect(items) {
        dropdownState.items = items
    }
    val density = LocalDensity.current
    val backgroundColor = Color(0xffd1f1e6).copy(alpha = 0.05f) // 2B7A78 with 5% opacity
    val borderColor = Color(0xffd1f1e6)

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
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xffd1f1e6),  // replaces containerColor
                unfocusedContainerColor = Color(0xffd1f1e6),
                focusedIndicatorColor = Color(0xffd1f1e6),
                unfocusedIndicatorColor = Color(0xffd1f1e6),
                cursorColor = Color(0xffd1f1e6)
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
                .verticalScroll(rememberScrollState())
                .heightIn(max = 200.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxHeight().background(Color(0xffd1f1e6))
            ) {
                dropdownState.items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item.label) },
                        onClick = {
                            dropdownState.onEnabled(false)
                            dropdownState.value = item.label
                            dropdownState.selectedIndex = dropdownState.items.indexOf(item)

                            // Check permission before making the call
                            if (callPermissionState.status.isGranted) {
                                makePhoneCall(
                                    context,
                                    item.number
                                ) // Make the call directly if permission is granted
                            } else {
                                // Request permission if not granted
                                callPermissionState.launchPermissionRequest()
                                Toast.makeText(
                                    context,
                                    "Accorder la permission pour initier l'appel.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                }
            }
        }

    }
}
