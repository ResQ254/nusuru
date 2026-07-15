package com.resq254.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// 1. Define the Light Palette mapping to Material3 tokens
private val LightColors = lightColorScheme(
    primary = AccentRed,
    background = SurfaceWhite,
    surface = AppCardLight,
    onPrimary = SurfaceWhite,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

// 2. Define the Dark Palette mapping to Material3 tokens
private val DarkColors = darkColorScheme(
    primary = AccentRed,
    background = DarkBg,
    surface = DarkSurface,
    onPrimary = SurfaceWhite,
    onBackground = TextLight,
    onSurface = TextLight
)

val AppTypography = Typography(
    headlineSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 20.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodySmall = TextStyle(fontWeight = FontWeight.Normal, fontSize = 12.sp)
)

@Composable
fun NusuruTheme(
    // By default, it listens to the phone's system settings
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Select the correct color palette based on the boolean status
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}