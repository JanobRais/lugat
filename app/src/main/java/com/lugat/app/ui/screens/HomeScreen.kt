package com.lugat.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Search
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
    onNavigateToSettings: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToStats: () -> Unit
) {
    val settings by viewModel.dailySettings.collectAsState()
    val activeDictionary by viewModel.activeDictionary.collectAsState()
    
    var wordStats by remember { mutableStateOf<Triple<Int, Int, Int>?>(null) }
    var essentialStats by remember { mutableStateOf<Triple<Int, Int, Int>?>(null) }
    
    LaunchedEffect(Unit) {
        wordStats = viewModel.getWordStats()
        essentialStats = viewModel.getEssentialStats()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lugat", fontWeight = FontWeight.Bold) },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onNavigateToSearch) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = onNavigateToStats) {
                            Icon(Icons.Default.BarChart, contentDescription = "Stats")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.LocalFireDepartment, contentDescription = "Streak", tint = androidx.compose.ui.graphics.Color(0xFFFFA500))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "${viewModel.streakCount}", fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color(0xFFFFA500))
                        Spacer(modifier = Modifier.width(8.dp))
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Daily Progress Widget
            DailyProgressWidget(
                trilingualProgress = wordStats?.let { it.second.toFloat() / it.first } ?: 0f,
                essentialProgress = essentialStats?.let { it.second.toFloat() / it.first } ?: 0f,
                trilingualCount = "${wordStats?.second ?: 0}/${wordStats?.first ?: 0}",
                essentialCount = "${essentialStats?.second ?: 0}/${essentialStats?.first ?: 0}"
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Learning ${settings.third.displayName}",
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
                Text(
                    text = "Essential 4000 Menu",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Start).padding(bottom = 16.dp)
                )
                
                Column {
                    Row(Modifier.fillMaxWidth()) {
                        SmallActionCard(
                            title = "Daily Test",
                            subtitle = "New words",
                            modifier = Modifier.weight(1f).padding(end = 8.dp),
                            onClick = onNavigateToTest
                        )
                        SmallActionCard(
                            title = "Review",
                            subtitle = "Learned",
                            modifier = Modifier.weight(1f).padding(start = 8.dp),
                            onClick = { /* TODO: Navigate to review learned */ onNavigateToTest() }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth()) {
                        SmallActionCard(
                            title = "Mistakes",
                            subtitle = "Practice",
                            modifier = Modifier.weight(1f).padding(end = 8.dp),
                            onClick = onNavigateToMistakes
                        )
                        SmallActionCard(
                            title = "Mixed",
                            subtitle = "All modes",
                            modifier = Modifier.weight(1f).padding(start = 8.dp),
                            onClick = { /* TODO: Mixed mode */ onNavigateToTest() }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                ActionCard(
                    title = "Learn Units",
                    subtitle = "Browse all books",
                    onClick = onNavigateToLearn
                )
            } else {
                ActionCard(
                    title = "Learn New Words",
                    subtitle = "${settings.first} words today",
                    onClick = onNavigateToLearn
                )
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
}

@Composable
fun DailyProgressWidget(
    trilingualProgress: Float,
    essentialProgress: Float,
    trilingualCount: String,
    essentialCount: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("Daily Progress", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            
            ProgressRow(label = "Trilingual 2000", progress = trilingualProgress, count = trilingualCount)
            Spacer(modifier = Modifier.height(12.dp))
            ProgressRow(label = "Essential 4000", progress = essentialProgress, count = essentialCount)
        }
    }
}

@Composable
fun ProgressRow(label: String, progress: Float, count: String) {
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(count, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth().height(8.dp),
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionCard(title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(90.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallActionCard(title: String, subtitle: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
