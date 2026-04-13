package com.nestorian87.orionix_track.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SkyTealLight,
    onPrimary = DeepNavy,
    primaryContainer = PurpleGlow,
    secondary = SignalAmber,
    onSecondary = DeepNavy,
    tertiary = RouteGreen,
    onTertiary = White,
    background = Night,
    onBackground = White,
    surface = NightSurface,
    onSurface = White,
    surfaceVariant = NightSurfaceSoft,
    onSurfaceVariant = Mist,
    surfaceTint = SkyTealLight,
    outlineVariant = NightLine,
    outline = Color(0xFF41556F),
    error = AlertCoral,
    onError = White,
    inverseSurface = Paper,
    inverseOnSurface = Ink
)

private val LightColorScheme = lightColorScheme(
    primary = DeepNavy,
    onPrimary = White,
    primaryContainer = Color(0xFF243753),
    secondary = SignalAmber,
    onSecondary = DeepNavy,
    tertiary = RouteGreen,
    onTertiary = White,
    background = Paper,
    onBackground = Ink,
    surface = White,
    onSurface = Ink,
    surfaceVariant = Cloud,
    onSurfaceVariant = Steel,
    surfaceTint = DeepNavy,
    outlineVariant = Mist,
    outline = Mist,
    error = AlertCoral,
    onError = White,
    inverseSurface = NightSurface,
    inverseOnSurface = White
)

@Composable
fun OrionixTrackTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
