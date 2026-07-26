package com.example.domo.feature.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domo.feature.home.presentation.model.QuickActionItem
import com.example.domo.feature.home.presentation.model.defaultQuickActions
import com.example.domo.core.designsystem.TocaCreme
import com.example.domo.core.designsystem.TocaMarrom
import com.example.domo.core.designsystem.TocaTheme

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
    Box(
        modifier = modifier
            .height(76.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(TocaCreme)
            .clickable(onClick = onClick)
            .defaultMinSize(minHeight = 48.dp)
            .semantics { contentDescription = action.label }
            .padding(horizontal = 6.dp, vertical = 12.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Text(
            text = action.label,
            color = TocaMarrom,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
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
