package com.example.domo.ui.minigame.calculadora.model

enum class Operation { SOMA, SUBTRACAO, MULTIPLICACAO, DIVISAO }

enum class QuestionFormat { RESULTADO, OPERANDO_FALTANTE, COMPARACAO }

data class DifficultyParams(
    val level: Int,
    val operations: List<Operation>,
    val rangeMin: Int,
    val rangeMax: Int,
    val questionFormats: List<QuestionFormat>,
    val timeLimitSeconds: Int,
    val questionsPerSession: Int = 10,
)
