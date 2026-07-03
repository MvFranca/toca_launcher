package com.example.domo.ui.home.preview

import com.example.domo.ui.home.HomeUiState
import com.example.domo.ui.home.model.DailyMission
import com.example.domo.ui.home.model.FoxAnimationState
import com.example.domo.ui.home.model.HomeBottomTab
import com.example.domo.ui.home.model.HomeScreenStatus
import com.example.domo.ui.home.model.HomeUser
import com.example.domo.ui.home.model.MORE_APPS_PACKAGE
import com.example.domo.ui.home.model.UnlockedAppUi
import com.example.domo.ui.home.model.defaultQuickActions

val previewHomeUiState = HomeUiState(
    status = HomeScreenStatus.Success,
    user = HomeUser(name = "Marcos"),
    greeting = "Bom dia,",
    xp = 1250,
    overallProgress = 0.65f,
    mission = DailyMission(
        title = "Missão do dia",
        description = "Complete 3 atividades de Matemática",
        current = 1,
        total = 3,
    ),
    quickActions = defaultQuickActions(),
    unlockedApps = listOf(
        UnlockedAppUi("com.google.android.youtube", "YouTube", isLocked = false),
        UnlockedAppUi("com.whatsapp", "WhatsApp", isLocked = false),
        UnlockedAppUi("com.spotify.music", "Spotify", isLocked = false),
        UnlockedAppUi("com.android.camera", "Câmera", isLocked = false),
        UnlockedAppUi(MORE_APPS_PACKAGE, "Mais apps", isLocked = true),
    ),
    availableScreenMinutes = 35,
    notificationCount = 1,
    foxState = FoxAnimationState.Reading,
    selectedBottomTab = HomeBottomTab.HOME,
    isDefaultLauncher = true,
)
