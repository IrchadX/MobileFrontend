package com.example.mobileuser_frontend.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mobileuser_frontend.R
import com.example.mobileuser_frontend.functions.changeUserData
import com.example.mobileuser_frontend.functions.changeUserPassword
import com.example.mobileuser_frontend.functions.checkUserPassword
import com.example.mobileuser_frontend.functions.fetchUserInfo
import com.example.mobileuser_frontend.module.Dialog
import com.example.mobileuser_frontend.module.fontSizeSmallText
import com.example.mobileuser_frontend.module.fontSizeSubTitle
import com.example.mobileuser_frontend.module.fontSizeText
import com.example.mobileuser_frontend.module.fontSizeTitle
import com.example.mobileuser_frontend.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.firstOrNull


/*@Preview
@Composable
private fun InformationPreview() {
    Information()
}*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Information(viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    var changed = remember { mutableStateOf(false) }
    val context = LocalContext.current
    var text = remember { mutableStateOf<String?>(null) }
    val pwd = remember { mutableStateOf<String>("") }
    var newpwd = remember { mutableStateOf<String>("") }
    val dialog = remember { Dialog() } //For pop ups
    var success = remember { mutableStateOf(true) }

    var id by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        id = viewModel.authRepository.getUserId().firstOrNull() ?: ""
        // now you can use id
    }

    LaunchedEffect(Unit) {
        fetchUserInfo(id) { name ->
            text.value = name
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(color = Color(0xfffcfffe))
    ) {
        Text(
            text = "Informations Personnelles",
            color = Color(0xff17252a),
            style = TextStyle(
                fontSize = fontSizeTitle(),
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.Start)
        )

    Spacer(modifier = Modifier.height(20.dp))
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
                    fontSize = fontSizeText(),
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
                        value = text?.value ?: "",
                        onValueChange = { newText ->
                            text.value = newText
                            changed.value = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .heightIn(min = 24.dp) //Minimum touch target
                            .wrapContentHeight(align = Alignment.CenterVertically),
                        textStyle = TextStyle(
                            fontSize = fontSizeSmallText(),
                            color = Color(0xff17252a)
                        ),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,  // replaces containerColor
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color(0xFF17252A)
                        )
                    )
                            }
            Spacer(modifier = Modifier.height(20.dp))
            Divider(
                color = Color.Gray.copy(alpha = 0.3f),
                thickness = 2.dp,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Changer le mot de passe",
                color = Color(0xff17252a),
                style = TextStyle(
                    fontSize = fontSizeText(),
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .wrapContentHeight(align = Alignment.CenterVertically)
            )
            Text(
                text = "Mot de passe",
                color = Color(0xff17252a),
                style = TextStyle(
                    fontSize = fontSizeSmallText(),
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
                    value = pwd.value,
                    onValueChange = { newText -> pwd.value = newText }, // (String) -> Unit
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(align = Alignment.CenterVertically),
                    textStyle = TextStyle(
                        fontSize = fontSizeSmallText(),
                        color = Color(0xff17252a)
                    ),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,  // replaces containerColor
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xFF17252A)
                    )
                )
            }


            Text(
                text = "Nouveau Mot de passe",
                color = Color(0xff17252a),
                style = TextStyle(
                    fontSize = fontSizeSmallText(),
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
                    value = newpwd.value, // String
                    onValueChange = { newText -> newpwd.value = newText }, // (String) -> Unit
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(align = Alignment.CenterVertically),
                    textStyle = TextStyle(
                        fontSize = fontSizeSmallText(),
                        color = Color(0xff17252a)
                    ),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,  // replaces containerColor
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xFF17252A)
                    )
                )
            }
            Divider(
                color = Color.Gray.copy(alpha = 0.3f),
                thickness = 2.dp,
                modifier = Modifier.fillMaxWidth()
            )
            var messages = remember { mutableStateOf("") }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    // Track completion states
                    var nameChangeCompleted = false
                    var passwordChangeCompleted = false

                    // Track success states for each operation
                    var nameChangeSuccess = true
                    var passwordChangeSuccess = true

                    // Track the final message
                    var finalMessage = "Opération réussie"
                    var overallSuccess = true

                    // Function to check conditions and show the popup
                    fun checkAndShowPopup() {
                        if (nameChangeCompleted && passwordChangeCompleted) {
                            success.value = overallSuccess
                            messages.value = finalMessage
                            dialog.text = finalMessage
                            dialog.icon = if (overallSuccess) R.drawable.iconconfirm else R.drawable.iconerror
                            dialog.showDialog = true
                        }
                    }

                    // Handle name change if needed
                    if (changed.value && text.value != null) {
                        changeUserData(id, text.value!!) { mess ->
                            nameChangeCompleted = true
                            if (mess != "Opération Réussie") {
                                nameChangeSuccess = false
                                finalMessage = mess.toString()
                                overallSuccess = false
                            }
                            checkAndShowPopup()
                        }
                    } else {
                        nameChangeCompleted = true // No name change needed
                    }

                    // Handle password change if needed
                    if (pwd.value.isNotBlank() && newpwd.value.isNotBlank()) {
                        checkUserPassword(id, pwd.value) { mes ->
                            if (mes == "true") {
                                // Current password is valid, proceed to change it
                                changeUserPassword(id, newpwd.value) { result ->
                                    passwordChangeCompleted = true
                                    if (result != "Opération Réussie") {
                                        passwordChangeSuccess = false
                                        finalMessage = result ?: "Une erreur inconnue est survenue"
                                        overallSuccess = false
                                    }
                                    checkAndShowPopup()
                                }
                            } else {
                                // Invalid current password
                                passwordChangeCompleted = true
                                passwordChangeSuccess = false
                                finalMessage = mes ?: "Mot de passe actuel incorrect"
                                overallSuccess = false
                                checkAndShowPopup()
                            }
                        }
                    } else {
                        passwordChangeCompleted = true // No password change needed
                    }

                    // Final check to ensure popup is shown if no changes are needed
                    checkAndShowPopup()
                },

                        modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                            .semantics {
                                contentDescription = "Sauvegarder"
                            },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3AAFA9)),
                elevation = ButtonDefaults.elevatedButtonElevation(5.dp)
            ) {
                Text(
                    text = "Sauvegarder",
                    color = Color(0xfffcfffe),
                    style = TextStyle(
                        fontSize = fontSizeSubTitle(),
                        fontWeight = FontWeight.SemiBold,
                    )
                )

            }
            dialog.display()
        }


    }
}

