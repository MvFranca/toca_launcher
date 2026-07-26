package com.example.domo.feature.learn.domain.calculadora

data class Question(
    val displayText: String,
    val correctAnswer: Int,
    val options: List<Int>,
)
