package com.lugat.app.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import java.util.Locale

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
    var currentIndex by remember { mutableIntStateOf(0) }
    var options by remember { mutableStateOf<List<Any>>(emptyList()) }
    var selected by remember { mutableStateOf<Any?>(null) }
    var answers by remember { mutableStateOf<List<Triple<Any, Any, Boolean>>>(emptyList()) }
    var isDone by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    val settings by viewModel.dailySettings.collectAsState()
    val isDbInit by viewModel.isDbInitialized.collectAsState()
    val activeDictionary by viewModel.activeDictionary.collectAsState()

    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(Unit) {
        var ttsObj: TextToSpeech? = null
        ttsObj = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val t = ttsObj ?: return@TextToSpeech
                if (t.setLanguage(Locale.US) >= TextToSpeech.LANG_AVAILABLE) {
                    t.setPitch(1.1f); t.setSpeechRate(0.9f)
                }
            }
        }
        tts = ttsObj
        onDispose { ttsObj?.stop(); ttsObj?.shutdown() }
    }

    LaunchedEffect(isDbInit) {
        if (isDbInit) {
            val limit = settings.first
            questions = when {
                book != null && unit != null -> viewModel.getEssentialWordsForUnit(book, unit).shuffled()
                activeDictionary == "essential_4000" -> {
                    if (mistakesOnly) viewModel.getEssentialMistakeWords(limit)
                    else {
                        val due = viewModel.getEssentialWordsDue(limit)
                        if (due.isEmpty()) viewModel.getNewEssentialWords(limit) else due
                    }
                }
                else -> {
                    if (mistakesOnly) viewModel.getMistakeWords(limit)
                    else (viewModel.getDailyWords() + viewModel.getMistakeWords(limit / 3) +
                        viewModel.getOldLearnedWords(limit / 3)).shuffled().take(limit)
                }
            }
            isLoading = false
        }
    }

    LaunchedEffect(currentIndex, questions) {
        if (!isLoading && currentIndex < questions.size) {
            val currWord = questions[currentIndex]
            options = when (currWord) {
                is EssentialWord -> (viewModel.getRandomEssentialOptions(currWord.id, 3) + currWord).shuffled()
                is Word -> (viewModel.getRandomOptions(currWord.id, 3) + currWord).shuffled()
                else -> emptyList()
            }
            selected = null
        }
    }

    val direction = if (overrideDirection != null) LanguageDirection.valueOf(overrideDirection) else settings.third

    // ── Done Screen ──────────────────────────────────────────────────────
    if (isDone) {
        val score = answers.count { it.third }
        val pct = if (answers.isNotEmpty()) (score * 100 / answers.size) else 0
        val excellent = pct >= 80
        Box(
            Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),

            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
                    .verticalScroll(androidx.compose.foundation.rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(if (excellent) "🏆" else "📖", fontSize = 64.sp)
                Text(if (excellent) "Ajoyib!" else "Yaxshi mashq!",
                    fontSize = 24.sp, fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface)
                Text("$score/${answers.size} to'g'ri javob",
                    fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                // Score ring
                Box(Modifier.size(110.dp), contentAlignment = Alignment.Center) {
                    CircleProgressCanvas(pct / 100f, 110.dp, 9.dp)
                    Text("$pct%", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold,
                        color = if (excellent) Color(0xFF1B7F5A) else Color(0xFFF9AA33))
                }

                Text("Javoblar", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp))

                answers.forEach { (q, chosen, correct) ->
                    val qText = when (q) {
                        is EssentialWord -> q.en; is Word -> q.ru; else -> ""
                    }
                    val chosenText = when (chosen) {
                        is EssentialWord -> chosen.uz; is Word -> chosen.uz; else -> "$chosen"
                    }
                    Card(
                        Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (correct) Color(0xFFD4F7EA) else Color(0xFFFFDAD6)
                        )
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(10.dp, 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(qText, fontSize = 16.sp, fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface)
                                Text("→ $chosenText", fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(if (correct) "✅" else "❌", fontSize = 18.sp)
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { currentIndex = 0; selected = null; answers = emptyList(); isDone = false },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(99.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) { Text("Qayta urinish", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onPrimary) }
                OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(99.dp)) {
                    Text("Bosh sahifaga")
                }
            }
        }
        return
    }

    // ── Main Test Screen ──────────────────────────────────────────────────
    if (isLoading || questions.isEmpty()) {
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center) {
            if (isLoading) CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            else Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("📭", fontSize = 48.sp)
                Text("Test uchun so'z topilmadi", color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center)
                Button(onClick = onBack, shape = RoundedCornerShape(99.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                    Text("Orqaga")
                }
            }
        }
        return
    }

    val q = questions[currentIndex]
    val questionText = when (q) {
        is EssentialWord -> if (direction == LanguageDirection.UZ_EN) q.uz else q.en
        is Word -> direction.getSourceText(q)
        else -> ""
    }
    val questionSubLabel = when (q) {
        is EssentialWord -> if (direction == LanguageDirection.UZ_EN) "O'zbek tili" else "Ingliz tili"
        is Word -> "Rus tili"
        else -> ""
    }

    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = MaterialTheme.colorScheme.onSurface)
            }
            Column(Modifier.weight(1f).padding(start = 8.dp)) {
                Text("Kundalik test", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            }
            Text("${currentIndex + 1}/${questions.size}", fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        ThinProgressBar(
            currentIndex.toFloat(), questions.size.toFloat(),
            MaterialTheme.colorScheme.primary, 5.dp
        )

        Column(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Question card
            val cardBg by animateColorAsState(
                targetValue = when {
                    selected == null -> MaterialTheme.colorScheme.surface
                    isCorrectAnswer(q, selected!!) -> Color(0xFFD4F7EA)
                    else -> Color(0xFFFFDAD6)
                }, animationSpec = tween(250)
            )
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Tarjimasini toping", fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.outline, letterSpacing = 1.2.sp,
                        modifier = Modifier.padding(bottom = 12.dp))
                    Text(questionText, fontSize = 48.sp, fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface)
                    Text(questionSubLabel, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 6.dp))
                    if (selected != null) {
                        Spacer(Modifier.height(14.dp))
                        Text(
                            if (isCorrectAnswer(q, selected!!)) "✅ To'g'ri!" else "❌ To'g'ri javob: ${getCorrectText(q, direction)}",
                            fontSize = 15.sp, fontWeight = FontWeight.ExtraBold,
                            color = if (isCorrectAnswer(q, selected!!)) Color(0xFF1B7F5A) else Color(0xFFBA1A1A)
                        )
                    }
                }
            }

            // Options
            options.forEachIndexed { i, opt ->
                val isCorrect = isCorrectAnswer(q, opt)
                val isSelected = selected == opt
                val borderColor by animateColorAsState(
                    targetValue = when {
                        selected == null -> MaterialTheme.colorScheme.outlineVariant
                        isCorrect -> Color(0xFF1B7F5A)
                        isSelected -> Color(0xFFBA1A1A)
                        else -> MaterialTheme.colorScheme.outlineVariant
                    }, animationSpec = tween(200)
                )
                val optBg by animateColorAsState(
                    targetValue = when {
                        selected == null -> MaterialTheme.colorScheme.surface
                        isCorrect -> Color(0xFFD4F7EA)
                        isSelected -> Color(0xFFFFDAD6)
                        else -> MaterialTheme.colorScheme.surface
                    }, animationSpec = tween(200)
                )
                val optTextColor by animateColorAsState(
                    targetValue = when {
                        selected != null && isCorrect -> Color(0xFF1B7F5A)
                        selected != null && isSelected -> Color(0xFFBA1A1A)
                        else -> MaterialTheme.colorScheme.onSurface
                    }, animationSpec = tween(200)
                )
                val optText = when (opt) {
                    is EssentialWord -> if (direction == LanguageDirection.UZ_EN) opt.en else opt.uz
                    is Word -> direction.getTargetText(opt)
                    else -> ""
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(optBg)
                        .border(2.dp, borderColor, RoundedCornerShape(16.dp))
                        .clickable(enabled = selected == null) {
                            selected = opt
                            val correct = isCorrectAnswer(q, opt)
                            answers = answers + Triple(q, opt, correct)
                            viewModel.recordAnswer(correct)
                            if (!correct) {
                                scope.launch {
                                    when (q) {
                                        is EssentialWord -> viewModel.reportEssentialMistake(q.id)
                                        is Word -> viewModel.reportMistake(q.id)
                                    }
                                }
                            }
                            scope.launch {
                                delay(900)
                                if (currentIndex == questions.size - 1) {
                                    viewModel.markTodayActive()
                                    isDone = true
                                } else {
                                    currentIndex++
                                }
                            }
                        }
                        .padding(16.dp, 16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        selected != null && isCorrect -> Color(0xFFD4F7EA)
                                        selected != null && isSelected -> Color(0xFFFFDAD6)
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                )
                                .border(2.dp, borderColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(('A' + i).toString(), fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
                                color = optTextColor)
                        }
                        Text(optText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = optTextColor)
                    }
                }
            }
        }
    }
}

private fun isCorrectAnswer(question: Any, option: Any): Boolean = when {
    question is EssentialWord && option is EssentialWord -> question.id == option.id
    question is Word && option is Word -> question.id == option.id
    else -> false
}

private fun getCorrectText(question: Any, direction: LanguageDirection): String = when (question) {
    is EssentialWord -> if (direction == LanguageDirection.UZ_EN) question.en else question.uz
    is Word -> direction.getTargetText(question)
    else -> ""
}
