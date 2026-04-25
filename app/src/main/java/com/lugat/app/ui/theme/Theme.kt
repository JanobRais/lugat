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

private val LightColorScheme = lightColorScheme(
    primary               = PrimaryTeal,
    onPrimary             = OnPrimaryTeal,
    primaryContainer      = PrimaryTealLight,
    onPrimaryContainer    = OnPrimaryContainer,
    secondary             = SecondaryTeal,
    onSecondary           = OnPrimaryTeal,
    secondaryContainer    = SecondaryContainer,
    onSecondaryContainer  = OnSecondaryContainer,
    background            = BackgroundLight,
    onBackground          = TextDark,
    surface               = SurfaceLight,
    onSurface             = TextDark,
    surfaceVariant        = Surface1Light,
    onSurfaceVariant      = TextSecondary,
    outline               = OutlineLight,
    outlineVariant        = OutlineVariant,
    error                 = ErrorRed,
    errorContainer        = ErrorContainer,
    onError               = SurfaceLight,
    onErrorContainer      = ErrorRed,
)

private val DarkColorScheme = darkColorScheme(
    primary               = PrimaryTealLight,
    onPrimary             = OnPrimaryContainer,
    primaryContainer      = PrimaryTealDark,
    onPrimaryContainer    = PrimaryTealLight,
    secondary             = SecondaryContainer,
    onSecondary           = OnSecondaryContainer,
    secondaryContainer    = Color(0xFF243533),
    onSecondaryContainer  = SecondaryContainer,
    background            = BackgroundDark,
    onBackground          = TextLight,
    surface               = SurfaceDark,
    onSurface             = TextLight,
    surfaceVariant        = Surface1Dark,
    onSurfaceVariant      = TextSecondaryLight,
    outline               = Color(0xFF899390),
    outlineVariant        = Color(0xFF3F4946),
    error                 = Color(0xFFFFB4AB),
    errorContainer        = Color(0xFF93000A),
    onError               = Color(0xFF690005),
    onErrorContainer      = Color(0xFFFFDAD6),
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
    MaterialTheme(colorScheme = colorScheme, content = content)
}
