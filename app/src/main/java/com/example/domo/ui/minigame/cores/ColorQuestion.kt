package com.example.domo.ui.minigame.cores

import androidx.compose.ui.graphics.Color

sealed class ColorQuestion {
    abstract val correct: Color
    abstract val options: List<Color>

    /** Duas cores entram → qual cor sai? */
    data class Direct(
        val colorA: Color,
        val colorB: Color,
        val ratio: Float,
        override val correct: Color,
        override val options: List<Color>,
    ) : ColorQuestion()

    /** Resultado + cor conhecida → qual cor entrou? */
    data class Inverse(
        val result: Color,
        val knownColor: Color,
        val ratio: Float,
        override val correct: Color,
        override val options: List<Color>,
    ) : ColorQuestion()

    /** Dado início e fim → qual foi a cor do meio? */
    data class Middle(
        val start: Color,
        val end: Color,
        override val correct: Color,
        override val options: List<Color>,
    ) : ColorQuestion()
}
