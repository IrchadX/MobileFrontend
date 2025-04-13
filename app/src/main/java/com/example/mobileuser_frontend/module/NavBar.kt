package com.example.mobileuser_frontend.module

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun NavBarItem(
    iconId: Int,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val background = if (isSelected) Color.Gray.copy(alpha = 0.2f) else Color.Transparent

    Box(
        modifier = Modifier
            .size(70.dp)
            .background(background, shape = RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = iconId),
            contentDescription = description,
            modifier = Modifier.size(40.dp)
        )
    }
}