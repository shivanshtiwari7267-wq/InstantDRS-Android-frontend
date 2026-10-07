package com.example.instantdrs_android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
fun GameSessionScreen(
    sportName: String,
    rules: List<String>,
    onStartRecordingClick: () -> Unit,
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
                text = "GAME SESSION",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = sportName.uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = spacing.large)
            )

            // Session Status Card
            InstantDRSCard(modifier = Modifier.padding(bottom = spacing.medium)) {
                Text(
                    text = "SESSION STATUS",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = spacing.small)
                )
                InstantDRSStatusBadge(
                    text = "READY",
                    containerColor = Color(0xFF4CAF50), // Green for ready status
                    contentColor = Color.White
                )
            }

            // Selected DRS Rules
            InstantDRSCard(modifier = Modifier.padding(bottom = spacing.medium)) {
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
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }

            // Recording Section Placeholder
            InstantDRSCard(modifier = Modifier.padding(bottom = spacing.large)) {
                Text(
                    text = "RECORDING",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = spacing.medium)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Camera Ready",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(spacing.small))
                    Text(
                        text = "No recording started",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Session Timer
            Text(
                text = "SESSION TIME",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "00:00",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = spacing.large)
            )

            // Main Actions
            InstantDRSButton(
                text = "START RECORDING",
                onClick = onStartRecordingClick,
                modifier = Modifier.padding(bottom = spacing.medium)
            )

            InstantDRSSecondaryButton(
                text = "DRS REVIEW",
                onClick = { /* Not implemented yet */ },
                enabled = false,
                modifier = Modifier.padding(bottom = spacing.medium)
            )

            // Back Button
            TextButton(
                onClick = onBackClick,
                modifier = Modifier.padding(vertical = spacing.small)
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

@Preview(showBackground = true)
@Composable
fun GameSessionScreenPreview() {
    InstantDRSAndroidTheme {
        GameSessionScreen(
            sportName = "Volleyball",
            rules = listOf(
                "Ball In / Out",
                "Net Touch"
            ),
            onStartRecordingClick = {},
            onBackClick = {}
        )
    }
}
