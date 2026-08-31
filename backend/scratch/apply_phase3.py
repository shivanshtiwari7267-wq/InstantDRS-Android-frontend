import os

base_dir = r"C:\Users\Shivansh Tiwari\InstantDRS\InstantDRS-fronten\app\src\main\java\com\example\instantdrs_android"
screens_dir = os.path.join(base_dir, "ui", "screens")

build_gradle_path = r"C:\Users\Shivansh Tiwari\InstantDRS\InstantDRS-fronten\app\build.gradle.kts"
main_activity_path = os.path.join(base_dir, "MainActivity.kt")
recorded_videos_screen_path = os.path.join(screens_dir, "RecordedVideosScreen.kt")
video_player_screen_path = os.path.join(screens_dir, "VideoPlayerScreen.kt")

# 1. Update build.gradle.kts
with open(build_gradle_path, "r", encoding="utf-8") as f:
    bg_content = f.read()

if "media3-exoplayer" not in bg_content:
    bg_content = bg_content.replace(
        'implementation("androidx.camera:camera-video:1.3.4")',
        'implementation("androidx.camera:camera-video:1.3.4")\n    implementation("androidx.media3:media3-exoplayer:1.3.1")\n    implementation("androidx.media3:media3-ui:1.3.1")'
    )
    with open(build_gradle_path, "w", encoding="utf-8") as f:
        f.write(bg_content)

# 2. Create RecordedVideosScreen.kt
recorded_videos_content = """package com.example.instantdrs_android.ui.screens

import android.os.Environment
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.instantdrs_android.ui.components.InstantDRSScreenContainer
import com.example.instantdrs_android.ui.components.InstantDRSCard
import com.example.instantdrs_android.ui.theme.LocalSpacing
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RecordedVideosScreen(
    onVideoClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val spacing = LocalSpacing.current
    var videos by remember { mutableStateOf<List<File>>(emptyList()) }

    LaunchedEffect(Unit) {
        val moviesDir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        if (moviesDir != null && moviesDir.exists()) {
            videos = moviesDir.listFiles { file ->
                file.isFile && file.name.endsWith(".mp4")
            }?.sortedByDescending { it.lastModified() } ?: emptyList()
        }
    }

    InstantDRSScreenContainer {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "RECORDED VIDEOS",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = spacing.medium)
            )

            if (videos.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No recordings yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(videos) { file ->
                        VideoItemCard(
                            file = file,
                            onClick = { onVideoClick(file.absolutePath) }
                        )
                        Spacer(modifier = Modifier.height(spacing.small))
                    }
                }
            }

            TextButton(
                onClick = onBackClick,
                modifier = Modifier.padding(top = spacing.medium, bottom = spacing.medium)
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
fun VideoItemCard(file: File, onClick: () -> Unit) {
    val spacing = LocalSpacing.current
    val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault())
    val dateString = formatter.format(Date(file.lastModified()))
    val sizeMb = file.length() / (1024 * 1024f)

    InstantDRSCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(spacing.small)) {
            Text(
                text = file.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = String.format(Locale.US, "%.1f MB", sizeMb),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
"""
with open(recorded_videos_screen_path, "w", encoding="utf-8") as f:
    f.write(recorded_videos_content)


# 3. Create VideoPlayerScreen.kt
video_player_content = """package com.example.instantdrs_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.instantdrs_android.ui.components.InstantDRSScreenContainer
import com.example.instantdrs_android.ui.theme.LocalSpacing
import java.io.File

@Composable
fun VideoPlayerScreen(
    videoPath: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val spacing = LocalSpacing.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val file = File(videoPath)
            if (file.exists()) {
                setMediaItem(MediaItem.fromUri(android.net.Uri.fromFile(file)))
                prepare()
                playWhenReady = true
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    InstantDRSScreenContainer {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBackClick) {
                    Text(
                        text = "< Back",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "PLAYBACK",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.Black)
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                        }
                    }
                )
            }
        }
    }
}
"""
with open(video_player_screen_path, "w", encoding="utf-8") as f:
    f.write(video_player_content)


# 4. Modify MainActivity.kt
with open(main_activity_path, "r", encoding="utf-8") as f:
    ma_content = f.read()

# Add imports for new screens
imports_str = """import com.example.instantdrs_android.ui.screens.RecordedVideosScreen
import com.example.instantdrs_android.ui.screens.VideoPlayerScreen
"""
if "RecordedVideosScreen" not in ma_content:
    ma_content = ma_content.replace(
        'import com.example.instantdrs_android.ui.screens.SavedReview',
        'import com.example.instantdrs_android.ui.screens.SavedReview\n' + imports_str
    )

# Add enum values
if "RecordedVideos" not in ma_content:
    ma_content = ma_content.replace(
        'enum class Screen {\n    Splash, Login, Register, Home, GameRules, GameSession, Camera, RecordingPreview, DRSReviewDashboard, Timeline, Replay, History\n}',
        'enum class Screen {\n    Splash, Login, Register, Home, GameRules, GameSession, Camera, RecordingPreview, DRSReviewDashboard, Timeline, Replay, History, RecordedVideos, VideoPlayer\n}'
    )

# Add state for selectedVideoPath
if "var selectedVideoPath" not in ma_content:
    ma_content = ma_content.replace(
        'var savedReviews by remember { mutableStateOf<List<SavedReview>>(emptyList()) }',
        'var savedReviews by remember { mutableStateOf<List<SavedReview>>(emptyList()) }\n                var selectedVideoPath by remember { mutableStateOf("") }'
    )

# Update back navigation logic
if "Screen.RecordedVideos -> Screen.Camera" not in ma_content:
    ma_content = ma_content.replace(
        'Screen.RecordingPreview -> Screen.Camera',
        'Screen.RecordedVideos -> Screen.Camera\n                        Screen.VideoPlayer -> Screen.RecordedVideos\n                        Screen.RecordingPreview -> Screen.Camera'
    )

# Change Camera onViewRecordingClick to go to RecordedVideos
ma_content = ma_content.replace(
    'onViewRecordingClick = { currentScreen = Screen.RecordingPreview }',
    'onViewRecordingClick = { currentScreen = Screen.RecordedVideos }'
)

# Add screen handlers inside when block
new_screens_logic = """
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
                            onBackClick = { currentScreen = Screen.RecordedVideos }
                        )
                    }
"""
if "Screen.RecordedVideos ->" not in ma_content:
    ma_content = ma_content.replace(
        '                    Screen.RecordingPreview -> {',
        new_screens_logic + '                    Screen.RecordingPreview -> {'
    )

with open(main_activity_path, "w", encoding="utf-8") as f:
    f.write(ma_content)

print("Applied Phase 3 modifications.")
