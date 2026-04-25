package com.lugat.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lugat.app.ui.LugatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: LugatViewModel,
    onNavigateToLearn: () -> Unit,
    onNavigateToTest: () -> Unit,
    onNavigateToMistakes: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToStats: () -> Unit
) {
    val settings by viewModel.dailySettings.collectAsState()
    val activeDictionary by viewModel.activeDictionary.collectAsState()

    var wordStats by remember { mutableStateOf<Map<String, Int>?>(null) }
    var essentialStats by remember { mutableStateOf<Map<String, Int>?>(null) }

    LaunchedEffect(Unit) {
        wordStats = viewModel.getWordStats()
        essentialStats = viewModel.getEssentialStats()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Lugat",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        IconButton(onClick = onNavigateToSearch) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(onClick = onNavigateToStats) {
                            Icon(
                                Icons.Default.BarChart,
                                contentDescription = "Stats",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        // Streak Chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(Color(0xFFFF6B35), Color(0xFFFFA500))
                                    )
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(2.dp))
                                Text(
                                    "${viewModel.streakCount}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                        Spacer(Modifier.width(4.dp))
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Daily Progress Widget
            DailyProgressWidget(
                trilingualProgress = wordStats?.let { (it["learned"] ?: 0).toFloat() / (it["total"] ?: 1).coerceAtLeast(1) } ?: 0f,
                essentialProgress = essentialStats?.let { (it["learned"] ?: 0).toFloat() / (it["total"] ?: 1).coerceAtLeast(1) } ?: 0f,
                trilingualCount = "${wordStats?.get("learned") ?: 0}/${wordStats?.get("total") ?: 0}",
                essentialCount = "${essentialStats?.get("learned") ?: 0}/${essentialStats?.get("total") ?: 0}"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Dictionary Switcher
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                val options = listOf("Trilingual 2000", "Essential 4000")
                val selectedIndex = if (activeDictionary == "essential_4000") 1 else 0

                options.forEachIndexed { index, title ->
                    SegmentedButton(
                        selected = selectedIndex == index,
                        onClick = {
                            viewModel.switchDictionary(if (index == 0) "trilingual_2000" else "essential_4000")
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size)
                    ) {
                        Text(text = title, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            if (activeDictionary == "essential_4000") {
                Text(
                    text = "Essential 4000",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 16.dp)
                )

                GradientActionCard(
                    title = "Birliklarni o'rganish",
                    subtitle = "Barcha kitoblar va unitlar",
                    icon = Icons.Default.MenuBook,
                    gradientColors = listOf(Color(0xFF2563EB), Color(0xFF1D4ED8)),
                    onClick = onNavigateToLearn
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(Modifier.fillMaxWidth()) {
                    SmallGradientCard(
                        title = "Kunlik Test",
                        subtitle = "Yangi so'zlar",
                        icon = Icons.Default.Quiz,
                        gradientColors = listOf(Color(0xFF7C3AED), Color(0xFF5B21B6)),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 6.dp),
                        onClick = onNavigateToTest
                    )
                    SmallGradientCard(
                        title = "Xatolar",
                        subtitle = "Mashq qil",
                        icon = Icons.Default.ErrorOutline,
                        gradientColors = listOf(Color(0xFFDC2626), Color(0xFFB91C1C)),
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 6.dp),
                        onClick = onNavigateToMistakes
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth()) {
                    SmallGradientCard(
                        title = "Ko'rib chiqish",
                        subtitle = "O'rganilganlar",
                        icon = Icons.Default.PlayArrow,
                        gradientColors = listOf(Color(0xFF059669), Color(0xFF047857)),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 6.dp),
                        onClick = onNavigateToTest
                    )
                    SmallGradientCard(
                        title = "Aralash",
                        subtitle = "Barcha rejim",
                        icon = Icons.Default.Shuffle,
                        gradientColors = listOf(Color(0xFFD97706), Color(0xFFB45309)),
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 6.dp),
                        onClick = onNavigateToTest
                    )
                }
            } else {
                Text(
                    text = "Trilingual 2000",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 16.dp)
                )

                GradientActionCard(
                    title = "Yangi so'zlar o'rganish",
                    subtitle = "Bugun: ${settings.first} so'z",
                    icon = Icons.Default.MenuBook,
                    gradientColors = listOf(Color(0xFF2563EB), Color(0xFF1D4ED8)),
                    onClick = onNavigateToLearn
                )
                Spacer(modifier = Modifier.height(12.dp))
                GradientActionCard(
                    title = "Kunlik Test",
                    subtitle = "Bilimingizni tekshiring",
                    icon = Icons.Default.Quiz,
                    gradientColors = listOf(Color(0xFF7C3AED), Color(0xFF5B21B6)),
                    onClick = onNavigateToTest
                )
                Spacer(modifier = Modifier.height(12.dp))
                GradientActionCard(
                    title = "Xatolarni ko'rib chiqish",
                    subtitle = "Noto'g'ri so'zlarni mashq qiling",
                    icon = Icons.Default.ErrorOutline,
                    gradientColors = listOf(Color(0xFFDC2626), Color(0xFFB91C1C)),
                    onClick = onNavigateToMistakes
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun DailyProgressWidget(
    trilingualProgress: Float,
    essentialProgress: Float,
    trilingualCount: String,
    essentialCount: String
) {
    val animatedTrilingual by animateFloatAsState(
        targetValue = trilingualProgress,
        animationSpec = tween(durationMillis = 800)
    )
    val animatedEssential by animateFloatAsState(
        targetValue = essentialProgress,
        animationSpec = tween(durationMillis = 900)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                "Kunlik Progress",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(16.dp))

            ProgressRow(
                label = "Trilingual 2000",
                progress = animatedTrilingual,
                count = trilingualCount,
                color = Color(0xFF2563EB)
            )
            Spacer(modifier = Modifier.height(14.dp))
            ProgressRow(
                label = "Essential 4000",
                progress = animatedEssential,
                count = essentialCount,
                color = Color(0xFF16A34A)
            )
        }
    }
}

@Composable
fun ProgressRow(label: String, progress: Float, count: String, color: Color) {
    Column {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                count,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.15f),
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
fun GradientActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(gradientColors))
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
            }
        }
    }
}

@Composable
fun SmallGradientCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(90.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(gradientColors))
                .padding(14.dp),
        ) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

// Legacy kept for compatibility (not used)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionCard(title: String, subtitle: String, onClick: () -> Unit) {
    GradientActionCard(
        title = title,
        subtitle = subtitle,
        icon = Icons.Default.PlayArrow,
        gradientColors = listOf(Color(0xFF2563EB), Color(0xFF1D4ED8)),
        onClick = onClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallActionCard(title: String, subtitle: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
