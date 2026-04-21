package com.lugat.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lugat.app.data.entity.Word
import com.lugat.app.ui.LugatViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestScreen(
    viewModel: LugatViewModel,
    mistakesOnly: Boolean = false,
    onBack: () -> Unit,
    onComplete: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var questions by remember { mutableStateOf<List<Word>>(emptyList()) }
    var currentIndex by remember { mutableStateOf(0) }
    var options by remember { mutableStateOf<List<Word>>(emptyList()) }
    var selectedOption by remember { mutableStateOf<Word?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    val settings by viewModel.dailySettings.collectAsState()

    LaunchedEffect(Unit) {
        val limit = settings.first
        questions = if (mistakesOnly) {
            viewModel.getMistakeWords(limit)
        } else {
            // Mixed test: New words + Mistake words + Old words
            val mixed = mutableListOf<Word>()
            mixed.addAll(viewModel.getDailyWords())
            mixed.addAll(viewModel.getMistakeWords(limit / 3))
            mixed.addAll(viewModel.getOldLearnedWords(limit / 3))
            mixed.shuffled().take(limit)
        }
        isLoading = false
    }

    LaunchedEffect(currentIndex, questions) {
        if (currentIndex < questions.size) {
            val currentWord = questions[currentIndex]
            val randomOptions = viewModel.getRandomOptions(currentWord.id, 3).toMutableList()
            randomOptions.add(currentWord)
            options = randomOptions.shuffled()
            selectedOption = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (mistakesOnly) "Review Mistakes" else "Daily Test") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (questions.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(if (mistakesOnly) "No mistakes to review yet! Great job!" else "No words to test.")
            }
        } else if (currentIndex < questions.size) {
            val currentWord = questions[currentIndex]
            val direction = settings.second

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${currentIndex + 1} / ${questions.size}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = direction.getSourceText(currentWord),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(48.dp))

                options.forEach { option ->
                    val isSelected = selectedOption == option
                    val isCorrect = option.id == currentWord.id
                    
                    val containerColor = if (selectedOption != null) {
                        if (isCorrect) MaterialTheme.colorScheme.primaryContainer // wait, success color
                        else if (isSelected) MaterialTheme.colorScheme.errorContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    } else MaterialTheme.colorScheme.surfaceVariant

                    val contentColor = if (selectedOption != null) {
                        if (isCorrect) MaterialTheme.colorScheme.onPrimaryContainer
                        else if (isSelected) MaterialTheme.colorScheme.onErrorContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    } else MaterialTheme.colorScheme.onSurfaceVariant

                    Card(
                        onClick = {
                            if (selectedOption == null) {
                                selectedOption = option
                                scope.launch {
                                    if (!isCorrect) {
                                        viewModel.reportMistake(currentWord.id)
                                    }
                                    delay(1000)
                                    if (currentIndex == questions.size - 1) {
                                        onComplete()
                                    } else {
                                        currentIndex++
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .height(64.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = containerColor,
                            contentColor = contentColor
                        )
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = direction.getTargetText(option),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
