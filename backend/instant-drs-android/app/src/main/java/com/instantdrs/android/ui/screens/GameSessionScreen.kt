package com.instantdrs.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.instantdrs.android.data.GameRepository
import com.instantdrs.android.model.GameSessionResponse
import kotlinx.coroutines.launch
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun GameSessionScreen(
    gameId: Long,
    gameRepository: GameRepository,
    onNavigateToReview: (Long, Long, Long) -> Unit,
    onNavigateToCamera: (Long) -> Unit,
    onNavigateToProcessing: (Long, Long) -> Unit,
    onBack: () -> Unit
) {
    var session by remember { mutableStateOf<GameSessionResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var processingJobIdText by remember { mutableStateOf("1") }
    var analysisJobIdText by remember { mutableStateOf("1") }
    val coroutineScope = rememberCoroutineScope()

    fun loadSession() {
        coroutineScope.launch {
            isLoading = true
            error = null
            val result = gameRepository.getSession(gameId)
            if (result.isSuccess) {
                session = result.getOrNull()
            } else {
                error = result.exceptionOrNull()?.message ?: "Failed to load session"
            }
            isLoading = false
        }
    }

    fun createSession() {
        coroutineScope.launch {
            isLoading = true
            error = null
            val result = gameRepository.createSession(gameId)
            if (result.isSuccess) {
                session = result.getOrNull()
            } else {
                error = result.exceptionOrNull()?.message ?: "Failed to create session"
            }
            isLoading = false
        }
    }

    LaunchedEffect(gameId) {
        loadSession()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Game Session", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else if (error != null) {
            if (error!!.contains("404")) { // Session not found
                Text("No active session found for this game.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { createSession() }) {
                    Text("Create Session")
                }
            } else {
                Text("Error: $error", color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { loadSession() }) {
                    Text("Retry")
                }
            }
        } else if (session != null) {
            Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Session ID: ${session!!.id}")
                    Text("Game ID: ${session!!.gameId}")
                    Text("Status: ${session!!.status.name}")
                    Text("Started At: ${session!!.startedAt ?: "Not Started"}")
                    Text("Ended At: ${session!!.endedAt ?: "Not Ended"}")
                    session!!.recordingMetadata?.let {
                        Text("Metadata: $it")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text("Review Navigation", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = processingJobIdText,
                onValueChange = { processingJobIdText = it },
                label = { Text("Processing Job ID") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )
            OutlinedTextField(
                value = analysisJobIdText,
                onValueChange = { analysisJobIdText = it },
                label = { Text("Analysis Job ID") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { loadSession() }) {
                    Text("Refresh")
                }
                
                Button(
                    onClick = { 
                        val pId = processingJobIdText.toLongOrNull() ?: 1L
                        val aId = analysisJobIdText.toLongOrNull() ?: 1L
                        onNavigateToReview(gameId, pId, aId) 
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("View DRS Review")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    val pId = processingJobIdText.toLongOrNull() ?: 1L
                    onNavigateToProcessing(gameId, pId)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Processing Status")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onNavigateToCamera(gameId) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Record Video")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}
