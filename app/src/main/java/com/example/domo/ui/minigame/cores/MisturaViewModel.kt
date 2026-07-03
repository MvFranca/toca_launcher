package com.example.domo.ui.minigame.cores

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.domo.ui.minigame.cores.model.ColorSessionResult
import com.example.domo.ui.minigame.cores.model.CoresDifficultyParams
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class MisturaAnswerState { IDLE, CORRECT, WRONG }

data class MisturaUiState(
    val params: CoresDifficultyParams = CoresDifficultyManager.paramsForLevel(1),
    val question: ColorQuestion = ColorQuestion.Direct(
        colorA = Color.Gray, colorB = Color.Gray, ratio = 0.5f,
        correct = Color.Gray, options = emptyList(),
    ),
    val questionIndex: Int = 0,
    val timeProgress: Float = 1f,
    val answerState: MisturaAnswerState = MisturaAnswerState.IDLE,
    val tappedOptionIndex: Int = -1,
    val combo: Int = 0,
    val showPersistenceBonus: Boolean = false,
    val showComboBonus: Boolean = false,
    val sessionResult: ColorSessionResult? = null,
    val isSessionFinished: Boolean = false,
)

class MisturaViewModel(application: Application) : AndroidViewModel(application) {

    private val difficultyManager = CoresDifficultyManager(application)

    private val _uiState = MutableStateFlow(MisturaUiState())
    val uiState: StateFlow<MisturaUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    private var hits = 0
    private var misses = 0
    private var totalRetries = 0
    private var persistenceBonusTriggered = false
    private var comboTriggered = false
    private var questionStartTimeMs = 0L
    private val questionTimesMs = mutableListOf<Long>()
    private var currentQuestionHadError = false
    private var combo = 0

    fun startSession() {
        val params = difficultyManager.currentParams()
        hits = 0
        misses = 0
        totalRetries = 0
        persistenceBonusTriggered = false
        comboTriggered = false
        questionTimesMs.clear()
        currentQuestionHadError = false
        combo = 0

        val firstQuestion = ColorMixer.generateQuestion(params)
        _uiState.update {
            MisturaUiState(
                params = params,
                question = firstQuestion,
                questionIndex = 0,
                timeProgress = 1f,
                answerState = MisturaAnswerState.IDLE,
                combo = 0,
            )
        }
        questionStartTimeMs = System.currentTimeMillis()
        startTimer(params.timeLimitSeconds)
    }

    fun onOptionSelected(optionIndex: Int, selectedColor: Color) {
        val state = _uiState.value
        if (state.answerState != MisturaAnswerState.IDLE) return

        timerJob?.cancel()
        val elapsed = System.currentTimeMillis() - questionStartTimeMs
        questionTimesMs.add(elapsed)

        val isCorrect = selectedColor == state.question.correct

        if (isCorrect) {
            hits++
            if (currentQuestionHadError) persistenceBonusTriggered = true
            combo++
            if (combo >= 5) comboTriggered = true

            _uiState.update {
                it.copy(
                    answerState = MisturaAnswerState.CORRECT,
                    tappedOptionIndex = optionIndex,
                    combo = combo,
                    showPersistenceBonus = currentQuestionHadError,
                    showComboBonus = combo >= 5,
                )
            }
            viewModelScope.launch {
                delay(600)
                hideBonusToasts()
                advanceQuestion()
            }
        } else {
            misses++
            totalRetries++
            currentQuestionHadError = true
            combo = 0

            _uiState.update {
                it.copy(
                    answerState = MisturaAnswerState.WRONG,
                    tappedOptionIndex = optionIndex,
                    combo = 0,
                )
            }
            viewModelScope.launch {
                delay(400)
                _uiState.update { it.copy(answerState = MisturaAnswerState.CORRECT, tappedOptionIndex = -1) }
                delay(600)
                resetToIdle()
                startTimer(state.params.timeLimitSeconds)
            }
        }
    }

