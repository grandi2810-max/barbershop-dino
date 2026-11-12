package com.example.barbershopdino.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.barbershopdino.model.AppThemeMode

// BarberShop Dino Modern Color Palette
val GoldPrimary = Color(0xFFD4AF37)
val GoldSecondary = Color(0xFFE6C200)
val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF1E1E24)
val DarkSurfaceVariant = Color(0xFF2A2A32)
val DarkOnBackground = Color(0xFFF0F0F0)

val LightBackground = Color(0xFFF8F9FA)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFECEFF1)
val LightOnBackground = Color(0xFF1C1B1F)

val AccentGold = Color(0xFFFFB300)
val EmeraldGreen = Color(0xFF2ECC71)
val CoralRed = Color(0xFFE74C3C)

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF3B3113),
    onPrimaryContainer = GoldSecondary,
    secondary = AccentGold,
    onSecondary = Color.Black,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnBackground,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFCCCCCC),
    error = CoralRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFB8860B), // Dark Goldenrod
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFF4D6),
    onPrimaryContainer = Color(0xFF5C4300),
    secondary = Color(0xFFD4AF37),
    onSecondary = Color.Black,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnBackground,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF444444),
    error = CoralRed,
    onError = Color.White
)

@Composable
fun BarberShopTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
    }

    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
