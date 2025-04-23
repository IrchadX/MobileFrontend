package com.example.mobileuser_frontend.module

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun fontSizeTitle(): TextUnit {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    return (screenWidth * 0.12).sp
}

@Composable
fun fontSizeSubTitle(): TextUnit {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    return (screenWidth * 0.08).sp
}
@Composable
fun fontSizeText(): TextUnit {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    return (screenWidth * 0.06).sp
}
@Composable
fun fontSizeSmallText(): TextUnit {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    return (screenWidth * 0.04).sp
}