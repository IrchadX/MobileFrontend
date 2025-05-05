package com.example.mobileuser_frontend.pages

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobileuser_frontend.R
import com.example.mobileuser_frontend.Screens
import com.example.mobileuser_frontend.module.fontSizeSmallText
import com.example.mobileuser_frontend.module.fontSizeSubTitle
import com.example.mobileuser_frontend.state.AuthUIState
import com.example.mobileuser_frontend.ui.theme.Maingreen
import com.example.mobileuser_frontend.viewmodel.AuthViewModel


@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    navController: NavController,
    viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state by viewModel.state.collectAsState()
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(state) {
        if (state is AuthUIState.Success) {
            navController.navigate(Screens.MainScreen.route) {
                popUpTo("SignIn") { inclusive = true }
            }
        }
    }

    LaunchedEffect(authState) {
        if (authState is AuthUIState.Authenticated) {
            navController.navigate(Screens.MainScreen.route) {
                popUpTo("SignIn") { inclusive = true }
            }
        }
    }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Define the teal color used in the design
    val tealColor = Color(0xFF2D7A78)
    val lightTealColor = Color(0xFF8ECBC9)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top=5.dp),
            horizontalAlignment = Alignment.CenterHorizontally ,
            verticalArrangement = Arrangement.Center
        ) {
            // Header with IRCHAD logo
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .fillMaxWidth()
                    .background(Maingreen)
                    .padding(horizontal = 20.dp, vertical = 4.dp),
            ) {
                // Background circles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left side with logo and text
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.irchadlogo),
                            contentDescription = "Logo",
                            modifier = Modifier.size(100.dp),
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                    }
                }
            }

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Hello Again !",
                    fontSize = fontSizeSubTitle(),
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Welcome back, you've been missed",
                    fontSize = fontSizeSmallText(),
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Email field
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Email",
                        fontSize = fontSizeSmallText(),
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("example@email.com") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email",
                                tint = Color.Gray
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,      // Background when focused
                            unfocusedContainerColor = Color.White,   // Background when unfocused
                            focusedIndicatorColor = Color.LightGray,  // Bottom indicator when focused
                            unfocusedIndicatorColor = Color.LightGray // Bottom indicator when unfocused
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Password field
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Password",
                        fontSize = fontSizeSmallText(),
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("At least 8 characters") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Password",
                                tint = Color.Gray
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = Color.Gray
                                )
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,      // Background when focused
                            unfocusedContainerColor = Color.White,   // Background when unfocused
                            focusedIndicatorColor = Color.LightGray,  // Bottom indicator when focused
                            unfocusedIndicatorColor = Color.LightGray // Bottom indicator when unfocused
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                }



                Spacer(modifier = Modifier.height(15.dp))

                // Sign in button
                Button(
                    onClick = { viewModel.signIn(email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = tealColor
                    )
                ) {
                    Text(
                        "Sign in",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Not registered
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Not registered ? ",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Sign Up now !",
                        color = Color.DarkGray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            navController.navigate(Screens.SignUpScreen.route)
                        }
                    )
                }
            }

            // Footer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    text = "Irchad © 2025. All rights reserved. Proudly crafted by XCEED.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }

        // Show loading or error states
        when (authState) {
            is AuthUIState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x80FFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = tealColor)
                }
            }
            is AuthUIState.Error -> {
                val message = (authState as AuthUIState.Error).message
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Text(
                        text = message,
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color.Red.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(16.dp)
                    )
                }
            }
            else -> {}
        }
    }
}