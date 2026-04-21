package com.lugat.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            delay(300) // Debounce
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
                    TextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Search words...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                            unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (essentialResults.isNotEmpty()) {
                item {
                    Text(
                        "Essential 4000 Results",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                items(essentialResults) { word ->
                    ListItem(
                        headlineContent = { Text(word.en, fontWeight = FontWeight.Bold) },
                        supportingContent = { Text(word.uz) },
                        trailingContent = { Text("${word.bookName} - ${word.unitName}", fontSize = 12.sp) }
                    )
                }
            }

            if (trilingualResults.isNotEmpty()) {
                item {
                    Text(
                        "Trilingual 2000 Results",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                items(trilingualResults) { word ->
                    ListItem(
                        headlineContent = { Text("${word.en} | ${word.ru}", fontWeight = FontWeight.Bold) },
                        supportingContent = { Text(word.uz) }
                    )
                }
            }

            if (query.length >= 2 && essentialResults.isEmpty() && trilingualResults.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No results found for \"$query\"")
                    }
                }
            } else if (query.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                            Text("Type at least 2 characters to search", color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }
        }
    }
}
