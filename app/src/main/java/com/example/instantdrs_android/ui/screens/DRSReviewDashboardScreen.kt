package com.example.instantdrs_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.remember
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import android.net.Uri
import android.util.Log
import com.example.instantdrs_android.ui.components.*
import com.example.instantdrs_android.ui.theme.InstantDRSAndroidTheme
import com.example.instantdrs_android.ui.theme.LocalSpacing

@Composable
fun DRSReviewDashboardScreen(
    sportName: String,
    decision: String,
    confidence: Int,
    ruleName: String,
    reviewId: String,
    status: String,
    evidenceVideoUrl: String? = null,
    onTimelineClick: () -> Unit,
    onReplayClick: () -> Unit,
    onSaveReviewClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    
    val finalUrl = if (evidenceVideoUrl != null) {
        if (evidenceVideoUrl.startsWith("http")) evidenceVideoUrl else "http://10.0.2.2:8080$evidenceVideoUrl"
    } else null
    
    Log.d("DRS_DEBUG", "Final Playback URL: $finalUrl")
    
    val exoPlayer = remember(finalUrl) {
        if (finalUrl != null) {
            ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(Uri.parse(finalUrl)))
                prepare()
                playWhenReady = true
                repeatMode = ExoPlayer.REPEAT_MODE_ALL
            }
        } else null
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer?.release()
        }
    }

    InstantDRSScreenContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "DRS REVIEW",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = sportName.uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = spacing.medium)
            )

            // Review Information Card
            InstantDRSCard(modifier = Modifier.padding(bottom = spacing.medium)) {
                Text(
                    text = "REVIEW INFORMATION",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = spacing.small)
                )
                ReviewInfoRow(label = "Game", value = sportName)
                ReviewInfoRow(label = "Review ID", value = reviewId)
                ReviewInfoRow(label = "Time", value = "10:42 PM")
                
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    val badgeColor = when (status) {
                        "COMPLETED" -> Color(0xFF4CAF50)
                        "FALLBACK_REPLAY" -> Color(0xFFFF9800)
                        "FAILED", "INVALID_VIDEO" -> Color.Red
                        else -> Color.Gray
                    }
                    InstantDRSStatusBadge(
                        text = status,
                        containerColor = badgeColor
                    )
                }
            }

            // Evidence Preview Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .background(Color.DarkGray, shape = RoundedCornerShape(16.dp))
                    .padding(if (exoPlayer == null) spacing.medium else 0.dp)
            ) {
                if (exoPlayer != null) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                player = exoPlayer
                                useController = true
                            }
                        }
                    )
                } else {
                    Text(
                        text = "DRS EVIDENCE",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.LightGray,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )

                    Text(
                        text = "▶",
                        style = MaterialTheme.typography.displayMedium,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            Spacer(modifier = Modifier.height(spacing.medium))

            // DRS Decision Card
            InstantDRSCard(modifier = Modifier.padding(bottom = spacing.medium)) {
                Text(
                    text = "DRS DECISION",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = spacing.small)
                )
                
                if (status == "FALLBACK_REPLAY") {
                    Text(
                        text = "NO DECISION",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = spacing.medium)
                    )
                    Text(
                        text = "Valid DRS decision could not be determined from this video.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Fallback Evidence Replay",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else if (status == "FAILED" || status == "INVALID_VIDEO") {
                    Text(
                        text = decision.uppercase(),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = spacing.medium)
                    )
                    Text(
                        text = "Processing failed. No valid decision or evidence available.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = decision.uppercase(),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = spacing.medium)
                    )
                    
                    Text(
                        text = "CONFIDENCE",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "$confidence%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(Color.LightGray, RoundedCornerShape(4.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(confidence / 100f)
                                .height(8.dp)
                                .background(Color(0xFF4CAF50), RoundedCornerShape(4.dp))
                        )
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
                ReviewInfoRow(label = "Decision", value = decision.uppercase())
                ReviewInfoRow(label = "Confidence", value = "$confidence%")
                ReviewInfoRow(label = "Rule", value = ruleName)
                ReviewInfoRow(label = "Status", value = status)
            }

            // Actions
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

            InstantDRSButton(
                text = "SAVE REVIEW",
                onClick = onSaveReviewClick,
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
            sportName = "Volleyball",
            decision = "BALL IN",
            confidence = 94,
            ruleName = "Ball In / Out",
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
