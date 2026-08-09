package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RapidoLightColorScheme = lightColorScheme(
    primary = RapidoYellow,
    onPrimary = RapidoDark,
    primaryContainer = RapidoYellowContainer,
    onPrimaryContainer = RapidoDark,
    secondary = RapidoDark,
    onSecondary = Color.White,
    background = RapidoBg,
    onBackground = RapidoTextPrimary,
    surface = RapidoCardBg,
    onSurface = RapidoTextPrimary,
    surfaceVariant = Color(0xFFF0F0F0),
    onSurfaceVariant = RapidoTextSecondary,
    error = RapidoRed,
    errorContainer = RapidoRedContainer
)

private val RapidoDarkColorScheme = darkColorScheme(
    primary = RapidoYellow,
    onPrimary = RapidoDark,
    primaryContainer = RapidoYellowDark,
    onPrimaryContainer = Color.Black,
    secondary = Color.White,
    onSecondary = RapidoDark,
    background = RapidoDarkHeader,
    onBackground = Color.White,
    surface = RapidoDark,
    onSurface = Color.White,
    surfaceVariant = RapidoSurfaceVariant,
    onSurfaceVariant = Color(0xFFB0B0B0)
)

@Composable
fun RapidoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) RapidoDarkColorScheme else RapidoLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
