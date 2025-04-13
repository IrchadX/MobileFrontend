package com.example.mobileuser_frontend.module

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
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
