package com.example.domo.ui.minigame.calculadora

import com.example.domo.ui.minigame.calculadora.model.DifficultyParams
import com.example.domo.ui.minigame.calculadora.model.Operation
import com.example.domo.ui.minigame.calculadora.model.Question
import com.example.domo.ui.minigame.calculadora.model.QuestionFormat
import kotlin.random.Random

class QuestionGenerator {

    fun generate(params: DifficultyParams): Question {
        val format = params.questionFormats.random()
        return when (format) {
            QuestionFormat.RESULTADO -> generateResultado(params)
            QuestionFormat.OPERANDO_FALTANTE -> generateOperandoFaltante(params)
            QuestionFormat.COMPARACAO -> generateComparacao(params)
        }
    }

    private fun generateResultado(params: DifficultyParams): Question {
        val operation = params.operations.random()
        val (a, b, result) = generateOperands(operation, params)
        val display = "Quanto é $a ${operation.symbol()} $b?"
        return buildQuestion(display, result, params.level)
    }

    private fun generateOperandoFaltante(params: DifficultyParams): Question {
        val operation = params.operations.random()
        val (a, b, result) = generateOperands(operation, params)
        val hideFirst = Random.nextBoolean()
        val (display, answer) = if (hideFirst) {
            "__ ${operation.symbol()} $b = $result" to a
        } else {
            "$a ${operation.symbol()} __ = $result" to b
        }
        return buildQuestion(display, answer, params.level)
    }

    private fun generateComparacao(params: DifficultyParams): Question {
        val safeOps = params.operations.filter { it != Operation.DIVISAO }
            .takeIf { it.isNotEmpty() } ?: params.operations
        val op1 = safeOps.random()
        val op2 = safeOps.random()
        val (a1, b1, r1) = generateOperands(op1, params)
        val (a2, b2, r2) = generateOperands(op2, params)
        val display = "Qual é maior?\n$a1 ${op1.symbol()} $b1  ou  $a2 ${op2.symbol()} $b2"
        val correctAnswer = maxOf(r1, r2)
        return buildQuestion(display, correctAnswer, params.level, seedOptions = listOf(r1, r2))
    }

    private fun generateOperands(
        operation: Operation,
        params: DifficultyParams,
    ): Triple<Int, Int, Int> {
        val min = params.rangeMin.coerceAtLeast(1)
        val max = params.rangeMax.coerceAtLeast(min + 1)
        return when (operation) {
            Operation.SOMA -> {
                val a = Random.nextInt(min, max + 1)
                val b = Random.nextInt(min, max + 1)
                Triple(a, b, a + b)
            }
            Operation.SUBTRACAO -> {
                val a = Random.nextInt(min, max + 1)
                val b = Random.nextInt(min, a + 1)
                Triple(a, b, a - b)
            }
            Operation.MULTIPLICACAO -> {
                val tableLimit = if (params.level <= 3) 5 else 10
                val a = Random.nextInt(1, tableLimit + 1)
                val b = Random.nextInt(1, tableLimit + 1)
                Triple(a, b, a * b)
            }
            Operation.DIVISAO -> {
                val divisor = Random.nextInt(2, minOf(max, 10) + 1)
                val maxQuotient = (max / divisor).coerceAtLeast(1)
                val quotient = Random.nextInt(1, maxQuotient + 1)
                val dividend = divisor * quotient
                Triple(dividend, divisor, quotient)
            }
        }
    }

    private fun buildQuestion(
        display: String,
        correct: Int,
        level: Int,
        seedOptions: List<Int>? = null,
    ): Question {
        val options = buildOptions(correct, level, seedOptions)
        return Question(displayText = display, correctAnswer = correct, options = options)
    }

    private fun buildOptions(correct: Int, level: Int, seedOptions: List<Int>?): List<Int> {
        val allowNegative = level >= 5
        val distractors = mutableSetOf<Int>()

        seedOptions?.forEach { v -> if (v != correct) distractors.add(v) }

        val offsets = listOf(1, -1, 2, -2, 3, -3).shuffled()
        for (offset in offsets) {
            if (distractors.size >= 3) break
            val candidate = correct + offset
            if (candidate != correct && (allowNegative || candidate >= 0)) {
                distractors.add(candidate)
            }
        }

        var extra = 4
        while (distractors.size < 3) {
            val candidate = correct + extra
            if (candidate != correct && (allowNegative || candidate >= 0)) distractors.add(candidate)
            extra++
        }

        return (distractors.take(3) + correct).shuffled()
    }
}

private fun Operation.symbol(): String = when (this) {
    Operation.SOMA -> "+"
    Operation.SUBTRACAO -> "-"
    Operation.MULTIPLICACAO -> "×"
    Operation.DIVISAO -> "÷"
}
