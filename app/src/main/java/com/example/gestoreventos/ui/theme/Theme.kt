package com.example.gestoreventos.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = lightColorScheme(
    primary = BrandGold,
    onPrimary = BrandBlack,
    secondary = GoldLight,
    onSecondary = BrandBlack,
    tertiary = GoldDark,
    onTertiary = SurfaceWhite,
    background = CreamBackground,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = CreamBackground,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    outlineVariant = CardBorder,
    error = ErrorRed,
    onError = SurfaceWhite,
    errorContainer = ErrorRedBg,
    onErrorContainer = ErrorRed
)

@Composable
fun GestorEventosTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content
    )
}