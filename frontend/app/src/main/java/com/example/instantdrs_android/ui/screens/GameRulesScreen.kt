package com.example.instantdrs_android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.instantdrs_android.ui.components.InstantDRSButton
import com.example.instantdrs_android.ui.components.InstantDRSScreenContainer
import com.example.instantdrs_android.ui.theme.InstantDRSAndroidTheme
import com.example.instantdrs_android.ui.theme.LocalSpacing

@Composable
fun GameRulesScreen(
    gameTitle: String = "Volleyball",
    onNavigateBack: () -> Unit = {},
    onStartGameClick: () -> Unit = {}
) {
    val displayTitle = "${gameTitle.uppercase()} RULES"
    val rules = when (gameTitle.lowercase()) {
        "tennis" -> listOf("✓ Ball In / Out", "✓ Line/Boundary Review")
        "cricket" -> listOf("✓ Decision Review")
        else -> listOf("✓ Ball In / Out", "✓ Net Touch") // Default to Volleyball
    }

    InstantDRSScreenContainer {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = displayTitle,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = LocalSpacing.current.medium)
            )

            Text(
                text = gameTitle,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = LocalSpacing.current.large)
            )

            Text(
                text = "DRS RULES",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = LocalSpacing.current.medium)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(LocalSpacing.current.small)
            ) {
                rules.forEach { rule ->
                    Text(rule, style = MaterialTheme.typography.bodyLarge, color = androidx.compose.ui.graphics.Color.White)
                }
            }

            InstantDRSButton(
                text = "START GAME",
                onClick = onStartGameClick,
                modifier = Modifier.padding(bottom = LocalSpacing.current.medium)
            )

            TextButton(
                onClick = onNavigateBack
            ) {
                Text("< Back", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameRulesScreenPreview() {
    InstantDRSAndroidTheme {
        GameRulesScreen()
    }
}
