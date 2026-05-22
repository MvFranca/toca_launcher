package com.example.domo.ui.minigame.calculadora

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.domo.ui.minigame.calculadora.model.SessionResult

// ── Paleta game UI ──────────────────────────────────────────────────────────────
private val CBgStart      = Color(0xFFA659FE)
private val CBgEnd        = Color(0xFF6F53FD)
private val CGreenFace    = Color(0xFF67EB00)
private val CGreenShadow  = Color(0xFF4EC307)
private val CBlueFace     = Color(0xFF4CDAFE)
private val CBlueShadow   = Color(0xFF09B9FF)
private val CPinkFace     = Color(0xFFFC8AFF)
private val CPinkShadow   = Color(0xFFDA57F0)
private val CYellowFace   = Color(0xFFFFDD17)
private val CYellowShadow = Color(0xFFFFB213)
private val CCardBg       = Color(0xFFFFFEFF)
private val CCardShadow   = Color(0xFFD1D8FF)
private val CTextDark     = Color(0xFF1A1A18)
private val CCorrect      = Color(0xFF1D9E75)
private val CCorrectDark  = Color(0xFF15805E)
private val CWrong        = Color(0xFFE24B4A)
private val CWrongDark    = Color(0xFFC4193E)
private val CComboStart   = Color(0xFFFF6B00)
private val CComboEnd     = Color(0xFFFF8C00)

// Cores face/sombra dos botões A/B/C/D
private val optionFaceColors   = listOf(CBlueFace, CPinkFace, CYellowFace, CGreenFace)
private val optionShadowColors = listOf(CBlueShadow, CPinkShadow, CYellowShadow, CGreenShadow)
private val optionLetters      = listOf("A", "B", "C", "D")

@Composable
fun CalculadoraVelozScreen(
    onNavigateToResult: (SessionResult) -> Unit,
    onBack: () -> Unit,
    viewModel: CalculadoraVelozViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) { viewModel.startSession() }

    LaunchedEffect(uiState.sessionResult) {
        uiState.sessionResult?.let { onNavigateToResult(it) }
    }

    LaunchedEffect(uiState.answerState) {
        if (uiState.answerState == AnswerState.WRONG) triggerHaptic(context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(CBgStart, CBgEnd)))
            .safeDrawingPadding(),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GameHeader(
                questionIndex = uiState.questionIndex,
                totalQuestions = uiState.params.questionsPerSession,
                timeProgress = uiState.timeProgress,
                onBack = onBack,
            )

            Spacer(modifier = Modifier.height(28.dp))

            QuestionCard(
                displayText = uiState.question.displayText,
                combo = uiState.combo,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            )

            Spacer(modifier = Modifier.weight(1f))

            AnswerGrid(
                options = uiState.question.options,
                correctAnswer = uiState.question.correctAnswer,
                answerState = uiState.answerState,
                tappedIndex = uiState.tappedOptionIndex,
                onOptionTap = viewModel::onOptionSelected,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 28.dp),
            )
        }

        // Bonus toasts overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 240.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            AnimatedVisibility(
                visible = uiState.showPersistenceBonus,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            ) {
                PersistenceBonusToast()
            }
            AnimatedVisibility(
                visible = uiState.showComboBonus,
                enter = scaleIn(initialScale = 0.7f) + fadeIn(),
                exit = scaleOut() + fadeOut(),
            ) {
                ComboBonusToast()
            }
        }
    }
}

// ── Header roxo com timer ───────────────────────────────────────────────────────
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
                // Botão voltar
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
                    text = "Calculadora Veloz",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = TextStyle(shadow = Shadow(Color.Black.copy(alpha = 0.3f), offset = Offset(0f, 2f))),
                )

                // Contador pill
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

            TimerRow(progress = timeProgress)
        }
    }
}

