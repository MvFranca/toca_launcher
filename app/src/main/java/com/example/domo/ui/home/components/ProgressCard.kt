package com.example.domo.ui.home.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domo.ui.theme.TocaCardBranco
import com.example.domo.ui.theme.TocaCreme
import com.example.domo.ui.theme.TocaLaranja
import com.example.domo.ui.theme.TocaMarrom
import com.example.domo.ui.theme.TocaTextoMuted
import com.example.domo.ui.theme.TocaTheme
import com.example.domo.ui.theme.TocaVerdeSuave

@Composable
fun ProgressCard(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "progress",
    )
    val percentage = (animatedProgress * 100).toInt()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(TocaCardBranco)
            .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(TocaVerdeSuave.copy(alpha = 0.5f), TocaCreme),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "🏡", fontSize = 28.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Você já percorreu",
                    color = TocaTextoMuted,
                    fontSize = 13.sp,
                )
                Text(
                    text = "$percentage%",
                    color = TocaLaranja,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(10.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .background(TocaCreme),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(animatedProgress)
                                .clip(RoundedCornerShape(50.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(TocaLaranja, TocaLaranja.copy(alpha = 0.8f)),
                                    ),
                                ),
                        )
                    }
                    PawTrail(modifier = Modifier.width(48.dp))
                }
            }
        }
    }
}

@Composable
private fun PawTrail(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.height(16.dp)) {
        val pawColor = TocaLaranja.copy(alpha = 0.45f)
        listOf(0f, 16f, 32f).forEach { x ->
            drawCircle(color = pawColor, radius = 3f, center = Offset(x + 4f, size.height / 2f))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProgressCardPreview() {
    TocaTheme {
        ProgressCard(progress = 0.65f, modifier = Modifier.padding(16.dp))
    }
}
