package com.lugat.app.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.lugat.app.data.entity.Word
import com.lugat.app.ui.LugatViewModel
import android.speech.tts.TextToSpeech
import java.util.Locale

@Composable
fun LearningScreen(
    viewModel: LugatViewModel,
    onBack: () -> Unit,
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    val isDbInit by viewModel.isDbInitialized.collectAsState()

    var currentIndex by remember { mutableIntStateOf(0) }
    var words by remember { mutableStateOf<List<Word>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

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
            val settings = viewModel.dailySettings.value
            words = viewModel.getDailyWords()
            isLoading = false
        }
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    if (words.isEmpty()) {
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("🎉", fontSize = 64.sp)
                Text("Bugungi so'zlar tugadi!", style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
                Text("Erta yangi so'zlar kutmoqda!", color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center)
                Spacer(Modifier.height(8.dp))
                Button(onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(99.dp), modifier = Modifier.padding(horizontal = 32.dp)) {
                    Text("Orqaga", fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    val word = words[currentIndex]

    Column(
        modifier = Modifier
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
            Spacer(Modifier.width(8.dp))
            Column {
                Text("Kunlik dars", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface)
                Text("Trilingual 2000", fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.weight(1f))
            Text("${currentIndex + 1}/${words.size}", fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Progress bar
        ThinProgressBar(
            (currentIndex + 1).toFloat(), words.size.toFloat(),
            MaterialTheme.colorScheme.primary, 5.dp
        )

        Spacer(Modifier.height(24.dp))

        // Animated word card
        AnimatedContent(
            targetState = currentIndex,
            transitionSpec = {
                (slideInHorizontally { it } + fadeIn()) togetherWith
                (slideOutHorizontally { -it } + fadeOut())
            },
            modifier = Modifier.padding(horizontal = 20.dp)
        ) { idx ->
            val w = words[idx]
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    Modifier.padding(28.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Lang label
                    Text("Rus tili", fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.outline,
                        letterSpacing = 1.2.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(w.ru, fontSize = 44.sp, fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(24.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(Modifier.height(16.dp))
                    // Translations
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🇺🇿", fontSize = 18.sp)
                        Text(w.uz, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🇬🇧", fontSize = 18.sp)
                        Text(w.en, fontSize = 28.sp, fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(Modifier.height(20.dp))
                    // TTS button
                    IconButton(
                        onClick = { tts?.speak(w.en, TextToSpeech.QUEUE_FLUSH, null, null) },
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .size(44.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, null,
                            tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        // Next button
        Button(
            onClick = {
                if (currentIndex < words.size - 1) {
                    currentIndex++
                } else {
                    viewModel.markTodayActive()
                    onComplete()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .height(54.dp),
            shape = RoundedCornerShape(99.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                if (currentIndex < words.size - 1) "Keyingisi →" else "Tugatish ✓",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
