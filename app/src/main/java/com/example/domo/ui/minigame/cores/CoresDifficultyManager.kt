package com.example.domo.ui.minigame.cores

import android.content.Context
import com.example.domo.ui.minigame.cores.model.ColorSessionResult
import com.example.domo.ui.minigame.cores.model.CoresDifficultyParams
import com.example.domo.ui.minigame.cores.model.QuestionDirection

private const val PREFS_NAME = "learvo_minigames"
private const val KEY_LEVEL  = "difficulty_mistura_de_cores"

class CoresDifficultyManager(private val context: Context) {

    private val prefs by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun currentLevel(): Int = prefs.getInt(KEY_LEVEL, 1).coerceIn(1, 5)

    fun currentParams(): CoresDifficultyParams = paramsForLevel(currentLevel())

    fun calibrate(result: ColorSessionResult, params: CoresDifficultyParams): CoresDifficultyParams {
        val total = result.hits + result.misses
        if (total == 0) return params

        val hitRate = result.hits.toFloat() / total.toFloat()
        val timeFraction = result.averageTimeSeconds / params.timeLimitSeconds.toFloat()

        val newLevel = when {
            hitRate > 0.80f && timeFraction < 0.60f -> (params.level + 1).coerceAtMost(5)
            hitRate < 0.50f -> (params.level - 1).coerceAtLeast(1)
            else -> params.level
        }

        prefs.edit().putInt(KEY_LEVEL, newLevel).apply()
        return paramsForLevel(newLevel)
    }

    companion object {
        fun paramsForLevel(level: Int): CoresDifficultyParams = when (level) {
            1 -> CoresDifficultyParams(
                level = 1,
                questionsPerSession = 8,
                timeLimitSeconds = 20,
                distractorHueDelta = 60f,
                allowedDirections = setOf(QuestionDirection.DIRECT),
                paletteSize = 5,
            )
            2 -> CoresDifficultyParams(
                level = 2,
                questionsPerSession = 8,
                timeLimitSeconds = 18,
                distractorHueDelta = 45f,
                allowedDirections = setOf(QuestionDirection.DIRECT),
                paletteSize = 6,
            )
            3 -> CoresDifficultyParams(
                level = 3,
                questionsPerSession = 10,
                timeLimitSeconds = 15,
                distractorHueDelta = 30f,
                allowedDirections = setOf(QuestionDirection.DIRECT, QuestionDirection.MIDDLE),
                paletteSize = 7,
            )
            4 -> CoresDifficultyParams(
                level = 4,
                questionsPerSession = 10,
                timeLimitSeconds = 12,
                distractorHueDelta = 20f,
                allowedDirections = setOf(
                    QuestionDirection.DIRECT,
                    QuestionDirection.MIDDLE,
                    QuestionDirection.INVERSE,
                ),
                paletteSize = 8,
            )
            else -> CoresDifficultyParams(
                level = 5,
                questionsPerSession = 12,
                timeLimitSeconds = 10,
                distractorHueDelta = 15f,
                allowedDirections = setOf(
                    QuestionDirection.DIRECT,
                    QuestionDirection.MIDDLE,
                    QuestionDirection.INVERSE,
                ),
                paletteSize = 10,
            )
        }
    }
}
