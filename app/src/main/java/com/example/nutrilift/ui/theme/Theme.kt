package com.example.nutrilift.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF256D85),
    onPrimary = Color.White,
    secondary = Color(0xFF617A55),
    onSecondary = Color.White,
    tertiary = Color(0xFFB2604D),
    background = Color(0xFFFAFBF8),
    onBackground = Color(0xFF1B1F1E),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1B1F1E),
    surfaceVariant = Color(0xFFEAF0EA),
    onSurfaceVariant = Color(0xFF3F4944),
    error = Color(0xFFB3261E)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF88D1EA),
    onPrimary = Color(0xFF003544),
    secondary = Color(0xFFBCD6B0),
    onSecondary = Color(0xFF273620),
    tertiary = Color(0xFFFFB4A2),
    onTertiary = Color(0xFF5F1608),
    background = Color(0xFF111412),
    onBackground = Color(0xFFE1E4DF),
    surface = Color(0xFF191C1A),
    onSurface = Color(0xFFE1E4DF),
    surfaceVariant = Color(0xFF3F4944),
    onSurfaceVariant = Color(0xFFC2CAC1)
)

@Composable
fun NutriLiftTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = MaterialTheme.typography,
        content = content
    )
}
