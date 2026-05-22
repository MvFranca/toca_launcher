package com.example.domo.ui.minigame.calculadora.model

data class SessionResult(
    val hits: Int,
    val misses: Int,
    val retries: Int,
    val persistenceBonusTriggered: Boolean,
    val comboTriggered: Boolean,
    val averageTimeSeconds: Float,
    val energyEarned: Int,
    val xpEarned: Int,
)
