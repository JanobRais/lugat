package com.lugat.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lugat.app.ui.LugatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: LugatViewModel,
    onNavigateToLearn: () -> Unit,
    onNavigateToTest: () -> Unit,
    onNavigateToMistakes: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val settings by viewModel.dailySettings.collectAsState()
    val activeDictionary by viewModel.activeDictionary.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lugat", fontWeight = FontWeight.Bold) },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalFireDepartment, contentDescription = "Streak", tint = androidx.compose.ui.graphics.Color(0xFFFFA500))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "${viewModel.streakCount}", fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color(0xFFFFA500))
                        Spacer(modifier = Modifier.width(16.dp))
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome to Lugat!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Learning ${settings.second.displayName}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Dictionary Switcher
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                val options = listOf("Trilingual 2000", "Essential 4000")
                val selectedIndex = if (activeDictionary == "essential_4000") 1 else 0
                
                options.forEachIndexed { index, title ->
                    SegmentedButton(
                        selected = selectedIndex == index,
                        onClick = {
                            viewModel.switchDictionary(if (index == 0) "trilingual_2000" else "essential_4000")
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size)
                    ) {
                        Text(text = title)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            if (activeDictionary == "essential_4000") {
                ActionCard(
                    title = "Learn Essential Units",
                    subtitle = "Select a unit to study",
                    onClick = onNavigateToLearn
                )
            } else {
                ActionCard(
                    title = "Learn New Words",
                    subtitle = "${settings.first} words today",
                    onClick = onNavigateToLearn
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            ActionCard(
                title = "Daily Test",
                subtitle = "Test your knowledge",
                onClick = onNavigateToTest
            )
            Spacer(modifier = Modifier.height(16.dp))
            ActionCard(
                title = "Review Mistakes",
                subtitle = "Practice words you missed",
                onClick = onNavigateToMistakes
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionCard(title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
