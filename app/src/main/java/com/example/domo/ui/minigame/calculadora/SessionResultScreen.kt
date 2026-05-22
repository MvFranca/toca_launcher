package com.example.domo.ui.minigame.calculadora

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ElectricBolt
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domo.ui.minigame.calculadora.model.SessionResult

// ── Paleta game UI ──────────────────────────────────────────────────────────────
private val CBgStart      = Color(0xFFA659FE)
private val CBgEnd        = Color(0xFF6F53FD)
private val CGreenFace    = Color(0xFF67EB00)
private val CGreenShadow  = Color(0xFF4EC307)
private val CPurpleFace   = Color(0xFFC286FF)
private val CPurpleShadow = Color(0xFFA75CF4)
private val CCardBg       = Color(0xFFFFFEFF)
private val CCardShadow   = Color(0xFFD1D8FF)
private val CTextDark     = Color(0xFF1A1A18)
private val CTextMuted    = Color(0xFF888780)
private val CCorrect      = Color(0xFF1D9E75)
private val CWrong        = Color(0xFFE24B4A)
private val CEnergy       = Color(0xFFFF6B00)
private val CXp           = Color(0xFFD97706)
// Ribbon hero
private val CRibbonTop    = Color(0xFFFFA4EB)
private val CRibbonBottom = Color(0xFFED36CA)
private val CRibbonShadow = Color(0xFFD71095)
private val CScoreBg      = Color(0xFFC2FDFF)
private val CScoreText    = Color(0xFF228AED)

@Composable
fun SessionResultScreen(
    result: SessionResult,
    onPlayAgain: () -> Unit,
    onBackToHome: () -> Unit,
) {
    var animationStarted by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animationStarted = true }

    val star1Scale by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0f,
        animationSpec = tween(400, delayMillis = 100, easing = FastOutSlowInEasing),
        label = "star1",
    )
    val star2Scale by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0f,
        animationSpec = tween(400, delayMillis = 220, easing = FastOutSlowInEasing),
        label = "star2",
    )
    val star3Scale by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0f,
        animationSpec = tween(400, delayMillis = 340, easing = FastOutSlowInEasing),
        label = "star3",
    )

    val total = (result.hits + result.misses).coerceAtLeast(1)
    val hitRate = result.hits.toFloat() / total.toFloat()

    val animatedHitRate by animateFloatAsState(
        targetValue = if (animationStarted) hitRate else 0f,
        animationSpec = tween(900, delayMillis = 300, easing = FastOutSlowInEasing),
        label = "hit_rate",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(CBgStart, CBgEnd)))
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // ── Hero ribbon + estrelas ────────────────────────────────────────────
        HeroSection(
            star1Scale = star1Scale,
            star2Scale = star2Scale,
            star3Scale = star3Scale,
            total = total,
            hits = result.hits,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Card score ────────────────────────────────────────────────────────
        GameCard(modifier = Modifier.padding(horizontal = 20.dp)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    StatColumn(
                        value = result.hits.toString(),
                        label = "Acertos",
                        icon = Icons.Rounded.CheckCircle,
                        iconTint = CCorrect,
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(72.dp)
                            .background(Color(0xFFE8E6DF))
                            .align(Alignment.CenterVertically),
                    )
                    StatColumn(
                        value = result.misses.toString(),
                        label = "Erros",
                        icon = Icons.Rounded.Cancel,
                        iconTint = CWrong,
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Score pill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50.dp))
                        .background(CScoreBg)
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "${result.hits * 100 + result.misses * 0}",
                        color = CScoreText,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp,
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                HitRateBar(progress = animatedHitRate)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Card recompensas ──────────────────────────────────────────────────
        GameCard(modifier = Modifier.padding(horizontal = 20.dp)) {
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
                Text(
                    text = "RECOMPENSAS",
                    color = CTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                )
                Spacer(modifier = Modifier.height(14.dp))
                RewardPill(
                    icon = Icons.Rounded.ElectricBolt,
                    label = "Energia",
                    value = "+${result.energyEarned}",
                    iconTint = CEnergy,
                    bgColor = Color(0xFFFFF3E0),
                    valueColor = CEnergy,
                )
                Spacer(modifier = Modifier.height(10.dp))
                RewardPill(
                    icon = Icons.Rounded.Star,
                    label = "XP",
                    value = "+${result.xpEarned}",
                    iconTint = CXp,
                    bgColor = Color(0xFFFFFDE7),
                    valueColor = CXp,
                )
            }
        }

        // ── Badges de conquistas ──────────────────────────────────────────────
        val showBadges = result.persistenceBonusTriggered || result.comboTriggered
        if (showBadges) {
            Spacer(modifier = Modifier.height(16.dp))
            GameCard(modifier = Modifier.padding(horizontal = 20.dp)) {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
                    Text(
                        text = "CONQUISTAS",
                        color = CTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (result.persistenceBonusTriggered) {
                            BadgeChip(
                                icon = Icons.Rounded.Shield,
                                text = "Persistencia",
                                iconTint = Color(0xFFD97706),
                                bg = Color(0xFFFFF3CD),
                                borderColor = Color(0xFFFFD700),
                            )
                        }
                        if (result.comboTriggered) {
                            BadgeChip(
                                icon = Icons.Rounded.LocalFireDepartment,
                                text = "Combo x2",
                                iconTint = CEnergy,
                                bg = Color(0xFFFFE8D6),
                                borderColor = Color(0xFFFF6B00),
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ── Botões 3D ─────────────────────────────────────────────────────────
        GameButton3D(
            text = "JOGAR DE NOVO",
            faceColor = CGreenFace,
            shadowColor = CGreenShadow,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            onClick = onPlayAgain,
        )

        Spacer(modifier = Modifier.height(12.dp))

        GameButton3D(
            text = "VOLTAR AO INICIO",
            faceColor = CPurpleFace,
            shadowColor = CPurpleShadow,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            onClick = onBackToHome,
        )

        Spacer(modifier = Modifier.height(36.dp))
    }
}

// ── Hero com ribbon e estrelas ────────────────────────────────────────────────
@Composable
private fun HeroSection(
    star1Scale: Float,
    star2Scale: Float,
    star3Scale: Float,
    total: Int,
    hits: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Box com estrelas sobrepostas à ribbon
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter,
        ) {
            // ── Ribbon com asas (deslocada para baixo para as estrelas sobreporem) ──
            // padding(top=28dp) deixa espaço para os 44-56dp das estrelas sobreporem
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Asa esquerda — triângulo apontando para a esquerda
                    Canvas(
                        modifier = Modifier
                            .width(32.dp)
                            .height(52.dp),
                    ) {
                        val path = Path().apply {
                            moveTo(size.width, 0f)
                            lineTo(size.width, size.height)
                            lineTo(0f, size.height * 0.5f)
                            close()
                        }
                        drawPath(path, Color(0xFFD71095))
                    }

                    // Centro da ribbon
                    Box(modifier = Modifier.weight(1f)) {
                        // Camada sombra
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CRibbonShadow)
                                .height(52.dp),
                        )
                        // Camada face com gradiente vertical
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.verticalGradient(listOf(CRibbonTop, CRibbonBottom)),
                                )
                                .height(50.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                // Linha pequena acima (como "level 20" no Figma)
                                Text(
                                    text = "sessao",
                                    color = Color(0xFFB20D78),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                // Texto principal com text-shadow
                                Text(
                                    text = "COMPLETA!",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 22.sp,
                                    style = TextStyle(
                                        shadow = Shadow(Color(0xFFB20D78), offset = Offset(0f, 2f)),
                                    ),
                                )
                            }
                        }
                    }

                    // Asa direita — triângulo apontando para a direita
                    Canvas(
                        modifier = Modifier
                            .width(32.dp)
                            .height(52.dp),
                    ) {
                        val path = Path().apply {
                            moveTo(0f, 0f)
                            lineTo(0f, size.height)
                            lineTo(size.width, size.height * 0.5f)
                            close()
                        }
                        drawPath(path, Color(0xFFD71095))
                    }
                }
            }

            // ── Estrelas sobrepostas no topo do Box (sobre a ribbon) ──
            // verticalAlignment = Bottom: bottoms alinhados → estrela central maior aparece mais alta
            Row(
                modifier = Modifier.align(Alignment.TopCenter),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center,
            ) {
                // Estrela esquerda pequena (46dp) — base alinhada, topo mais baixo
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFDD17),
                    modifier = Modifier
                        .size(46.dp)
                        .scale(star1Scale),
                )
                // Estrela central grande (56dp) — base alinhada, aparece elevada
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFDD17),
                    modifier = Modifier
                        .size(56.dp)
                        .scale(star2Scale),
                )
                // Estrela direita pequena (46dp)
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFDD17),
                    modifier = Modifier
                        .size(46.dp)
                        .scale(star3Scale),
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Voce acertou $hits de $total questoes",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
        )
    }
}

