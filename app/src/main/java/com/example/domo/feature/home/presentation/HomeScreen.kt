package com.example.domo.feature.home.presentation

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.domo.core.designsystem.TocaHomePanelGradientBottom
import com.example.domo.core.designsystem.TocaHomePanelGradientMid
import com.example.domo.core.designsystem.TocaHomePanelGradientTop
import com.example.domo.core.designsystem.TocaLaranja
import com.example.domo.core.designsystem.TocaTheme
import com.example.domo.core.launcher.AppBlockerUtils
import com.example.domo.core.launcher.LauncherUtils
import com.example.domo.feature.home.presentation.components.HeroSection
import com.example.domo.feature.home.presentation.components.HomeBackground
import com.example.domo.feature.home.presentation.components.HomeTopBar
import com.example.domo.feature.home.presentation.components.MissionCard
import com.example.domo.feature.home.presentation.components.QuickActionsGrid
import com.example.domo.feature.home.presentation.components.TocaBottomNavigation
import com.example.domo.feature.home.presentation.components.UnlockedAppsSection
import com.example.domo.feature.home.presentation.model.HomeScreenStatus
import com.example.domo.feature.home.presentation.preview.previewHomeUiState
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(
    onNavigateToGame: () -> Unit = {},
    onNavigateToLearn: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToMission: () -> Unit = {},
    onNavigateToExplore: () -> Unit = {},
    onNavigateToPass: () -> Unit = {},
    onNavigateToAchievements: () -> Unit = {},
    onNavigateToRewards: () -> Unit = {},
    onNavigateToMoreApps: () -> Unit = {},
    viewModel: HomeViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasUsagePermission by rememberSaveable {
        mutableStateOf(AppBlockerUtils.hasUsageStatsPermission(context))
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshDefaultLauncherStatus()
                hasUsagePermission = AppBlockerUtils.hasUsageStatsPermission(context)
                if (hasUsagePermission) AppBlockerUtils.startBlockerService(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.onMessageShown()
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collectLatest { event ->
            when (event) {
                HomeEvent.NavigateToProfile -> onNavigateToProfile()
                HomeEvent.NavigateToNotifications ->
                    Toast.makeText(context, "Notificações em breve", Toast.LENGTH_SHORT).show()
                HomeEvent.NavigateToMission -> onNavigateToMission()
                HomeEvent.NavigateToExplore -> onNavigateToExplore()
                HomeEvent.NavigateToPass -> onNavigateToPass()
                HomeEvent.NavigateToLearn -> onNavigateToLearn()
                HomeEvent.NavigateToAchievements -> onNavigateToAchievements()
                HomeEvent.NavigateToRewards -> onNavigateToRewards()
                HomeEvent.NavigateToContinueMission -> onNavigateToGame()
                HomeEvent.NavigateToMoreApps -> onNavigateToMoreApps()
            }
        }
    }

    HomeBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
            ) {
                if (!uiState.isDefaultLauncher) {
                    ActivateBanner(
                        onActivateClick = { LauncherUtils.openDefaultLauncherSettings(context) },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    )
                }
                if (!hasUsagePermission) {
                    UsagePermissionBanner(
                        onActivateClick = { AppBlockerUtils.openUsageStatsSettings(context) },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    )
                }

                AnimatedContent(
                    targetState = uiState.status,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "homeStatus",
                    modifier = Modifier.weight(1f),
                ) { status ->
                    when (status) {
                        HomeScreenStatus.Loading -> HomeLoadingState()
                        HomeScreenStatus.Error -> HomeErrorState(onRetry = viewModel::loadHome)
                        HomeScreenStatus.Empty -> HomeEmptyState()
                        HomeScreenStatus.Offline -> HomeOfflineState(onRetry = viewModel::loadHome)
                        HomeScreenStatus.Success -> HomeSuccessContent(
                            uiState = uiState,
                            onProfileClick = viewModel::onProfileClick,
                            onNotificationsClick = viewModel::onNotificationsClick,
                            onMissionClick = viewModel::onMissionClick,
                            onQuickActionClick = viewModel::onQuickActionClick,
                            onAppClick = viewModel::onAppClick,
                        )
                    }
                }
            }

            TocaBottomNavigation(
                selected = uiState.selectedBottomTab,
                onSelect = viewModel::onBottomTabSelected,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
private fun HomeSuccessContent(
    uiState: HomeUiState,
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onMissionClick: () -> Unit,
    onQuickActionClick: (String) -> Unit,
    onAppClick: (String) -> Unit,
) {
    val density = LocalDensity.current
    val panelOffsetY = with(density) { 250.toDp() }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            HomeTopBar(
                user = uiState.user,
                greeting = uiState.greeting,
                xp = uiState.xp,
                notificationCount = uiState.notificationCount,
                onProfileClick = onProfileClick,
                onNotificationsClick = onNotificationsClick,
            )
            HeroSection(
                foxState = uiState.foxState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = panelOffsetY)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to TocaHomePanelGradientTop.copy(alpha = 0f),
                            0.36f to TocaHomePanelGradientMid.copy(alpha = 0.63f),
                            1f to TocaHomePanelGradientBottom,
                        ),
                    ),
                )
                .padding(start = 16.dp, end = 16.dp, top = 28.dp, bottom = 130.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            MissionCard(mission = uiState.mission, onClick = onMissionClick)
            QuickActionsGrid(
                actions = uiState.quickActions,
                onActionClick = onQuickActionClick,
            )
            UnlockedAppsSection(
                apps = uiState.unlockedApps,
                availableScreenMinutes = uiState.availableScreenMinutes,
                onAppClick = onAppClick,
            )
        }
    }
}

@Composable
private fun HomeLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = TocaLaranja)
    }
}

@Composable
private fun HomeErrorState(onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Não foi possível carregar a Toca",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onRetry) {
                Text("Tentar novamente")
            }
        }
    }
}

@Composable
private fun HomeEmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Sua toca está vazia por enquanto",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun HomeOfflineState(onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Você está offline",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onRetry) {
                Text("Recarregar")
            }
        }
    }
}

@Composable
private fun ActivateBanner(onActivateClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(TocaLaranja.copy(alpha = 0.9f))
            .clickable(onClick = onActivateClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Definir como tela inicial",
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
            Text(text = "Ativar", color = Color.White, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun UsagePermissionBanner(onActivateClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFFFE4E6))
            .clickable(onClick = onActivateClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Ativar proteção de apps",
                    color = Color(0xFF9B1C1C),
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Necessário para bloquear apps com PIN",
                    color = Color(0xFFB91C1C),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Text(text = "Ativar", color = Color(0xFF9B1C1C), fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    TocaTheme {
        HomeBackground {
            HomeSuccessContent(
                uiState = previewHomeUiState,
                onProfileClick = {},
                onNotificationsClick = {},
                onMissionClick = {},
                onQuickActionClick = {},
                onAppClick = {},
            )
        }
    }
}
