package com.example.mobileuser_frontend.module


import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mobileuser_frontend.R

class Dialog {
    var showDialog by mutableStateOf(false)
    var onDismiss: () -> Unit = {}
    var text by mutableStateOf("")
    var icon: Int = 0

    @Composable
    fun display(){
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { onDismiss(); showDialog = false },
                    text = { Text(text = text) },
                    title = {Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null
                    )},
                    confirmButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
}
