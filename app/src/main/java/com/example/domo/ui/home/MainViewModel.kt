package com.example.domo.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.domo.core.utils.LauncherUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        refreshDefaultLauncherStatus()
    }

    fun refreshDefaultLauncherStatus() {
        _uiState.update {
            it.copy(isDefaultLauncher = LauncherUtils.isDefaultLauncher(getApplication()))
        }
    }

    fun onBottomTabSelected(tab: HomeBottomTab) {
        _uiState.update { it.copy(selectedBottomTab = tab) }
    }

    fun onSettingsClick() {
        _uiState.update { it.copy(userMessage = "Configuracoes") }
    }

    fun onPlayClick() {
        _uiState.update { it.copy(userMessage = "Jogar") }
    }

    fun onJourneyNodeClick(index: Int) {
        _uiState.update { it.copy(userMessage = "Fase: $index") }
    }

    fun onMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }

    fun showTrophyPopup() {
        _uiState.update { it.copy(showTrophyPopup = true) }
    }

    fun dismissTrophyPopup() {
        _uiState.update { it.copy(showTrophyPopup = false) }
    }
}
