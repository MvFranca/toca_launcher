package com.example.domo.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.domo.ui.theme.TocaBege
import com.example.domo.ui.theme.TocaLaranjaClaro
import com.example.domo.ui.theme.TocaMarromEscuro
import com.example.domo.ui.theme.TocaMarromMedio
import com.example.domo.ui.theme.TocaTheme

@Composable
fun HomeBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        TocaMarromEscuro,
                        TocaMarromMedio,
                        Color(0xFF6B5344),
                        TocaLaranjaClaro.copy(alpha = 0.35f),
                        TocaBege,
                    ),
                ),
            )
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0x33E8703A),
                        Color.Transparent,
                    ),
                    center = Offset(0.5f, 0.15f),
                    radius = 900f,
                ),
            ),
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeBackgroundPreview() {
    TocaTheme {
        HomeBackground {}
    }
}
