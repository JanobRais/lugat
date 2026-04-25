package com.lugat.app.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lugat.app.model.LanguageDirection
import com.lugat.app.ui.LugatViewModel

@Composable
fun SettingsScreen(
    viewModel: LugatViewModel,
    onBack: () -> Unit
) {
    val settings by viewModel.dailySettings.collectAsState()
    var wordLimit by remember { mutableIntStateOf(settings.first) }
    var essentialLimit by remember { mutableIntStateOf(settings.second) }
    var selectedDirection by remember { mutableStateOf(settings.third) }

    val triGoals = listOf(5, 10, 15, 20, 30, 40)
    val essGoals = listOf(5, 10, 15, 20, 30, 50)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp)) {
            Text("Sozlamalar", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface)
        }

        Column(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ── Profile card ─────────────────────────────────────────────
            LCard {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("O", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    Column(Modifier.weight(1f)) {
                        Text("O'quvchi", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface)
                        Text("Offline rejim faol ✓", fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // ── Learning Settings ─────────────────────────────────────────
            LCard {
                SectionLabel("O'rganish")

                // Trilingual Daily Goal
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("🇷🇺 Trilingual — Kunlik maqsad", fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                            Text("Har kuni o'rganiladigan so'zlar", fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text("$wordLimit", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary)
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        triGoals.forEach { g ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (wordLimit == g) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { wordLimit = g }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "$g",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (wordLimit == g) MaterialTheme.colorScheme.onPrimary
                                            else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(Modifier.height(14.dp))

                // Essential Daily Goal
                Column(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.fillMaxWidth().padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("🇬🇧 Essential — Kunlik maqsad", fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                            Text("Har kuni o'rganiladigan so'zlar", fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text("$essentialLimit", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFF9AA33))
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        essGoals.forEach { g ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (essentialLimit == g) Color(0xFFF9AA33)
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { essentialLimit = g }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "$g",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (essentialLimit == g) Color.White
                                            else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(Modifier.height(12.dp))

                // Language direction
                Text("O'rganish tili", fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(bottom = 8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val langOptions = listOf(
                        Triple("ru", "🇷🇺", "Ruscha"),
                        Triple("en", "🇬🇧", "Inglizcha"),
                        Triple("uz", "🇺🇿", "O'zbekcha"),
                    )
                    langOptions.forEach { (code, flag, label) ->
                        val isSelected = when (code) {
                            "ru" -> selectedDirection == LanguageDirection.RU_UZ
                            "en" -> selectedDirection == LanguageDirection.EN_UZ
                            else -> selectedDirection == LanguageDirection.UZ_EN
                        }
                        val dir = when (code) {
                            "ru" -> LanguageDirection.RU_UZ
                            "en" -> LanguageDirection.EN_UZ
                            else -> LanguageDirection.UZ_EN
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { selectedDirection = dir }
                                .padding(vertical = 10.dp, horizontal = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(flag, fontSize = 20.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                        else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // ── About ──────────────────────────────────────────────────────
            LCard {
                SectionLabel("Ilova haqida")
                SettingsRow("Versiya", "1.0.0")
                SettingsRow("Lug'at bazasi", "6000 so'z")
                SettingsRow("Offline rejim", "✓ Faol", valueColor = Color(0xFF1B7F5A))
            }

            // Save button
            Button(
                onClick = {
                    viewModel.updateSettings(wordLimit, essentialLimit, selectedDirection)
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(99.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Saqlash", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimary)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun SectionLabel(label: String) {
    Text(
        label.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SettingsRow(label: String, value: String, valueColor: Color = Color.Unspecified) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface)
        Text(value, fontSize = 13.sp, color = if (valueColor == Color.Unspecified)
            MaterialTheme.colorScheme.onSurfaceVariant else valueColor,
            fontWeight = if (valueColor == Color.Unspecified) FontWeight.Normal else FontWeight.SemiBold)
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
}
