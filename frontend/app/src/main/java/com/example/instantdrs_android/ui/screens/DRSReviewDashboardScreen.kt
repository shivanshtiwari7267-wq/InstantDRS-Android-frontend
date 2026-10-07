package com.example.instantdrs_android.ui.screens

import android.app.Activity
import android.content.pm.ActivityInfo
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.instantdrs_android.R
import com.example.instantdrs_android.ui.components.InstantDRSButton
import com.example.instantdrs_android.ui.components.InstantDRSCard
import com.example.instantdrs_android.ui.components.InstantDRSScreenContainer
import com.example.instantdrs_android.ui.components.InstantDRSSecondaryButton
import com.example.instantdrs_android.ui.theme.InstantDRSAndroidTheme
import kotlinx.coroutines.delay

@Composable
fun DRSReviewDashboardScreen(
    sportName: String,
    ruleName: String,
    reviewId: String,
    status: String,
    evidenceVideoUrl: String?,
    onTimelineClick: () -> Unit,
    onReplayClick: () -> Unit,
    onSaveReviewClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val spacing = MaterialTheme.colorScheme.run {
        object {
            val small = 8.dp
            val medium = 16.dp
            val large = 24.dp
        }
    }

    val displayUrl = if (evidenceVideoUrl != null && evidenceVideoUrl.startsWith("/")) {
        "${com.example.instantdrs_android.data.remote.RetrofitClient.BASE_URL.removeSuffix("/")}$evidenceVideoUrl"
    } else {
        evidenceVideoUrl
    }

    Log.d("DRS_DEBUG", "EVIDENCE_URL: $displayUrl")

    var selectedDecision by remember { mutableStateOf<String?>(null) }
    var isFullScreen by remember { mutableStateOf(false) }
    var playbackPositionMs by remember { mutableLongStateOf(0L) }
    var isPlaying by remember { mutableStateOf(true) }
    var videoDurationMs by remember { mutableLongStateOf(0L) }

    // Sport-specific decision options
    val decisionOptions = when (sportName.uppercase().trim()) {
        "CRICKET" -> listOf("OUT", "NOT OUT", "INCONCLUSIVE")
        "TENNIS" -> listOf("IN", "OUT", "INCONCLUSIVE")
        "VOLLEYBALL" -> listOf("IN", "OUT", "INCONCLUSIVE")
        "BASKETBALL" -> listOf("IN", "OUT", "INCONCLUSIVE")
        "FOOTBALL" -> listOf("GOAL", "NO GOAL", "INCONCLUSIVE")
        else -> listOf("CONFIRM CALL", "OVERTURN CALL", "INCONCLUSIVE")
    }

    // Inline ExoPlayer Instance for the processed DRS review video
    val exoPlayer = remember(displayUrl) {
        if (displayUrl != null) {
            ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(displayUrl))
                prepare()
                playWhenReady = true
            }
        } else {
            null
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer?.release()
        }
    }

    // Continuously update position & duration for timeline controls
    LaunchedEffect(exoPlayer) {
        while (exoPlayer != null) {
            playbackPositionMs = exoPlayer.currentPosition
            if (exoPlayer.duration > 0) {
                videoDurationMs = exoPlayer.duration
            }
            isPlaying = exoPlayer.isPlaying
            delay(200)
        }
    }

    InstantDRSScreenContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Screen Title & Subtitle
            Text(
                text = "DRS REVIEW",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "Review the processed video and make your final decision.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = spacing.medium)
            )

            // Processed DRS Video Player Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black)
            ) {
                if (exoPlayer != null && displayUrl != null) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                player = if (isFullScreen) null else exoPlayer
                                useController = false
                                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                            }
                        },
                        update = { playerView ->
                            val target = if (isFullScreen) null else exoPlayer
                            if (playerView.player != target) {
                                playerView.player = target
                            }
                        }
                    )

                    // Minimal overlay Full-Screen Icon Button on top right of video
                    IconButton(
                        onClick = {
                            playbackPositionMs = exoPlayer.currentPosition
                            isFullScreen = true
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(36.dp)
                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_fullscreen),
                            contentDescription = "Full Screen",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "DRS EVIDENCE",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (status == "FAILED") "Processing Failed" else "Video Unavailable",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.small))

            // Inline Video Controls (-5s, Play/Pause, +15s, Seekbar, Full Screen Icon)
            if (exoPlayer != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.small)
                ) {
                    // Timeline Slider & Duration
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatDuration(playbackPositionMs),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Slider(
                            value = if (videoDurationMs > 0) (playbackPositionMs.toFloat() / videoDurationMs.toFloat()).coerceIn(0f, 1f) else 0f,
                            onValueChange = { frac ->
                                if (videoDurationMs > 0) {
                                    val targetMs = (frac * videoDurationMs).toLong()
                                    exoPlayer.seekTo(targetMs)
                                    playbackPositionMs = targetMs
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        )
                        Text(
                            text = formatDuration(videoDurationMs),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Transport Buttons (-5s, Play/Pause, +15s, Full Screen Icon)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                val newPos = (exoPlayer.currentPosition - 5000L).coerceAtLeast(0L)
                                exoPlayer.seekTo(newPos)
                                playbackPositionMs = newPos
                            }
                        ) {
                            Text("⏪ -5s")
                        }

                        Button(
                            onClick = {
                                if (exoPlayer.isPlaying) {
                                    exoPlayer.pause()
                                    isPlaying = false
                                } else {
                                    exoPlayer.play()
                                    isPlaying = true
                                }
                            }
                        ) {
                            Text(if (isPlaying) "PAUSE Ⅱ" else "PLAY ▶")
                        }

                        OutlinedButton(
                            onClick = {
                                val newPos = (exoPlayer.currentPosition + 15000L).coerceAtMost(exoPlayer.duration)
                                exoPlayer.seekTo(newPos)
                                playbackPositionMs = newPos
                            }
                        ) {
                            Text("+15s ⏩")
                        }

                        // Full Screen Control: Clean minimal icon only (no text, no large colored button)
                        IconButton(
                            onClick = {
                                playbackPositionMs = exoPlayer.currentPosition
                                isFullScreen = true
                            },
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_fullscreen),
                                contentDescription = "Full Screen",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.medium))

            // Human Decision Controls Card
            InstantDRSCard(modifier = Modifier.padding(bottom = spacing.medium)) {
                Text(
                    text = "FINAL DECISION",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Text(
                    text = "Select official call based on video evidence:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = spacing.medium)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    decisionOptions.forEach { option ->
                        val isSelected = selectedDecision == option
                        val containerColor = if (isSelected) {
                            when (option.uppercase()) {
                                "OUT", "NO GOAL", "OVERTURN CALL" -> Color(0xFFD32F2F)
                                "NOT OUT", "IN", "GOAL", "CONFIRM CALL" -> Color(0xFF2E7D32)
                                else -> MaterialTheme.colorScheme.primary
                            }
                        } else {
                            MaterialTheme.colorScheme.surface
                        }

                        val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(containerColor)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedDecision = option },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = contentColor
                            )
                        }
                    }
                }
            }

            // Review Summary Card
            InstantDRSCard(modifier = Modifier.padding(bottom = spacing.large)) {
                Text(
                    text = "REVIEW SUMMARY",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = spacing.small)
                )
                ReviewInfoRow(label = "Sport", value = sportName)
                ReviewInfoRow(label = "Rule", value = ruleName)
                ReviewInfoRow(label = "Review ID", value = reviewId)
                ReviewInfoRow(
                    label = "Decision Status",
                    value = selectedDecision ?: "AWAITING USER DECISION"
                )
            }

            // Secondary Navigation Actions
            InstantDRSSecondaryButton(
                text = "TIMELINE",
                onClick = onTimelineClick,
                modifier = Modifier.padding(bottom = spacing.small)
            )

            InstantDRSSecondaryButton(
                text = "REPLAY",
                onClick = onReplayClick,
                modifier = Modifier.padding(bottom = spacing.medium)
            )

            // Primary Save Action
            InstantDRSButton(
                text = "SAVE REVIEW",
                onClick = {
                    if (selectedDecision.isNullOrBlank()) {
                        Toast.makeText(context, "Please select a decision before saving", Toast.LENGTH_SHORT).show()
                    } else {
                        onSaveReviewClick(selectedDecision!!)
                    }
                },
                modifier = Modifier.padding(bottom = spacing.medium)
            )

            TextButton(
                onClick = onBackClick,
                modifier = Modifier.padding(bottom = spacing.medium)
            ) {
                Text(
                    text = "< Back",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    // Full Screen Video Player Overlay Dialog using the EXACT same ExoPlayer instance
    if (isFullScreen && exoPlayer != null) {
        FullScreenVideoDialog(
            exoPlayer = exoPlayer,
            onDismiss = {
                isFullScreen = false
            }
        )
    }
}

@Composable
fun FullScreenVideoDialog(
    exoPlayer: ExoPlayer,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var currentPosMs by remember { mutableLongStateOf(exoPlayer.currentPosition) }
    var durationMs by remember { mutableLongStateOf(exoPlayer.duration.coerceAtLeast(0L)) }
    var isPlaying by remember { mutableStateOf(exoPlayer.isPlaying) }

    // Lock to Landscape while Full Screen is open and hide system bars
    DisposableEffect(Unit) {
        val activity = context as? Activity
        val originalOrientation = activity?.requestedOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        val window = activity?.window
        val insetsController = window?.let { WindowCompat.getInsetsController(it, it.decorView) }
        insetsController?.hide(WindowInsetsCompat.Type.systemBars())
        insetsController?.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        onDispose {
            activity?.requestedOrientation = originalOrientation
            insetsController?.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    // Keep position, duration and play status synced with exoPlayer
    LaunchedEffect(exoPlayer) {
        while (true) {
            currentPosMs = exoPlayer.currentPosition
            if (exoPlayer.duration > 0) {
                durationMs = exoPlayer.duration
            }
            isPlaying = exoPlayer.isPlaying
            delay(200)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    }
                },
                update = { view ->
                    if (view.player != exoPlayer) {
                        view.player = exoPlayer
                    }
                }
            )

            // Top Bar (Title & Clean Exit Full Screen Icon Button)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DRS REVIEW - FULL SCREEN",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_fullscreen_exit),
                        contentDescription = "Exit Full Screen",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            // Bottom Full-Screen Overlay Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatDuration(currentPosMs),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White
                    )
                    Slider(
                        value = if (durationMs > 0) (currentPosMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f,
                        onValueChange = { frac ->
                            if (durationMs > 0) {
                                val target = (frac * durationMs).toLong()
                                exoPlayer.seekTo(target)
                                currentPosMs = target
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = formatDuration(durationMs),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            val target = (exoPlayer.currentPosition - 5000L).coerceAtLeast(0L)
                            exoPlayer.seekTo(target)
                            currentPosMs = target
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Text("⏪ -5s")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {
                            if (exoPlayer.isPlaying) {
                                exoPlayer.pause()
                                isPlaying = false
                            } else {
                                exoPlayer.play()
                                isPlaying = true
                            }
                        }
                    ) {
                        Text(if (isPlaying) "PAUSE Ⅱ" else "PLAY ▶")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    OutlinedButton(
                        onClick = {
                            val target = (exoPlayer.currentPosition + 15000L).coerceAtMost(exoPlayer.duration)
                            exoPlayer.seekTo(target)
                            currentPosMs = target
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Text("+15s ⏩")
                    }
                }
            }
        }
    }
}

private fun formatDuration(millis: Long): String {
    if (millis <= 0L) return "00:00"
    val seconds = (millis / 1000) % 60
    val minutes = (millis / (1000 * 60)) % 60
    return String.format("%02d:%02d", minutes, seconds)
}

@Composable
fun ReviewInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DRSReviewDashboardScreenPreview() {
    InstantDRSAndroidTheme {
        DRSReviewDashboardScreen(
            sportName = "Cricket",
            ruleName = "Decision Review",
            reviewId = "DRS-0001",
            status = "COMPLETED",
            evidenceVideoUrl = null,
            onTimelineClick = {},
            onReplayClick = {},
            onSaveReviewClick = {},
            onBackClick = {}
        )
    }
}
