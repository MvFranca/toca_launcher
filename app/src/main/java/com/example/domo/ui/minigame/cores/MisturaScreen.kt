package com.example.domo.ui.minigame.cores

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.domo.ui.components.ButtonColors
import com.example.domo.ui.components.GameProgressBar
import com.example.domo.ui.components.timerProgressColors
import com.example.domo.ui.minigame.cores.model.ColorSessionResult
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// ── Paleta — idêntica à Calculadora Veloz ──────────────────────────────────────
private val CBgStart     = Color(0xFFA659FE)
private val CBgEnd       = Color(0xFF6F53FD)
private val CCardBg      = Color(0xFFFFFEFF)
private val CCardShadow  = Color(0xFFD1D8FF)
private val CTextDark    = Color(0xFF1A1A18)
private val CCorrect     = Color(0xFF1D9E75)
private val CCorrectDark = Color(0xFF15805E)
private val CWrong       = Color(0xFFE24B4A)
private val CWrongDark   = Color(0xFFC4193E)
private val CComboStart  = Color(0xFFFF6B00)
private val CComboEnd    = Color(0xFFFF8C00)

private val ColorsCorrect = ButtonColors(
    faceTop = Color(0xFF28BE8A), faceBtm = CCorrect, shadow = CCorrectDark,
)
private val ColorsWrong = ButtonColors(
    faceTop = Color(0xFFFF6666), faceBtm = CWrong, shadow = CWrongDark,
)

private val optionLetters = listOf("A", "B", "C", "D")

