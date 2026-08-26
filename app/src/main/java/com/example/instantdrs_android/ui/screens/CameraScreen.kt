package com.example.instantdrs_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.instantdrs_android.ui.components.*
import com.example.instantdrs_android.ui.theme.InstantDRSAndroidTheme
import com.example.instantdrs_android.ui.theme.LocalSpacing

@Composable
fun CameraScreen(
    sportName: String,
    rules: List<String>,
    onViewRulesClick: () -> Unit,
    onViewRecordingClick: () -> Unit,
    onDrsReviewClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var recordingState by remember { mutableStateOf("READY") }
    var isFullScreen by remember { mutableStateOf(false) }

    if (isFullScreen) {
        FullScreenCameraMode(
            sportName = sportName,
            recordingState = recordingState,
            onStartRecording = { recordingState = "RECORDING" },
            onStopRecording = { 
                recordingState = "STOPPED"
                isFullScreen = false 
            },
            onExitFullScreen = { isFullScreen = false }
        )
    } else {
        NormalCameraMode(
            sportName = sportName,
            rules = rules,
            recordingState = recordingState,
            onStartRecording = { recordingState = "RECORDING" },
            onStopRecording = { recordingState = "STOPPED" },
            onEnterFullScreen = { isFullScreen = true },
            onViewRulesClick = onViewRulesClick,
            onViewRecordingClick = onViewRecordingClick,
            onDrsReviewClick = onDrsReviewClick,
            onBackClick = onBackClick
        )
    }
}

@Composable
fun NormalCameraMode(
    sportName: String,
    rules: List<String>,
    recordingState: String,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onEnterFullScreen: () -> Unit,
    onViewRulesClick: () -> Unit,
    onViewRecordingClick: () -> Unit,
    onDrsReviewClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val spacing = LocalSpacing.current

    InstantDRSScreenContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "CAMERA",
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

            // Recording Status Card
            InstantDRSCard(modifier = Modifier.padding(bottom = spacing.medium)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "RECORDING STATUS",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        val statusColor = when (recordingState) {
                            "RECORDING" -> Color(0xFFE53935) // Red
                            "READY" -> Color(0xFF4CAF50) // Green
                            else -> Color.Gray
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(statusColor, shape = RoundedCornerShape(50))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = recordingState,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        }
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "RECORDING TIME",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "00:00:00",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Camera Preview Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .background(Color.Black, shape = RoundedCornerShape(16.dp))
                    .padding(spacing.medium)
            ) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CAMERA PREVIEW",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Camera preview will appear here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.LightGray,
                        textAlign = TextAlign.Center
                    )
                }
                
                // Full Screen Button
                TextButton(
                    onClick = onEnterFullScreen,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = "□",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(spacing.medium))

            // DRS Rules Card
            InstantDRSCard(modifier = Modifier.padding(bottom = spacing.medium)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "DRS RULES",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = spacing.small)
                        )
                        rules.forEach { rule ->
                            Text(
                                text = "✓ $rule",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                    TextButton(onClick = onViewRulesClick) {
                        Text(
                            text = "VIEW RULES",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Recording Controls
            InstantDRSButton(
                text = "START RECORDING",
                onClick = onStartRecording,
                modifier = Modifier.padding(bottom = spacing.small),
                enabled = recordingState != "RECORDING"
            )

            InstantDRSSecondaryButton(
                text = "STOP RECORDING",
                onClick = onStopRecording,
                modifier = Modifier.padding(bottom = spacing.medium),
                enabled = recordingState == "RECORDING"
            )

            if (recordingState == "STOPPED") {
                InstantDRSButton(
                    text = "VIEW RECORDING",
                    onClick = onViewRecordingClick,
                    modifier = Modifier.padding(bottom = spacing.medium)
                )
            }

            // DRS Review Button
            InstantDRSButton(
                text = "DRS REVIEW",
                onClick = onDrsReviewClick,
                modifier = Modifier.padding(bottom = 4.dp),
                enabled = true
            )
            Text(
                text = "Press during recording to mark a review",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = spacing.large)
            )

            // Back Button
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
fun FullScreenCameraMode(
    sportName: String,
    recordingState: String,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onExitFullScreen: () -> Unit
) {
    val spacing = LocalSpacing.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Main Placeholder area (Camera Preview)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 200.dp) // Leave space for controls at the bottom
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "CAMERA PREVIEW",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Full-screen camera preview will appear here",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = sportName.uppercase(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.background(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                ).padding(horizontal = 12.dp, vertical = 6.dp)
            )
            TextButton(
                onClick = onExitFullScreen,
                modifier = Modifier.background(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                )
            ) {
                Text(
                    text = "X", // Exit icon
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
            }
        }

        // Bottom Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Recording Status
            val statusColor = when (recordingState) {
                "RECORDING" -> Color(0xFFE53935)
                "READY" -> Color(0xFF4CAF50)
                else -> Color.Gray
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = spacing.medium)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(statusColor, shape = RoundedCornerShape(50))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = recordingState,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }

            // Buttons
            InstantDRSButton(
                text = "START RECORDING",
                onClick = onStartRecording,
                modifier = Modifier.padding(bottom = spacing.small),
                enabled = recordingState != "RECORDING"
            )

            InstantDRSSecondaryButton(
                text = "STOP RECORDING",
                onClick = onStopRecording,
                modifier = Modifier.padding(bottom = spacing.small),
                enabled = recordingState == "RECORDING"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CameraScreenPreview() {
    InstantDRSAndroidTheme {
        CameraScreen(
            sportName = "Volleyball",
            rules = listOf("Ball In / Out", "Net Touch"),
            onViewRulesClick = {},
            onViewRecordingClick = {},
            onDrsReviewClick = {},
            onBackClick = {}
        )
    }
}
