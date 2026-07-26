package com.example.domo.feature.learn.domain.cores

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
