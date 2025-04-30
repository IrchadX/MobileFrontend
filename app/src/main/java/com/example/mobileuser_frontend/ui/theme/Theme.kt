package com.example.mobileuser_frontend.ui.theme

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration

private val DarkColorPalette = darkColorScheme(
    primary = Color(0xFF2B7A78),
    secondary = Color(0xFFD1F1E6),
    tertiary = Color(0xFF17252A)
)

private val LightColorPalette = lightColorScheme(
    primary = Color(0xFF2B7A78),
    secondary = Color(0xFFD1F1E6),
    tertiary = Color(0xFF17252A)

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun MobileUser_FrontendTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    activity: Activity = LocalActivity.current!!,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }
    val window = calculateWindowSizeClass(activity = activity)
    val config = LocalConfiguration.current

    var appDimensions = MediumDimens

    when(window.widthSizeClass) {
       WindowWidthSizeClass.Compact->{
           if(config.screenWidthDp <= 360){
               appDimensions= CompactSmallDimens
           }
           else if(config.screenWidthDp <599){
               appDimensions= MediumDimens
           }else{
               appDimensions= LargeDimens
           }
       }
        WindowWidthSizeClass.Medium -> {
            appDimensions = MediumDimens
        }
        WindowWidthSizeClass.Expanded-> {
            appDimensions = ExtraLargeDimens
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}