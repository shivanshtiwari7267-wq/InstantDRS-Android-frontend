package com.example.instantdrs_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.instantdrs_android.ui.components.InstantDRSScreenContainer
import com.example.instantdrs_android.ui.components.InstantDRSButton
import com.example.instantdrs_android.ui.theme.LocalSpacing
import com.example.instantdrs_android.data.remote.DrsReviewResponse
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    videoPath: String,
    viewModel: DrsReviewViewModel = viewModel(),
    onBackClick: () -> Unit,
    onReviewComplete: (DrsReviewResponse?, String, String, String?) -> Unit
) {
    val context = LocalContext.current
    val spacing = LocalSpacing.current

    val uiState by viewModel.uiState.collectAsState()
    val progress by viewModel.progressPercent.collectAsState()
    val drsResult by viewModel.drsResult.collectAsState()
    val evidenceVideoUrl by viewModel.evidenceVideoUrl.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    var showDrsDialog by remember { mutableStateOf(false) }
    var selectedSport by remember { mutableStateOf("TENNIS") }
    var selectedRule by remember { mutableStateOf("Ball In / Out") }
    var isSportDropdownExpanded by remember { mutableStateOf(false) }
    var isRuleDropdownExpanded by remember { mutableStateOf(false) }
    
    val sports = listOf("TENNIS", "CRICKET", "VOLLEYBALL")
    val rulesMap = mapOf(
        "TENNIS" to listOf("Ball In / Out", "Line/Boundary Review"),
        "CRICKET" to listOf("Decision Review", "LBW"),
        "VOLLEYBALL" to listOf("Ball In / Out", "Net Touch")
    )

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val file = File(videoPath)
            if (file.exists()) {
                setMediaItem(MediaItem.fromUri(android.net.Uri.fromFile(file)))
                prepare()
                playWhenReady = true
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
            viewModel.resetState()
        }
    }
    
    LaunchedEffect(uiState, drsResult, evidenceVideoUrl) {
        if (uiState == DrsReviewState.COMPLETED && drsResult != null) {
            onReviewComplete(drsResult, selectedSport, "COMPLETED", null)
        } else if (uiState == DrsReviewState.FALLBACK_REPLAY) {
            onReviewComplete(drsResult, selectedSport, "FALLBACK_REPLAY", evidenceVideoUrl)
        } else if (uiState == DrsReviewState.FAILED) {
            onReviewComplete(null, selectedSport, "FAILED", null)
        } else if (uiState == DrsReviewState.INVALID_VIDEO) {
            onReviewComplete(null, selectedSport, "INVALID_VIDEO", null)
        }
    }

    InstantDRSScreenContainer {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBackClick) {
                    Text(
                        text = "< Back",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "PLAYBACK",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.Black)
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                        }
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(spacing.medium))
            
            if (uiState == DrsReviewState.IDLE) {
                InstantDRSButton(
                    text = "TAKE DRS / REVIEW",
                    onClick = { showDrsDialog = true },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(spacing.medium),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Status: ${uiState.name}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(spacing.small))
                    LinearProgressIndicator(
                        progress = { (progress / 100f).toFloat() },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                    Text(
                        text = "${progress.toInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
    
    if (showDrsDialog) {
        AlertDialog(
            onDismissRequest = { showDrsDialog = false },
            title = { Text("Start DRS Review") },
            text = {
                Column {
                    Text("Select Sport:")
                    ExposedDropdownMenuBox(
                        expanded = isSportDropdownExpanded,
                        onExpandedChange = { isSportDropdownExpanded = !isSportDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedSport,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSportDropdownExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = isSportDropdownExpanded,
                            onDismissRequest = { isSportDropdownExpanded = false }
                        ) {
                            sports.forEach { sport ->
                                DropdownMenuItem(
                                    text = { Text(sport) },
                                    onClick = {
                                        selectedSport = sport
                                        selectedRule = rulesMap[sport]?.firstOrNull() ?: ""
                                        isSportDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Select Rule:")
                    ExposedDropdownMenuBox(
                        expanded = isRuleDropdownExpanded,
                        onExpandedChange = { isRuleDropdownExpanded = !isRuleDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedRule,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isRuleDropdownExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = isRuleDropdownExpanded,
                            onDismissRequest = { isRuleDropdownExpanded = false }
                        ) {
                            rulesMap[selectedSport]?.forEach { rule ->
                                DropdownMenuItem(
                                    text = { Text(rule) },
                                    onClick = {
                                        selectedRule = rule
                                        isRuleDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDrsDialog = false
                        viewModel.startDrsReview(videoPath, selectedSport, selectedRule)
                    }
                ) {
                    Text("START")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDrsDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }
}
