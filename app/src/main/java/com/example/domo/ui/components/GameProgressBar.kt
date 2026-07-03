package com.example.domo.ui.components

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Modelo de cores ─────────────────────────────────────────────────────────────

/**
 * Cores de uma barra de progresso — derivável de [ButtonColors] via [asProgressBarColors].
 */
data class ProgressBarColors(
    val fillStart : Color,
    val fillEnd   : Color,
    val track     : Color,
)

/**
 * Converte [ButtonColors] em [ProgressBarColors], garantindo consistência de paleta
 * entre botões e barras no mesmo design system.
 *
 * ```kotlin
 * GameProgressBarDefaults.Verde == GameButtonDefaults.Verde.asProgressBarColors()
 * ```
 */
fun ButtonColors.asProgressBarColors(trackAlpha: Float = 0.22f): ProgressBarColors =
    ProgressBarColors(
        fillStart = faceTop,
        fillEnd   = faceBtm,
        track     = shadow.copy(alpha = trackAlpha),
    )

// ── Presets ─────────────────────────────────────────────────────────────────────

object GameProgressBarDefaults {
    val Verde   = GameButtonDefaults.Verde.asProgressBarColors()
    val Rosa    = GameButtonDefaults.Rosa.asProgressBarColors()
    val Teal    = GameButtonDefaults.Teal.asProgressBarColors()
    val Amarelo = GameButtonDefaults.Amarelo.asProgressBarColors()
    val Lilas   = GameButtonDefaults.Lilas.asProgressBarColors()
}

/**
 * Helper para o caso de uso de timer: seleciona automaticamente
 * verde / amarelo / vermelho conforme o nível de progresso restante.
 *
 * - `progress > 0.55f` → Verde (confortável)
 * - `progress > 0.28f` → Amarelo (atenção)
 * - `progress ≤ 0.28f` → Vermelho (urgência)
 */
fun timerProgressColors(progress: Float): ProgressBarColors = when {
    progress > 0.55f -> ProgressBarColors(
        fillStart = Color(0xFF34D399),
        fillEnd   = Color(0xFF1D9E75),
        track     = Color(0xFF1D9E75).copy(alpha = 0.22f),
    )
    progress > 0.28f -> ProgressBarColors(
        fillStart = Color(0xFFFCD34D),
        fillEnd   = Color(0xFFF59E0B),
        track     = Color(0xFFF59E0B).copy(alpha = 0.22f),
    )
    else -> ProgressBarColors(
        fillStart = Color(0xFFFF6B6B),
        fillEnd   = Color(0xFFE24B4A),
        track     = Color(0xFFE24B4A).copy(alpha = 0.22f),
    )
}

// ── Componente ──────────────────────────────────────────────────────────────────

/**
 * Barra de progresso gamificada do design system Domo.
 *
 * Segue a mesma linguagem visual do [GameButton3D]:
 * - Fill com `horizontalGradient` (fillStart → fillEnd)
 * - Gloss sutil no topo do fill via Canvas (White 30% → Transparent)
 * - Track tintado com a cor do fill (22% alpha)
 * - Border radius full (pill)
 *
 * Uso básico:
 * ```kotlin
 * GameProgressBar(progress = 0.7f, colors = GameProgressBarDefaults.Verde)
 * ```
 *
 * Com label e porcentagem:
 * ```kotlin
 * GameProgressBar(
 *     progress       = missionProgress,
 *     colors         = GameProgressBarDefaults.Rosa,
 *     label          = "Missão",
 *     showPercentage = true,
 * )
 * ```
 *
 * Timer (cor muda automaticamente):
 * ```kotlin
 * GameProgressBar(
 *     progress = timeProgress,
 *     colors   = timerProgressColors(timeProgress),
 *     height   = 10.dp,
 *     animationSpec = tween(80, easing = LinearEasing),
 * )
 * ```
 */
@Composable
fun GameProgressBar(
    progress       : Float,
    modifier       : Modifier              = Modifier,
    colors         : ProgressBarColors     = GameProgressBarDefaults.Verde,
    height         : Dp                    = 14.dp,
    label          : String?               = null,
    showPercentage : Boolean               = false,
    animationSpec  : AnimationSpec<Float>  = tween(durationMillis = 200, easing = LinearEasing),
) {
    val animatedProgress by animateFloatAsState(
        targetValue   = progress.coerceIn(0f, 1f),
        animationSpec = animationSpec,
        label         = "progress_bar",
    )

    Column(modifier = modifier) {
        // ── Label + porcentagem ───────────────────────────────────────────────
        if (label != null || showPercentage) {
            Row(modifier = Modifier.fillMaxWidth()) {
                if (label != null) {
                    Text(
                        text       = label,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = colors.fillEnd.copy(alpha = 0.85f),
                        modifier   = Modifier.weight(1f),
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
                if (showPercentage) {
                    Text(
                        text       = "${(animatedProgress * 100).toInt()}%",
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = colors.fillEnd,
                    )
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
        }

        // ── Track ─────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(50))
                .background(colors.track),
        ) {
            // ── Fill ─────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(height)
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.horizontalGradient(
                            listOf(colors.fillStart, colors.fillEnd),
                        ),
                    ),
            ) {
                // Reflexo diagonal — mesmo clipPath trapezoidal do GameButton3D
                // Para barras finas (10-14dp) o trapézio 80%→20% cria um brilho
                // concentrado na metade esquerda, dando o visual 3D desejado.
                Canvas(modifier = Modifier.matchParentSize()) {
                    val alpha = 0.40f
                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(size.width * 0.80f, 0f)
                        lineTo(size.width * 0.20f, size.height)
                        lineTo(0f, size.height)
                        close()
                    }
                    clipPath(path) {
                        drawRect(
                            brush = Brush.horizontalGradient(
                                0.00f to Color.White.copy(alpha = alpha),
                                0.55f to Color.White.copy(alpha = alpha * 0.38f),
                                1.00f to Color.Transparent,
                                startX = 0f,
                                endX   = size.width * 0.80f,
                            ),
                        )
                    }
                }
            }
        }
    }
}
