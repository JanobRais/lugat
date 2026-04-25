package com.lugat.app.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lugat.app.data.entity.EssentialWord
import com.lugat.app.ui.LugatViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun FlashcardScreen(
    viewModel: LugatViewModel,
    book: String,
    unit: String,
    onBack: () -> Unit,
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var words by remember { mutableStateOf<List<EssentialWord>>(emptyList()) }
    var knownIds by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var unknownIds by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var isLoading by remember { mutableStateOf(true) }
    var isDone by remember { mutableStateOf(false) }
    val isDbInit by viewModel.isDbInitialized.collectAsState()

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
            words = viewModel.getEssentialWordsForUnit(book, unit)
            isLoading = false
        }
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    // ── Done Screen ───────────────────────────────────────────────────────
    if (isDone || words.isEmpty()) {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("🎉", fontSize = 64.sp)
                Text(
                    "Mashq tugadi!", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "${knownIds.size} so'z bilasiz, ${unknownIds.size} qayta o'rganasiz",
                    fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                // Score card
                Card(
                    Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "${knownIds.size}", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1B7F5A)
                            )
                            Text("Bilaman ✓", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "${unknownIds.size}", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFF4511E)
                            )
                            Text("Qayta", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.markEssentialWordsAsLearned(words.filter { it.id in knownIds })
                            viewModel.markTodayActive()
                            onComplete()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(99.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) { Text("Saqlash va Chiqish ✓", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onPrimary) }
                OutlinedButton(
                    onClick = { knownIds = emptySet(); unknownIds = emptySet(); isDone = false },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(99.dp)
                ) { Text("Qayta boshlash") }
            }
        }
        return
    }

    // ── Main Pager Screen ─────────────────────────────────────────────────
    val pagerState = rememberPagerState(pageCount = { words.size })
    val currentWord = words[pagerState.currentPage]

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Header ────────────────────────────────────────────────────────
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.onSurface)
            }
            Column(Modifier.weight(1f).padding(start = 8.dp)) {
                Text(
                    "Flesh-kartalar", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "$book · $unit", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                "${pagerState.currentPage + 1}/${words.size}", fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // ── Progress bar ──────────────────────────────────────────────────
        ThinProgressBar(
            (pagerState.currentPage + 1).toFloat(), words.size.toFloat(),
            MaterialTheme.colorScheme.primary, 4.dp
        )

        // ── Dot indicators ────────────────────────────────────────────────
        Spacer(Modifier.height(12.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val visible = minOf(words.size, 7)
            val start = maxOf(0, pagerState.currentPage - 3)
            val end = minOf(words.size, start + visible)
            (start until end).forEach { i ->
                val isActive = i == pagerState.currentPage
                Box(
                    Modifier
                        .padding(horizontal = 3.dp)
                        .size(if (isActive) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (when {
                                words[i].id in knownIds -> true
                                else -> false
                            }) Color(0xFF1B7F5A)
                            else if (words[i].id in unknownIds) Color(0xFFF4511E)
                            else if (isActive) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant
                        )
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── HorizontalPager ───────────────────────────────────────────────
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp),
            pageSpacing = 12.dp
        ) { page ->
            val word = words[page]
            val isKnown   = word.id in knownIds
            val isUnknown = word.id in unknownIds

            // Card background gradient based on known/unknown state
            val cardGrad = when {
                isKnown   -> Brush.linearGradient(listOf(Color(0xFF1B7F5A), Color(0xFF00BFA5)))
                isUnknown -> Brush.linearGradient(listOf(Color(0xFFBA1A1A), Color(0xFFE57373)))
                else      -> Brush.linearGradient(listOf(
                    MaterialTheme.colorScheme.surface,
                    MaterialTheme.colorScheme.surface
                ))
            }
            val textColor = if (isKnown || isUnknown) Color.White else MaterialTheme.colorScheme.onSurface
            val subColor  = if (isKnown || isUnknown) Color.White.copy(0.8f) else MaterialTheme.colorScheme.onSurfaceVariant

            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(cardGrad)
                        .padding(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Status badge
                        if (isKnown || isUnknown) {
                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(99.dp))
                                    .background(Color.White.copy(0.2f))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    if (isKnown) "✓ Bilaman" else "✗ Bilmadim",
                                    fontSize = 12.sp, fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }

                        // English word
                        Text(
                            "Inglizcha",
                            fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                            color = subColor, letterSpacing = 1.2.sp
                        )
                        Text(
                            word.en,
                            fontSize = 38.sp, fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center, color = textColor
                        )

                        // Divider
                        HorizontalDivider(
                            color = if (isKnown || isUnknown) Color.White.copy(0.3f)
                                    else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth(0.5f)
                        )

                        // Uzbek translation
                        Text(
                            "O'zbekcha",
                            fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                            color = subColor, letterSpacing = 1.2.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🇺🇿", fontSize = 18.sp)
                            Text(
                                word.uz,
                                fontSize = 30.sp, fontWeight = FontWeight.ExtraBold,
                                color = if (isKnown || isUnknown) Color.White
                                        else MaterialTheme.colorScheme.primary
                            )
                        }

                        // TTS button
                        IconButton(
                            onClick = { tts?.speak(word.en, TextToSpeech.QUEUE_FLUSH, null, null) },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(
                                    if (isKnown || isUnknown) Color.White.copy(0.2f)
                                    else MaterialTheme.colorScheme.primaryContainer
                                )
                                .size(44.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.VolumeUp, null,
                                tint = if (isKnown || isUnknown) Color.White
                                       else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }

        // ── Swipe hint ────────────────────────────────────────────────────
        Text(
            "← chapga suring · o'ngga suring →",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            textAlign = TextAlign.Center
        )

        // ── Action buttons ────────────────────────────────────────────────
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Bilmadim button
            Button(
                onClick = {
                    unknownIds = unknownIds + currentWord.id
                    knownIds = knownIds - currentWord.id
                    scope.launch {
                        val next = pagerState.currentPage + 1
                        if (next >= words.size) isDone = true
                        else pagerState.animateScrollToPage(next)
                    }
                },
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(99.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFDAD6))
            ) {
                Text("😕 Bilmadim", fontWeight = FontWeight.ExtraBold, color = Color(0xFFBA1A1A))
            }

            // Bilaman button
            Button(
                onClick = {
                    knownIds = knownIds + currentWord.id
                    unknownIds = unknownIds - currentWord.id
                    scope.launch {
                        val next = pagerState.currentPage + 1
                        if (next >= words.size) isDone = true
                        else pagerState.animateScrollToPage(next)
                    }
                },
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(99.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4F7EA))
            ) {
                Text("✅ Bilaman", fontWeight = FontWeight.ExtraBold, color = Color(0xFF1B7F5A))
            }
        }

        // ── Stats bar ─────────────────────────────────────────────────────
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "✓ ${knownIds.size}",
                fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1B7F5A)
            )
            Text(
                "  ·  ",
                fontSize = 13.sp, color = MaterialTheme.colorScheme.outlineVariant
            )
            Text(
                "✗ ${unknownIds.size}",
                fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                color = Color(0xFFF4511E)
            )
            Text(
                "  ·  ${words.size - knownIds.size - unknownIds.size} qoldi",
                fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
