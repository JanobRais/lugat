package com.lugat.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
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
import com.lugat.app.data.entity.EssentialWord
import com.lugat.app.data.entity.Word
import com.lugat.app.model.LanguageDirection
import com.lugat.app.ui.LugatViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.speech.tts.TextToSpeech
import androidx.compose.ui.platform.LocalContext
import java.util.Locale
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestScreen(
    viewModel: LugatViewModel,
    mistakesOnly: Boolean = false,
    book: String? = null,
    unit: String? = null,
    overrideDirection: String? = null,
    onBack: () -> Unit,
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var questions by remember { mutableStateOf<List<Any>>(emptyList()) }
    var currentIndex by remember { mutableStateOf(0) }
    var options by remember { mutableStateOf<List<Any>>(emptyList()) }
    var selectedOption by remember { mutableStateOf<Any?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        var ttsInstance: TextToSpeech? = null
        ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val ttsRef = ttsInstance ?: return@TextToSpeech
                val result = ttsRef.setLanguage(Locale.US)
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    try {
                        val voices = ttsRef.voices
                        if (voices != null) {
                            val femaleVoice = voices.find { 
                                it.name.lowercase().contains("female") || 
                                it.name.lowercase().contains("network-f") 
                            }
                            if (femaleVoice != null) {
                                ttsRef.voice = femaleVoice
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    ttsRef.setPitch(1.1f) 
                    ttsRef.setSpeechRate(0.9f)
                }
            }
        }
        tts = ttsInstance
        onDispose {
            ttsInstance?.stop()
            ttsInstance?.shutdown()
        }
    }
    var isTypingMode by remember { mutableStateOf(false) }
    var typedText by remember { mutableStateOf("") }
    var isAnswerChecked by remember { mutableStateOf(false) }
    var isAnswerCorrect by remember { mutableStateOf(false) }
    
    val settings by viewModel.dailySettings.collectAsState()

    val isDbInitialized by viewModel.isDbInitialized.collectAsState()
    val activeDictionary by viewModel.activeDictionary.collectAsState()

    LaunchedEffect(isDbInitialized) {
        if (isDbInitialized) {
            val limit = settings.first
            if (book != null && unit != null) {
                // Unit-specific test
                questions = viewModel.getEssentialWordsForUnit(book, unit).shuffled()
            } else if (activeDictionary == "essential_4000") {
                questions = if (mistakesOnly) {
                    viewModel.getEssentialMistakeWords(limit)
                } else {
                    val due = viewModel.getEssentialWordsDue(limit)
                    if (due.isEmpty()) {
                        // If no words due, fetch some new words to keep user engaged
                        viewModel.getNewEssentialWords(limit)
                    } else {
                        due
                    }
                }
            } else {
                questions = if (mistakesOnly) {
                    viewModel.getMistakeWords(limit)
                } else {
                    val mixed = mutableListOf<Word>()
                    mixed.addAll(viewModel.getDailyWords())
                    mixed.addAll(viewModel.getMistakeWords(limit / 3))
                    mixed.addAll(viewModel.getOldLearnedWords(limit / 3))
                    mixed.shuffled().take(limit)
                }
            }
            isLoading = false
        }
    }

    LaunchedEffect(currentIndex, questions) {
        if (currentIndex < questions.size) {
            val currentWord = questions[currentIndex]
            if (currentWord is com.lugat.app.data.entity.EssentialWord) {
                val randomOptions = viewModel.getRandomEssentialOptions(currentWord.id, 3).toMutableList()
                randomOptions.add(currentWord)
                options = randomOptions.shuffled()
            } else if (currentWord is Word) {
                val randomOptions = viewModel.getRandomOptions(currentWord.id, 3).toMutableList()
                randomOptions.add(currentWord)
                options = randomOptions.shuffled()
            }
            selectedOption = null
            typedText = ""
            isAnswerChecked = false
            isAnswerCorrect = false
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
                },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Typing Mode", fontSize = 14.sp)
                        Switch(
                            checked = isTypingMode,
                            onCheckedChange = { isTypingMode = it },
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
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
            val direction = settings.third

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
                Spacer(modifier = Modifier.height(16.dp))

                // TTS Speaker Icon
                IconButton(onClick = {
                    val textToSpeak = when (val word = questions[currentIndex]) {
                        is EssentialWord -> word.en
                        is Word -> word.en
                        else -> ""
                    }
                    tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null)
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Speak",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                val currentDirection = if (overrideDirection != null) {
                    LanguageDirection.valueOf(overrideDirection)
                } else {
                    direction
                }

                val sourceText = when (val word = currentWord) {
                    is EssentialWord -> {
                        if (currentDirection == LanguageDirection.UZ_EN) word.uz else word.en
                    }
                    is Word -> currentDirection.getSourceText(word)
                    else -> ""
                }

                Text(
                    text = sourceText,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(48.dp))

                if (!isTypingMode) {
                    options.forEach { option ->
                        val isSelected = selectedOption == option
                        val isCorrect = when {
                            currentWord is com.lugat.app.data.entity.EssentialWord && option is com.lugat.app.data.entity.EssentialWord -> option.id == currentWord.id
                            currentWord is Word && option is Word -> option.id == currentWord.id
                            else -> false
                        }
                        
                        val containerColor = if (selectedOption != null) {
                            if (isCorrect) MaterialTheme.colorScheme.primaryContainer
                            else if (isSelected) MaterialTheme.colorScheme.errorContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                        } else MaterialTheme.colorScheme.surfaceVariant

                        val contentColor = if (selectedOption != null) {
                            if (isCorrect) MaterialTheme.colorScheme.onPrimaryContainer
                            else if (isSelected) MaterialTheme.colorScheme.onErrorContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        } else MaterialTheme.colorScheme.onSurfaceVariant

                        val targetText = when (option) {
                            is com.lugat.app.data.entity.EssentialWord -> {
                                if (currentDirection == LanguageDirection.UZ_EN) option.en else option.uz
                            }
                            is Word -> currentDirection.getTargetText(option)
                            else -> ""
                        }

                        Card(
                            onClick = {
                                if (selectedOption == null) {
                                    selectedOption = option
                                    scope.launch {
                                        if (!isCorrect) {
                                            if (currentWord is com.lugat.app.data.entity.EssentialWord) viewModel.reportEssentialMistake(currentWord.id)
                                            else if (currentWord is Word) viewModel.reportMistake(currentWord.id)
                                        }
                                        delay(1000)
                                        if (currentIndex == questions.size - 1) {
                                            viewModel.markSessionCompleted()
                                            onComplete()
                                        } else {
                                            currentIndex++
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .height(56.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = containerColor,
                                contentColor = contentColor
                            )
                        ) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = targetText,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                } else {
                    val focusManager = LocalFocusManager.current
                    val currentDirection = if (overrideDirection != null) {
                        LanguageDirection.valueOf(overrideDirection)
                    } else {
                        direction
                    }
                    val correctText = when (currentWord) {
                        is com.lugat.app.data.entity.EssentialWord -> {
                            if (currentDirection == LanguageDirection.UZ_EN) currentWord.en else currentWord.uz
                        }
                        is Word -> currentDirection.getTargetText(currentWord)
                        else -> ""
                    }
                    
                    OutlinedTextField(
                        value = typedText,
                        onValueChange = { if (!isAnswerChecked) typedText = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Type the translation") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (typedText.isNotBlank() && !isAnswerChecked) {
                                    focusManager.clearFocus()
                                    isAnswerChecked = true
                                    isAnswerCorrect = typedText.trim().equals(correctText, ignoreCase = true)
                                    
                                    scope.launch {
                                        if (!isAnswerCorrect) {
                                            if (currentWord is EssentialWord) viewModel.reportEssentialMistake(currentWord.id)
                                            else if (currentWord is Word) viewModel.reportMistake(currentWord.id)
                                        }
                                        delay(1500)
                                        if (currentIndex == questions.size - 1) {
                                            viewModel.markSessionCompleted()
                                            onComplete()
                                        } else {
                                            currentIndex++
                                        }
                                    }
                                }
                            }
                        ),
                        isError = isAnswerChecked && !isAnswerCorrect
                    )
                    
                    if (isAnswerChecked) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isAnswerCorrect) "Correct!" else "Incorrect! Answer: $correctText",
                            color = if (isAnswerCorrect) androidx.compose.ui.graphics.Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    } else {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (typedText.isNotBlank()) {
                                    focusManager.clearFocus()
                                    isAnswerChecked = true
                                    isAnswerCorrect = typedText.trim().equals(correctText, ignoreCase = true)
                                    
                                    scope.launch {
                                        if (!isAnswerCorrect) {
                                            if (currentWord is EssentialWord) viewModel.reportEssentialMistake(currentWord.id)
                                            else if (currentWord is Word) viewModel.reportMistake(currentWord.id)
                                        }
                                        delay(1500)
                                        if (currentIndex == questions.size - 1) {
                                            viewModel.markSessionCompleted()
                                            onComplete()
                                        } else {
                                            currentIndex++
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Check")
                        }
                    }
                }
            }
        }
    }
}
