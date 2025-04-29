package com.example.mobileuser_frontend.module

import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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
                        tint = LocalContentColor.current,
                        modifier = Modifier.size(48.dp)
                    )
                },
                text = {
                    Text(text = text)
                },
                confirmButton = {

                }
            )
        }
    }
}
