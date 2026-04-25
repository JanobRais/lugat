package com.lugat.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
                            if (femaleVoice != null) ttsRef.voice = femaleVoice
                        }
                    } catch (e: Exception) { e.printStackTrace() }
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
                questions = viewModel.getEssentialWordsForUnit(book, unit).shuffled()
            } else if (activeDictionary == "essential_4000") {
                questions = if (mistakesOnly) {
                    viewModel.getEssentialMistakeWords(limit)
                } else {
                    val due = viewModel.getEssentialWordsDue(limit)
                    if (due.isEmpty()) viewModel.getNewEssentialWords(limit) else due
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
            if (currentWord is EssentialWord) {
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

    val topBarTitle = when {
        book != null && unit != null -> unit
        mistakesOnly -> "Xatolarni Ko'rib Chiqish"
        else -> "Kunlik Test"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(topBarTitle, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            "Yozish",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.width(4.dp))
                        Switch(
                            checked = isTypingMode,
                            onCheckedChange = { isTypingMode = it },
                            modifier = Modifier.height(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        when {
            isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF2563EB))
                }
            }
            questions.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(if (mistakesOnly) "🏆" else "📚", fontSize = 64.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            if (mistakesOnly) "Xato yo'q! Ajoyib!" else "Test so'zi topilmadi.",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            currentIndex < questions.size -> {
                val currentWord = questions[currentIndex]
                val direction = settings.third
                val currentDirection = if (overrideDirection != null) {
                    LanguageDirection.valueOf(overrideDirection)
                } else direction

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(8.dp))

                    // Progress
                    Column(Modifier.fillMaxWidth()) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                "${currentIndex + 1} / ${questions.size}",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                if (mistakesOnly) "Xatolar" else "Test",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (mistakesOnly) Color(0xFFDC2626) else Color(0xFF2563EB)
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { (currentIndex + 1).toFloat() / questions.size },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (mistakesOnly) Color(0xFFDC2626) else Color(0xFF2563EB),
                            trackColor = (if (mistakesOnly) Color(0xFFDC2626) else Color(0xFF2563EB)).copy(alpha = 0.15f),
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    // Question Card
                    val sourceText = when (val word = currentWord) {
                        is EssentialWord -> if (currentDirection == LanguageDirection.UZ_EN) word.uz else word.en
                        is Word -> currentDirection.getSourceText(word)
                        else -> ""
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(0.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFF2563EB), Color(0xFF6D28D9))
                                    )
                                )
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                // TTS
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    IconButton(onClick = {
                                        val textToSpeak = when (val word = questions[currentIndex]) {
                                            is EssentialWord -> word.en
                                            is Word -> word.en
                                            else -> ""
                                        }
                                        tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null)
                                    }) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.VolumeUp,
                                            contentDescription = "Speak",
                                            tint = Color.White,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                                Spacer(Modifier.width(16.dp))
                                Text(
                                    text = sourceText,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Center,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (!isTypingMode) {
                        // MCQ Options
                        options.forEach { option ->
                            val isSelected = selectedOption == option
                            val isCorrect = when {
                                currentWord is EssentialWord && option is EssentialWord -> option.id == currentWord.id
                                currentWord is Word && option is Word -> option.id == currentWord.id
                                else -> false
                            }

                            val targetBg = when {
                                selectedOption == null -> MaterialTheme.colorScheme.surfaceVariant
                                isCorrect -> Color(0xFF16A34A)
                                isSelected -> Color(0xFFDC2626)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }

                            val animatedBg by animateColorAsState(
                                targetValue = targetBg,
                                animationSpec = tween(300)
                            )

                            val textColor = when {
                                selectedOption != null && (isCorrect || isSelected) -> Color.White
                                else -> MaterialTheme.colorScheme.onSurface
                            }

                            val targetText = when (option) {
                                is EssentialWord -> if (currentDirection == LanguageDirection.UZ_EN) option.en else option.uz
                                is Word -> currentDirection.getTargetText(option)
                                else -> ""
                            }

                            Card(
                                onClick = {
                                    if (selectedOption == null) {
                                        selectedOption = option
                                        scope.launch {
                                            if (!isCorrect) {
                                                if (currentWord is EssentialWord) viewModel.reportEssentialMistake(currentWord.id)
                                                else if (currentWord is Word) viewModel.reportMistake(currentWord.id)
                                            }
                                            delay(1200)
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
                                    .padding(vertical = 5.dp)
                                    .height(58.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = animatedBg),
                                elevation = CardDefaults.cardElevation(0.dp)
                            ) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = targetText,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = textColor
                                    )
                                }
                            }
                        }
                    } else {
                        // Typing Mode
                        val focusManager = LocalFocusManager.current
                        val correctText = when (currentWord) {
                            is EssentialWord -> if (currentDirection == LanguageDirection.UZ_EN) currentWord.en else currentWord.uz
                            is Word -> currentDirection.getTargetText(currentWord)
                            else -> ""
                        }

                        OutlinedTextField(
                            value = typedText,
                            onValueChange = { if (!isAnswerChecked) typedText = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Tarjimani yozing") },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
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

                        Spacer(Modifier.height(16.dp))

                        if (isAnswerChecked) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isAnswerCorrect) Color(0xFFDCFCE7) else Color(0xFFFFEDED)
                                )
                            ) {
                                Row(
                                    Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        if (isAnswerCorrect) "✅" else "❌",
                                        fontSize = 20.sp
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        if (isAnswerCorrect) "To'g'ri!" else "Xato! Javob: $correctText",
                                        color = if (isAnswerCorrect) Color(0xFF16A34A) else Color(0xFFDC2626),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        } else {
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                            ) {
                                Text("Tekshirish", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
