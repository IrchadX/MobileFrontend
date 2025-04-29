package com.example.mobileuser_frontend.module

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

class Dialog {
    var showDialog by mutableStateOf(false)
    var onDismiss: () -> Unit = {}
    var text by mutableStateOf("")
    var icon: Int = 0

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun display() {
        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                    onDismiss()
                },
                icon = {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = text,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(48.dp)
                    )
                },
                text = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = text,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {

                }
            )
        }
    }
}
