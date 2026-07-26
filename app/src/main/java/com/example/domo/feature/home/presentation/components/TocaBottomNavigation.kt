package com.example.domo.feature.home.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domo.R
import com.example.domo.core.designsystem.TocaNavBackdrop
import com.example.domo.core.designsystem.TocaNavInactive
import com.example.domo.core.designsystem.TocaNavOrange
import com.example.domo.core.designsystem.TocaNavSurface
import com.example.domo.core.designsystem.TocaTheme
import com.example.domo.feature.home.presentation.model.HomeBottomTab

private val NavBarHeight = 64.dp
private val FoxButtonSize = 64.dp
private val FoxButtonBorder = 5.dp
private val FoxOuterSize = FoxButtonSize + FoxButtonBorder * 2
private val FoxLift = FoxOuterSize / 2
private val NotchGap = 6.dp
private val TopCornerRadius = 28.dp

@Composable
fun TocaBottomNavigation(
    selected: HomeBottomTab,
    onSelect: (HomeBottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val barShape = remember {
        BottomNavBarShape(
            cornerRadius = TopCornerRadius,
            cradleRadius = FoxOuterSize / 2 + NotchGap,
        )
    }

    Box(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(NavBarHeight + FoxLift),
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(NavBarHeight)
                        .shadow(elevation = 10.dp, shape = barShape, clip = false)
                        .clip(barShape)
                        .background(TocaNavSurface),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(NavBarHeight)
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SideNavItem(
                            tab = HomeBottomTab.HOME,
                            label = "Home",
                            icon = Icons.Rounded.Home,
                            selected = selected,
                            onSelect = onSelect,
                            modifier = Modifier.weight(1f),
                        )
                        SideNavItem(
                            tab = HomeBottomTab.EXPLORE,
                            label = "Explorar",
                            icon = Icons.Rounded.Explore,
                            selected = selected,
                            onSelect = onSelect,
                            modifier = Modifier.weight(1f),
                        )
                        Spacer(modifier = Modifier.width(FoxOuterSize + 4.dp))
                        SideNavItem(
                            tab = HomeBottomTab.PASS,
                            label = "Passe",
                            icon = Icons.Rounded.Map,
                            selected = selected,
                            onSelect = onSelect,
                            modifier = Modifier.weight(1f),
                        )
                        SideNavItem(
                            tab = HomeBottomTab.PROFILE,
                            label = "Perfil",
                            icon = Icons.Rounded.Spa,
                            selected = selected,
                            onSelect = onSelect,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                CentralFoxButton(
                    onClick = { onSelect(HomeBottomTab.TOCA) },
                    modifier = Modifier.align(Alignment.TopCenter),
                )
            }

            // Continua o branco da barra até a borda inferior (gesture/nav do sistema)
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsBottomHeight(WindowInsets.navigationBars)
                    .background(TocaNavSurface),
            )
        }
    }
}

@Composable
private fun SideNavItem(
    tab: HomeBottomTab,
    label: String,
    icon: ImageVector,
    selected: HomeBottomTab,
    onSelect: (HomeBottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSelected = selected == tab
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "navScale",
    )
    val tint = if (isSelected) TocaNavOrange else TocaNavInactive

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onSelect(tab) }
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .semantics { contentDescription = label }
            .padding(vertical = 6.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = tint,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        )
    }
}

@Composable
private fun CentralFoxButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(FoxOuterSize)
            .shadow(8.dp, CircleShape)
            .clip(CircleShape)
            .background(TocaNavSurface)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(FoxButtonBorder)
            .semantics { contentDescription = "Toca, mascote" },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(TocaNavOrange)
                .padding(7.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.fox_simple),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

/**
 * Barra com cantos superiores arredondados e recorte (cradle) central
 * para encaixar o botão da raposa.
 */
private class BottomNavBarShape(
    private val cornerRadius: Dp,
    private val cradleRadius: Dp,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val corner = with(density) { cornerRadius.toPx() }
        val cradle = with(density) { cradleRadius.toPx() }
        val width = size.width
        val height = size.height
        val centerX = width / 2f

        val path = Path().apply {
            moveTo(0f, height)
            lineTo(0f, corner)
            quadraticTo(0f, 0f, corner, 0f)

            lineTo(centerX - cradle, 0f)

            arcTo(
                rect = Rect(
                    left = centerX - cradle,
                    top = -cradle,
                    right = centerX + cradle,
                    bottom = cradle,
                ),
                startAngleDegrees = 180f,
                sweepAngleDegrees = -180f,
                forceMoveTo = false,
            )

            lineTo(width - corner, 0f)
            quadraticTo(width, 0f, width, corner)
            lineTo(width, height)
            close()
        }

        return Outline.Generic(path)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFEECD4)
@Composable
private fun TocaBottomNavigationPreview() {
    TocaTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(TocaNavBackdrop)
                .padding(top = 48.dp),
        ) {
            TocaBottomNavigation(
                selected = HomeBottomTab.HOME,
                onSelect = {},
            )
        }
    }
}
