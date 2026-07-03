package com.example.domo.ui.home.model

import androidx.compose.ui.graphics.ImageBitmap

enum class HomeScreenStatus {
    Loading,
    Success,
    Error,
    Empty,
    Offline,
}

enum class FoxAnimationState {
    Reading,
    Playing,
    Sleeping,
    Celebrating,
    Thinking,
    Teaching,
}

enum class QuickActionIcon {
    ContinueMission,
    Learn,
    Achievements,
    Rewards,
}

enum class HomeBottomTab {
    HOME,
    EXPLORE,
    TOCA,
    PASS,
    PROFILE,
}

data class HomeUser(
    val name: String,
    val avatarContentDescription: String = "Avatar da raposa",
)

data class DailyMission(
    val title: String,
    val description: String,
    val current: Int,
    val total: Int,
)

data class QuickActionItem(
    val id: String,
    val label: String,
    val icon: QuickActionIcon,
)

data class UnlockedAppUi(
    val packageName: String,
    val label: String,
    val isLocked: Boolean,
    val icon: ImageBitmap? = null,
)

const val MORE_APPS_PACKAGE = "__more_apps__"

fun defaultQuickActions(): List<QuickActionItem> = listOf(
    QuickActionItem("continue_mission", "Continuar missão", QuickActionIcon.ContinueMission),
    QuickActionItem("learn", "Aprender", QuickActionIcon.Learn),
    QuickActionItem("achievements", "Conquistas", QuickActionIcon.Achievements),
    QuickActionItem("rewards", "Recompensas", QuickActionIcon.Rewards),
)
