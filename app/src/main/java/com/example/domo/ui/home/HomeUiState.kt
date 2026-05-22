package com.example.domo.ui.home

data class HomeUiState(
    val profileName: String = "Ana",
    val level: Int = 1,
    val streakLabelDays: Int = 5,
    val currentXp: Int = 380,
    val nextLevelXp: Int = 1000,
    val displayXpLine: String = "85",
    val diamonds: Int = 200,
    val selectedBottomTab: HomeBottomTab = HomeBottomTab.GAMES,
    val hasNewRewards: Boolean = true,
    val heroGreeting: String = "Boa tarde, Ana! 👋",
    val heroCtaLine1: String = "Você está a",
    val heroCtaLine2: String = "1 fase do troféu!",
    val playButtonBadge: String = "Cores",
    val missionTitle: String = "Missão do dia",
    val missionSubtitle: String = "Quase lá!",
    val missionProgress: Float = 2f / 3f,
    val missionBadge: String = "2/3",
    val leagueTitle: String = "Liga atual",
    val leagueSubtitle: String = "Suba para o pódio!",
    val leagueProgress: Float = 0.42f,
    val leagueRankBadge: String = "4º",
    val streakCount: Int = 5,
    val userMessage: String? = null,
    val isDefaultLauncher: Boolean = false,
    val showTrophyPopup: Boolean = false,
)

enum class HomeBottomTab {
    GAMES,
    MISSION,
    REWARDS,
    PROFILE,
}
