package com.example.domo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ChildColorScheme = lightColorScheme(
    primary = DeepBlue,
    onPrimary = Color.White,
    primaryContainer = LightBlueContainer,
    onPrimaryContainer = NavyText,
    secondary = CoralOrange,
    onSecondary = Color.White,
    secondaryContainer = SunshineYellow,
    onSecondaryContainer = NavyText,
    background = LightBackground,
    onBackground = NavyText,
    surface = LightBackground,
    onSurface = NavyText,
)

@Composable
fun DomoTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = ChildColorScheme,
        typography = Typography,
        content = content,
    )
}
