package com.example.domo.ui.minigame.calculadora.model

data class Question(
    val displayText: String,
    val correctAnswer: Int,
    val options: List<Int>,
)
