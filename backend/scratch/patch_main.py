import os

main_activity_path = r"C:\Users\Shivansh Tiwari\InstantDRS\InstantDRS-fronten\app\src\main\java\com\example\instantdrs_android\MainActivity.kt"

with open(main_activity_path, "r", encoding="utf-8") as f:
    lines = f.readlines()

new_screens_logic = """                    Screen.RecordedVideos -> {
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

with open(main_activity_path, "w", encoding="utf-8") as f:
    for line in lines:
        if "Screen.RecordingPreview -> {" in line:
            f.write(new_screens_logic)
        f.write(line)

print("MainActivity patched successfully")
