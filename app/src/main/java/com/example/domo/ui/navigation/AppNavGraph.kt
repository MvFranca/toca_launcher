package com.example.domo.ui.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.domo.ui.home.HomeScreen
import com.example.domo.ui.home.HomeViewModel
import com.example.domo.ui.minigame.calculadora.CalculadoraVelozScreen
import com.example.domo.ui.minigame.calculadora.CalculadoraVelozViewModel
import com.example.domo.ui.minigame.calculadora.SessionResultScreen
import com.example.domo.ui.minigame.calculadora.model.SessionResult
import com.example.domo.ui.minigame.cores.MisturaScreen
import com.example.domo.ui.minigame.cores.MisturaViewModel

private object Routes {
    const val HOME = "home"
    const val CALCULADORA_VELOZ = "calculadora_veloz"
    const val MISTURA_DE_CORES = "mistura_de_cores"
    const val SESSION_RESULT = "session_result" +
        "/{hits}/{misses}/{energy}/{xp}/{combo}/{persistence}"
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel()

    fun showStub(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToGame = {
                    navController.navigate(Routes.CALCULADORA_VELOZ)
                },
                onNavigateToLearn = {
                    navController.navigate(Routes.MISTURA_DE_CORES)
                },
                onNavigateToProfile = { showStub("Perfil em breve") },
                onNavigateToMission = { showStub("Missão em breve") },
                onNavigateToExplore = { showStub("Explorar em breve") },
                onNavigateToPass = { showStub("Passe em breve") },
                onNavigateToAchievements = { showStub("Conquistas em breve") },
                onNavigateToRewards = { showStub("Recompensas em breve") },
                onNavigateToMoreApps = { showStub("Mais aplicativos em breve") },
                viewModel = homeViewModel,
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

        composable(Routes.MISTURA_DE_CORES) { backStackEntry ->
            val viewModel: MisturaViewModel = viewModel(backStackEntry)
            MisturaScreen(
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
                        popUpTo(Routes.MISTURA_DE_CORES) { inclusive = true }
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
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = false }
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}
