package com.instantdrs.android.ui.processing

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoProcessingScreen(
    gameId: Long,
    processingJobId: Long,
    viewModel: VideoProcessingViewModel,
    onNavigateToReview: (Long, Long, Long) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Processing Status") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    TextButton(onClick = { viewModel.manualRefresh() }) {
                        Text("Refresh", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (val state = uiState) {
                    is ProcessingState.Initializing -> {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Connecting...")
                    }
                    is ProcessingState.ProcessingQueued -> {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Video uploaded successfully.")
                        Text("Processing queued...")
                    }
                    is ProcessingState.Processing -> {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Video uploaded successfully.")
                        Text("Processing video...")
                    }
                    is ProcessingState.AnalysisProcessing -> {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Video processing complete.")
                        Text("Analyzing evidence...")
                    }
                    is ProcessingState.ReviewReady -> {
                        Text("Review is ready.", style = MaterialTheme.typography.headlineSmall)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { onNavigateToReview(gameId, processingJobId, state.analysisJobId) }) {
                            Text("View DRS Review")
                        }
                    }
                    is ProcessingState.Failed -> {
                        Text(
                            text = "Processing failed: ${state.message}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Button(onClick = onNavigateBack, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
                                Text("Back to Game Session")
                            }
                            Button(onClick = { viewModel.manualRefresh() }) {
                                Text("Retry Status")
                            }
                        }
                    }
                }
            }
        }
    }
}
