package com.example.domo.ui.minigame.calculadora

import android.content.Context
import com.example.domo.ui.minigame.calculadora.model.DifficultyParams
import com.example.domo.ui.minigame.calculadora.model.Operation
import com.example.domo.ui.minigame.calculadora.model.QuestionFormat
import com.example.domo.ui.minigame.calculadora.model.SessionResult

private const val PREFS_NAME = "learvo_minigames"
private const val KEY_LEVEL = "difficulty_calculadora_veloz"

class DifficultyManager(private val context: Context) {

    private val prefs by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun currentLevel(): Int = prefs.getInt(KEY_LEVEL, 1).coerceIn(1, 5)

    fun currentParams(): DifficultyParams = paramsForLevel(currentLevel())

    fun calibrate(result: SessionResult, params: DifficultyParams): DifficultyParams {
        val total = result.hits + result.misses
        if (total == 0) return params

        val hitRate = result.hits.toFloat() / total.toFloat()
        val timeFraction = result.averageTimeSeconds / params.timeLimitSeconds.toFloat()

        val currentLevel = params.level
        val newLevel = when {
            hitRate > 0.80f && timeFraction < 0.60f -> (currentLevel + 1).coerceAtMost(5)
            hitRate < 0.50f -> (currentLevel - 1).coerceAtLeast(1)
            else -> currentLevel
        }

        prefs.edit().putInt(KEY_LEVEL, newLevel).apply()
        return paramsForLevel(newLevel)
    }

    companion object {
        fun paramsForLevel(level: Int): DifficultyParams = when (level) {
            1 -> DifficultyParams(
                level = 1,
                operations = listOf(Operation.SOMA, Operation.SUBTRACAO),
                rangeMin = 1,
                rangeMax = 10,
                questionFormats = listOf(QuestionFormat.RESULTADO),
                timeLimitSeconds = 15,
            )
            2 -> DifficultyParams(
                level = 2,
                operations = listOf(Operation.SOMA, Operation.SUBTRACAO),
                rangeMin = 1,
                rangeMax = 20,
                questionFormats = listOf(QuestionFormat.RESULTADO),
                timeLimitSeconds = 12,
            )
            3 -> DifficultyParams(
                level = 3,
                operations = listOf(Operation.SOMA, Operation.SUBTRACAO, Operation.MULTIPLICACAO),
                rangeMin = 1,
                rangeMax = 30,
                questionFormats = listOf(QuestionFormat.RESULTADO, QuestionFormat.OPERANDO_FALTANTE),
                timeLimitSeconds = 12,
            )
            4 -> DifficultyParams(
                level = 4,
                operations = listOf(Operation.SOMA, Operation.SUBTRACAO, Operation.MULTIPLICACAO),
                rangeMin = 1,
                rangeMax = 50,
                questionFormats = listOf(QuestionFormat.RESULTADO, QuestionFormat.OPERANDO_FALTANTE),
                timeLimitSeconds = 10,
            )
            else -> DifficultyParams(
                level = 5,
                operations = listOf(
                    Operation.SOMA,
                    Operation.SUBTRACAO,
                    Operation.MULTIPLICACAO,
                    Operation.DIVISAO,
                ),
                rangeMin = 1,
                rangeMax = 100,
                questionFormats = listOf(
                    QuestionFormat.RESULTADO,
                    QuestionFormat.OPERANDO_FALTANTE,
                    QuestionFormat.COMPARACAO,
                ),
                timeLimitSeconds = 10,
            )
        }
    }
}
