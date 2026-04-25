package com.lugat.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkErrorContainer = Color(0xFF4A0000)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = SurfaceLight,
    primaryContainer = CardBlueLight,
    onPrimaryContainer = PrimaryBlueDark,
    secondary = SecondaryBlue,
    onSecondary = SurfaceLight,
    secondaryContainer = LightBlue,
    onSecondaryContainer = PrimaryBlueDark,
    background = BackgroundLight,
    onBackground = TextDark,
    surface = SurfaceLight,
    onSurface = TextDark,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    errorContainer = ErrorContainer,
    onError = SurfaceLight,
    onErrorContainer = ErrorRed,
    outline = TextSecondary
)

private val DarkColorScheme = darkColorScheme(
    primary = SecondaryBlue,
    onPrimary = BackgroundDark,
    primaryContainer = CardBlueDark,
    onPrimaryContainer = SecondaryBlue,
    secondary = GradientMid,
    onSecondary = BackgroundDark,
    secondaryContainer = SurfaceVariantDark,
    onSecondaryContainer = SecondaryBlue,
    background = BackgroundDark,
    onBackground = TextLight,
    surface = SurfaceDark,
    onSurface = TextLight,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryLight,
    error = ErrorRed,
    errorContainer = DarkErrorContainer,
    onError = TextLight,
    onErrorContainer = ErrorRed,
    outline = TextSecondaryLight
)

@Composable
fun LugatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
