package com.example.instantdrs_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.instantdrs_android.ui.components.*
import com.example.instantdrs_android.ui.theme.InstantDRSAndroidTheme
import com.example.instantdrs_android.ui.theme.LocalSpacing

@Composable
fun ReplayScreen(
    sportName: String,
    eventTime: String,
    ruleName: String,
    decision: String,
    confidence: Int,
    eventDescription: String,
    onBackClick: () -> Unit,
    onFullScreenClick: () -> Unit
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBackClick) {
                    Text(
                        text = "< Back",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
            
            Text(
                text = "DRS REPLAY",
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

            // Video Replay Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.Black, shape = RoundedCornerShape(16.dp))
                    .padding(spacing.medium)
                    .clickable { isPlaying = !isPlaying }
            ) {
                Text(
                    text = "RECORDED VIDEO",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.LightGray,
                    modifier = Modifier.align(Alignment.TopCenter)
                )

                Text(
                    text = if (isPlaying) "Ⅱ" else "▶",
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )

                Text(
                    text = eventTime,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(spacing.medium))

            // Play / Pause Control
            InstantDRSSecondaryButton(
                text = if (isPlaying) "Ⅱ PAUSE" else "▶ PLAY",
                onClick = { isPlaying = !isPlaying },
                modifier = Modifier.padding(bottom = spacing.medium)
            )

            // Video Progress
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = spacing.large),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = eventTime,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Slider(
                    value = 0.3f, // Mock progress
                    onValueChange = {},
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = Color.DarkGray
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "05:32",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // DRS Event Card
            InstantDRSCard(modifier = Modifier.padding(bottom = spacing.medium)) {
                Text(
                    text = "DRS EVENT",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = spacing.small)
                )
                
                ReviewInfoRow(label = "Time", value = eventTime)
                ReviewInfoRow(label = "Rule", value = ruleName)
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Decision",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    InstantDRSStatusBadge(
                        text = decision,
                        containerColor = if (decision.uppercase() == "BALL IN") Color(0xFF4CAF50) else Color(0xFFE53935)
                    )
                }
                
                ReviewInfoRow(label = "Confidence", value = "$confidence%")
            }

            // Event Description Card
            InstantDRSCard(modifier = Modifier.padding(bottom = spacing.large)) {
                Text(
                    text = "EVENT DESCRIPTION",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = spacing.small)
                )
                Text(
                    text = eventDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Full Screen Button
            InstantDRSButton(
                text = "FULL SCREEN",
                onClick = onFullScreenClick,
                modifier = Modifier.padding(bottom = spacing.medium)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReplayScreenPreview() {
    InstantDRSAndroidTheme {
        ReplayScreen(
            sportName = "Volleyball",
            eventTime = "00:48",
            ruleName = "Ball In / Out",
            decision = "BALL IN",
            confidence = 94,
            eventDescription = "The selected DRS event shows the ball landing close to the boundary line.",
            onBackClick = {},
            onFullScreenClick = {}
        )
    }
}
