package com.example.instantdrs_android.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
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
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun CameraScreen(
    sportName: String,
    rules: List<String>,
    onViewRulesClick: () -> Unit,
    onViewRecordingClick: () -> Unit,
    onDrsReviewClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    var recordingState by remember { mutableStateOf("READY") }
    var isFullScreen by remember { mutableStateOf(false) }
    var recordingDuration by remember { mutableIntStateOf(0) }
    var lastRecordedVideo by remember { mutableStateOf<String?>(null) }

    
    LaunchedEffect(recordingState) {
        if (recordingState == "RECORDING") {
            while(true) {
                kotlinx.coroutines.delay(1000L)
                recordingDuration++
            }
        } else {
            recordingDuration = 0
        }
    }
    
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

    // --- CameraX Video Recording Setup ---
    val recorder = remember {
        Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
            .build()
    }
    val videoCapture = remember { VideoCapture.withOutput(recorder) }
    var activeRecording by remember { mutableStateOf<Recording?>(null) }

    val startRecording = {
        val name = "instantdrs_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis()) + ".mp4"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), name)
        val outputOptions = FileOutputOptions.Builder(file).build()

        recordingState = "RECORDING"
        activeRecording = videoCapture.output
            .prepareRecording(context, outputOptions)
            .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                if (recordEvent is VideoRecordEvent.Finalize) {
                    if (recordEvent.hasError()) {
                        recordingState = "READY"
                        Toast.makeText(context, "Recording error: ${recordEvent.error}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Video saved", Toast.LENGTH_SHORT).show()
                        lastRecordedVideo = file.absolutePath
                    }
                    activeRecording = null
                }
            }
    }

    val stopRecording = {
        activeRecording?.stop()
        recordingState = "STOPPED"
    }

    // Always release the recording if we leave the composable cleanly
    DisposableEffect(Unit) {
        onDispose {
            activeRecording?.stop()
            activeRecording = null
        }
    }
    // ------------------------------------

    if (isFullScreen) {
        FullScreenCameraMode(
            sportName = sportName,
            recordingState = recordingState,
            recordingDuration = recordingDuration,
            videoCapture = videoCapture,
            onStartRecording = startRecording,
            onStopRecording = { 
                stopRecording()
                isFullScreen = false 
            },
            onExitFullScreen = { isFullScreen = false }
        )
    } else {
        NormalCameraMode(
            sportName = sportName,
            rules = rules,
            recordingState = recordingState,
            recordingDuration = recordingDuration,
            videoCapture = videoCapture,
            onStartRecording = startRecording,
            onStopRecording = stopRecording,
            onEnterFullScreen = { isFullScreen = true },
            onViewRulesClick = onViewRulesClick,
            onViewRecordingClick = onViewRecordingClick,
            onDrsReviewClick = { 
                if (lastRecordedVideo != null) {
                    onDrsReviewClick(lastRecordedVideo!!)
                } else {
                    Toast.makeText(context, "Please record a video first", Toast.LENGTH_SHORT).show()
                }
            },
            onBackClick = onBackClick
        )
    }
}

@Composable
fun CameraPreviewView(
    videoCapture: VideoCapture<Recorder>,
    modifier: Modifier = Modifier
) {
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
                        preview,
                        videoCapture
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
    recordingDuration: Int,
    videoCapture: VideoCapture<Recorder>,
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
                            text = if (recordingState == "RECORDING") "REC ${formatTime(recordingDuration)}" else "00:00:00",
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
                CameraPreviewView(
                    videoCapture = videoCapture,
                    modifier = Modifier.fillMaxSize()
                )
                
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
    recordingDuration: Int,
    videoCapture: VideoCapture<Recorder>,
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
            CameraPreviewView(
                videoCapture = videoCapture,
                modifier = Modifier.fillMaxSize()
            )
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
                    text = if (recordingState == "RECORDING") "REC ${formatTime(recordingDuration)}" else recordingState,
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

// We mock a null VideoCapture for the preview using a dummy builder, or we just omit the preview 
// since Preview requires context and CameraX initialization. But for ComposePreview, we can just omit it or mock it.
// To keep it simple and compile safely, we'll avoid previewing the actual camera screen or pass a mocked object.
// Actually, Compose Preview can't initialize CameraX easily, so it's best to comment out the `@ComposePreview`
// if it fails, but I will provide a dummy object. Wait, `Recorder.Builder().build()` can be called in preview? 
// No, it might crash. I will comment out `@ComposePreview` to be safe and avoid build errors.
/*
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
*/


@Composable
fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format(Locale.US, "%02d:%02d", minutes, secs)
}