@Composable
fun MisturaScreen(
    onNavigateToResult: (ColorSessionResult) -> Unit,
    onBack: () -> Unit,
    viewModel: MisturaViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val animScope = rememberCoroutineScope()

    // ── Estado da animação de voo ───────────────────────────────────────────────
    var isFlying by remember { mutableStateOf(false) }
    var flyingColor by remember { mutableStateOf(Color.Transparent) }
    val flyingX = remember { Animatable(0f) }
    val flyingY = remember { Animatable(0f) }
    val flyingRadius = remember { Animatable(0f) }

    // Posição da interrogação no card (atualizada via onGloballyPositioned)
    var questionMarkCenter by remember { mutableStateOf(Offset.Zero) }
    var questionMarkRadius by remember { mutableStateOf(0f) }

    // Posições dos 4 botões-resposta
    val buttonCenters = remember { Array(4) { Offset.Zero } }
    val buttonRadii   = remember { FloatArray(4) }

    // Dispara animação e só depois chama o ViewModel
    fun handleButtonTap(index: Int, color: Color) {
        if (uiState.answerState != MisturaAnswerState.IDLE || isFlying) return
        val qRadius  = questionMarkRadius
        val bCenter  = buttonCenters[index]
        val bRadius  = buttonRadii[index]

        // Fallback sem animação caso as posições ainda não tenham sido medidas
        if (qRadius < 1f || bRadius < 1f) {
            viewModel.onOptionSelected(index, color)
            return
        }

        animScope.launch {
            flyingColor = color
            isFlying    = true

            flyingX.snapTo(bCenter.x)
            flyingY.snapTo(bCenter.y)
            flyingRadius.snapTo(bRadius)

            val spec = tween<Float>(durationMillis = 340, easing = FastOutSlowInEasing)
            val jx = launch { flyingX.animateTo(questionMarkCenter.x, spec) }
            val jy = launch { flyingY.animateTo(questionMarkCenter.y, spec) }
            flyingRadius.animateTo(qRadius, spec)
            jx.join(); jy.join()

            // Pequeno "pop" ao encaixar
            val popSpec = spring<Float>(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessHigh)
            flyingRadius.animateTo(qRadius * 1.18f, popSpec)
            flyingRadius.animateTo(qRadius, popSpec)

            isFlying = false
            viewModel.onOptionSelected(index, color)
        }
    }

    LaunchedEffect(Unit) { viewModel.startSession() }

    LaunchedEffect(uiState.sessionResult) {
        uiState.sessionResult?.let { onNavigateToResult(it) }
    }

    LaunchedEffect(uiState.answerState) {
        if (uiState.answerState == MisturaAnswerState.WRONG) triggerHaptic(context)
    }

    // Box raiz sem safeDrawingPadding — necessário para o Canvas voador
    // usar o mesmo sistema de coordenadas que positionInRoot()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(CBgStart, CBgEnd))),
    ) {
        // Conteúdo principal com safe drawing padding
        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                GameHeader(
                    questionIndex  = uiState.questionIndex,
                    totalQuestions = uiState.params.questionsPerSession,
                    timeProgress   = uiState.timeProgress,
                    onBack         = onBack,
                )

                Spacer(modifier = Modifier.height(24.dp))

                QuestionCard(
                    question = uiState.question,
                    combo    = uiState.combo,
                    onQuestionMarkPositioned = { center, radius ->
                        questionMarkCenter = center
                        questionMarkRadius = radius
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                )

                Spacer(modifier = Modifier.weight(1f))

                ColorAnswerGrid(
                    options      = uiState.question.options,
                    correctColor = uiState.question.correct,
                    answerState  = uiState.answerState,
                    tappedIndex  = uiState.tappedOptionIndex,
                    isFlying     = isFlying,
                    onOptionTap  = ::handleButtonTap,
                    onButtonPositioned = { idx, center, radius ->
                        buttonCenters[idx] = center
                        buttonRadii[idx]   = radius
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 28.dp),
                )
            }

            // Toasts de bônus
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 240.dp, start = 24.dp, end = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                AnimatedVisibility(
                    visible = uiState.showPersistenceBonus,
                    enter   = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit    = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                ) { PersistenceBonusToast() }

                AnimatedVisibility(
                    visible = uiState.showComboBonus,
                    enter   = scaleIn(initialScale = 0.7f) + fadeIn(),
                    exit    = scaleOut() + fadeOut(),
                ) { ComboBonusToast() }
            }
        }

        // ── Blob voador — Canvas na camada raiz (sem safe padding)
        // positionInRoot() e Canvas compartilham o mesmo (0,0) = topo da tela
        if (isFlying) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = flyingX.value
                val cy = flyingY.value
                val r  = flyingRadius.value
                val bodyPath = splatPath(cx = cx, cy = cy, r = r)

                // Aura / glow
                drawPath(
                    path = splatPath(cx = cx, cy = cy, r = r * 1.40f),
                    color = flyingColor.copy(alpha = 0.22f),
                )

                // Corpo principal
                drawPath(
                    path = bodyPath,
                    color = flyingColor,
                )

                // Borda escura para destacar o contorno orgânico
                drawPath(
                    path = bodyPath,
                    color = ColorMixer.darken(flyingColor, 0.35f).copy(alpha = 0.85f),
                    style = Stroke(width = 3.dp.toPx()),
                )

                // Especular — destaque luminoso no canto superior-esquerdo
                drawPath(
                    path = splatPath(cx = cx - r * 0.26f, cy = cy - r * 0.30f, r = r * 0.34f),
                    color = Color.White.copy(alpha = 0.38f),
                )
            }
        }
    }
}

// ── Header ─────────────────────────────────────────────────────────────────────
@Composable
private fun GameHeader(
    questionIndex: Int,
    totalQuestions: Int,
    timeProgress: Float,
    onBack: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(Color.Black.copy(alpha = 0.18f))
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 20.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f))
                        .border(1.dp, Color.White.copy(alpha = 0.35f), CircleShape)
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp),
                    )
                }

                Text(
                    text = "Mistura de Cores",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = TextStyle(shadow = Shadow(Color.Black.copy(alpha = 0.3f), offset = Offset(0f, 2f))),
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.18f))
                        .border(1.5.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = "${questionIndex + 1}/$totalQuestions",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Timer,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                GameProgressBar(
                    progress      = timeProgress,
                    colors        = timerProgressColors(timeProgress),
                    height        = 10.dp,
                    animationSpec = tween(durationMillis = 80, easing = LinearEasing),
                    modifier      = Modifier.weight(1f),
                )
            }
        }
    }
}

