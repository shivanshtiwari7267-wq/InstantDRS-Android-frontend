import os
import re

build_gradle_path = r"C:\Users\Shivansh Tiwari\InstantDRS\InstantDRS-fronten\app\build.gradle.kts"
manifest_path = r"C:\Users\Shivansh Tiwari\InstantDRS\InstantDRS-fronten\app\src\main\AndroidManifest.xml"
camera_screen_path = r"C:\Users\Shivansh Tiwari\InstantDRS\InstantDRS-fronten\app\src\main\java\com\example\instantdrs_android\ui\screens\CameraScreen.kt"

# 1. Update build.gradle.kts
with open(build_gradle_path, "r", encoding="utf-8") as f:
    bg_content = f.read()

camera_deps = """
    implementation("androidx.camera:camera-core:1.3.4")
    implementation("androidx.camera:camera-camera2:1.3.4")
    implementation("androidx.camera:camera-lifecycle:1.3.4")
    implementation("androidx.camera:camera-view:1.3.4")
"""
bg_content = bg_content.replace(
    "implementation(libs.androidx.lifecycle.runtime.ktx)",
    "implementation(libs.androidx.lifecycle.runtime.ktx)" + camera_deps
)
with open(build_gradle_path, "w", encoding="utf-8") as f:
    f.write(bg_content)

# 2. Update AndroidManifest.xml
with open(manifest_path, "r", encoding="utf-8") as f:
    man_content = f.read()

man_content = man_content.replace(
    '<uses-permission android:name="android.permission.INTERNET" />',
    '<uses-permission android:name="android.permission.INTERNET" />\n    <uses-permission android:name="android.permission.CAMERA" />'
)
with open(manifest_path, "w", encoding="utf-8") as f:
    f.write(man_content)


