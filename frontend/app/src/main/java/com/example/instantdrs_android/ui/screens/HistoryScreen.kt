package com.example.instantdrs_android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.instantdrs_android.ui.components.InstantDRSCard
import com.example.instantdrs_android.ui.components.InstantDRSScreenContainer
import com.example.instantdrs_android.ui.theme.InstantDRSAndroidTheme
import com.example.instantdrs_android.ui.theme.LocalSpacing

data class SavedReview(
    val id: Int,
    val sportName: String,
    val gameName: String,
    val ruleName: String,
    val decision: String,
    val dateTime: String
)

@Composable
fun HistoryScreen(
    reviews: List<SavedReview> = emptyList(),
    onNavigateBack: () -> Unit = {}
) {
    InstantDRSScreenContainer {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "HISTORY",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = LocalSpacing.current.large)
            )

            Text(
                text = "Previous Games",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = LocalSpacing.current.medium)
            )

            if (reviews.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "NO SAVED REVIEWS",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(LocalSpacing.current.small))
                        Text(
                            text = "Your saved DRS reviews will appear here.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(LocalSpacing.current.medium)
                ) {
                    items(reviews) { review ->
                        InstantDRSCard {
                            Text(
                                text = review.gameName.uppercase(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = review.sportName,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = LocalSpacing.current.small)
                            )
                            
                            Text(
                                text = "Rule: ${review.ruleName}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Decision: ${review.decision}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Date: ${review.dateTime}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = LocalSpacing.current.small)
                            )
                        }
                    }
                }
            }

            TextButton(
                onClick = onNavigateBack,
                modifier = Modifier.padding(top = LocalSpacing.current.medium)
            ) {
                Text("< Back", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    InstantDRSAndroidTheme {
        HistoryScreen(
            reviews = listOf(
                SavedReview(
                    id = 1,
                    sportName = "Volleyball",
                    gameName = "FINAL MATCH",
                    ruleName = "Ball In / Out",
                    decision = "BALL IN",
                    dateTime = "20 Aug 2026, 19:45"
                )
            )
        )
    }
}