// ── QuestionCard ───────────────────────────────────────────────────────────────
@Composable
private fun QuestionCard(
    question: ColorQuestion,
    combo: Int,
    onQuestionMarkPositioned: (Offset, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(CCardShadow)
                .heightIn(min = 130.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(CCardBg)
                .padding(horizontal = 20.dp, vertical = 28.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = questionLabel(question),
                    color = CTextDark.copy(alpha = 0.55f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp,
                )

                ColorOperationRow(
                    question = question,
                    onQuestionMarkPositioned = onQuestionMarkPositioned,
                )

                if (combo > 0) {
                    ComboIndicator(combo = combo)
                }
            }
        }
    }
}

private fun questionLabel(question: ColorQuestion): String = when (question) {
    is ColorQuestion.Direct  -> "QUAL COR RESULTA DA MISTURA?"
    is ColorQuestion.Inverse -> "QUAL COR ENTROU NA MISTURA?"
    is ColorQuestion.Middle  -> "QUAL FOI A COR DO MEIO?"
}

private fun splatPath(cx: Float, cy: Float, r: Float): Path {
    val contour = floatArrayOf(1.00f, 1.14f, 0.86f, 1.10f, 0.78f, 1.08f, 0.92f, 1.15f, 0.84f, 1.06f)
    val count = contour.size
    val pts = Array(count) { i ->
        val angle = ((2.0 * PI * i / count) - (PI / 2.0)).toFloat()
        val dist = r * contour[i]
        Offset(
            x = cx + cos(angle) * dist,
            y = cy + sin(angle) * dist,
        )
    }

    fun tangent(index: Int): Offset {
        val prev = pts[(index - 1 + count) % count]
        val next = pts[(index + 1) % count]
        val smooth = 0.23f
        return Offset((next.x - prev.x) * smooth, (next.y - prev.y) * smooth)
    }

    return Path().apply {
        moveTo(pts[0].x, pts[0].y)
        for (i in 0 until count) {
            val curr = pts[i]
            val next = pts[(i + 1) % count]
            val t0 = tangent(i)
            val t1 = tangent((i + 1) % count)
            cubicTo(
                curr.x + t0.x,
                curr.y + t0.y,
                next.x - t1.x,
                next.y - t1.y,
                next.x,
                next.y,
            )
        }
        close()
    }
}

@Composable
private fun ColorOperationRow(
    question: ColorQuestion,
    onQuestionMarkPositioned: (Offset, Float) -> Unit,
) {
    when (question) {
        is ColorQuestion.Direct -> Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ColorBlob(color = question.colorA)
            OperatorText("+")
            ColorBlob(color = question.colorB)
            OperatorText("=")
            QuestionBlob(onPositioned = onQuestionMarkPositioned)
        }

        is ColorQuestion.Inverse -> Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            QuestionBlob(onPositioned = onQuestionMarkPositioned)
            OperatorText("+")
            ColorBlob(color = question.knownColor)
            OperatorText("=")
            ColorBlob(color = question.result)
        }

        is ColorQuestion.Middle -> Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ColorBlob(color = question.start)
            OperatorText("→")
            QuestionBlob(onPositioned = onQuestionMarkPositioned)
            OperatorText("→")
            ColorBlob(color = question.end)
        }
    }
}

