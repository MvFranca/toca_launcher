package com.example.domo.ui.components

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
    // Mantém compatibilidade com o verde vívido já usado no HeroCard original
    val GreenLime = ButtonColors(
        faceTop = Color(0xFF80F020),
        faceBtm = Color(0xFF67EB00),
        shadow  = Color(0xFF4EC307),
    )
}

/**
 * Botão gamificado 3D com efeito de profundidade, gloss no topo e animação de press.
 *
 * Anatomia:
 *   ┌─────────────────────────────┐ ← outer (cor shadow = profundidade inferior)
 *   │ ┌─────────────────────────┐ │ ← face (gradiente top→btm + gloss overlay)
 *   │ │        LABEL            │ │
 *   │ └─────────────────────────┘ │
 *   └─────────────────────────────┘
 *
 * No estado pressed: o espaço inferior (depth) colapsa de 4.dp → 0.dp via animação
 * spring, dando a sensação de que o botão afunda ao toque.
 */
@Composable
fun GameButton3D(
    text: String,
    colors: ButtonColors,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Profundidade: colapsa ao pressionar, animado com spring para resposta snappy
    val depth by animateDpAsState(
        targetValue = if (isPressed || !enabled) 0.dp else 4.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessMedium,
        ),
        label = "btn_depth",
    )

    Box(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.45f)
            // Camada outer = cor de sombra/profundidade
            .clip(RoundedCornerShape(24.dp))
            .background(colors.shadow)
            // Borda branca totalmente opaca — acabamento arcade
            .border(3.dp, Color.White, RoundedCornerShape(24.dp))
            // depth → espaço inferior que cria o efeito 3D
            .padding(bottom = depth),
    ) {
        // Camada face = gradiente + gloss + texto
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(21.dp))
                .background(
                    Brush.verticalGradient(listOf(colors.faceTop, colors.faceBtm)),
                )
                .then(
                    if (enabled) {
                        Modifier.clickable(
                            interactionSource = interactionSource,
                            indication        = null, // ripple customizado via depth
                            onClick           = onClick,
                        )
                    } else {
                        Modifier
                    },
                ),
        ) {
            // Reflexo diagonal via Canvas + clipPath.
            //
            // Botões largos (aspect ratio ~5-7:1) quebram gradientes com
            // Offset(+∞,+∞) porque a diagonal se torna quase horizontal e
            // o reflexo desaparece. A solução correta é desenhar um trapézio
            // que cobre 80% do topo afunilando para 20% na base, e dentro
            // dele um gradiente horizontal branco→transparente.
            //
            //  topo:  |▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒▒░░|   (80% da largura)
            //  meio:  |▓▓▓▓▓▓▓▓▒▒░░         |
            //  base:  |▓▒░░                 |   (20% da largura)
            //
            Canvas(modifier = Modifier.matchParentSize()) {
                val alpha = if (isPressed) 0.04f else 0.40f

                // Trapézio: lado esquerdo completo (0→height),
                // topo vai até 80%, base vai até 20%
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

            // Label — determina a altura da face (matchParentSize do gloss depende disto)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 13.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text         = text,
                    color        = Color.White,
                    fontWeight   = FontWeight.ExtraBold,
                    fontSize     = 18.sp,
                    letterSpacing = 1.sp,
                    style        = TextStyle(
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
