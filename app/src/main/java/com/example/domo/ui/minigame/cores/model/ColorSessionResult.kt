package com.example.domo.ui.minigame.cores.model

data class ColorSessionResult(
    val hits: Int,
    val misses: Int,
    val retries: Int,
    val persistenceBonusTriggered: Boolean,
    val comboTriggered: Boolean,
    val averageTimeSeconds: Float,
    val energyEarned: Int,
    val xpEarned: Int,
)
