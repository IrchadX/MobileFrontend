package com.example.mobileuser_frontend.module

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.example.mobileuser_frontend.R

class DropDown {
    var enabled by mutableStateOf(false)
    var value by mutableStateOf("Emplacement actuel")
    var selectedIndex by mutableStateOf(-1)
    var items by mutableStateOf(listOf<ListItems>())
    var size by mutableStateOf(Size.Zero) //To make the list Size equal the field
    val icon: Int
        @Composable get() = if (enabled){
            R.drawable.arrowdown
        }else{
            R.drawable.arrowup
        }

    fun onEnabled(newValue:Boolean){
        enabled =newValue
    }
    fun onSize(newValue: Size){
        size =newValue
    }


}
@Composable
fun CustomDropdown(
    label: String,
    items: List<ListItems>,
    onItemSelected: (ListItems) -> Unit = {},
    modifier: Modifier = Modifier,
    initialValue: String = "",
    backgroundColor: Color = Color(0xFF2B7A78).copy(alpha = 0.05f),
    borderColor: Color = Color(0xFF3AAFA9),
    textColor: Color = Color(0xFF17252A),
    textStyle: TextStyle = TextStyle(fontSize = 16.sp)
) {
    val dropdownState = remember { DropDown() }.apply {
        this.value = initialValue.ifEmpty { label }
        this.items = items
    }

    val density = LocalDensity.current

    Box(modifier = modifier.fillMaxWidth()) {
        // Custom styled TextField
        OutlinedTextField(
            value = dropdownState.value,
            onValueChange = {},  // No need to change text manually
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    painter = painterResource(id = dropdownState.icon),
                    contentDescription = "Dropdown menu",
                    modifier = Modifier.clickable {
                        dropdownState.onEnabled(!dropdownState.enabled)
                    },
                    tint = textColor
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
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color(0xFF17252A)
            ),
            textStyle = textStyle
        )

        // Dropdown Menu
        DropdownMenu(
            expanded = dropdownState.enabled,
            onDismissRequest = { dropdownState.onEnabled(false) },
            modifier = Modifier
                .width(with(density) { dropdownState.size.width.toDp() })
                .heightIn(max = 200.dp) // enables scrolling if needed
        ) {
            dropdownState.items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item.label) },
                    onClick = {
                        dropdownState.onEnabled(false)
                        dropdownState.value = item.label  // Update the text field with the selected label
                        dropdownState.selectedIndex = items.indexOf(item)
                        onItemSelected(item)  // Notify item selected
                    }
                )
            }
        }
    }
}
