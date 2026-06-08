package com.derdimet.mobil.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.derdimet.mobil.ui.theme.DerdimColors

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8FB4FF),
    secondary = Color(0xFF89D2FF),
    tertiary = Color(0xFF7EE7C5),
    background = Color(0xFF0B1220),
    surface = Color(0xFF111A2C),
    onPrimary = Color(0xFF0B1220),
    onBackground = Color(0xFFE6EAF2),
    onSurface = Color(0xFFE6EAF2),
)

private val LightColorScheme = lightColorScheme(
    primary = DerdimColors.Primary,
    secondary = DerdimColors.Secondary,
    tertiary = DerdimColors.Success,
    background = DerdimColors.Background,
    surface = DerdimColors.Card,
    onPrimary = DerdimColors.PrimaryForeground,
    onBackground = DerdimColors.Foreground,
    onSurface = DerdimColors.Foreground,
    outline = DerdimColors.Border,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
