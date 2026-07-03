package com.example.domo.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Tokens fixos do input — derivados do design system Domo ───────────────────
private val InputShadow      = Color(0xFF8060BE) // Lilas.shadow
private val InputFace        = Color(0xFFFAFAFE) // branco levemente lavanda
private val InputBorderIdle  = Color.White
private val InputBorderFocus = Color(0xFF5DD0BC) // Teal.faceBtm
private val InputTextColor   = Color(0xFF2C2C2A) // CTextDark
private val InputHintColor   = Color(0xFF888780) // CTextMuted
private val InputIconColor   = Color(0xFF8060BE) // Lilas.shadow

/**
 * Campo de texto gamificado 3D — mesma linguagem visual do [GameButton3D].
 *
 * Anatomia:
 *   ┌─────────────────────────────┐ ← outer (InputShadow, borda animada, radius 24dp)
 *   │ ┌─────────────────────────┐ │ ← face (branco, radius 21dp)
 *   │ │ [ícone?]  placeholder   │ │
 *   │ └─────────────────────────┘ │
 *   └─────────────────────────────┘
 *
 * Foco: a borda muda de branca → Teal via [animateColorAsState] e a
 * profundidade colapsa de 4dp → 0dp (mesmo comportamento do botão ao press),
 * dando a sensação de que o campo "afunda" ao receber foco.
 */
@Composable
fun GameInput3D(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
) {
    var isFocused by remember { mutableStateOf(false) }

    // Borda: branca em repouso → Teal ao focar
    val borderColor by animateColorAsState(
        targetValue  = if (isFocused) InputBorderFocus else InputBorderIdle,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label        = "input_border",
    )

    // Profundidade: idêntica ao botão — colapsa ao focar
    val depth by animateDpAsState(
        targetValue  = if (isFocused) 0.dp else 4.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessMedium,
        ),
        label = "input_depth",
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(InputShadow)
            .border(3.dp, borderColor, RoundedCornerShape(24.dp))
            .padding(bottom = depth),
    ) {
        // Face branca
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(21.dp))
                .background(InputFace),
        ) {
            // Reflexo diagonal sutil — mesmo clipPath do botão, alpha reduzido
            Canvas(modifier = Modifier.matchParentSize()) {
                val alpha = if (isFocused) 0.02f else 0.06f

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
                            0.00f to Color.White.copy(alpha = alpha * 4f),
                            0.55f to Color.White.copy(alpha = alpha),
                            1.00f to Color.Transparent,
                            startX = 0f,
                            endX   = size.width * 0.80f,
                        ),
                    )
                }
            }

            // Conteúdo: ícone + campo de texto
            BasicTextField(
                value          = value,
                onValueChange  = onValueChange,
                singleLine     = singleLine,
                keyboardOptions = keyboardOptions,
                cursorBrush    = SolidColor(InputBorderFocus),
                textStyle      = TextStyle(
                    color      = InputTextColor,
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isFocused = it.isFocused }
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                decorationBox = { innerTextField ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier          = Modifier.fillMaxWidth(),
                    ) {
                        if (leadingIcon != null) {
                            Icon(
                                imageVector = leadingIcon,
                                contentDescription = null,
                                tint     = if (isFocused) InputBorderFocus else InputIconColor,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            // Placeholder visível apenas quando o campo está vazio
                            if (value.isEmpty()) {
                                Text(
                                    text       = placeholder,
                                    color      = InputHintColor,
                                    fontSize   = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                )
                            }
                            innerTextField()
                        }
                    }
                },
            )
        }
    }
}
