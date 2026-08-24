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
import com.example.instantdrs_android.ui.screens.GamesScreen

enum class Screen {
    Splash, Login, Register, Home, Games
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InstantDRSAndroidTheme {
                var currentScreen by remember { mutableStateOf(Screen.Splash) }

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
                            onGamesClick = { currentScreen = Screen.Games }
                        )
                    }
                    Screen.Games -> {
                        GamesScreen(
                            onNavigateBack = { currentScreen = Screen.Home },
                            onCreateGameClick = { /* Placeholder for CreateGameScreen */ },
                            onGameClick = { /* Placeholder for GameDetailsScreen */ }
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
