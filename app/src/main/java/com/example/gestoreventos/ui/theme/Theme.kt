package com.example.gestoreventos.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BrandGold,
    onPrimary = BrandBlack,
    secondary = GoldLight,
    onSecondary = BrandBlack,
    tertiary = GoldDark,
    onTertiary = BrandWhite,
    background = BrandBlack,
    onBackground = BrandWhite,
    surface = BlackLight,
    onSurface = BrandWhite,
    surfaceVariant = BlackMedium,
    onSurfaceVariant = GrayMedium,
    outline = BrandGold,
    outlineVariant = GoldDark
)

private val LightColorScheme = lightColorScheme(
    primary = BrandGold,
    onPrimary = BrandBlack,
    secondary = GoldLight,
    onSecondary = BrandBlack,
    tertiary = GoldDark,
    onTertiary = BrandWhite,
    background = BrandWhite,
    onBackground = BrandBlack,
    surface = BrandWhite,
    onSurface = BrandBlack,
    surfaceVariant = GrayLight,
    onSurfaceVariant = BlackMedium,
    outline = BrandGold,
    outlineVariant = GoldDark
)

@Composable
fun GestorEventosTheme(
    darkTheme: Boolean = false, // Forzar siempre modo claro
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Desactivado para usar colores de marca
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}