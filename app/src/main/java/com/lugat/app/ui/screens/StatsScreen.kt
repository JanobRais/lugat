package com.lugat.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lugat.app.ui.LugatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: LugatViewModel,
    onBack: () -> Unit
) {
    var trilingualStats by remember { mutableStateOf<Map<String, Int>?>(null) }
    var essentialStats by remember { mutableStateOf<Map<String, Int>?>(null) }
    
    LaunchedEffect(Unit) {
        trilingualStats = viewModel.getWordStats()
        essentialStats = viewModel.getEssentialStats()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Progress Report") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
                StreakCard(viewModel.streakCount)
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                trilingualStats?.let { stats ->
                    StatSection(
                        title = "Trilingual 2000",
                        learned = stats["learned"] ?: 0,
                        total = stats["total"] ?: 2000,
                        mistakes = stats["mistakes"] ?: 0,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                essentialStats?.let { stats ->
                    StatSection(
                        title = "Essential 4000",
                        learned = stats["learned"] ?: 0,
                        total = stats["total"] ?: 4000,
                        mistakes = stats["mistakes"] ?: 0,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }
    }
}

@Composable
fun StreakCard(count: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint = Color(0xFFE65100),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Learning Streak", style = MaterialTheme.typography.titleMedium, color = Color(0xFFE65100))
                Text("$count Days", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
            }
        }
    }
}

@Composable
fun StatSection(title: String, learned: Int, total: Int, mistakes: Int, color: Color) {
    Column {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        
        val progress = if (total > 0) learned.toFloat() / total else 0f
        
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(12.dp).padding(vertical = 4.dp),
            color = color,
            trackColor = color.copy(alpha = 0.2f),
        )
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("${(progress * 100).toInt()}% Completed", fontSize = 14.sp)
            Text("$learned / $total words", fontSize = 14.sp)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("$mistakes words in your mistake list.", fontSize = 14.sp)
            }
        }
    }
}

private fun Modifier.size(dp: androidx.compose.ui.unit.Dp): Modifier = this.size(dp)
