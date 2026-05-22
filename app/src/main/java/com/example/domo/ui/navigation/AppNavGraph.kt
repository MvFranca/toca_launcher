package com.example.domo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.domo.ui.home.HomeScreen
import com.example.domo.ui.home.MainViewModel
import com.example.domo.ui.minigame.calculadora.CalculadoraVelozScreen
import com.example.domo.ui.minigame.calculadora.CalculadoraVelozViewModel
import com.example.domo.ui.minigame.calculadora.SessionResultScreen
import com.example.domo.ui.minigame.calculadora.model.SessionResult

private object Routes {
    const val HOME = "home"
    const val CALCULADORA_VELOZ = "calculadora_veloz"
    const val SESSION_RESULT = "session_result" +
        "/{hits}/{misses}/{energy}/{xp}/{combo}/{persistence}"
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    // Instanciado fora do NavHost → escopo de Activity, compartilhado com todos os destinos
    val mainViewModel: MainViewModel = viewModel()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToGame = {
                    navController.navigate(Routes.CALCULADORA_VELOZ)
                },
                viewModel = mainViewModel,
            )
        }

        composable(Routes.CALCULADORA_VELOZ) { backStackEntry ->
            val viewModel: CalculadoraVelozViewModel = viewModel(backStackEntry)
            CalculadoraVelozScreen(
                viewModel = viewModel,
                onNavigateToResult = { result ->
                    navController.navigate(
                        "session_result" +
                            "/${result.hits}" +
                            "/${result.misses}" +
                            "/${result.energyEarned}" +
                            "/${result.xpEarned}" +
                            "/${if (result.comboTriggered) 1 else 0}" +
                            "/${if (result.persistenceBonusTriggered) 1 else 0}",
                    ) {
                        popUpTo(Routes.CALCULADORA_VELOZ) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Routes.SESSION_RESULT,
            arguments = listOf(
                navArgument("hits") { type = NavType.IntType },
                navArgument("misses") { type = NavType.IntType },
                navArgument("energy") { type = NavType.IntType },
                navArgument("xp") { type = NavType.IntType },
                navArgument("combo") { type = NavType.IntType },
                navArgument("persistence") { type = NavType.IntType },
            ),
        ) { backStackEntry ->
            val args = backStackEntry.arguments!!
            val result = remember(args) {
                SessionResult(
                    hits = args.getInt("hits"),
                    misses = args.getInt("misses"),
                    retries = 0,
                    persistenceBonusTriggered = args.getInt("persistence") == 1,
                    comboTriggered = args.getInt("combo") == 1,
                    averageTimeSeconds = 0f,
                    energyEarned = args.getInt("energy"),
                    xpEarned = args.getInt("xp"),
                )
            }
            SessionResultScreen(
                result = result,
                onPlayAgain = {
                    navController.navigate(Routes.CALCULADORA_VELOZ) {
                        popUpTo(Routes.SESSION_RESULT) { inclusive = true }
                    }
                },
                onBackToHome = {
                    mainViewModel.showTrophyPopup()
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = false }
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}
