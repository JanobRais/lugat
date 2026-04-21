package com.lugat.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lugat.app.ui.LugatViewModel
import com.lugat.app.ui.screens.HomeScreen
import com.lugat.app.ui.screens.LearningScreen
import com.lugat.app.ui.screens.SettingsScreen
import com.lugat.app.ui.screens.TestScreen
import com.lugat.app.ui.theme.LugatTheme

@Composable
fun LugatApp() {
    LugatTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val navController = rememberNavController()
            val viewModel: LugatViewModel = hiltViewModel()

            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToLearn = { navController.navigate("learn") },
                        onNavigateToTest = { navController.navigate("test_daily") },
                        onNavigateToMistakes = { navController.navigate("test_mistakes") },
                        onNavigateToSettings = { navController.navigate("settings") }
                    )
                }
                composable("learn") {
                    LearningScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onComplete = { navController.popBackStack() }
                    )
                }
                composable("test_daily") {
                    TestScreen(
                        viewModel = viewModel,
                        mistakesOnly = false,
                        onBack = { navController.popBackStack() },
                        onComplete = { navController.popBackStack() }
                    )
                }
                composable("test_mistakes") {
                    TestScreen(
                        viewModel = viewModel,
                        mistakesOnly = true,
                        onBack = { navController.popBackStack() },
                        onComplete = { navController.popBackStack() }
                    )
                }
                composable("settings") {
                    SettingsScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
