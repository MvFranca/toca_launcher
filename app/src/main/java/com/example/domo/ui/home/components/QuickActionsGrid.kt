package com.example.domo.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domo.ui.home.model.QuickActionIcon
import com.example.domo.ui.home.model.QuickActionItem
import com.example.domo.ui.home.model.defaultQuickActions
import com.example.domo.ui.theme.TocaAmarelo
import com.example.domo.ui.theme.TocaBege
import com.example.domo.ui.theme.TocaLaranja
import com.example.domo.ui.theme.TocaMarrom
import com.example.domo.ui.theme.TocaRoxoSuave
import com.example.domo.ui.theme.TocaTheme
import com.example.domo.ui.theme.TocaVerdeSuave

@Composable
fun QuickActionsGrid(
    actions: List<QuickActionItem>,
    onActionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        actions.forEach { action ->
            QuickActionCard(
                action = action,
                onClick = { onActionClick(action.id) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    action: QuickActionItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (icon, tint) = iconFor(action.icon)

    Column(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(TocaBege)
            .clickable(onClick = onClick)
            .defaultMinSize(minHeight = 48.dp)
            .semantics { contentDescription = action.label }
            .padding(vertical = 14.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(32.dp),
        )
        Text(
            text = action.label,
            color = TocaMarrom,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

private fun iconFor(icon: QuickActionIcon): Pair<ImageVector, Color> = when (icon) {
    QuickActionIcon.ContinueMission -> Icons.Rounded.SportsEsports to TocaLaranja
    QuickActionIcon.Learn -> Icons.AutoMirrored.Rounded.MenuBook to TocaAmarelo
    QuickActionIcon.Achievements -> Icons.Rounded.EmojiEvents to TocaVerdeSuave
    QuickActionIcon.Rewards -> Icons.Rounded.CardGiftcard to TocaRoxoSuave
}

@Preview(showBackground = true, backgroundColor = 0xFF6B5344)
@Composable
private fun QuickActionsGridPreview() {
    TocaTheme {
        QuickActionsGrid(
            actions = defaultQuickActions(),
            onActionClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
