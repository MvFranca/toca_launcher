package com.example.domo.ui.home.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domo.ui.home.model.HomeBottomTab
import com.example.domo.ui.theme.TocaBege
import com.example.domo.ui.theme.TocaCardBranco
import com.example.domo.ui.theme.TocaLaranja
import com.example.domo.ui.theme.TocaMarrom
import com.example.domo.ui.theme.TocaTextoMuted
import com.example.domo.ui.theme.TocaTheme

@Composable
fun TocaBottomNavigation(
    selected: HomeBottomTab,
    onSelect: (HomeBottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            color = TocaCardBranco,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom,
            ) {
                SideNavItem(
                    tab = HomeBottomTab.HOME,
                    label = "Início",
                    icon = Icons.Rounded.Home,
                    selected = selected,
                    onSelect = onSelect,
                )
                SideNavItem(
                    tab = HomeBottomTab.EXPLORE,
                    label = "Explorar",
                    icon = Icons.Rounded.Explore,
                    selected = selected,
                    onSelect = onSelect,
                )
                CentralFoxButton(
                    selected = selected == HomeBottomTab.TOCA,
                    onClick = { onSelect(HomeBottomTab.TOCA) },
                )
                SideNavItem(
                    tab = HomeBottomTab.PASS,
                    label = "Passe",
                    icon = Icons.Rounded.Map,
                    selected = selected,
                    onSelect = onSelect,
                )
                SideNavItem(
                    tab = HomeBottomTab.PROFILE,
                    label = "Perfil",
                    icon = Icons.Rounded.Person,
                    selected = selected,
                    onSelect = onSelect,
                )
            }
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
) {
    val isSelected = selected == tab
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "navScale",
    )
    val tint = if (isSelected) TocaLaranja else TocaTextoMuted

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scale)
            .clickable { onSelect(tab) }
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .semantics { contentDescription = label }
            .padding(horizontal = 4.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = label,
            color = tint,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        )
    }
}

@Composable
private fun CentralFoxButton(
    selected: Boolean,
    onClick: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.1f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "foxScale",
    )

    Box(
        modifier = Modifier
            .offset(y = (-18).dp)
            .scale(scale)
            .size(64.dp)
            .shadow(6.dp, CircleShape)
            .clip(CircleShape)
            .background(TocaLaranja)
            .clickable(onClick = onClick)
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .semantics { contentDescription = "Toca, mascote" },
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "🦊", fontSize = 30.sp)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFDF8F4)
@Composable
private fun TocaBottomNavigationPreview() {
    TocaTheme {
        TocaBottomNavigation(
            selected = HomeBottomTab.HOME,
            onSelect = {},
        )
    }
}
