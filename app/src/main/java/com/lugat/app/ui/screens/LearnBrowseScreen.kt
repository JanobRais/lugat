package com.lugat.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lugat.app.data.entity.EssentialWord
import com.lugat.app.data.entity.Word
import com.lugat.app.ui.LugatViewModel
import kotlinx.coroutines.delay

@Composable
fun LearnBrowseScreen(
    viewModel: LugatViewModel,
    onStartLearn: () -> Unit,
    onUnitSelected: (String, String) -> Unit,
    onTestUnitSelected: (String, String, String) -> Unit,
) {
    val isDbInit by viewModel.isDbInitialized.collectAsState()
    var activeTab by remember { mutableIntStateOf(0) }  // 0=trilingual, 1=essential
    var searchQuery by remember { mutableStateOf("") }
    var triWords by remember { mutableStateOf<List<Word>>(emptyList()) }
    var triLearned by remember { mutableIntStateOf(0) }
    var triTotal by remember { mutableIntStateOf(2000) }
    var essUnits by remember { mutableStateOf<List<Pair<String, List<String>>>>(emptyList()) }  // book -> units
    var essLearned by remember { mutableIntStateOf(0) }
    var essTotal by remember { mutableIntStateOf(4000) }
    var searchResults by remember { mutableStateOf<Pair<List<Word>, List<EssentialWord>>>(emptyList<Word>() to emptyList()) }

    LaunchedEffect(isDbInit) {
        if (isDbInit) {
            triWords = viewModel.getDailyWords().take(100)
            val tStats = viewModel.getWordStats()
            triLearned = tStats["learned"] ?: 0
            triTotal = tStats["total"] ?: 2000
            val eStats = viewModel.getEssentialStats()
            essLearned = eStats["learned"] ?: 0
            essTotal = eStats["total"] ?: 4000
            val books = viewModel.getEssentialBooks()
            essUnits = books.map { book -> book to viewModel.getEssentialUnitsForBook(book) }
        }
    }

    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 2) {
            delay(300)
            val tri = viewModel.searchWords(searchQuery)
            val ess = viewModel.searchEssentialWords(searchQuery)
            searchResults = tri to ess
        } else {
            searchResults = emptyList<Word>() to emptyList<EssentialWord>()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Lug'atlar", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface)
        }

        // Search bar
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 0.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(MaterialTheme.colorScheme.surface),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(start = 16.dp).size(18.dp))
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("So'z qidirish...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            if (searchQuery.isNotEmpty()) {
                TextButton(onClick = { searchQuery = "" }) {
                    Text("×", fontSize = 18.sp, color = MaterialTheme.colorScheme.outline)
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Search results
        if (searchQuery.length >= 2) {
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val (triResults, essResults) = searchResults
                if (essResults.isNotEmpty()) {
                    item {
                        SearchSectionHeader("Essential 4000", essResults.size, Color(0xFFF9AA33))
                    }
                    items(essResults) { w ->
                        SearchResultCard(primaryText = "${w.en}", secondaryText = w.uz,
                            badgeText = "${w.bookName} · ${w.unitName}", accentColor = Color(0xFFF9AA33))
                    }
                }
                if (triResults.isNotEmpty()) {
                    item {
                        if (essResults.isNotEmpty()) Spacer(Modifier.height(4.dp))
                        SearchSectionHeader("Trilingual 2000", triResults.size, MaterialTheme.colorScheme.primary)
                    }
                    items(triResults) { w ->
                        SearchResultCard(primaryText = "${w.ru} · ${w.en}", secondaryText = w.uz,
                            badgeText = null, accentColor = MaterialTheme.colorScheme.primary)
                    }
                }
                if (triResults.isEmpty() && essResults.isEmpty()) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(vertical = 64.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🔍", fontSize = 48.sp)
                                Spacer(Modifier.height(12.dp))
                                Text("\"$searchQuery\" uchun natija topilmadi",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
            return@Column
        }

        // Tabs (Trilingual / Essential)
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("🇷🇺 Trilingual 2000", "🇬🇧 Essential 4000").forEachIndexed { i, label ->
                val isActive = activeTab == i
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(
                            if (isActive) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable { activeTab = i }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Content by tab
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (activeTab == 0) {
                // Trilingual
                item {
                    LCard {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Umumiy progress", fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text("$triLearned", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary)
                                    Text("/$triTotal", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            // Mini circle progress
                            Box(Modifier.size(68.dp), contentAlignment = Alignment.Center) {
                                val pct = if (triTotal > 0) triLearned.toFloat() / triTotal else 0f
                                CircleProgressCanvas(pct, 68.dp, 6.dp)
                                Text("${(pct * 100).toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        Button(
                            onClick = onStartLearn,
                            modifier = Modifier.fillMaxWidth().height(44.dp),
                            shape = RoundedCornerShape(99.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) { Text("O'rganishni davom ettirish →", fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary) }
                    }
                }
                // Word list (sample)
                items(triWords) { w ->
                    LCard {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                Modifier.size(8.dp).clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                            Column(Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(w.ru, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface)
                                    Text("рус", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(w.uz, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary)
                                    Text("·", color = MaterialTheme.colorScheme.outlineVariant)
                                    Text(w.en, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            } else {
                // Essential 4000
                item {
                    LCard {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Umumiy progress", fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text("$essLearned", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFF9AA33))
                                    Text("/$essTotal", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Box(Modifier.size(68.dp), contentAlignment = Alignment.Center) {
                                val pct = if (essTotal > 0) essLearned.toFloat() / essTotal else 0f
                                CircleProgressCanvas(pct, 68.dp, 6.dp)
                                Text("${(pct * 100).toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFF9AA33))
                            }
                        }
                    }
                }
                // Books → Units
                essUnits.forEachIndexed { bookIdx, (book, units) ->
                    item {
                        Text(book, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 4.dp, top = if (bookIdx > 0) 8.dp else 0.dp))
                    }
                    items(units) { unit ->
                        LCard(onClick = { onUnitSelected(book, unit) }) {
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(
                                    Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFF9AA33).copy(0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        unit.filter { it.isDigit() }.take(2).ifEmpty { "U" },
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFF9AA33), fontSize = 14.sp
                                    )
                                }
                                Text(unit, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                                Button(
                                    onClick = { onTestUnitSelected(book, unit, "EN_UZ") },
                                    shape = RoundedCornerShape(99.dp),
                                    modifier = Modifier.height(32.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFF9AA33).copy(0.2f),
                                        contentColor = Color(0xFFF9AA33)
                                    )
                                ) { Text("Test", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold) }
                            }
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}