@Composable
private fun ColorBlob(color: Color, blobSizeDp: Int = 68) {
    Box(
        modifier = Modifier
            .size(blobSizeDp.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val baseR = minOf(size.width, size.height) * 0.40f
            val blob = splatPath(cx = cx, cy = cy, r = baseR)

            drawPath(
                path = splatPath(cx = cx, cy = cy, r = baseR * 1.12f),
                color = Color.Black.copy(alpha = 0.14f),
            )
            drawPath(path = blob, color = color)
            drawPath(
                path = blob,
                color = ColorMixer.darken(color, 0.30f),
                style = Stroke(width = 3.dp.toPx()),
            )
            drawPath(
                path = splatPath(cx = cx - baseR * 0.24f, cy = cy - baseR * 0.28f, r = baseR * 0.34f),
                color = Color.White.copy(alpha = 0.36f),
            )
        }
    }
}

@Composable
private fun QuestionBlob(
    blobSizeDp: Int = 68,
    onPositioned: (Offset, Float) -> Unit,
) {
    Box(
        modifier = Modifier
            .size(blobSizeDp.dp)
            .onGloballyPositioned { coords ->
                val pos = coords.positionInRoot()
                val w   = coords.size.width
                val h   = coords.size.height
                onPositioned(
                    Offset(pos.x + w / 2f, pos.y + h / 2f),
                    minOf(w, h) / 2f,
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val baseR = minOf(size.width, size.height) * 0.40f
            val blob = splatPath(cx = cx, cy = cy, r = baseR)

            drawPath(
                path = splatPath(cx = cx, cy = cy, r = baseR * 1.10f),
                color = CTextDark.copy(alpha = 0.08f),
            )
            drawPath(path = blob, color = CCardShadow)
            drawPath(
                path = blob,
                color = CTextDark.copy(alpha = 0.30f),
                style = Stroke(width = 2.5.dp.toPx()),
            )
            drawPath(
                path = splatPath(cx = cx - baseR * 0.24f, cy = cy - baseR * 0.28f, r = baseR * 0.34f),
                color = Color.White.copy(alpha = 0.30f),
            )
        }
        Text(
            text = "?",
            color = CTextDark.copy(alpha = 0.45f),
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

@Composable
private fun OperatorText(symbol: String) {
    Text(
        text = symbol,
        color = CTextDark.copy(alpha = 0.5f),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun ComboIndicator(combo: Int) {
    val scale by animateFloatAsState(
        targetValue = if (combo > 0) 1f else 0f,
        animationSpec = tween(280, easing = FastOutSlowInEasing),
        label = "combo_scale",
    )
    Row(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.horizontalGradient(listOf(CComboStart, CComboEnd)))
            .padding(horizontal = 14.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Rounded.LocalFireDepartment,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "x$combo",
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp,
        )
    }
}

// ── AnswerGrid ─────────────────────────────────────────────────────────────────
@Composable
private fun ColorAnswerGrid(
    options: List<Color>,
    correctColor: Color,
    answerState: MisturaAnswerState,
    tappedIndex: Int,
    isFlying: Boolean,
    onOptionTap: (Int, Color) -> Unit,
    onButtonPositioned: (Int, Offset, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        for (row in 0..1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                for (col in 0..1) {
                    val index = row * 2 + col
                    if (index < options.size) {
                        val color = options[index]
                        val isCorrectValue = color == correctColor

                        val buttonState = when {
                            answerState == MisturaAnswerState.IDLE -> ColorButtonState.IDLE
                            index == tappedIndex && answerState == MisturaAnswerState.WRONG -> ColorButtonState.WRONG
                            isCorrectValue && answerState != MisturaAnswerState.IDLE -> ColorButtonState.CORRECT
                            else -> ColorButtonState.IDLE
                        }

                        val pulseScale by animateFloatAsState(
                            targetValue = if (buttonState == ColorButtonState.CORRECT) 1.06f else 1f,
                            animationSpec = tween(200, easing = FastOutSlowInEasing),
                            label = "pulse_$index",
                        )

                        ColorAnswerButton(
                            answerColor = color,
                            letter      = optionLetters[index],
                            visualState = buttonState,
                            scale       = pulseScale,
                            enabled     = answerState == MisturaAnswerState.IDLE && !isFlying,
                            onClick     = { onOptionTap(index, color) },
                            onPositioned = { center, radius ->
                                onButtonPositioned(index, center, radius)
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

private enum class ColorButtonState { IDLE, CORRECT, WRONG }

@Composable
private fun ColorAnswerButton(
    answerColor: Color,
    letter: String,
    visualState: ColorButtonState,
    scale: Float,
    enabled: Boolean,
    onClick: () -> Unit,
    onPositioned: (Offset, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activeColors = when (visualState) {
        ColorButtonState.CORRECT -> ColorsCorrect
        ColorButtonState.WRONG   -> ColorsWrong
        ColorButtonState.IDLE    -> ButtonColors(
            faceTop = ColorMixer.lighten(answerColor, 0.15f),
            faceBtm = answerColor,
            shadow  = ColorMixer.darken(answerColor, 0.25f),
        )
    }

    Box(
        modifier = modifier
            .scale(scale)
            .heightIn(min = 88.dp)
            .onGloballyPositioned { coords ->
                val pos = coords.positionInRoot()
                val w   = coords.size.width
                val h   = coords.size.height
                onPositioned(
                    Offset(pos.x + w / 2f, pos.y + h / 2f),
                    minOf(w, h) / 2f,
                )
            },
    ) {
        // Camada outer/shadow 3D
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 88.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(activeColors.shadow)
                .border(3.dp, Color.White, RoundedCornerShape(18.dp)),
        )

        // Camada face com gradiente + reflexo diagonal
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 84.dp)
                .padding(bottom = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.verticalGradient(listOf(activeColors.faceTop, activeColors.faceBtm)))
                .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier),
        ) {
            // Reflexo diagonal trapezoidal — padrão do design system
            Canvas(modifier = Modifier.matchParentSize()) {
                val alpha = 0.35f
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
                    .heightIn(min = 84.dp)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
            ) {
                // Badge com letra no canto superior esquerdo
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.30f))
                        .align(Alignment.TopStart),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = letter,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                    )
                }

                // Ícone de feedback no canto inferior direito
                when (visualState) {
                    ColorButtonState.CORRECT -> Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(22.dp)
                            .align(Alignment.BottomEnd),
                    )
                    ColorButtonState.WRONG -> Icon(
                        imageVector = Icons.Rounded.Cancel,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(22.dp)
                            .align(Alignment.BottomEnd),
                    )
                    else -> Unit
                }
            }
        }
    }
}

// ── Bonus toasts — idênticos à Calculadora Veloz ───────────────────────────────
@Composable
private fun PersistenceBonusToast() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1A1A18))
            .border(3.dp, Color(0xFFFFE066), RoundedCornerShape(16.dp))
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector = Icons.Rounded.Shield,
            contentDescription = null,
            tint = Color(0xFFFFE066),
            modifier = Modifier.size(22.dp),
        )
        Column {
            Text(
                text = "Bonus de Persistencia!",
                color = Color(0xFFFFE066),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
            )
            Text(
                text = "+50% de energia",
                color = Color.White.copy(alpha = 0.75f),
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun ComboBonusToast() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.horizontalGradient(listOf(CComboStart, CComboEnd)))
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector = Icons.Rounded.LocalFireDepartment,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp),
        )
        Column {
            Text(
                text = "Combo x2!",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
            )
            Text(
                text = "+20% de energia",
                color = Color.White.copy(alpha = 0.85f),
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
            )
        }
    }
}

// ── Haptic ─────────────────────────────────────────────────────────────────────
@Suppress("DEPRECATION")
private fun triggerHaptic(context: android.content.Context) {
    try {
        val vibrator = context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? Vibrator
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(40)
            }
        }
    } catch (_: Exception) {}
}