// ── Card branco com sombra colorida ───────────────────────────────────────────
@Composable
private fun GameCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        // Sombra colorida
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .clip(RoundedCornerShape(21.dp))
                .background(CCardShadow)
                .heightIn(min = 60.dp),
        )
        // Card branco
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(21.dp))
                .background(CCardBg),
        ) {
            content()
        }
    }
}

// ── Botão 3D ──────────────────────────────────────────────────────────────────
@Composable
private fun GameButton3D(
    text: String,
    faceColor: Color,
    shadowColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(shadowColor)
            .border(2.dp, Color.White, RoundedCornerShape(18.dp))
            .padding(bottom = 4.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(faceColor)
                .clickable(onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                style = TextStyle(shadow = Shadow(Color.Black.copy(alpha = 0.2f), offset = Offset(0f, 2f))),
            )
        }
    }
}

// ── Componentes auxiliares ─────────────────────────────────────────────────────
@Composable
private fun StatColumn(value: String, label: String, icon: ImageVector, iconTint: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(28.dp),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 48.sp,
            fontWeight = FontWeight.ExtraBold,
            color = iconTint,
        )
        Text(
            text = label,
            fontSize = 13.sp,
            color = CTextMuted,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun HitRateBar(progress: Float) {
    val percentage = (progress * 100).toInt()
    val barColor = when {
        progress >= 0.75f -> Brush.horizontalGradient(listOf(Color(0xFF1D9E75), Color(0xFF34D399)))
        progress >= 0.45f -> Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFFCD34D)))
        else              -> Brush.horizontalGradient(listOf(Color(0xFFE24B4A), Color(0xFFFF6B6B)))
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Taxa de acerto",
                fontSize = 12.sp,
                color = CTextMuted,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "$percentage%",
                fontSize = 12.sp,
                color = CTextDark,
                fontWeight = FontWeight.ExtraBold,
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFEEEBE4)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .height(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(barColor),
            )
        }
    }
}

@Composable
private fun RewardPill(
    icon: ImageVector,
    label: String,
    value: String,
    iconTint: Color,
    bgColor: Color,
    valueColor: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            color = CTextDark,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            color = valueColor,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 26.sp,
        )
    }
}

@Composable
private fun BadgeChip(
    icon: ImageVector,
    text: String,
    iconTint: Color,
    bg: Color,
    borderColor: Color,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .border(1.5.dp, borderColor.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = CTextDark,
        )
    }
}