# 3. Update CameraScreen.kt
# We'll just write the entire content for CameraScreen to make it clean and easy
camera_screen_code = """package com.example.instantdrs_android.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview as ComposePreview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.instantdrs_android.ui.components.*
import com.example.instantdrs_android.ui.theme.InstantDRSAndroidTheme
import com.example.instantdrs_android.ui.theme.LocalSpacing

@Composable
fun CameraScreen(
    sportName: String,
    rules: List<String>,
    onViewRulesClick: () -> Unit,
    onViewRecordingClick: () -> Unit,
    onDrsReviewClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var recordingState by remember { mutableStateOf("READY") }
    var isFullScreen by remember { mutableStateOf(false) }

    var hasCameraPermission by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            hasCameraPermission = true
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (!hasCameraPermission) {
        InstantDRSScreenContainer {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Camera permission is required to use this feature.", color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.height(16.dp))
                    InstantDRSButton(text = "Grant Permission", onClick = {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    })
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onBackClick) { Text("Back") }
                }
            }
        }
        return
    }

    BackHandler(enabled = isFullScreen) {
        isFullScreen = false
    }

    if (isFullScreen) {
        FullScreenCameraMode(
            sportName = sportName,
            recordingState = recordingState,
            onStartRecording = { recordingState = "RECORDING" },
            onStopRecording = { 
                recordingState = "STOPPED"
                isFullScreen = false 
            },
            onExitFullScreen = { isFullScreen = false }
        )
    } else {
        NormalCameraMode(
            sportName = sportName,
            rules = rules,
            recordingState = recordingState,
            onStartRecording = { recordingState = "RECORDING" },
            onStopRecording = { recordingState = "STOPPED" },
            onEnterFullScreen = { isFullScreen = true },
            onViewRulesClick = onViewRulesClick,
            onViewRecordingClick = onViewRecordingClick,
            onDrsReviewClick = onDrsReviewClick,
            onBackClick = onBackClick
        )
    }
}

@Composable
fun CameraPreviewView(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview
                    )
                } catch(exc: Exception) {
                    exc.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))
            
            previewView
        }
    )
}

@Composable
fun NormalCameraMode(
    sportName: String,
    rules: List<String>,
    recordingState: String,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onEnterFullScreen: () -> Unit,
    onViewRulesClick: () -> Unit,
    onViewRecordingClick: () -> Unit,
    onDrsReviewClick: () -> Unit,
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
            Text(
                text = "CAMERA",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = sportName.uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = spacing.medium)
            )

            InstantDRSCard(modifier = Modifier.padding(bottom = spacing.medium)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "RECORDING STATUS",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        val statusColor = when (recordingState) {
                            "RECORDING" -> Color(0xFFE53935)
                            "READY" -> Color(0xFF4CAF50)
                            else -> Color.Gray
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(statusColor, shape = RoundedCornerShape(50))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = recordingState,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        }
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "RECORDING TIME",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "00:00:00",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .background(Color.Black, shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
            ) {
                CameraPreviewView(modifier = Modifier.fillMaxSize())
                
                TextButton(
                    onClick = onEnterFullScreen,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = "□",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(spacing.medium))

            InstantDRSCard(modifier = Modifier.padding(bottom = spacing.medium)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
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
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                    TextButton(onClick = onViewRulesClick) {
                        Text(
                            text = "VIEW RULES",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            InstantDRSButton(
                text = "START RECORDING",
                onClick = onStartRecording,
                modifier = Modifier.padding(bottom = spacing.small),
                enabled = recordingState != "RECORDING"
            )

            InstantDRSSecondaryButton(
                text = "STOP RECORDING",
                onClick = onStopRecording,
                modifier = Modifier.padding(bottom = spacing.medium),
                enabled = recordingState == "RECORDING"
            )

            if (recordingState == "STOPPED") {
                InstantDRSButton(
                    text = "VIEW RECORDING",
                    onClick = onViewRecordingClick,
                    modifier = Modifier.padding(bottom = spacing.medium)
                )
            }

            InstantDRSButton(
                text = "DRS REVIEW",
                onClick = onDrsReviewClick,
                modifier = Modifier.padding(bottom = 4.dp),
                enabled = true
            )
            Text(
                text = "Press during recording to mark a review",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = spacing.large)
            )

            TextButton(
                onClick = onBackClick,
                modifier = Modifier.padding(bottom = spacing.medium)
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
fun FullScreenCameraMode(
    sportName: String,
    recordingState: String,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onExitFullScreen: () -> Unit
) {
    val spacing = LocalSpacing.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 200.dp)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            CameraPreviewView(modifier = Modifier.fillMaxSize())
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = sportName.uppercase(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.background(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                ).padding(horizontal = 12.dp, vertical = 6.dp)
            )
            TextButton(
                onClick = onExitFullScreen,
                modifier = Modifier.background(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                )
            ) {
                Text(
                    text = "X",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val statusColor = when (recordingState) {
                "RECORDING" -> Color(0xFFE53935)
                "READY" -> Color(0xFF4CAF50)
                else -> Color.Gray
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = spacing.medium)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(statusColor, shape = RoundedCornerShape(50))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = recordingState,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }

            InstantDRSButton(
                text = "START RECORDING",
                onClick = onStartRecording,
                modifier = Modifier.padding(bottom = spacing.small),
                enabled = recordingState != "RECORDING"
            )

            InstantDRSSecondaryButton(
                text = "STOP RECORDING",
                onClick = onStopRecording,
                modifier = Modifier.padding(bottom = spacing.small),
                enabled = recordingState == "RECORDING"
            )
        }
    }
}

@ComposePreview(showBackground = true)
@Composable
fun CameraScreenPreview() {
    InstantDRSAndroidTheme {
        CameraScreen(
            sportName = "Volleyball",
            rules = listOf("Ball In / Out", "Net Touch"),
            onViewRulesClick = {},
            onViewRecordingClick = {},
            onDrsReviewClick = {},
            onBackClick = {}
        )
    }
}
"""

with open(camera_screen_path, "w", encoding="utf-8") as f:
    f.write(camera_screen_code)

print("All files updated successfully.")