@Composable
private fun TimerRow(progress: Float) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 80, easing = LinearEasing),
        label = "timer",
    )
    val barColors = when {
        animatedProgress > 0.55f -> listOf(Color(0xFF1D9E75), Color(0xFF34D399))
        animatedProgress > 0.28f -> listOf(Color(0xFFF59E0B), Color(0xFFFCD34D))
        else -> listOf(Color(0xFFE24B4A), Color(0xFFFF6B6B))
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Rounded.Timer,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.85f),
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(10.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.Black.copy(alpha = 0.20f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress.coerceIn(0f, 1f))
                    .height(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Brush.horizontalGradient(barColors)),
            )
        }
    }
}

// ── QuestionCard com sombra colorida ───────────────────────────────────────────
@Composable
private fun QuestionCard(displayText: String, combo: Int, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        // Sombra colorida
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(CCardShadow)
                .heightIn(min = 120.dp),
        )
        // Card branco
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(CCardBg)
                .padding(horizontal = 28.dp, vertical = 36.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = displayText,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CTextDark,
                    textAlign = TextAlign.Center,
                    lineHeight = 42.sp,
                )
                if (combo > 0) {
                    Spacer(modifier = Modifier.height(18.dp))
                    ComboIndicator(combo = combo)
                }
            }
        }
    }
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

// ── Grid de respostas 3D ───────────────────────────────────────────────────────
@Composable
private fun AnswerGrid(
    options: List<Int>,
    correctAnswer: Int,
    answerState: AnswerState,
    tappedIndex: Int,
    onOptionTap: (Int, Int) -> Unit,
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
                        val value = options[index]
                        val isCorrectValue = value == correctAnswer

                        val buttonState = when {
                            answerState == AnswerState.IDLE -> ButtonVisualState.IDLE
                            index == tappedIndex && answerState == AnswerState.WRONG -> ButtonVisualState.WRONG
                            isCorrectValue && answerState != AnswerState.IDLE -> ButtonVisualState.CORRECT
                            else -> ButtonVisualState.IDLE
                        }

                        val pulseScale by animateFloatAsState(
                            targetValue = if (buttonState == ButtonVisualState.CORRECT) 1.06f else 1f,
                            animationSpec = tween(200, easing = FastOutSlowInEasing),
                            label = "pulse_$index",
                        )

                        AnswerButton(
                            value = value,
                            letter = optionLetters[index],
                            faceColor = optionFaceColors[index],
                            shadowColor = optionShadowColors[index],
                            visualState = buttonState,
                            scale = pulseScale,
                            enabled = answerState == AnswerState.IDLE,
                            onClick = { onOptionTap(index, value) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

private enum class ButtonVisualState { IDLE, CORRECT, WRONG }

@Composable
private fun AnswerButton(
    value: Int,
    letter: String,
    faceColor: Color,
    shadowColor: Color,
    visualState: ButtonVisualState,
    scale: Float,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (activeFace, activeShadow) = when (visualState) {
        ButtonVisualState.CORRECT -> CCorrect to CCorrectDark
        ButtonVisualState.WRONG   -> CWrong to CWrongDark
        ButtonVisualState.IDLE    -> faceColor to shadowColor
    }

    Box(
        modifier = modifier
            .scale(scale)
            .heightIn(min = 84.dp),
    ) {
        // Camada sombra (3D base)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 84.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(activeShadow)
                .border(2.dp, Color.White, RoundedCornerShape(18.dp)),
        )
        // Camada face
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp)
                .padding(bottom = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(activeFace)
                .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
                .padding(horizontal = 12.dp, vertical = 14.dp),
        ) {
            // Label de letra (canto superior esquerdo)
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

            // Valor centralizado
            Text(
                text = value.toString(),
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center),
                style = TextStyle(shadow = Shadow(Color.Black.copy(alpha = 0.2f), offset = Offset(0f, 2f))),
            )

            // Ícone de feedback no canto direito
            when (visualState) {
                ButtonVisualState.CORRECT -> Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(22.dp)
                        .align(Alignment.BottomEnd),
                )
                ButtonVisualState.WRONG -> Icon(
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

// ── Bonus toasts ───────────────────────────────────────────────────────────────
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
