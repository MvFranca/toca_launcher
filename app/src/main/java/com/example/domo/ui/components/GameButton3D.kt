package com.example.domo.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Cores de um botão ───────────────────────────────────────────────────────────
data class ButtonColors(
    val faceTop: Color,
    val faceBtm: Color,
    val shadow: Color,
)

// ── Variante estrutural ─────────────────────────────────────────────────────────
/**
 * Define a estrutura visual do botão, independente da cor.
 *
 * - [Solid]    — botão 3D completo: outer shadow + face gradiente + gloss + borda branca.
 *                Press: profundidade colapsa 4dp→0dp via spring.
 * - [Outlined] — borda colorida (shadow color), face transparente, texto colorido. Sem 3D depth.
 *                Press: fundo anima 0%→15% da shadow color.
 * - [Ghost]    — sem borda, sem background, só texto colorido em área tocável.
 *                Press: fundo anima 0%→12% da shadow color.
 */
enum class ButtonStyle { Solid, Outlined, Ghost }

// ── Paleta de variantes do design system Domo ──────────────────────────────────
object GameButtonDefaults {
    val Amarelo = ButtonColors(
        faceTop = Color(0xFFFFDA5A),
        faceBtm = Color(0xFFF5C230),
        shadow  = Color(0xFFC07800),
    )
    val Rosa = ButtonColors(
        faceTop = Color(0xFFFF8FCA),
        faceBtm = Color(0xFFF165B0),
        shadow  = Color(0xFFC03680),
    )
    val Teal = ButtonColors(
        faceTop = Color(0xFF85E8D8),
        faceBtm = Color(0xFF5DD0BC),
        shadow  = Color(0xFF279E8E),
    )
    val Lilas = ButtonColors(
        faceTop = Color(0xFFD8C8F5),
        faceBtm = Color(0xFFC5AAEC),
        shadow  = Color(0xFF8060BE),
    )
    val Verde = ButtonColors(
        faceTop = Color(0xFF96E865),
        faceBtm = Color(0xFF78D045),
        shadow  = Color(0xFF3E9910),
    )
    val GreenLime = ButtonColors(
        faceTop = Color(0xFF80F020),
        faceBtm = Color(0xFF67EB00),
        shadow  = Color(0xFF4EC307),
    )
}

/**
 * Botão gamificado com 3 variantes estruturais ([ButtonStyle]) e 6 paletas de cor.
 *
 * Uso:
 * ```kotlin
 * // Sólido (padrão)
 * GameButton3D("JOGAR", GameButtonDefaults.Verde, Modifier.weight(1f)) {}
 *
 * // Outlined
 * GameButton3D("JOGAR", GameButtonDefaults.Verde, style = ButtonStyle.Outlined) {}
 *
 * // Ghost
 * GameButton3D("JOGAR", GameButtonDefaults.Verde, style = ButtonStyle.Ghost) {}
 * ```
 */
@Composable
fun GameButton3D(
    text: String,
    colors: ButtonColors,
    modifier: Modifier = Modifier,
    style: ButtonStyle = ButtonStyle.Solid,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val clickableModifier = if (enabled) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication        = null,
            onClick           = onClick,
        )
    } else {
        Modifier
    }

    when (style) {
        ButtonStyle.Solid   -> SolidButton(text, colors, modifier, enabled, isPressed, clickableModifier)
        ButtonStyle.Outlined -> OutlinedButton(text, colors, modifier, enabled, isPressed, clickableModifier)
        ButtonStyle.Ghost   -> GhostButton(text, colors, modifier, enabled, isPressed, clickableModifier)
    }
}

// ── Solid ───────────────────────────────────────────────────────────────────────
@Composable
private fun SolidButton(
    text: String,
    colors: ButtonColors,
    modifier: Modifier,
    enabled: Boolean,
    isPressed: Boolean,
    clickableModifier: Modifier,
) {
    val depth by animateDpAsState(
        targetValue  = if (isPressed || !enabled) 0.dp else 4.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessMedium,
        ),
        label = "btn_solid_depth",
    )

    Box(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.45f)
            .clip(RoundedCornerShape(24.dp))
            .background(colors.shadow)
            .border(3.dp, Color.White, RoundedCornerShape(24.dp))
            .padding(bottom = depth),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(21.dp))
                .background(Brush.verticalGradient(listOf(colors.faceTop, colors.faceBtm)))
                .then(clickableModifier),
        ) {
            // Reflexo diagonal — trapézio 80%→20%, mesmo padrão do design system
            Canvas(modifier = Modifier.matchParentSize()) {
                val alpha = if (isPressed) 0.04f else 0.40f
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 13.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text          = text,
                    color         = Color.White,
                    fontWeight    = FontWeight.ExtraBold,
                    fontSize      = 18.sp,
                    letterSpacing = 1.sp,
                    style         = TextStyle(
                        shadow = Shadow(
                            color      = colors.shadow.copy(alpha = 0.55f),
                            offset     = Offset(0f, 2f),
                            blurRadius = 3f,
                        ),
                    ),
                )
            }
        }
    }
}

// ── Outlined ────────────────────────────────────────────────────────────────────
@Composable
private fun OutlinedButton(
    text: String,
    colors: ButtonColors,
    modifier: Modifier,
    enabled: Boolean,
    isPressed: Boolean,
    clickableModifier: Modifier,
) {
    // Press feedback: fundo anima de transparente → 15% da shadow color
    val pressedBg by animateColorAsState(
        targetValue   = if (isPressed) colors.shadow.copy(alpha = 0.15f) else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label         = "btn_outlined_bg",
    )

    Box(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.45f)
            .clip(RoundedCornerShape(24.dp))
            .border(3.dp, colors.shadow, RoundedCornerShape(24.dp))
            .background(pressedBg)
            .then(clickableModifier)
            .padding(horizontal = 24.dp, vertical = 13.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text          = text,
            color         = colors.shadow,
            fontWeight    = FontWeight.ExtraBold,
            fontSize      = 18.sp,
            letterSpacing = 1.sp,
        )
    }
}

// ── Ghost ───────────────────────────────────────────────────────────────────────
@Composable
private fun GhostButton(
    text: String,
    colors: ButtonColors,
    modifier: Modifier,
    enabled: Boolean,
    isPressed: Boolean,
    clickableModifier: Modifier,
) {
    // Press feedback: fundo anima de transparente → 12% da shadow color
    val pressedBg by animateColorAsState(
        targetValue   = if (isPressed) colors.shadow.copy(alpha = 0.12f) else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label         = "btn_ghost_bg",
    )

    Box(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.45f)
            .clip(RoundedCornerShape(24.dp))
            .background(pressedBg)
            .then(clickableModifier)
            .padding(horizontal = 24.dp, vertical = 13.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text          = text,
            color         = colors.shadow,
            fontWeight    = FontWeight.Bold,
            fontSize      = 18.sp,
            letterSpacing = 1.sp,
        )
    }
}
