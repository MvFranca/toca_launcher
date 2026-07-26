package com.example.domo.feature.home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.domo.R
import com.example.domo.core.designsystem.TocaTheme

@Composable
fun HomeBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val shiftUp = with(density) { 250.toDp() }

    Box(modifier = modifier.fillMaxSize()) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds(),
        ) {
            Image(
                painter = painterResource(R.drawable.imagem_background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(maxWidth)
                    .height(maxHeight + shiftUp)
                    .align(Alignment.TopStart)
                    .offset(y = -shiftUp),
            )
        }
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
