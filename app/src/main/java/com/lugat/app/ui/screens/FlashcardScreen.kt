package com.lugat.app.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
    var currentIdx by remember { mutableIntStateOf(0) }
    var flipped by remember { mutableStateOf(false) }
    var knownIds by remember { mutableStateOf<Set<Int>>(emptySet()) }
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
                Text("Mashq tugadi!", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    "${knownIds.size} so'z bilasiz, ${words.size - knownIds.size} qayta o'rganasiz",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                // Score card
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Row(Modifier.fillMaxWidth().padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceAround) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${knownIds.size}", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1B7F5A))
                            Text("Bilaman ✓", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${words.size - knownIds.size}", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFF4511E))
                            Text("Qayta", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.markEssentialWordsAsLearned(words)
                            viewModel.markTodayActive()
                            onComplete()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(99.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) { Text("Saqlash va Chiqish ✓", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onPrimary) }
                OutlinedButton(
                    onClick = { currentIdx = 0; flipped = false; knownIds = emptySet(); isDone = false },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(99.dp)
                ) { Text("Qayta boshlash") }
            }
        }
        return
    }

    val word = words[currentIdx]

    // ── Main Card Screen ──────────────────────────────────────────────────
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
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
                Text("Flesh-kartalar", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface)
                Text("$book · $unit", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("${currentIdx + 1}/${words.size}", fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Progress
        ThinProgressBar(
            currentIdx.toFloat(), words.size.toFloat(),
            MaterialTheme.colorScheme.primary, 4.dp
        )

        Spacer(Modifier.height(24.dp))

        // Card (tap to flip)
        Box(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clickable { flipped = !flipped }
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (flipped) MaterialTheme.colorScheme.primaryContainer
                                     else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    content = {
                        Spacer(Modifier.height(20.dp))
                        if (!flipped) {
                            Text("Inglizcha", fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.outline, letterSpacing = 1.2.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(word.en, fontSize = 44.sp, fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(Modifier.height(20.dp))
                            Text("Ko'rish uchun bosing", fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            Text("Tarjimalar", fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary, letterSpacing = 1.2.sp)
                            Spacer(Modifier.height(4.dp))
                            Text("Inglizcha: ${word.en}", fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("🇺🇿", fontSize = 18.sp)
                                Text(word.uz, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(Modifier.height(6.dp))
                            // TTS
                            IconButton(
                                onClick = { tts?.speak(word.en, TextToSpeech.QUEUE_FLUSH, null, null) },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(0.1f))
                                    .size(40.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.VolumeUp, null,
                                    tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(Modifier.height(20.dp))
                    }
                )
            }
        }

        if (!flipped) {
            Text(
                "👆 Ko'rish uchun bosing",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f),
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.weight(1f))

        // Action buttons (Bilmadim / Bilaman)
        if (flipped) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        // Don't know — don't add to knownIds
                        flipped = false
                        if (currentIdx < words.size - 1) currentIdx++
                        else isDone = true
                    },
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(99.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFDAD6))
                ) {
                    Text("😕 Bilmadim", fontWeight = FontWeight.ExtraBold, color = Color(0xFFBA1A1A))
                }
                Button(
                    onClick = {
                        knownIds = knownIds + word.id
                        flipped = false
                        if (currentIdx < words.size - 1) currentIdx++
                        else isDone = true
                    },
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(99.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4F7EA))
                ) {
                    Text("✅ Bilaman", fontWeight = FontWeight.ExtraBold, color = Color(0xFF1B7F5A))
                }
            }
        } else {
            Text(
                "${knownIds.size} ta so'z bilasiz",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}
