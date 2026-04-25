package com.lugat.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lugat.app.ui.LugatViewModel

private val WEEK_LABELS = listOf("Du", "Se", "Ch", "Pa", "Ju", "Sh", "Ya")

@Composable
fun StatsScreen(viewModel: LugatViewModel) {
    val isDbInit by viewModel.isDbInitialized.collectAsState()
    var triLearned by remember { mutableIntStateOf(0) }
    var triTotal   by remember { mutableIntStateOf(2000) }
    var essLearned by remember { mutableIntStateOf(0) }
    var essTotal   by remember { mutableIntStateOf(4000) }
    var accuracy   by remember { mutableIntStateOf(0) }
    var weeklyActivity by remember { mutableStateOf(List(7) { false }) }

    val todayCount = viewModel.getTodayLearnedCount()
    val streak     = viewModel.streakCount
    val totalLearned = triLearned + essLearned

    LaunchedEffect(isDbInit) {
        if (isDbInit) {
            val tStats = viewModel.getWordStats()
            triLearned = tStats["learned"] ?: 0
            triTotal   = tStats["total"]   ?: 2000
            val eStats = viewModel.getEssentialStats()
            essLearned = eStats["learned"] ?: 0
            essTotal   = eStats["total"]   ?: 4000
            accuracy   = viewModel.getAccuracyPercent()
            weeklyActivity = viewModel.getWeeklyActivity()
        }
    }

    // Fake daily data for bar chart (last 7 days activity → mapped to count range)
    val dailyBarData = weeklyActivity.mapIndexed { i, done ->
        if (done) (5..20).random() else 0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp)) {
            Text(
                "Statistika",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "So'nggi 7 kun",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Big Numbers Grid 2×2 ────────────────────────────────────
            val statItems = listOf(
                Triple("📚", "$totalLearned", "Jami o'rganildi"),
                Triple("🔥", "$streak kun", "Kunlik seriya"),
                Triple("⚡", "$todayCount so'z", "Bugun"),
                Triple("🎯", "$accuracy%", "Aniqlik"),
            )
            val statColors = listOf(
                MaterialTheme.colorScheme.primary,
                Color(0xFFF4511E),
                Color(0xFF6750A4),
                Color(0xFF1B7F5A),
            )

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatGridCard("📚", "$totalLearned", "Jami o'rganildi", MaterialTheme.colorScheme.primary,
                    Modifier.weight(1f))
                StatGridCard("🔥", "$streak kun", "Kunlik seriya", Color(0xFFF4511E), Modifier.weight(1f))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatGridCard("⚡", "$todayCount so'z", "Bugun", Color(0xFF6750A4), Modifier.weight(1f))
                StatGridCard("🎯", "$accuracy%", "Aniqlik", Color(0xFF1B7F5A), Modifier.weight(1f))
            }

            // ── Weekly Bar Chart ─────────────────────────────────────────
            LCard {
                Text(
                    "Haftalik faollik",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 14.dp)
                )
                val maxVal = dailyBarData.maxOrNull()?.takeIf { it > 0 } ?: 1
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    dailyBarData.forEachIndexed { i, v ->
                        val animH by animateFloatAsState(
                            targetValue = (v.toFloat() / maxVal),
                            animationSpec = tween(800, delayMillis = i * 80)
                        )
                        Column(
                            Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(animH.coerceAtLeast(0.04f))
                                    .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                    .background(
                                        if (i == 6) MaterialTheme.colorScheme.surfaceVariant
                                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                                    )
                            )
                        }
                    }
                }
                // Day labels
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    WEEK_LABELS.forEach { day ->
                        Text(
                            day,
                            modifier = Modifier.weight(1f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            // ── Vocabulary Progress ──────────────────────────────────────
            LCard {
                Text(
                    "Lug'at holati",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 14.dp)
                )
                VocabProgressRow(
                    "Trilingual 2000",
                    triLearned, triTotal,
                    MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(14.dp))
                VocabProgressRow(
                    "Essential 4000",
                    essLearned, essTotal,
                    Color(0xFFF9AA33)
                )
            }

            // ── Achievements ─────────────────────────────────────────────
            Text(
                "Yutuqlar",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 4.dp)
            )

            val achievements = listOf(
                Triple("🔥", "5 kun", "Seriya"),
                Triple("📖", "100+", "O'rganildi"),
                Triple("🏆", "Unit 1", "Tugatildi"),
                Triple("⚡", "Tez", "O'quvchi"),
                Triple("🌟", "500+", "So'z"),
                Triple("🎯", "90%", "Aniqlik"),
            )
            val achievementsUnlocked = listOf(streak >= 5, totalLearned >= 100, true, false, totalLearned >= 500, accuracy >= 90)

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                repeat(3) { i ->
                    AchievementCard(achievements[i], achievementsUnlocked[i], Modifier.weight(1f))
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                repeat(3) { i ->
                    AchievementCard(achievements[i + 3], achievementsUnlocked[i + 3], Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun StatGridCard(icon: String, value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(icon, fontSize = 22.sp, modifier = Modifier.padding(bottom = 4.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

@Composable
fun VocabProgressRow(label: String, value: Int, total: Int, color: Color) {
    val pct = if (total > 0) value.toFloat() / total else 0f
    Column {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface)
            Text("$value/$total", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(6.dp))
        ThinProgressBar(value.toFloat(), total.toFloat(), color, 8.dp)
    }
}

@Composable
fun AchievementCard(data: Triple<String, String, String>, unlocked: Boolean, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(data.first, fontSize = 24.sp,
                color = if (unlocked) Color.Unspecified else Color.Unspecified,
                modifier = Modifier.padding(bottom = 4.dp).then(
                    if (!unlocked) Modifier.wrapContentSize().then(Modifier) else Modifier
                )
            )
            Text(data.second, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
                color = if (unlocked) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
            Text(data.third, fontSize = 10.sp,
                color = if (unlocked) MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
        }
    }
}
