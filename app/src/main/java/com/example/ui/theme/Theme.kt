package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BoxingDarkColorScheme = darkColorScheme(
    primary = BoxingRed,
    onPrimary = Color.White,
    primaryContainer = BoxingRedDark,
    onPrimaryContainer = Color.White,
    secondary = BoxingOrange,
    onSecondary = Color.White,
    secondaryContainer = SurfaceVariantDark,
    onSecondaryContainer = BoxingOrange,
    tertiary = BoxingAmber,
    onTertiary = Color.Black,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceBorderDark
)

@Composable
fun BoxTimerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BoxingDarkColorScheme,
        typography = Typography,
        content = content
    )
}
