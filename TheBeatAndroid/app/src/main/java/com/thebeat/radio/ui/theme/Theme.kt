package com.thebeat.radio.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val NeonBlue = Color(0xFF00FFFF)
val NeonPurple = Color(0xFFBC13FE)
val Black = Color(0xFF000000)
val DarkGray = Color(0xFF121212)
val White = Color(0xFFFFFFFF)

private val DarkColorScheme = darkColorScheme(
    primary = NeonBlue,
    secondary = NeonPurple,
    tertiary = NeonPurple,
    background = Black,
    surface = DarkGray,
    onPrimary = Black,
    onSecondary = White,
    onTertiary = White,
    onBackground = White,
    onSurface = White,
)

// We force dark theme for this app style
@Composable
fun TheBeatTheme(
    darkTheme: Boolean = true, // Force Dark
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
