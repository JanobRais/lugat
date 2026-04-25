package com.lugat.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lugat.app.ui.LugatViewModel
import com.lugat.app.ui.screens.*
import com.lugat.app.ui.theme.LugatTheme

// ── Bottom Nav destinations ─────────────────────────────────────────────
private sealed class Tab(val route: String, val label: String, val icon: String) {
    object Home      : Tab("home",     "Bosh",       "🏠")
    object Learn     : Tab("learn",    "O'rganish",  "📖")
    object Flashcard : Tab("flashcard","Kartalar",   "🃏")
    object Stats     : Tab("stats",    "Natijalar",  "📊")
    object Settings  : Tab("settings", "Sozlamalar", "⚙️")
}

private val ALL_TABS = listOf(Tab.Home, Tab.Learn, Tab.Flashcard, Tab.Stats, Tab.Settings)
private val MAIN_ROUTES = ALL_TABS.map { it.route }.toSet()

@Composable
fun LugatApp() {
    LugatTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val navController = rememberNavController()
            val viewModel: LugatViewModel = hiltViewModel()
            val activeDictionary by viewModel.activeDictionary.collectAsState()
            val currentBackStack by navController.currentBackStackEntryAsState()
            val currentRoute = currentBackStack?.destination?.route

            val activeTab = ALL_TABS.find { it.route == currentRoute } ?: Tab.Home
            val showBottomNav = currentRoute in MAIN_ROUTES

            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                bottomBar = {
                    if (showBottomNav) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f),
                            tonalElevation = 0.dp,
                        ) {
                            ALL_TABS.forEach { tab ->
                                val isActive = activeTab == tab
                                NavigationBarItem(
                                    selected = isActive,
                                    onClick = {
                                        if (!isActive) navController.navigate(tab.route) {
                                            popUpTo("home") { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = {
                                        Box(
                                            contentAlignment = androidx.compose.ui.Alignment.Center
                                        ) {
                                            if (isActive) {
                                                Surface(
                                                    shape = RoundedCornerShape(99.dp),
                                                    color = MaterialTheme.colorScheme.primaryContainer,
                                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                                ) {
                                                    Text(tab.icon, fontSize = 18.sp,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                                                }
                                            } else {
                                                Text(tab.icon, fontSize = 20.sp)
                                            }
                                        }
                                    },
                                    label = {
                                        Text(
                                            tab.label,
                                            fontSize = 10.sp,
                                            fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Normal,
                                            color = if (isActive) MaterialTheme.colorScheme.primary
                                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        indicatorColor = Color.Transparent
                                    )
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = "home",
                    modifier = Modifier.padding(innerPadding)
                ) {
                    // ── Main tabs ──────────────────────────────────────────
                    composable("home") {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateTrilingual = {
                                viewModel.setActiveDictionary("lugat_2000")
                                navController.navigate("learn_session")
                            },
                            onNavigateEssential = {
                                viewModel.setActiveDictionary("essential_4000")
                                navController.navigate("essential_units")
                            },
                            onNavigateFlashcard = {
                                if (activeDictionary == "essential_4000")
                                    navController.navigate("essential_units")
                                else
                                    navController.navigate("learn_session")
                            },
                            onNavigateTest = { navController.navigate("test_daily") }
                        )
                    }

                    composable("learn") {
                        LearnBrowseScreen(
                            viewModel = viewModel,
                            onStartLearn = { navController.navigate("learn_session") },
                            onUnitSelected = { book, unit ->
                                navController.navigate("essential_learn/$book/$unit")
                            },
                            onTestUnitSelected = { book, unit, dir ->
                                navController.navigate("test_unit/$book/$unit/$dir")
                            }
                        )
                    }

                    composable("flashcard") {
                        LearnBrowseScreen(
                            viewModel = viewModel,
                            onStartLearn = { navController.navigate("learn_session") },
                            onUnitSelected = { book, unit ->
                                navController.navigate("essential_learn/$book/$unit")
                            },
                            onTestUnitSelected = { book, unit, dir ->
                                navController.navigate("test_unit/$book/$unit/$dir")
                            }
                        )
                    }

                    composable("stats") {
                        StatsScreen(viewModel = viewModel)
                    }

                    composable("settings") {
                        SettingsScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // ── Sub-screens ────────────────────────────────────────
                    composable("learn_session") {
                        LearningScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                            onComplete = { navController.popBackStack() }
                        )
                    }

                    composable("essential_units") {
                        UnitListScreen(
                            viewModel = viewModel,
                            onUnitSelected = { book, unit ->
                                navController.navigate("essential_learn/$book/$unit")
                            },
                            onTestUnitSelected = { book, unit, direction ->
                                navController.navigate("test_unit/$book/$unit/$direction")
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("essential_learn/{book}/{unit}") { back ->
                        val book = back.arguments?.getString("book") ?: ""
                        val unit = back.arguments?.getString("unit") ?: ""
                        FlashcardScreen(
                            viewModel = viewModel,
                            book = book,
                            unit = unit,
                            onBack = { navController.popBackStack() },
                            onComplete = { navController.popBackStack() }
                        )
                    }

                    composable("test_unit/{book}/{unit}/{direction}") { back ->
                        val book = back.arguments?.getString("book") ?: ""
                        val unit = back.arguments?.getString("unit") ?: ""
                        val dir  = back.arguments?.getString("direction") ?: "EN_UZ"
                        TestScreen(
                            viewModel = viewModel,
                            book = book, unit = unit, overrideDirection = dir,
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

                    composable("search") {
                        SearchScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
