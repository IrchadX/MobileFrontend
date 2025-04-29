package com.example.mobileuser_frontend.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimensions(
    val extraSmall:Dp = 0.dp,
    val small1:Dp = 0.dp,
    val small2:Dp = 0.dp,
    val small3: Dp = 0.dp,
    val medium1:Dp = 0.dp,
    val medium2:Dp = 0.dp,
    val medium3:Dp = 0.dp,
    val large:Dp = 0.dp,
    val buttonHeight:Dp = 0.dp,
    val logo:Dp = 0.dp,
    )
val CompactSmallDimens = Dimensions(
    extraSmall = 2.dp,
    small1 = 4.dp,
    small2 = 6.dp,
    small3 = 8.dp,
    medium1 = 10.dp,
    medium2 = 12.dp,
    medium3 = 16.dp,
    large = 24.dp,
    buttonHeight = 40.dp,
    logo = 64.dp
)
val SmallDimens = Dimensions(
    extraSmall = 4.dp,
    small1 = 6.dp,
    small2 = 8.dp,
    small3 = 10.dp,
    medium1 = 16.dp,
    medium2 = 20.dp,
    medium3 = 24.dp,
    large = 32.dp,
    buttonHeight = 44.dp,
    logo = 72.dp
)

val MediumDimens = Dimensions(
    extraSmall = 4.dp,
    small1 = 8.dp,
    small2 = 12.dp,
    small3 = 16.dp,
    medium1 = 20.dp,
    medium2 = 24.dp,
    medium3 = 32.dp,
    large = 48.dp,
    buttonHeight = 48.dp,
    logo = 80.dp
)
val LargeDimens = Dimensions(
    extraSmall = 6.dp,
    small1 = 12.dp,
    small2 = 16.dp,
    small3 = 20.dp,
    medium1 = 24.dp,
    medium2 = 32.dp,
    medium3 = 40.dp,
    large = 64.dp,
    buttonHeight = 56.dp,
    logo = 96.dp
)
val ExtraLargeDimens = Dimensions(
    extraSmall = 8.dp,
    small1 = 16.dp,
    small2 = 20.dp,
    small3 = 24.dp,
    medium1 = 32.dp,
    medium2 = 40.dp,
    medium3 = 48.dp,
    large = 80.dp,
    buttonHeight = 64.dp,
    logo = 128.dp
)
