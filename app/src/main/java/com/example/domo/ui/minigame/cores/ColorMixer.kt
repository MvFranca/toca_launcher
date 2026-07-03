package com.example.domo.ui.minigame.cores

import androidx.compose.ui.graphics.Color
import com.example.domo.ui.minigame.cores.model.CoresDifficultyParams
import com.example.domo.ui.minigame.cores.model.QuestionDirection
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Lógica procedural de mistura e geração de perguntas/distratores.
 *
 * Mistura: blend RGB linear ponderado.
 * Distratores: deslocamento de hue no espaço HSL, mantendo saturação e
 * luminosidade para isolar a variável perceptiva conforme a dificuldade.
 *
 * Delta de hue por nível:
 *   L1=60°  L2=45°  L3=30°  L4=20°  L5=15°
 */
object ColorMixer {

    // ── Paleta base de cores primárias e secundárias ────────────────────────────
    private val BASE_PALETTE = listOf(
        Color(0xFFE63946), // Vermelho
        Color(0xFFFF9F1C), // Laranja
        Color(0xFFFFD60A), // Amarelo
        Color(0xFF06D6A0), // Verde
        Color(0xFF118AB2), // Azul
        Color(0xFF9B5DE5), // Roxo
        Color(0xFFF72585), // Rosa
        Color(0xFF3A86FF), // Azul claro
        Color(0xFFFF6B6B), // Salmão
        Color(0xFF2EC4B6), // Teal
    )

    // ── Mistura ─────────────────────────────────────────────────────────────────

    /** Blend RGB linear ponderado: ratio=0 → c1 puro; ratio=1 → c2 puro. */
    fun mix(c1: Color, c2: Color, ratio: Float = 0.5f): Color {
        val r = ratio.coerceIn(0f, 1f)
        return Color(
            red   = c1.red   * (1f - r) + c2.red   * r,
            green = c1.green * (1f - r) + c2.green * r,
            blue  = c1.blue  * (1f - r) + c2.blue  * r,
            alpha = 1f,
        )
    }

    // ── Conversão HSL ───────────────────────────────────────────────────────────

    /** Retorna FloatArray(3) com [hue 0–360, sat 0–1, lum 0–1]. */
    fun toHsl(color: Color): FloatArray {
        val r = color.red
        val g = color.green
        val b = color.blue

        val max = maxOf(r, g, b)
        val min = minOf(r, g, b)
        val delta = max - min

        val l = (max + min) / 2f

        val s = if (delta == 0f) 0f else delta / (1f - abs(2f * l - 1f))

        val h = when {
            delta == 0f -> 0f
            max == r    -> 60f * (((g - b) / delta) % 6f)
            max == g    -> 60f * (((b - r) / delta) + 2f)
            else        -> 60f * (((r - g) / delta) + 4f)
        }.let { if (it < 0f) it + 360f else it }

        return floatArrayOf(h, s, l)
    }

    /** Converte HSL [hue 0–360, sat 0–1, lum 0–1] de volta para Color. */
    fun fromHsl(h: Float, s: Float, l: Float): Color {
        val hNorm = ((h % 360f) + 360f) % 360f
        val sClamp = s.coerceIn(0f, 1f)
        val lClamp = l.coerceIn(0f, 1f)

        val c = (1f - abs(2f * lClamp - 1f)) * sClamp
        val x = c * (1f - abs((hNorm / 60f) % 2f - 1f))
        val m = lClamp - c / 2f

        val (r1, g1, b1) = when {
            hNorm < 60f  -> Triple(c, x, 0f)
            hNorm < 120f -> Triple(x, c, 0f)
            hNorm < 180f -> Triple(0f, c, x)
            hNorm < 240f -> Triple(0f, x, c)
            hNorm < 300f -> Triple(x, 0f, c)
            else         -> Triple(c, 0f, x)
        }

        return Color(
            red   = (r1 + m).coerceIn(0f, 1f),
            green = (g1 + m).coerceIn(0f, 1f),
            blue  = (b1 + m).coerceIn(0f, 1f),
            alpha = 1f,
        )
    }

    // ── Distância perceptiva ────────────────────────────────────────────────────

    /** Distância Euclidiana RGB (0–1 por canal). Máximo teórico: sqrt(3) ≈ 1.73. */
    fun colorDistance(a: Color, b: Color): Float {
        val dr = a.red   - b.red
        val dg = a.green - b.green
        val db = a.blue  - b.blue
        return sqrt(dr * dr + dg * dg + db * db)
    }

    private const val MIN_DIST_CORRECT  = 0.18f
    private const val MIN_DIST_BETWEEN  = 0.14f

    // ── Distratores ─────────────────────────────────────────────────────────────

