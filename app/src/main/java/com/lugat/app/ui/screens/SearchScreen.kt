package com.lugat.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lugat.app.data.entity.EssentialWord
import com.lugat.app.data.entity.Word
import com.lugat.app.ui.LugatViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: LugatViewModel,
    onBack: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var trilingualResults by remember { mutableStateOf<List<Word>>(emptyList()) }
    var essentialResults by remember { mutableStateOf<List<EssentialWord>>(emptyList()) }

    LaunchedEffect(query) {
        if (query.length >= 2) {
            delay(300)
            trilingualResults = viewModel.searchWords(query)
            essentialResults = viewModel.searchEssentialWords(query)
        } else {
            trilingualResults = emptyList()
            essentialResults = emptyList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = {
                            Text(
                                "So'z qidirish...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2563EB),
                            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Essential Results
            if (essentialResults.isNotEmpty()) {
                item {
                    SearchSectionHeader(
                        title = "Essential 4000",
                        count = essentialResults.size,
                        color = Color(0xFF16A34A)
                    )
                }
                items(essentialResults) { word ->
                    SearchResultCard(
                        primaryText = word.en,
                        secondaryText = word.uz,
                        badgeText = "${word.bookName} · ${word.unitName}",
                        accentColor = Color(0xFF16A34A)
                    )
                }
            }

            // Trilingual Results
            if (trilingualResults.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(8.dp))
                    SearchSectionHeader(
                        title = "Trilingual 2000",
                        count = trilingualResults.size,
                        color = Color(0xFF2563EB)
                    )
                }
                items(trilingualResults) { word ->
                    SearchResultCard(
                        primaryText = "${word.en} · ${word.ru}",
                        secondaryText = word.uz,
                        accentColor = Color(0xFF2563EB)
                    )
                }
            }

            // Empty / Hint states
            if (query.length >= 2 && essentialResults.isEmpty() && trilingualResults.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔍", fontSize = 48.sp)
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "\"$query\" uchun natija topilmadi",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else if (query.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.surfaceVariant
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "Qidirish uchun so'z kiriting",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "Kamida 2 ta harf",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchSectionHeader(title: String, count: Int, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(Modifier.width(8.dp))
        Badge(
            containerColor = color.copy(alpha = 0.15f),
            contentColor = color
        ) {
            Text("$count", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SearchResultCard(
    primaryText: String,
    secondaryText: String,
    badgeText: String? = null,
    accentColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Accent line
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(36.dp)
                    .padding(start = 0.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(2.dp),
                    colors = CardDefaults.cardColors(containerColor = accentColor)
                ) {}
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    primaryText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    secondaryText,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (badgeText != null) {
                Text(
                    badgeText,
                    fontSize = 10.sp,
                    color = accentColor,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
