package com.example.domo.feature.learn.domain.cores

enum class QuestionDirection { DIRECT, INVERSE, MIDDLE }

data class CoresDifficultyParams(
    val level: Int,
    val questionsPerSession: Int,
    val timeLimitSeconds: Int,
    val distractorHueDelta: Float,
    val allowedDirections: Set<QuestionDirection>,
    val paletteSize: Int,
)
