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
    onTimelineClick: () -> Unit,
    onReplayClick: () -> Unit,
    onSaveReviewClick: () -> Unit,
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
                    InstantDRSStatusBadge(
                        text = "READY",
                        containerColor = Color(0xFF4CAF50)
                    )
                }
            }

            // Evidence Preview Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .background(Color.DarkGray, shape = RoundedCornerShape(16.dp))
                    .padding(spacing.medium)
            ) {
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

                Text(
                    text = "00:12",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
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
                ReviewInfoRow(label = "Status", value = "READY FOR REVIEW")
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
            onTimelineClick = {},
            onReplayClick = {},
            onSaveReviewClick = {},
            onBackClick = {}
        )
    }
}
