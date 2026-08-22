package com.example.instantdrs_android.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = InstantDRS_Primary,
    onPrimary = InstantDRS_OnPrimary,
    secondary = InstantDRS_Secondary,
    onSecondary = InstantDRS_OnSecondary,
    background = InstantDRS_Dark_Background,
    surface = InstantDRS_Dark_Surface,
    surfaceVariant = InstantDRS_Dark_SurfaceVariant,
    error = InstantDRS_Error,
    outline = InstantDRS_Secondary
)

private val LightColorScheme = lightColorScheme(
    primary = InstantDRS_Light_Primary,
    onPrimary = Color.White,
    secondary = InstantDRS_Secondary,
    onSecondary = Color.White,
    background = InstantDRS_Light_Background,
    surface = InstantDRS_Light_Surface,
    error = InstantDRS_Error,
    outline = InstantDRS_Secondary
)

@Composable
fun InstantDRSAndroidTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is disabled to maintain strict branding as requested
    dynamicColor: Boolean = false,
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

    CompositionLocalProvider(LocalSpacing provides InstantDRSSpacing()) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}
