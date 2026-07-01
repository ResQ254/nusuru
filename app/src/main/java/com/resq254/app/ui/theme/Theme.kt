package com.resq254.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Resq254Colors = darkColorScheme(
    primary          = AccentGreen,
    onPrimary        = Color.Black,
    secondary        = SosRed,
    onSecondary      = Color.White,
    background       = AppBg,
    onBackground     = TextPrimary,
    surface          = AppSurface,
    onSurface        = TextPrimary,
    surfaceVariant   = AppCard,
    onSurfaceVariant = TextSub,
    outline          = AppBorder,
    error            = SosRed,
)

@Composable
fun Resq254Theme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Resq254Colors, typography = Typography, content = content)
}
