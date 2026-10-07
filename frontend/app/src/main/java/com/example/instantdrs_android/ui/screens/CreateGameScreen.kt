package com.example.instantdrs_android.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.instantdrs_android.ui.components.InstantDRSButton
import com.example.instantdrs_android.ui.components.InstantDRSErrorContainer
import com.example.instantdrs_android.ui.components.InstantDRSScreenContainer
import com.example.instantdrs_android.ui.theme.InstantDRSAndroidTheme
import com.example.instantdrs_android.ui.theme.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGameScreen(
    onNavigateBack: () -> Unit = {},
    onGameCreated: () -> Unit = {}
) {
    var gameName by remember { mutableStateOf("") }
    var gameDate by remember { mutableStateOf("") }
    var gameTime by remember { mutableStateOf("") }
    
    var ruleBallInOut by remember { mutableStateOf(false) }
    var ruleNetTouch by remember { mutableStateOf(false) }
    var customRule by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Game") },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) {
                        Text("< Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        InstantDRSScreenContainer(
            modifier = Modifier.padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 80.dp) // padding for the button
            ) {
                Text(
                    text = "Set up your game and choose the rules for DRS review.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = LocalSpacing.current.large)
                )

                if (errorMessage != null) {
                    InstantDRSErrorContainer(
                        message = errorMessage!!,
                        modifier = Modifier.padding(bottom = LocalSpacing.current.medium)
                    )
                }

                // Game Name
                Text(
                    text = "GAME NAME",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = LocalSpacing.current.small)
                )
                OutlinedTextField(
                    value = gameName,
                    onValueChange = { gameName = it; errorMessage = null },
                    placeholder = { Text("Enter game name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )

                Spacer(modifier = Modifier.height(LocalSpacing.current.large))

                // Game Date & Time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(LocalSpacing.current.medium)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "GAME DATE",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = LocalSpacing.current.small)
                        )
                        OutlinedTextField(
                            value = gameDate,
                            onValueChange = { gameDate = it },
                            placeholder = { Text("YYYY-MM-DD") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "GAME TIME",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = LocalSpacing.current.small)
                        )
                        OutlinedTextField(
                            value = gameTime,
                            onValueChange = { gameTime = it },
                            placeholder = { Text("HH:MM") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(LocalSpacing.current.large))

                // DRS Rules
                Text(
                    text = "DRS RULES",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = LocalSpacing.current.small)
                )
                Text(
                    text = "Select the rules that will be available during this game.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = LocalSpacing.current.medium)
                )

                RuleCheckboxItem(
                    name = "Ball In/Out",
                    description = "Determines if the ball landed inside or outside the court lines.",
                    checked = ruleBallInOut,
                    onCheckedChange = { ruleBallInOut = it; errorMessage = null }
                )
                Spacer(modifier = Modifier.height(LocalSpacing.current.small))
                RuleCheckboxItem(
                    name = "Net Touch",
                    description = "Determines if a player touched the net during play.",
                    checked = ruleNetTouch,
                    onCheckedChange = { ruleNetTouch = it; errorMessage = null }
                )

                Spacer(modifier = Modifier.height(LocalSpacing.current.large))

                // Custom Rule
                Text(
                    text = "CUSTOM RULE",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = LocalSpacing.current.small)
                )
                Text(
                    text = "Add a custom rule for this game if required.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = LocalSpacing.current.medium)
                )
                OutlinedTextField(
                    value = customRule,
                    onValueChange = { customRule = it },
                    placeholder = { Text("Enter custom rule") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
            }

            // Create Game Button pinned to bottom
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(top = LocalSpacing.current.medium)
            ) {
                InstantDRSButton(
                    text = "CREATE GAME",
                    onClick = {
                        if (gameName.trim().isEmpty()) {
                            errorMessage = "Game Name cannot be empty."
                            return@InstantDRSButton
                        }
                        if (!ruleBallInOut && !ruleNetTouch) {
                            errorMessage = "At least one DRS rule must be selected."
                            return@InstantDRSButton
                        }
                        errorMessage = null
                        onGameCreated()
                    }
                )
            }
        }
    }
}

@Composable
fun RuleCheckboxItem(
    name: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Column(modifier = Modifier.padding(start = LocalSpacing.current.small)) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateGameScreenPreview() {
    InstantDRSAndroidTheme {
        CreateGameScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun CreateGameScreenErrorPreview() {
    InstantDRSAndroidTheme {
        // Pre-populate with empty state to simulate error if we could pass error state in preview.
        // For simplicity, just show the screen.
        CreateGameScreen()
    }
}
