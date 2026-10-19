package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LonakDarkColorScheme = darkColorScheme(
    primary = PersianTurquoise,
    onPrimary = DarkBackground,
    primaryContainer = PersianLapisPrimary,
    onPrimaryContainer = PersianTurquoiseGlow,
    secondary = PersianGold,
    onSecondary = DarkBackground,
    secondaryContainer = PersianGoldDark,
    onSecondaryContainer = PersianGoldLight,
    tertiary = PersianTurquoiseGlow,
    onTertiary = DarkBackground,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFBAC7DB),
    outline = DarkOutline,
    error = LonakError,
    onError = Color.White
)

private val LonakLightColorScheme = lightColorScheme(
    primary = PersianLapisPrimary,
    onPrimary = Color.White,
    primaryContainer = PersianTurquoise,
    onPrimaryContainer = PersianLapisDark,
    secondary = PersianGoldDark,
    onSecondary = Color.White,
    secondaryContainer = PersianGoldLight,
    onSecondaryContainer = DarkBackground,
    tertiary = PersianTurquoiseDim,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF435165),
    outline = LightOutline,
    error = LonakError,
    onError = Color.White
)

@Composable
fun LonakTheme(
    darkTheme: Boolean = true, // VPN apps look gorgeous and battery-efficient in luxury dark mode
    dynamicColor: Boolean = false, // Keep authentic Persian Simurgh branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> LonakDarkColorScheme
        else -> LonakLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backward compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = LonakTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
