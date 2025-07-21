package com.lusion.lumanoiz.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DarkOnPrimary,
    secondary = DarkOnSecondary,
    tertiary = Pink80,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface
)

@Composable
fun LumaNoizAppTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme // Enforce dark theme as per instructions

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}