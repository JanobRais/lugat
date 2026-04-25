package com.lugat.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lugat.app.data.entity.Word
import com.lugat.app.ui.LugatViewModel
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.min

// ── Days of week labels ──────────────────────────────────────────────────
private val WEEK_DAYS = listOf("Du", "Se", "Ch", "Pa", "Ju", "Sh", "Ya")

@Composable
fun HomeScreen(
    viewModel: LugatViewModel,
    onNavigateLearn: () -> Unit,
    onNavigateFlashcard: () -> Unit,
    onNavigateTest: () -> Unit,
) {
    val isDbInit by viewModel.isDbInitialized.collectAsState()
    val settings by viewModel.dailySettings.collectAsState()
    val dailyGoal = settings.first

    var todayDone by remember { mutableIntStateOf(0) }
    var weeklyActivity by remember { mutableStateOf(List(7) { false }) }
    var wordOfDay by remember { mutableStateOf<Word?>(null) }
    var triLearned by remember { mutableIntStateOf(0) }
    var triTotal by remember { mutableIntStateOf(2000) }
    var essLearned by remember { mutableIntStateOf(0) }
    var essTotal by remember { mutableIntStateOf(4000) }

    LaunchedEffect(isDbInit) {
        if (isDbInit) {
            todayDone = viewModel.getTodayLearnedCount()
            weeklyActivity = viewModel.getWeeklyActivity()
            wordOfDay = viewModel.getWordOfDay()
            val tStats = viewModel.getWordStats()
            triLearned = tStats["learned"] ?: 0
            triTotal = tStats["total"] ?: 2000
            val eStats = viewModel.getEssentialStats()
            essLearned = eStats["learned"] ?: 0
            essTotal = eStats["total"] ?: 4000
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        // ── Header ──────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Salom,",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "O'quvchi 👋",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Streak badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(Color(0xFFFFF3EE))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("🔥", fontSize = 18.sp)
                    Text(
                        "${viewModel.streakCount}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFF4511E)
                    )
                }
                // Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "O",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Daily Goal Card ──────────────────────────────────────────
            DailyGoalCard(
                done = todayDone,
                goal = dailyGoal,
                onFlashcard = onNavigateFlashcard,
                onTest = onNavigateTest
            )

            // ── Weekly Streak ────────────────────────────────────────────
            LCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "🔥 ${viewModel.streakCount} kunlik seriya!",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Bu hafta",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    weeklyActivity.forEachIndexed { i, done ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (done) Color(0xFFF4511E)
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    if (done) "🔥" else "·",
                                    fontSize = if (done) 18.sp else 13.sp,
                                    color = if (!done) MaterialTheme.colorScheme.onSurfaceVariant else Color.Unspecified
                                )
                            }
                            Text(
                                WEEK_DAYS[i],
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (done) MaterialTheme.colorScheme.onSurface
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // ── Continue Learning ────────────────────────────────────────
            Text(
                "Davom eting",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 4.dp)
            )

            // Trilingual card
            LCard(onClick = onNavigateLearn) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) { Text("🇷🇺", fontSize = 26.sp) }
                    Column(Modifier.weight(1f)) {
                        Text("Trilingual 2000", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface)
                        Text("$triLearned/$triTotal so'z", fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp))
                        ThinProgressBar(triLearned.toFloat(), triTotal.toFloat(), MaterialTheme.colorScheme.primary)
                    }
                    Text("›", fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Essential card
            LCard(onClick = onNavigateLearn) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFFFF3E0)),
                        contentAlignment = Alignment.Center
                    ) { Text("🇬🇧", fontSize = 26.sp) }
                    Column(Modifier.weight(1f)) {
                        Text("Essential 4000", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface)
                        Text("$essLearned/$essTotal so'z", fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp))
                        ThinProgressBar(essLearned.toFloat(), essTotal.toFloat(), Color(0xFFF9AA33))
                    }
                    Text("›", fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // ── Word of the Day ──────────────────────────────────────────
            wordOfDay?.let { word ->
                Text(
                    "Kunning so'zi",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 4.dp)
                )
                LCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                word.ru,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                "${word.uz} · ${word.en}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "so'z · word",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "Yangi",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ── Daily Goal Card with circular progress ────────────────────────────────
@Composable
fun DailyGoalCard(
    done: Int, goal: Int,
    onFlashcard: () -> Unit, onTest: () -> Unit
) {
    val pct = if (goal > 0) done.toFloat() / goal else 0f
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF006A60), Color(0xFF00897B))
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Kunlik maqsad", fontSize = 13.sp, color = Color.White.copy(0.8f), fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("$done", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        Text("/$goal", fontSize = 16.sp, color = Color.White.copy(0.7f), fontWeight = FontWeight.Medium)
                    }
                    Text("so'z o'rganildi", fontSize = 13.sp, color = Color.White.copy(0.75f))
                    Spacer(Modifier.height(12.dp))
                    // Linear progress
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White.copy(0.25f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(pct.coerceIn(0f, 1f))
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White)
                        )
                    }
                }

                Spacer(Modifier.width(16.dp))

                // Circular progress
                Box(Modifier.size(80.dp), contentAlignment = Alignment.Center) {
                    CircleProgressCanvas(pct, 80.dp, 7.dp)
                    Text(
                        "${(pct * 100).toInt()}%",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }

            // Bottom buttons
            Column {
                Spacer(Modifier.height(130.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LBtn(
                        label = "🃏 Kartalar",
                        modifier = Modifier.weight(1f),
                        onClick = onFlashcard
                    )
                    LBtn(
                        label = "✏️ Test",
                        modifier = Modifier.weight(1f),
                        onClick = onTest
                    )
                }
            }
        }
    }
}

@Composable
fun CircleProgressCanvas(progress: Float, size: Dp, strokeWidth: Dp) {
    val animProg by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(900)
    )
    Canvas(Modifier.size(size)) {
        val s = min(this.size.width, this.size.height)
        val sw = strokeWidth.toPx()
        val r = (s - sw) / 2
        val topLeft = Offset((s - r * 2) / 2f, (s - r * 2) / 2f)
        val sz = Size(r * 2, r * 2)
        // Track
        drawArc(
            color = Color.White.copy(alpha = 0.25f),
            startAngle = -90f, sweepAngle = 360f,
            useCenter = false, topLeft = topLeft, size = sz,
            style = Stroke(width = sw, cap = StrokeCap.Round)
        )
        // Progress
        drawArc(
            color = Color.White.copy(alpha = 0.9f),
            startAngle = -90f, sweepAngle = 360f * animProg,
            useCenter = false, topLeft = topLeft, size = sz,
            style = Stroke(width = sw, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun ThinProgressBar(value: Float, total: Float, color: Color, height: Dp = 6.dp) {
    val pct = if (total > 0f) (value / total).coerceIn(0f, 1f) else 0f
    val animPct by animateFloatAsState(targetValue = pct, animationSpec = tween(800))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(height / 2))
            .background(color.copy(alpha = 0.18f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animPct)
                .height(height)
                .clip(RoundedCornerShape(height / 2))
                .background(color)
        )
    }
}

@Composable
fun LCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun LBtn(
    label: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White.copy(0.18f),
    textColor: Color = Color.White,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(99.dp))
            .background(color)
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textColor)
    }
}
