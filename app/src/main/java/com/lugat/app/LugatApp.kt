package com.lugat.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

            val activeDictionary by viewModel.activeDictionary.collectAsState()

            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToLearn = { 
                            if (activeDictionary == "essential_4000") navController.navigate("essential_units")
                            else navController.navigate("learn") 
                        },
                        onNavigateToTest = { navController.navigate("test_daily") },
                        onNavigateToMistakes = { navController.navigate("test_mistakes") },
                        onNavigateToSettings = { navController.navigate("settings") }
                    )
                }
                composable("essential_units") {
                    com.lugat.app.ui.screens.UnitListScreen(
                        viewModel = viewModel,
                        onUnitSelected = { book, unit -> 
                            navController.navigate("essential_learn/$book/$unit")
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("essential_learn/{book}/{unit}") { backStackEntry ->
                    val book = backStackEntry.arguments?.getString("book") ?: ""
                    val unit = backStackEntry.arguments?.getString("unit") ?: ""
                    
                    // The essential learn screen is a modified learning screen or new one
                    com.lugat.app.ui.screens.FlashcardScreen(
                        viewModel = viewModel,
                        book = book,
                        unit = unit,
                        onBack = { navController.popBackStack() },
                        onComplete = { navController.popBackStack() }
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
