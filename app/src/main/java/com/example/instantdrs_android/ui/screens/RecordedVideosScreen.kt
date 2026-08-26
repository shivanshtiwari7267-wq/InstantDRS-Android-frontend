package com.example.instantdrs_android.ui.screens

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
