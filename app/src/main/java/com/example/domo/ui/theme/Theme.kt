package com.example.domo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TocaColorScheme = lightColorScheme(
    primary = TocaLaranja,
    onPrimary = Color.White,
    primaryContainer = TocaLaranjaClaro,
    onPrimaryContainer = TocaMarrom,
    secondary = TocaVerdeSuave,
    onSecondary = Color.White,
    secondaryContainer = TocaCreme,
    onSecondaryContainer = TocaMarrom,
    background = TocaBege,
    onBackground = TocaMarrom,
    surface = TocaCardBranco,
    onSurface = TocaMarrom,
    surfaceVariant = TocaCreme,
    onSurfaceVariant = TocaTextoMuted,
)

@Composable
fun TocaTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = TocaColorScheme,
        typography = Typography,
        content = content,
    )
}

/** @deprecated Use [TocaTheme] */
@Composable
fun DomoTheme(
    content: @Composable () -> Unit,
) {
    TocaTheme(content = content)
}
