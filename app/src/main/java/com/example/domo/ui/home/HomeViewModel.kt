package com.example.domo.ui.home

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.domo.core.utils.LauncherUtils
import com.example.domo.data.repository.AppRepositoryImpl
import com.example.domo.ui.home.model.DailyMission
import com.example.domo.ui.home.model.FoxAnimationState
import com.example.domo.ui.home.model.HomeBottomTab
import com.example.domo.ui.home.model.HomeScreenStatus
import com.example.domo.ui.home.model.HomeUser
import com.example.domo.ui.home.model.MORE_APPS_PACKAGE
import com.example.domo.ui.home.model.UnlockedAppUi
import com.example.domo.ui.home.model.defaultQuickActions
import java.util.Calendar
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface HomeEvent {
    data object NavigateToProfile : HomeEvent
    data object NavigateToNotifications : HomeEvent
    data object NavigateToMission : HomeEvent
    data object NavigateToExplore : HomeEvent
    data object NavigateToPass : HomeEvent
    data object NavigateToLearn : HomeEvent
    data object NavigateToAchievements : HomeEvent
    data object NavigateToRewards : HomeEvent
    data object NavigateToContinueMission : HomeEvent
    data object NavigateToMoreApps : HomeEvent
}

private val FEATURED_APP_PACKAGES = listOf(
    "com.google.android.youtube",
    "com.whatsapp",
    "com.spotify.music",
    "com.android.camera2",
    "com.android.camera",
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val appRepository = AppRepositoryImpl(application)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HomeEvent>(extraBufferCapacity = 8)
    val events: SharedFlow<HomeEvent> = _events.asSharedFlow()

    init {
        refreshDefaultLauncherStatus()
        loadHome()
    }

    fun loadHome() {
        viewModelScope.launch {
            _uiState.update { it.copy(status = HomeScreenStatus.Loading) }
            try {
                val installedApps = appRepository.getInstalledApps()
                val featuredApps = buildFeaturedApps(installedApps)
                _uiState.update {
                    it.copy(
                        status = HomeScreenStatus.Success,
                        user = HomeUser(name = "Marcos"),
                        greeting = greetingForHour(),
                        xp = 1250,
                        overallProgress = 0.65f,
                        mission = DailyMission(
                            title = "Missão do dia",
                            description = "Complete 3 atividades de Matemática",
                            current = 1,
                            total = 3,
                        ),
                        quickActions = defaultQuickActions(),
                        unlockedApps = featuredApps,
                        availableScreenMinutes = 35,
                        notificationCount = 1,
                        foxState = FoxAnimationState.Reading,
                    )
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(status = HomeScreenStatus.Error) }
            }
        }
    }

    fun refreshDefaultLauncherStatus() {
        _uiState.update {
            it.copy(isDefaultLauncher = LauncherUtils.isDefaultLauncher(getApplication()))
        }
    }

    fun onBottomTabSelected(tab: HomeBottomTab) {
        _uiState.update { it.copy(selectedBottomTab = tab) }
        when (tab) {
            HomeBottomTab.HOME -> Unit
            HomeBottomTab.EXPLORE -> _events.tryEmit(HomeEvent.NavigateToExplore)
            HomeBottomTab.TOCA -> Unit
            HomeBottomTab.PASS -> _events.tryEmit(HomeEvent.NavigateToPass)
            HomeBottomTab.PROFILE -> _events.tryEmit(HomeEvent.NavigateToProfile)
        }
    }

    fun onProfileClick() {
        _events.tryEmit(HomeEvent.NavigateToProfile)
    }

    fun onNotificationsClick() {
        _events.tryEmit(HomeEvent.NavigateToNotifications)
    }

    fun onMissionClick() {
        _events.tryEmit(HomeEvent.NavigateToMission)
    }

    fun onQuickActionClick(actionId: String) {
        when (actionId) {
            "continue_mission" -> _events.tryEmit(HomeEvent.NavigateToContinueMission)
            "learn" -> _events.tryEmit(HomeEvent.NavigateToLearn)
            "achievements" -> _events.tryEmit(HomeEvent.NavigateToAchievements)
            "rewards" -> _events.tryEmit(HomeEvent.NavigateToRewards)
        }
    }

    fun onAppClick(packageName: String) {
        if (packageName == MORE_APPS_PACKAGE) {
            _events.tryEmit(HomeEvent.NavigateToMoreApps)
            return
        }
        val context = getApplication<Application>()
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(launchIntent)
            } catch (_: ActivityNotFoundException) {
                _uiState.update { it.copy(userMessage = "Não foi possível abrir o app") }
            }
        } else {
            _uiState.update { it.copy(userMessage = "App não instalado") }
        }
    }

    fun onMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }

    private fun buildFeaturedApps(
        installedApps: List<com.example.domo.domain.model.AppInfo>,
    ): List<UnlockedAppUi> {
        val byPackage = installedApps.associateBy { it.packageName }
        val featured = FEATURED_APP_PACKAGES
            .distinct()
            .mapNotNull { pkg ->
                val app = byPackage[pkg] ?: return@mapNotNull null
                UnlockedAppUi(
                    packageName = app.packageName,
                    label = app.label,
                    isLocked = false,
                    icon = app.icon,
                )
            }
            .take(4)

        val mockApps = if (featured.isEmpty()) {
            listOf(
                UnlockedAppUi("com.google.android.youtube", "YouTube", isLocked = false),
                UnlockedAppUi("com.whatsapp", "WhatsApp", isLocked = false),
                UnlockedAppUi("com.spotify.music", "Spotify", isLocked = false),
                UnlockedAppUi("com.android.camera", "Câmera", isLocked = false),
            )
        } else {
            featured
        }

        return mockApps + UnlockedAppUi(
            packageName = MORE_APPS_PACKAGE,
            label = "Mais apps",
            isLocked = true,
        )
    }

    companion object {
        fun greetingForHour(): String {
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            return when (hour) {
                in 5..11 -> "Bom dia,"
                in 12..17 -> "Boa tarde,"
                else -> "Boa noite,"
            }
        }
    }
}
