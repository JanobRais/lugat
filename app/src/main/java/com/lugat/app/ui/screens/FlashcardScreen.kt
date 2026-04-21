package com.lugat.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lugat.app.data.entity.EssentialWord
import com.lugat.app.ui.LugatViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardScreen(
    viewModel: LugatViewModel,
    book: String,
    unit: String,
    onBack: () -> Unit,
    onComplete: () -> Unit
) {
    var words by remember { mutableStateOf<List<EssentialWord>>(emptyList()) }
    var currentIndex by remember { mutableStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    val isDbInitialized by viewModel.isDbInitialized.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(isDbInitialized) {
        if (isDbInitialized) {
            words = viewModel.getEssentialWordsForUnit(book, unit)
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$book - $unit") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (words.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No words found.")
            }
        } else if (currentIndex >= words.size) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Unit Completed!", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        scope.launch {
                            viewModel.markSessionCompleted()
                            onComplete()
                        }
                    }) {
                        Text("Finish")
                    }
                }
            }
        } else {
            val currentWord = words[currentIndex]
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${currentIndex + 1} / ${words.size}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(32.dp))
                
                Flashcard(
                    front = currentWord.en,
                    back = currentWord.uz,
                    isFlipped = isFlipped,
                    onClick = { isFlipped = !isFlipped }
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            // Swiped Left implicitly (Don't know)
                            scope.launch {
                                viewModel.reportEssentialMistake(currentWord.id)
                                isFlipped = false
                                currentIndex++
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Don't Know")
                    }
                    
                    Button(
                        onClick = {
                            // Swiped Right implicitly (Know)
                            scope.launch {
                                viewModel.markEssentialWordsAsLearned(listOf(currentWord))
                                isFlipped = false
                                currentIndex++
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color(0xFF4CAF50))
                    ) {
                        Text("Know")
                    }
                }
            }
        }
    }
}

@Composable
fun Flashcard(
    front: String,
    back: String,
    isFlipped: Boolean,
    onClick: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        label = "rotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clickable(onClick = onClick)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (rotation <= 90f) {
                Text(
                    text = front,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = back,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.graphicsLayer { rotationY = 180f } // Fix mirror effect
                )
            }
        }
    }
}
