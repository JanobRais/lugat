package com.lugat.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lugat.app.ui.LugatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitListScreen(
    viewModel: LugatViewModel,
    onUnitSelected: (String, String) -> Unit,
    onTestUnitSelected: (String, String, String) -> Unit,
    onBack: () -> Unit
) {
    var books by remember { mutableStateOf<List<String>>(emptyList()) }
    val selectedBook by viewModel.selectedBook.collectAsState()
    var units by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showTestDialog by remember { mutableStateOf<String?>(null) }
    val isDbInitialized by viewModel.isDbInitialized.collectAsState()

    LaunchedEffect(isDbInitialized) {
        if (isDbInitialized) {
            books = viewModel.getEssentialBooks()
            isLoading = false
        }
    }

    LaunchedEffect(selectedBook) {
        if (selectedBook != null) {
            units = viewModel.getEssentialUnitsForBook(selectedBook!!)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (selectedBook == null) "Kitob Tanlash" else selectedBook!!,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedBook != null) viewModel.selectBook(null) else onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF2563EB))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (selectedBook == null) {
                    // Book list
                    items(books) { book ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectBook(book) },
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(0xFF2563EB), Color(0xFF1D4ED8))
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.MenuBook,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(Modifier.width(14.dp))
                                Text(
                                    book,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForwardIos,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                } else {
                    // Unit list
                    items(units) { unit ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF2563EB).copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        unit.filter { it.isDigit() }.take(2).ifEmpty { "U" },
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF2563EB),
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    unit,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onUnitSelected(selectedBook!!, unit) }
                                )
                                Spacer(Modifier.width(8.dp))
                                Button(
                                    onClick = { showTestDialog = unit },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(34.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF7C3AED)
                                    )
                                ) {
                                    Text("Test", fontSize = 12.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showTestDialog != null) {
        AlertDialog(
            onDismissRequest = { showTestDialog = null },
            title = { Text("Yo'nalish Tanlang", fontWeight = FontWeight.Bold) },
            text = { Text("${showTestDialog!!} uchun test yo'nalishini tanlang") },
            confirmButton = {
                Button(
                    onClick = {
                        onTestUnitSelected(selectedBook!!, showTestDialog!!, "EN_UZ")
                        showTestDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Inglizcha → O'zbekcha")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        onTestUnitSelected(selectedBook!!, showTestDialog!!, "UZ_EN")
                        showTestDialog = null
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("O'zbekcha → Inglizcha")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}
