package com.lugat.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lugat.app.ui.LugatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitListScreen(
    viewModel: LugatViewModel,
    onUnitSelected: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var books by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedBook by remember { mutableStateOf<String?>(null) }
    var units by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

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
                title = { Text(if (selectedBook == null) "Select Book" else selectedBook!!) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedBook != null) selectedBook = null else onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
                if (selectedBook == null) {
                    items(books) { book ->
                        ListItem(
                            headlineContent = { Text(book, fontWeight = FontWeight.SemiBold) },
                            modifier = Modifier.clickable { selectedBook = book }
                        )
                        Divider()
                    }
                } else {
                    items(units) { unit ->
                        ListItem(
                            headlineContent = { Text(unit) },
                            modifier = Modifier.clickable { onUnitSelected(selectedBook!!, unit) }
                        )
                        Divider()
                    }
                }
            }
        }
    }
}
