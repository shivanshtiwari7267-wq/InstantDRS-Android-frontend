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

data class MockHistoryGame(val title: String, val date: String, val status: String)

val mockHistoryGames = listOf(
    MockHistoryGame("Finals Match", "20 Aug 2026", "Completed"),
    MockHistoryGame("Semi Final", "19 Aug 2026", "Completed"),
    MockHistoryGame("Quarter Final", "18 Aug 2026", "Completed")
)

@Composable
fun HistoryScreen(
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(LocalSpacing.current.medium)
            ) {
                items(mockHistoryGames) { game ->
                    InstantDRSCard {
                        Text(
                            text = game.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = game.date,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(LocalSpacing.current.small))
                        Text(
                            text = game.status,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
        HistoryScreen()
    }
}
