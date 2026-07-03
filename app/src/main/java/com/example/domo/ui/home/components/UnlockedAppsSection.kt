package com.example.domo.ui.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domo.R
import com.example.domo.ui.home.model.MORE_APPS_PACKAGE
import com.example.domo.ui.home.model.UnlockedAppUi
import com.example.domo.ui.home.preview.previewHomeUiState
import com.example.domo.ui.theme.TocaBege
import com.example.domo.ui.theme.TocaLaranja
import com.example.domo.ui.theme.TocaMarrom
import com.example.domo.ui.theme.TocaTextoMuted
import com.example.domo.ui.theme.TocaTheme

@Composable
fun UnlockedAppsSection(
    apps: List<UnlockedAppUi>,
    availableScreenMinutes: Int,
    onAppClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(TocaBege)
            .padding(horizontal = 14.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Acesso ao mundo exterior",
                color = TocaMarrom,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f),
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Schedule,
                    contentDescription = null,
                    tint = TocaLaranja,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = "$availableScreenMinutes min disponíveis",
                    color = TocaLaranja,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Top,
        ) {
            apps.forEach { app ->
                AppDoorItem(
                    app = app,
                    onClick = { onAppClick(app.packageName) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun AppDoorItem(
    app: UnlockedAppUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = when {
        app.packageName == MORE_APPS_PACKAGE -> "Mais apps"
        else -> app.label
    }
    val doorDescription = if (app.isLocked) {
        "$label, bloqueado"
    } else {
        "Abrir $label"
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(onClick = onClick)
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .semantics { contentDescription = doorDescription }
            .padding(horizontal = 2.dp),
    ) {
        Box(
            modifier = Modifier.size(width = 56.dp, height = 68.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.door),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )

            if (!app.isLocked) {
                if (app.icon != null) {
                    Image(
                        bitmap = app.icon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp)),
                    )
                } else {
                    Text(
                        text = app.label.take(1).uppercase(),
                        color = TocaMarrom,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    )
                }
            } else if (app.icon != null && app.packageName != MORE_APPS_PACKAGE) {
                Image(
                    bitmap = app.icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .alpha(0.4f)
                        .clip(RoundedCornerShape(6.dp)),
                )
            }
        }

        Text(
            text = label,
            color = TocaTextoMuted,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF6B5344)
@Composable
private fun UnlockedAppsSectionPreview() {
    TocaTheme {
        UnlockedAppsSection(
            apps = previewHomeUiState.unlockedApps,
            availableScreenMinutes = 35,
            onAppClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
