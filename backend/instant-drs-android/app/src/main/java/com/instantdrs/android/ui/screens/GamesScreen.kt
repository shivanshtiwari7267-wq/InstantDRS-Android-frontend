package com.instantdrs.android.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.instantdrs.android.data.GameRepository

@Composable
fun GamesScreen(
    gameRepository: GameRepository,
    onNavigateToCreate: () -> Unit,
    onNavigateToGameDetails: (Long) -> Unit,
    onBack: () -> Unit
) {
    val cachedGames by gameRepository.cachedGames.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Games", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onNavigateToCreate, modifier = Modifier.fillMaxWidth()) {
            Text("Create New Game")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (cachedGames.isEmpty()) {
            Text("No games created in this session. Create a new game.")
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(cachedGames) { game ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { onNavigateToGameDetails(game.id) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Name: ${game.name}", style = MaterialTheme.typography.titleMedium)
                            Text("Status: ${game.status.name}")
                            Text("Created: ${game.createdAt ?: "N/A"}")
                        }
                    }
                }
            }
        }
    }
}
