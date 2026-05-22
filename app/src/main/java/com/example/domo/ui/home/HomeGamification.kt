package com.example.domo.ui.home

import kotlin.math.roundToInt

data class DailyMissionUi(
    val title: String,
    val progress: Int,
    val goal: Int,
    val rewardLabel: String,
)

data class QuickGameUi(
    val title: String,
    val icon: String,
)

fun calculatePointsRemainingForReward(
    totalPoints: Int,
    rewardTargetPoints: Int,
): Int = (rewardTargetPoints - totalPoints).coerceAtLeast(0)

fun getMotivationalMessage(pointsRemaining: Int): String {
    return when {
        pointsRemaining <= 0 -> "Boa! Prêmio desbloqueado. Vamos abrir agora?"
        pointsRemaining <= 10 -> "Uau! Só mais $pointsRemaining pontos para o prêmio!"
        pointsRemaining <= 25 -> "Faltam $pointsRemaining pontos para desbloquear o prêmio!"
        else -> "Vamos jogar? Você pode ganhar +15 pontos!"
    }
}

fun progressToNextReward(totalPoints: Int, rewardTargetPoints: Int): Float {
    if (rewardTargetPoints <= 0) return 1f
    return (totalPoints.toFloat() / rewardTargetPoints.toFloat()).coerceIn(0f, 1f)
}

fun progressToNextLevel(currentXp: Int, nextLevelXp: Int): Float {
    if (nextLevelXp <= 0) return 1f
    return (currentXp.toFloat() / nextLevelXp.toFloat()).coerceIn(0f, 1f)
}

fun formatMissionProgress(progress: Int, goal: Int): String {
    return "${progress.coerceAtLeast(0)}/${goal.coerceAtLeast(1)}"
}

fun missionProgress(progress: Int, goal: Int): Float {
    val safeGoal = goal.coerceAtLeast(1)
    return (progress.toFloat() / safeGoal.toFloat()).coerceIn(0f, 1f)
}

fun progressPercentLabel(progress: Float): String {
    return "${(progress.coerceIn(0f, 1f) * 100f).roundToInt()}%"
}
