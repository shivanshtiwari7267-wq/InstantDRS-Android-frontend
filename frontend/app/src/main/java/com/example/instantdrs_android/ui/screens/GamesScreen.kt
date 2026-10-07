package com.example.instantdrs_android.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.instantdrs_android.ui.components.InstantDRSButton
import com.example.instantdrs_android.ui.components.InstantDRSCard
import com.example.instantdrs_android.ui.components.InstantDRSScreenContainer
import com.example.instantdrs_android.ui.components.InstantDRSStatusBadge
import com.example.instantdrs_android.ui.theme.InstantDRSAndroidTheme
import com.example.instantdrs_android.ui.theme.LocalSpacing

data class SampleGame(
    val id: Long,
    val name: String,
    val status: String,
    val dateTime: String,
    val ruleCount: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesScreen(
    onNavigateBack: () -> Unit = {},
    onCreateGameClick: () -> Unit = {},
    onGameClick: (Long) -> Unit = {}
) {
    val sampleGames = listOf(
        SampleGame(
            id = 1,
            name = "Championship Final",
            status = "CREATED",
            dateTime = "Today • 7:30 PM",
            ruleCount = 2
        ),
        SampleGame(
            id = 2,
            name = "Semi-Final Match",
            status = "COMPLETED",
            dateTime = "Yesterday • 4:00 PM",
            ruleCount = 5
        ),
        SampleGame(
            id = 3,
            name = "Practice Session",
            status = "ACTIVE",
            dateTime = "Today • 10:00 AM",
            ruleCount = 1
        )
    )

    // For empty state testing, you can change this list to emptyList()
    val games = sampleGames

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Games") },
                navigationIcon = {
                    androidx.compose.material3.TextButton(onClick = onNavigateBack) {
                        Text("< Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        InstantDRSScreenContainer(
            modifier = Modifier.padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Manage your games and DRS sessions",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = LocalSpacing.current.large)
                )

                if (games.isEmpty()) {
                    EmptyGameState(onCreateGameClick = onCreateGameClick)
                } else {
                    Text(
                        text = "TODAY'S GAMES",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = LocalSpacing.current.medium)
                    )

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(LocalSpacing.current.medium)
                    ) {
                        items(games) { game ->
                            GameCard(
                                game = game,
                                onClick = { onGameClick(game.id) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(LocalSpacing.current.medium))

                    InstantDRSButton(
                        text = "CREATE GAME",
                        onClick = onCreateGameClick
                    )
                }
            }
        }
    }
}

@Composable
fun GameCard(
    game: SampleGame,
    onClick: () -> Unit
) {
    val statusColor = when (game.status) {
        "CREATED" -> MaterialTheme.colorScheme.secondary
        "ACTIVE" -> Color(0xFF4CAF50) // Green color for active
        "COMPLETED" -> MaterialTheme.colorScheme.outline
        else -> MaterialTheme.colorScheme.primary
    }

    InstantDRSCard(
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = game.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            InstantDRSStatusBadge(
                text = game.status,
                containerColor = statusColor
            )
        }
        
        Spacer(modifier = Modifier.height(LocalSpacing.current.small))
        
        Text(
            text = game.dateTime,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(LocalSpacing.current.small))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${game.ruleCount} Rules",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "VIEW GAME",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onClick() }.padding(4.dp)
            )
        }
    }
}

@Composable
fun EmptyGameState(onCreateGameClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = LocalSpacing.current.extraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No games yet",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(LocalSpacing.current.small))
        Text(
            text = "Create your first game to start a DRS session.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(LocalSpacing.current.large))
        InstantDRSButton(
            text = "CREATE GAME",
            onClick = onCreateGameClick
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GamesScreenPreview() {
    InstantDRSAndroidTheme {
        GamesScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun GamesScreenEmptyPreview() {
    InstantDRSAndroidTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { paddingValues ->
            InstantDRSScreenContainer(modifier = Modifier.padding(paddingValues)) {
                EmptyGameState(onCreateGameClick = {})
            }
        }
    }
}