    /**
     * Gera [count] distratores deslocando o hue em múltiplos de [deltaDegrees].
     *
     * Para cores dessaturadas (s < 0.28) o hue puro mal muda a aparência visual,
     * então também desloca a luminosidade alternadamente — garantindo que cada
     * opção pareça claramente diferente mesmo para crianças.
     *
     * Garante distância perceptiva mínima entre todas as opções.
     */
    fun generateDistractors(correct: Color, count: Int, deltaDegrees: Float): List<Color> {
        val (h, s, l) = toHsl(correct).let { Triple(it[0], it[1], it[2]) }
        val isDesaturated = s < 0.28f
        // Eleva a saturação nos distratores para evitar que tudo pareça cinza
        val targetSat = if (isDesaturated) (s + 0.30f).coerceAtMost(0.70f) else s
        val distractors = mutableListOf<Color>()
        var step = 1

        while (distractors.size < count && step <= 18) {
            val offset = deltaDegrees * step
            val lumShift = if (isDesaturated) step * 0.10f * (if (step % 2 == 0) 1f else -1f) else 0f

            for (c in listOf(
                fromHsl(h + offset, targetSat, (l + lumShift).coerceIn(0.22f, 0.76f)),
                fromHsl(h - offset, targetSat, (l - lumShift).coerceIn(0.22f, 0.76f)),
            )) {
                if (distractors.size >= count) break
                if (colorDistance(c, correct) >= MIN_DIST_CORRECT &&
                    distractors.all { colorDistance(c, it) >= MIN_DIST_BETWEEN }) {
                    distractors.add(c)
                }
            }
            step++
        }

        return distractors.take(count)
    }

    /**
     * Distratores "plausíveis": mistura outros pares da paleta e filtra por
     * distância perceptiva. Usado quando o hue-shift não gera opções distintas
     * (ex.: cor correta muito dessaturada, como resultado de cores complementares).
     */
    fun generatePlausibleDistractors(
        correct: Color,
        count: Int,
        palette: List<Color>,
        random: Random = Random,
    ): List<Color> {
        val candidates = mutableListOf<Color>()
        val ratios = listOf(0.33f, 0.5f, 0.67f)
        val shuffled = palette.shuffled(random)

        outer@ for (i in shuffled.indices) {
            for (j in shuffled.indices) {
                if (i == j) continue
                for (ratio in ratios) {
                    val c = mix(shuffled[i], shuffled[j], ratio)
                    if (colorDistance(c, correct) >= MIN_DIST_CORRECT &&
                        candidates.all { colorDistance(c, it) >= MIN_DIST_BETWEEN }) {
                        candidates.add(c)
                        if (candidates.size >= count) break@outer
                    }
                }
            }
        }

        return candidates.take(count)
    }

    // ── 3D button helpers ────────────────────────────────────────────────────────

    /** Escurece uma cor em HSL para usar como sombra do botão 3D. */
    fun darken(color: Color, factor: Float): Color {
        val (h, s, l) = toHsl(color).let { Triple(it[0], it[1], it[2]) }
        return fromHsl(h, s, (l * (1f - factor)).coerceAtLeast(0f))
    }

    /** Clareia uma cor em HSL para usar como topo do gradiente do botão 3D. */
    fun lighten(color: Color, factor: Float): Color {
        val (h, s, l) = toHsl(color).let { Triple(it[0], it[1], it[2]) }
        return fromHsl(h, s, (l + (1f - l) * factor).coerceAtMost(1f))
    }

    // ── Geração de perguntas ────────────────────────────────────────────────────

    fun generateQuestion(params: CoresDifficultyParams, random: Random = Random): ColorQuestion {
        val palette = BASE_PALETTE.take(params.paletteSize)
        val direction = params.allowedDirections.random(random)

        val ratios = listOf(0.33f, 0.5f, 0.67f)
        val ratio = if (params.level <= 2) 0.5f else ratios.random(random)

        return when (direction) {
            QuestionDirection.DIRECT -> {
                val cA = palette.random(random)
                val cB = palette.filter { it != cA }.random(random)
                val correct = mix(cA, cB, ratio)
                val distractors = smartDistractors(correct, 3, params, palette, random)
                val options = (listOf(correct) + distractors).shuffled(random)
                ColorQuestion.Direct(colorA = cA, colorB = cB, ratio = ratio,
                    correct = correct, options = options)
            }

            QuestionDirection.INVERSE -> {
                val cA = palette.random(random)
                val cB = palette.filter { it != cA }.random(random)
                val result = mix(cA, cB, ratio)
                val distractors = smartDistractors(cA, 3, params, palette, random)
                val options = (listOf(cA) + distractors).shuffled(random)
                ColorQuestion.Inverse(result = result, knownColor = cB, ratio = ratio,
                    correct = cA, options = options)
            }

            QuestionDirection.MIDDLE -> {
                val cA = palette.random(random)
                val cB = palette.filter { it != cA }.random(random)
                val middle = mix(cA, cB, 0.5f)
                val distractors = smartDistractors(middle, 3, params, palette, random)
                val options = (listOf(middle) + distractors).shuffled(random)
                ColorQuestion.Middle(start = cA, end = cB,
                    correct = middle, options = options)
            }
        }
    }

    /**
     * Escolhe a estratégia de distratores com base na saturação da cor correta:
     * - Cor saturada → hue-shift (isola a variável perceptiva por nível de dificuldade)
     * - Cor dessaturada → mixes plausíveis da paleta (garante distinção visual clara)
     */
    private fun smartDistractors(
        correct: Color,
        count: Int,
        params: CoresDifficultyParams,
        palette: List<Color>,
        random: Random,
    ): List<Color> {
        val sat = toHsl(correct)[1]
        return if (sat < 0.28f) {
            generatePlausibleDistractors(correct, count, palette, random)
        } else {
            generateDistractors(correct, count, params.distractorHueDelta)
        }
    }
}
