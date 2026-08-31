package com.example.instantdrs_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.instantdrs_android.ui.components.*
import com.example.instantdrs_android.ui.theme.InstantDRSAndroidTheme
import com.example.instantdrs_android.ui.theme.LocalSpacing

@Composable
fun RecordingPreviewScreen(
    sportName: String,
    onSaveReviewClick: () -> Unit,
    onDiscardClick: () -> Unit,
    onReplayClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val spacing = LocalSpacing.current
    var isPlaying by remember { mutableStateOf(false) }

    InstantDRSScreenContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "RECORDING PREVIEW",
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

            // Video Preview Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .background(Color.DarkGray, shape = RoundedCornerShape(16.dp))
                    .padding(spacing.medium)
            ) {
                // Video Placeholder text
                Text(
                    text = "RECORDED VIDEO",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.LightGray,
                    modifier = Modifier.align(Alignment.TopCenter)
                )

                // Play/Pause toggle
                TextButton(
                    onClick = { isPlaying = !isPlaying },
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(
                        text = if (isPlaying) "Ⅱ" else "▶",
                        style = MaterialTheme.typography.displayMedium,
                        color = Color.White
                    )
                }

                // Duration Indicator
                Text(
                    text = "05:32",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(spacing.small))

            // Video Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = { isPlaying = !isPlaying }) {
                    Text(
                        text = if (isPlaying) "Ⅱ" else "▶",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(Color.Gray, RoundedCornerShape(2.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.3f) // Static progress
                            .height(4.dp)
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
                    )
                }
                
                Spacer(modifier = Modifier.width(spacing.small))
                
                Text(
                    text = "00:00 / 05:32",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            
            Spacer(modifier = Modifier.height(spacing.medium))

            // Recording Details
            InstantDRSCard(modifier = Modifier.padding(bottom = spacing.large)) {
                Text(
                    text = "RECORDING DETAILS",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = spacing.small)
                )
                
                DetailRow(label = "Sport", value = sportName)
                DetailRow(label = "Duration", value = "05:32")
                
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    InstantDRSStatusBadge(
                        text = "READY FOR REVIEW",
                        containerColor = Color(0xFF4CAF50)
                    )
                }
            }

            // Replay Button
            InstantDRSSecondaryButton(
                text = "REPLAY",
                onClick = onReplayClick,
                modifier = Modifier.padding(bottom = spacing.small)
            )

            InstantDRSButton(
                text = "SAVE REVIEW",
                onClick = {
                    android.util.Log.d("DRS_DEBUG", "Navigating to Dashboard via RecordingPreviewScreen dummy button!")
                    onSaveReviewClick()
                },
                modifier = Modifier.padding(bottom = spacing.small)
            )

            // Discard Button
            Button(
                onClick = onDiscardClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = spacing.medium),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F), // Destructive Red
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "DISCARD",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }

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
fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
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
fun RecordingPreviewScreenPreview() {
    InstantDRSAndroidTheme {
        RecordingPreviewScreen(
            sportName = "Volleyball",
            onSaveReviewClick = {},
            onDiscardClick = {},
            onReplayClick = {},
            onBackClick = {}
        )
    }
}
