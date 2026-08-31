package com.instantdrs.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.instantdrs.android.data.GameRepository
import com.instantdrs.android.model.GameResponse
import com.instantdrs.android.model.RuleResponse
import kotlinx.coroutines.launch

@Composable
fun GameDetailsScreen(
    gameId: Long,
    gameRepository: GameRepository,
    onNavigateToSession: (Long) -> Unit,
    onBack: () -> Unit
) {
    var game by remember { mutableStateOf<GameResponse?>(null) }
    var rules by remember { mutableStateOf<List<RuleResponse>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    var showRulesDialog by remember { mutableStateOf(false) }
    var rulesInput by remember { mutableStateOf("") }
    
    val coroutineScope = rememberCoroutineScope()

    fun loadData() {
        coroutineScope.launch {
            isLoading = true
            error = null
            val gameResult = gameRepository.getGame(gameId)
            val rulesResult = gameRepository.getGameRules(gameId)
            
            if (gameResult.isSuccess) {
                game = gameResult.getOrNull()
                rules = rulesResult.getOrNull()
            } else {
                error = gameResult.exceptionOrNull()?.message ?: "Failed to load game"
            }
            isLoading = false
        }
    }

    LaunchedEffect(gameId) {
        loadData()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (error != null) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { loadData() }) { Text("Retry") }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onBack) { Text("Back") }
        } else if (game != null) {
            Text("Game Details", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("ID: ${game!!.id}")
                    Text("Name: ${game!!.name}", style = MaterialTheme.typography.titleMedium)
                    Text("Status: ${game!!.status.name}")
                    Text("Created At: ${game!!.createdAt ?: "N/A"}")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Selected Rules:", style = MaterialTheme.typography.titleMedium)
            if (rules.isNullOrEmpty()) {
                Text("No rules selected.")
            } else {
                rules!!.forEach { rule ->
                    Text("- [${rule.id}] ${rule.name} (${rule.ruleType})")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { showRulesDialog = true }) {
                Text("Set Rules")
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = {
                // To keep it simple, we check if there's a session or create one.
                // It's better to navigate to a screen that handles the session.
                onNavigateToSession(game!!.id)
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Manage Game Session")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Back")
            }
        }
        
        if (showRulesDialog) {
            AlertDialog(
                onDismissRequest = { showRulesDialog = false },
                title = { Text("Set Game Rules") },
                text = {
                    Column {
                        Text("Enter Rule IDs separated by commas (e.g. 1, 2):")
                        OutlinedTextField(
                            value = rulesInput,
                            onValueChange = { rulesInput = it }
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val ids = rulesInput.split(",")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }
                            .mapNotNull { it.toLongOrNull() }
                        
                        if (ids.isNotEmpty()) {
                            coroutineScope.launch {
                                isLoading = true
                                val result = gameRepository.setGameRules(gameId, ids)
                                if (result.isSuccess) {
                                    loadData() // Refresh
                                } else {
                                    error = result.exceptionOrNull()?.message
                                }
                                isLoading = false
                            }
                        }
                        showRulesDialog = false
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRulesDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
