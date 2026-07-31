package com.example.domo.feature.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domo.core.designsystem.TocaCreme
import com.example.domo.core.designsystem.TocaMarrom
import com.example.domo.core.designsystem.TocaNavOrange
import com.example.domo.core.designsystem.TocaTheme
import com.example.domo.feature.home.presentation.model.QuickActionIcon
import com.example.domo.feature.home.presentation.model.QuickActionItem
import com.example.domo.feature.home.presentation.model.defaultQuickActions

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
    Column(
        modifier = modifier
            .height(88.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(TocaCreme)
            .clickable(onClick = onClick)
            .defaultMinSize(minHeight = 48.dp)
            .semantics { contentDescription = action.label }
            .padding(horizontal = 6.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = iconFor(action.icon),
            contentDescription = null,
            tint = TocaNavOrange,
            modifier = Modifier.size(28.dp),
        )
        Text(
            text = action.label,
            color = TocaMarrom,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

private fun iconFor(icon: QuickActionIcon): ImageVector = when (icon) {
    QuickActionIcon.ContinueMission -> Icons.Rounded.Flag
    QuickActionIcon.Learn -> Icons.AutoMirrored.Rounded.MenuBook
    QuickActionIcon.Achievements -> Icons.Rounded.EmojiEvents
    QuickActionIcon.Rewards -> Icons.Rounded.CardGiftcard
}

@Preview(showBackground = true, backgroundColor = 0xFFFDF8F4)
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
