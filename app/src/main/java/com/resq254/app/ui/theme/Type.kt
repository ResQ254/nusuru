package com.resq254.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    displayLarge   = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold,     fontSize = 40.sp, letterSpacing = (-0.5).sp),
    headlineLarge  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold,     fontSize = 24.sp, letterSpacing = (-0.3).sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, letterSpacing = (-0.2).sp),
    titleLarge     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    bodyLarge      = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal,   fontSize = 14.sp, lineHeight = 20.sp),
    bodyMedium     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal,   fontSize = 13.sp, lineHeight = 18.sp),
    bodySmall      = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal,   fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium,   fontSize = 10.sp, letterSpacing = 0.8.sp),
)
