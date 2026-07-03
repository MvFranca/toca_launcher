package com.example.domo.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domo.ui.home.model.DailyMission
import com.example.domo.ui.theme.TocaCardBranco
import com.example.domo.ui.theme.TocaCreme
import com.example.domo.ui.theme.TocaLaranja
import com.example.domo.ui.theme.TocaMarrom
import com.example.domo.ui.theme.TocaTextoMuted
import com.example.domo.ui.theme.TocaTheme

@Composable
fun MissionCard(
    mission: DailyMission,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val progressLabel = "${mission.current}/${mission.total}"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(TocaCardBranco)
            .clickable(onClick = onClick)
            .defaultMinSize(minHeight = 48.dp)
            .semantics {
                contentDescription = "${mission.title}. ${mission.description}. Progresso $progressLabel"
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(TocaLaranja.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.TrackChanges,
                contentDescription = null,
                tint = TocaLaranja,
                modifier = Modifier.size(24.dp),
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = mission.title,
                color = TocaMarrom,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
            )
            Text(
                text = mission.description,
                color = TocaTextoMuted,
                fontSize = 13.sp,
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(TocaCreme)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(
                    text = progressLabel,
                    color = TocaMarrom,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = TocaTextoMuted,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MissionCardPreview() {
    TocaTheme {
        MissionCard(
            mission = DailyMission(
                title = "Missão do dia",
                description = "Complete 3 atividades de Matemática",
                current = 1,
                total = 3,
            ),
            onClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
