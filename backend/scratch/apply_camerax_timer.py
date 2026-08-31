import os

camera_screen_path = r"C:\Users\Shivansh Tiwari\InstantDRS\InstantDRS-fronten\app\src\main\java\com\example\instantdrs_android\ui\screens\CameraScreen.kt"

with open(camera_screen_path, "r", encoding="utf-8") as f:
    content = f.read()

# Add recording time state to CameraScreen
if "var recordingDuration by remember { mutableStateOf(0) }" not in content:
    content = content.replace(
        'var isFullScreen by remember { mutableStateOf(false) }',
        'var isFullScreen by remember { mutableStateOf(false) }\n    var recordingDuration by remember { mutableIntStateOf(0) }'
    )
    
    # Add timer logic
    timer_logic = """
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
    """
    content = content.replace(
        'var hasCameraPermission by remember { mutableStateOf(false) }',
        timer_logic + '\n    var hasCameraPermission by remember { mutableStateOf(false) }'
    )

# Format the timer for NormalCameraMode and FullScreenCameraMode
def format_timer_func():
    return """
@Composable
fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format(Locale.US, "%02d:%02d", minutes, secs)
}
"""

if "fun formatTime" not in content:
    content = content + "\n" + format_timer_func()

# Add imports if missing
if "import androidx.compose.runtime.mutableIntStateOf" not in content:
    content = content.replace(
        'import androidx.compose.runtime.mutableStateOf',
        'import androidx.compose.runtime.mutableStateOf\nimport androidx.compose.runtime.mutableIntStateOf'
    )

# Pass recordingDuration to modes
content = content.replace(
    'FullScreenCameraMode(\n            sportName = sportName,\n            recordingState = recordingState,',
    'FullScreenCameraMode(\n            sportName = sportName,\n            recordingState = recordingState,\n            recordingDuration = recordingDuration,'
)

content = content.replace(
    'NormalCameraMode(\n            sportName = sportName,\n            rules = rules,\n            recordingState = recordingState,',
    'NormalCameraMode(\n            sportName = sportName,\n            rules = rules,\n            recordingState = recordingState,\n            recordingDuration = recordingDuration,'
)

# Update NormalCameraMode signature
content = content.replace(
    'rules: List<String>,\n    recordingState: String,\n    videoCapture: VideoCapture<Recorder>,',
    'rules: List<String>,\n    recordingState: String,\n    recordingDuration: Int,\n    videoCapture: VideoCapture<Recorder>,'
)

# Update FullScreenCameraMode signature
content = content.replace(
    'sportName: String,\n    recordingState: String,\n    videoCapture: VideoCapture<Recorder>,',
    'sportName: String,\n    recordingState: String,\n    recordingDuration: Int,\n    videoCapture: VideoCapture<Recorder>,'
)

# Update NormalCameraMode UI
content = content.replace(
    'text = "00:00:00",\n                            style = MaterialTheme.typography.bodyLarge,',
    'text = if (recordingState == "RECORDING") "REC ${formatTime(recordingDuration)}" else "00:00:00",\n                            style = MaterialTheme.typography.bodyLarge,'
)

# Update FullScreenCameraMode UI
content = content.replace(
    'Text(\n                    text = recordingState,\n                    style = MaterialTheme.typography.titleMedium,\n                    fontWeight = FontWeight.Bold,\n                    color = statusColor\n                )',
    'Text(\n                    text = if (recordingState == "RECORDING") "REC ${formatTime(recordingDuration)}" else recordingState,\n                    style = MaterialTheme.typography.titleMedium,\n                    fontWeight = FontWeight.Bold,\n                    color = statusColor\n                )'
)

with open(camera_screen_path, "w", encoding="utf-8") as f:
    f.write(content)

print("Updated CameraScreen.kt with recording timer.")
