package com.example.mobileuser_frontend.module

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

class Dialog {
    var showDialog by mutableStateOf(false)
    var onDismiss: () -> Unit = {}
    var text by mutableStateOf("")
    var icon: Int = 0

    @Composable
    fun display() {
        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                    onDismiss() // Optional dismiss logic
                },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp) // spacing between icon and text
                    ) {
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(text = text)
                    }
                },
                buttons = {} // No buttons
            )
        }
    }
}