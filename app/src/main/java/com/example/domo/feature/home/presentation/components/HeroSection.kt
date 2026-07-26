package com.example.domo.feature.home.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.domo.feature.home.presentation.model.FoxAnimationState
import com.example.domo.core.designsystem.TocaTheme

@Composable
fun HeroSection(
    foxState: FoxAnimationState,
    modifier: Modifier = Modifier,
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset(lottieAssetFor(foxState)),
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 280.dp),
        contentAlignment = Alignment.Center,
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.fillMaxSize(0.55f),
        )
    }
}

private fun lottieAssetFor(state: FoxAnimationState): String = when (state) {
    FoxAnimationState.Reading,
    FoxAnimationState.Playing,
    FoxAnimationState.Sleeping,
    FoxAnimationState.Celebrating,
    FoxAnimationState.Thinking,
    FoxAnimationState.Teaching,
    -> "lotties/fox-hello.json"
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
