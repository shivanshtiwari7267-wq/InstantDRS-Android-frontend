package com.example.instantdrs_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import android.util.Log
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
import com.example.instantdrs_android.ui.screens.RecordingPreviewScreen
import com.example.instantdrs_android.ui.screens.DRSReviewDashboardScreen
import com.example.instantdrs_android.ui.screens.TimelineScreen
import com.example.instantdrs_android.ui.screens.TimelineEvent
import com.example.instantdrs_android.ui.screens.ReplayScreen
import com.example.instantdrs_android.ui.screens.SavedReview
import com.example.instantdrs_android.ui.screens.RecordedVideosScreen
import com.example.instantdrs_android.ui.screens.VideoPlayerScreen


enum class Screen {
    Splash, Login, Register, Home, GameRules, GameSession, Camera, RecordingPreview, DRSReviewDashboard, Timeline, Replay, History, RecordedVideos, VideoPlayer
}

enum class ReplaySource {
    Timeline,
    RecordingPreview,
    DRSReviewDashboard
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InstantDRSAndroidTheme {
                var currentScreen by remember { mutableStateOf(Screen.Splash) }
                var selectedGame by remember { mutableStateOf("") }
                var selectedTimelineEvent by remember { mutableStateOf<TimelineEvent?>(null) }
                var replaySource by remember { mutableStateOf(ReplaySource.Timeline) }
                var savedReviews by remember { mutableStateOf<List<SavedReview>>(emptyList()) }
                var selectedVideoPath by remember { mutableStateOf("") }
                var currentReviewResult by remember { mutableStateOf<com.example.instantdrs_android.data.remote.DrsReviewResponse?>(null) }
                var currentReviewStatus by remember { mutableStateOf("READY") }
                var currentEvidenceVideoUrl by remember { mutableStateOf<String?>(null) }

                BackHandler(
                    enabled = currentScreen !in listOf(Screen.Splash, Screen.Login, Screen.Home)
                ) {
                    currentScreen = when (currentScreen) {
                        Screen.Register -> Screen.Login
                        Screen.GameRules -> Screen.Home
                        Screen.GameSession -> Screen.GameRules
                        Screen.Camera -> Screen.GameSession
                        Screen.RecordedVideos -> Screen.Camera
                        Screen.VideoPlayer -> Screen.RecordedVideos
                        Screen.RecordingPreview -> Screen.Camera
                        Screen.DRSReviewDashboard -> Screen.Camera
                        Screen.Timeline -> Screen.DRSReviewDashboard
                        Screen.Replay -> {
                            when (replaySource) {
                                ReplaySource.RecordingPreview -> Screen.RecordingPreview
                                ReplaySource.DRSReviewDashboard -> Screen.DRSReviewDashboard
                                else -> Screen.Timeline
                            }
                        }
                        Screen.History -> Screen.Home
                        else -> currentScreen
                    }
                }

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
                            onViewRecordingClick = { currentScreen = Screen.RecordedVideos },
                            onDrsReviewClick = { videoPath ->
                                selectedVideoPath = videoPath
                                currentScreen = Screen.VideoPlayer
                            },
                            onBackClick = { currentScreen = Screen.GameSession }
                        )
                    }
                    Screen.RecordedVideos -> {
                        RecordedVideosScreen(
                            onVideoClick = { path ->
                                selectedVideoPath = path
                                currentScreen = Screen.VideoPlayer
                            },
                            onBackClick = { currentScreen = Screen.Camera }
                        )
                    }
                    Screen.VideoPlayer -> {
                        VideoPlayerScreen(
                            videoPath = selectedVideoPath,
                            onBackClick = { currentScreen = Screen.RecordedVideos },
                            onReviewComplete = { result, sport, status, evidenceUrl ->
                                currentReviewResult = result
                                selectedGame = sport
                                currentReviewStatus = status
                                currentEvidenceVideoUrl = evidenceUrl
                                currentScreen = Screen.DRSReviewDashboard
                            }
                        )
                    }
                    Screen.RecordingPreview -> {
                        RecordingPreviewScreen(
                            sportName = selectedGame,
                            onSaveReviewClick = { currentScreen = Screen.DRSReviewDashboard },
                            onDiscardClick = { currentScreen = Screen.Camera },
                            onReplayClick = {
                                replaySource = ReplaySource.RecordingPreview
                                selectedTimelineEvent = null
                                currentScreen = Screen.Replay
                            },
                            onBackClick = { currentScreen = Screen.Camera }
                        )
                    }
                    Screen.DRSReviewDashboard -> {
                        val rules = when (selectedGame.lowercase()) {
                            "tennis" -> listOf("Ball In / Out", "Line/Boundary Review")
                            "cricket" -> listOf("Decision Review")
                            else -> listOf("Ball In / Out", "Net Touch")
                        }
                        
                        val statusText = currentReviewStatus
                        val decisionText = if (statusText == "FAILED") "FAILED" 
                                           else if (statusText == "INVALID_VIDEO") "INVALID" 
                                           else if (statusText == "FALLBACK_REPLAY") "NO DECISION"
                                           else currentReviewResult?.decision ?: "PENDING"
                                           
                        val conf = (currentReviewResult?.drsConfidence?.times(100) ?: 0.0).toInt()
                        
                        val reviewIdText = if (statusText == "FAILED" || statusText == "INVALID_VIDEO") {
                            "N/A"
                        } else if (currentReviewResult?.analysisJobId != null) {
                            "DRS-${currentReviewResult?.analysisJobId}"
                        } else {
                            "N/A"
                        }
                        
                        Log.d("DRS_DEBUG", """
                            DASHBOARD DATA:
                            reviewId = $reviewIdText
                            decision = $decisionText
                            confidence = $conf
                            status = $statusText
                            rule = ${rules.firstOrNull() ?: "Decision Review"}
                        """.trimIndent())
                        
                        DRSReviewDashboardScreen(
                            sportName = selectedGame,
                            decision = decisionText,
                            confidence = conf,
                            ruleName = rules.firstOrNull() ?: "Decision Review",
                            reviewId = reviewIdText,
                            status = statusText,
                            evidenceVideoUrl = currentEvidenceVideoUrl,
                            onTimelineClick = { currentScreen = Screen.Timeline },
                            onReplayClick = { 
                                replaySource = ReplaySource.DRSReviewDashboard
                                selectedTimelineEvent = null
                                currentScreen = Screen.Replay 
                            },
                            onSaveReviewClick = { 
                                val newReview = SavedReview(
                                    id = savedReviews.size + 1,
                                    sportName = selectedGame,
                                    gameName = "Game Session ${savedReviews.size + 1}",
                                    ruleName = rules.firstOrNull() ?: "Decision Review",
                                    decision = decisionText,
                                    dateTime = java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
                                )
                                savedReviews = listOf(newReview) + savedReviews
                                android.widget.Toast.makeText(this@MainActivity, "REVIEW SAVED", android.widget.Toast.LENGTH_SHORT).show()
                                currentScreen = Screen.Home
                            },
                            onBackClick = { currentScreen = Screen.Camera }
                        )
                    }
                    Screen.Timeline -> {
                        TimelineScreen(
                            sportName = selectedGame,
                            onReplayEventClick = { event -> 
                                replaySource = ReplaySource.Timeline
                                selectedTimelineEvent = event
                                currentScreen = Screen.Replay 
                            },
                            onBackClick = { currentScreen = Screen.DRSReviewDashboard }
                        )
                    }
                    Screen.Replay -> {
                        val event = selectedTimelineEvent
                        if (event != null) {
                            ReplayScreen(
                                sportName = selectedGame,
                                eventTime = event.time,
                                ruleName = event.title,
                                decision = event.result ?: "REVIEW",
                                confidence = (currentReviewResult?.drsConfidence?.times(100) ?: 0.0).toInt(),
                                eventDescription = event.description,
                                onBackClick = { 
                                    currentScreen = when (replaySource) {
                                        ReplaySource.RecordingPreview -> Screen.RecordingPreview
                                        ReplaySource.DRSReviewDashboard -> Screen.DRSReviewDashboard
                                        else -> Screen.Timeline
                                    }
                                },
                                onFullScreenClick = { /* Placeholder */ }
                            )
                        } else {
                            ReplayScreen(
                                sportName = selectedGame,
                                eventTime = "00:00",
                                ruleName = "Recorded Game",
                                decision = "REVIEW",
                                confidence = 0,
                                eventDescription = "Recorded game preview",
                                onBackClick = { 
                                    currentScreen = when (replaySource) {
                                        ReplaySource.RecordingPreview -> Screen.RecordingPreview
                                        ReplaySource.DRSReviewDashboard -> Screen.DRSReviewDashboard
                                        else -> Screen.Timeline
                                    }
                                },
                                onFullScreenClick = { /* Placeholder */ }
                            )
                        }
                    }
                    Screen.History -> {
                        HistoryScreen(
                            reviews = savedReviews,
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