    private fun hideBonusToasts() {
        _uiState.update { it.copy(showPersistenceBonus = false, showComboBonus = false) }
    }

    private fun resetToIdle() {
        _uiState.update {
            it.copy(answerState = MisturaAnswerState.IDLE, tappedOptionIndex = -1, timeProgress = 1f)
        }
        questionStartTimeMs = System.currentTimeMillis()
    }

    private fun advanceQuestion() {
        val state = _uiState.value
        val nextIndex = state.questionIndex + 1
        if (nextIndex >= state.params.questionsPerSession) {
            finishSession()
            return
        }
        currentQuestionHadError = false
        val nextQuestion = ColorMixer.generateQuestion(state.params)
        _uiState.update {
            it.copy(
                question = nextQuestion,
                questionIndex = nextIndex,
                answerState = MisturaAnswerState.IDLE,
                tappedOptionIndex = -1,
                timeProgress = 1f,
                showPersistenceBonus = false,
                showComboBonus = false,
            )
        }
        questionStartTimeMs = System.currentTimeMillis()
        startTimer(state.params.timeLimitSeconds)
    }

    private fun startTimer(timeLimitSeconds: Int) {
        timerJob?.cancel()
        val totalMs = timeLimitSeconds * 1000L
        timerJob = viewModelScope.launch {
            val startMs = System.currentTimeMillis()
            while (true) {
                delay(50)
                val elapsed = System.currentTimeMillis() - startMs
                val progress = 1f - (elapsed.toFloat() / totalMs).coerceIn(0f, 1f)
                _uiState.update { it.copy(timeProgress = progress) }
                if (progress <= 0f) {
                    onTimeUp()
                    break
                }
            }
        }
    }

    private fun onTimeUp() {
        val elapsed = System.currentTimeMillis() - questionStartTimeMs
        questionTimesMs.add(elapsed)
        misses++
        combo = 0
        _uiState.update { it.copy(combo = 0) }
        viewModelScope.launch {
            delay(300)
            val state = _uiState.value
            val nextIndex = state.questionIndex + 1
            if (nextIndex >= state.params.questionsPerSession) {
                finishSession()
            } else {
                currentQuestionHadError = false
                val nextQuestion = ColorMixer.generateQuestion(state.params)
                _uiState.update {
                    it.copy(
                        question = nextQuestion,
                        questionIndex = nextIndex,
                        answerState = MisturaAnswerState.IDLE,
                        tappedOptionIndex = -1,
                        timeProgress = 1f,
                    )
                }
                questionStartTimeMs = System.currentTimeMillis()
                startTimer(state.params.timeLimitSeconds)
            }
        }
    }

    private fun finishSession() {
        timerJob?.cancel()
        val params = _uiState.value.params
        val averageMs = if (questionTimesMs.isEmpty()) 0f else questionTimesMs.average().toFloat()

        val result = buildSessionResult(params, averageMs / 1000f)
        difficultyManager.calibrate(result, params)

        _uiState.update {
            it.copy(isSessionFinished = true, sessionResult = result)
        }
    }

    private fun buildSessionResult(params: CoresDifficultyParams, averageTimeSecs: Float): ColorSessionResult {
        val total = params.questionsPerSession
        val hitRate = hits.toFloat() / total.toFloat()

        val baseEnergy = when (params.level) {
            1 -> 10; 2 -> 15; 3 -> 20; 4 -> 30; else -> 40
        }

        var energy = baseEnergy * hitRate
        if (persistenceBonusTriggered) energy *= 1.5f
        if (comboTriggered) energy *= 1.2f

        val energyEarned = energy.toInt()

        return ColorSessionResult(
            hits = hits,
            misses = misses,
            retries = totalRetries,
            persistenceBonusTriggered = persistenceBonusTriggered,
            comboTriggered = comboTriggered,
            averageTimeSeconds = averageTimeSecs,
            energyEarned = energyEarned,
            xpEarned = energyEarned,
        )
    }
}
