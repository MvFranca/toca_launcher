package com.example.domo.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domo.ui.home.model.FoxAnimationState
import com.example.domo.ui.theme.TocaBege
import com.example.domo.ui.theme.TocaLaranjaClaro
import com.example.domo.ui.theme.TocaMarromMedio
import com.example.domo.ui.theme.TocaTheme
import com.example.domo.ui.theme.TocaVerdeSuave

@Composable
fun HeroSection(
    foxState: FoxAnimationState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        TocaMarromMedio,
                        Color(0xFF5C4A3A),
                        TocaLaranjaClaro.copy(alpha = 0.4f),
                    ),
                ),
            )
            .height(220.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(TocaVerdeSuave.copy(alpha = 0.35f))
                .padding(horizontal = 24.dp, vertical = 6.dp),
        ) {
            Text(
                text = "🌿 janela para a floresta",
                color = TocaBege,
                fontSize = 11.sp,
            )
        }

        ColumnContent(foxState)

        Text(
            text = stateLabel(foxState),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(Color.Black.copy(alpha = 0.25f))
                .padding(horizontal = 14.dp, vertical = 6.dp),
            color = TocaBege,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun ColumnContent(foxState: FoxAnimationState) {
    val emoji = when (foxState) {
        FoxAnimationState.Reading -> "🦊📖"
        FoxAnimationState.Playing -> "🦊🧸"
        FoxAnimationState.Sleeping -> "🦊💤"
        FoxAnimationState.Celebrating -> "🦊🎉"
        FoxAnimationState.Thinking -> "🦊💭"
        FoxAnimationState.Teaching -> "🦊👨‍👧‍👦"
    }
    Text(text = emoji, fontSize = 56.sp)
}

private fun stateLabel(state: FoxAnimationState): String = when (state) {
    FoxAnimationState.Reading -> "Raposa lendo — Rive em breve"
    FoxAnimationState.Playing -> "Raposa brincando"
    FoxAnimationState.Sleeping -> "Raposa dormindo"
    FoxAnimationState.Celebrating -> "Raposa comemorando"
    FoxAnimationState.Thinking -> "Raposa pensando"
    FoxAnimationState.Teaching -> "Raposa ensinando"
}

@Preview(showBackground = true, backgroundColor = 0xFF2C2C2A)
@Composable
private fun HeroSectionPreview() {
    TocaTheme {
        HeroSection(
            foxState = FoxAnimationState.Reading,
            modifier = Modifier.padding(16.dp),
        )
    }
}
