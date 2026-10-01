package com.plarent.android.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.plarent.shared.data.PlantRepository
import com.plarent.shared.model.Plant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailScreen(
    repository: PlantRepository,
    plantId: String,
    onBack: () -> Unit,
) {
    var plant by remember { mutableStateOf<Plant?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var notFound by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(plantId) {
        isLoading = true
        errorMessage = null
        notFound = false
        try {
            val result = repository.getPlant(plantId)
            if (result == null) {
                notFound = true
            } else {
                plant = result
            }
        } catch (e: Exception) {
            errorMessage = e.message ?: "Failed to load plant"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(plant?.name ?: "Plant") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Back")
                    }
                },
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                errorMessage != null -> {
                    Text(
                        text = "Error: $errorMessage",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                    )
                }
                notFound -> {
                    Text(
                        text = "Plant not found.",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                    )
                }
                plant != null -> {
                    val current = plant!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                    ) {
                        Text(text = current.name, style = MaterialTheme.typography.headlineSmall)
                        DetailRow(label = "Species", value = current.species)
                        DetailRow(label = "Location", value = current.location)
                        DetailRow(label = "Acquired", value = current.acquiredDate)
                        DetailRow(label = "Notes", value = current.notes)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String?) {
    Column(modifier = Modifier.padding(top = 12.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Text(text = value ?: "-", style = MaterialTheme.typography.bodyLarge)
    }
}
