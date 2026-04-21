package com.lugat.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lugat.app.model.LanguageDirection
import com.lugat.app.ui.LugatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: LugatViewModel,
    onBack: () -> Unit
) {
    val settings by viewModel.dailySettings.collectAsState()
    var wordLimit by remember { mutableFloatStateOf(settings.first.toFloat()) }
    var selectedDirection by remember { mutableStateOf(settings.second) }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.updateSettings(wordLimit.toInt(), selectedDirection)
                        onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            Text("Daily Word Limit: ${wordLimit.toInt()}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Slider(
                value = wordLimit,
                onValueChange = { wordLimit = it },
                valueRange = 5f..30f,
                steps = 24
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text("Language Direction", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedDirection.displayName,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    LanguageDirection.values().forEach { direction ->
                        DropdownMenuItem(
                            text = { Text(direction.displayName) },
                            onClick = {
                                selectedDirection = direction
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
