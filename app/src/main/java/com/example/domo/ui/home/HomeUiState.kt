package com.example.domo.ui.home

import com.example.domo.ui.home.model.DailyMission
import com.example.domo.ui.home.model.FoxAnimationState
import com.example.domo.ui.home.model.HomeBottomTab
import com.example.domo.ui.home.model.HomeScreenStatus
import com.example.domo.ui.home.model.HomeUser
import com.example.domo.ui.home.model.QuickActionItem
import com.example.domo.ui.home.model.UnlockedAppUi
import com.example.domo.ui.home.model.defaultQuickActions

data class HomeUiState(
    val status: HomeScreenStatus = HomeScreenStatus.Loading,
    val user: HomeUser = HomeUser(name = "Marcos"),
    val greeting: String = "Bom dia,",
    val xp: Int = 0,
    val overallProgress: Float = 0f,
    val mission: DailyMission = DailyMission("", "", 0, 0),
    val quickActions: List<QuickActionItem> = defaultQuickActions(),
    val unlockedApps: List<UnlockedAppUi> = emptyList(),
    val availableScreenMinutes: Int = 0,
    val notificationCount: Int = 0,
    val foxState: FoxAnimationState = FoxAnimationState.Reading,
    val selectedBottomTab: HomeBottomTab = HomeBottomTab.HOME,
    val isDefaultLauncher: Boolean = false,
    val userMessage: String? = null,
)
