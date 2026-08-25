package com.example.instantdrs_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.instantdrs_android.ui.theme.InstantDRSAndroidTheme
import com.example.instantdrs_android.ui.screens.SplashScreen
import com.example.instantdrs_android.ui.screens.LoginScreen
import com.example.instantdrs_android.ui.screens.RegistrationScreen
import com.example.instantdrs_android.ui.screens.HomeScreen
import com.example.instantdrs_android.ui.screens.GameRulesScreen
import com.example.instantdrs_android.ui.screens.HistoryScreen
import com.example.instantdrs_android.ui.screens.GameSessionScreen
import com.example.instantdrs_android.ui.screens.CameraScreen

enum class Screen {
    Splash, Login, Register, Home, GameRules, GameSession, Camera, History
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InstantDRSAndroidTheme {
                var currentScreen by remember { mutableStateOf(Screen.Splash) }
                var selectedGame by remember { mutableStateOf("") }

                when (currentScreen) {
                    Screen.Splash -> {
                        SplashScreen(onTimeout = { currentScreen = Screen.Login })
                    }
                    Screen.Login -> {
                        LoginScreen(
                            onLoginClick = { currentScreen = Screen.Home },
                            onNavigateToRegister = { currentScreen = Screen.Register }
                        )
                    }
                    Screen.Register -> {
                        RegistrationScreen(
                            onNavigateToLogin = { currentScreen = Screen.Login }
                        )
                    }
                    Screen.Home -> {
                        HomeScreen(
                            onGameClick = { gameTitle -> 
                                selectedGame = gameTitle
                                currentScreen = Screen.GameRules 
                            },
                            onHistoryClick = { currentScreen = Screen.History }
                        )
                    }
                    Screen.GameRules -> {
                        GameRulesScreen(
                            gameTitle = selectedGame,
                            onNavigateBack = { currentScreen = Screen.Home },
                            onStartGameClick = { currentScreen = Screen.GameSession }
                        )
                    }
                    Screen.GameSession -> {
                        val rules = when (selectedGame.lowercase()) {
                            "tennis" -> listOf("Ball In / Out", "Line/Boundary Review")
                            "cricket" -> listOf("Decision Review")
                            else -> listOf("Ball In / Out", "Net Touch")
                        }
                        GameSessionScreen(
                            sportName = selectedGame,
                            rules = rules,
                            onStartRecordingClick = { currentScreen = Screen.Camera },
                            onBackClick = { currentScreen = Screen.GameRules }
                        )
                    }
                    Screen.Camera -> {
                        val rules = when (selectedGame.lowercase()) {
                            "tennis" -> listOf("Ball In / Out", "Line/Boundary Review")
                            "cricket" -> listOf("Decision Review")
                            else -> listOf("Ball In / Out", "Net Touch")
                        }
                        CameraScreen(
                            sportName = selectedGame,
                            rules = rules,
                            onViewRulesClick = { currentScreen = Screen.GameRules },
                            onBackClick = { currentScreen = Screen.GameSession }
                        )
                    }
                    Screen.History -> {
                        HistoryScreen(
                            onNavigateBack = { currentScreen = Screen.Home }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Greeting(
            name = "InstantDRS User",
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Welcome to $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    InstantDRSAndroidTheme {
        Greeting("InstantDRS User")
    }
}
