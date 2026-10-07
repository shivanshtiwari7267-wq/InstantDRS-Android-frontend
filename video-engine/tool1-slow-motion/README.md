# Tool 1: Slow Motion Video Processor

## Overview
This tool is the first step in the InstantDRS video-processing engine. It takes an input video and produces a slow-motion version of it. It processes both the video and audio streams correctly, ensuring they remain synchronized.

## Why FFmpeg?
We use FFmpeg because it is the industry standard for fast, high-quality, and robust video manipulation. It allows us to process video and audio streams efficiently without writing complex and slow frame-by-frame processing logic in pure Python. 

## Inputs and Outputs
- **Input:** Any standard video file (e.g., .mp4, .avi) provided via file path.
- **Output:** A new video file containing the slowed-down video and audio.
- **Speed Factor:** A float representing the speed multiplier. 
  - `1.0` = normal speed
  - `0.5` = half speed (slow motion)
  - `0.25` = quarter speed

## Prerequisites: Checking FFmpeg Installation
This tool requires `ffmpeg` and `ffprobe` to be installed and available in your system's PATH.

To check if FFmpeg is installed, open a terminal and run:
```bash
ffmpeg -version
```

If it is not installed:
- **Windows:** Download a pre-compiled binary from [gyan.dev](https://www.gyan.dev/ffmpeg/builds/) or use a package manager like `winget install ffmpeg` or `choco install ffmpeg`. Ensure the `bin` folder is added to your system's PATH.
- **macOS:** Use Homebrew: `brew install ffmpeg`
- **Linux (Ubuntu/Debian):** `sudo apt update && sudo apt install ffmpeg`

## How to Run

1. Navigate to this tool's directory.
2. Run the script using Python.

### Example Command
```bash
python slow_motion.py input/test_video.mp4 output/slow_test_video.mp4 0.5
```

### Expected Output
```text
[Tool 1] Starting slow-motion processing...
[Tool 1] Input: input/test_video.mp4
[Tool 1] Speed factor: 0.5
[Tool 1] Processing...
[Tool 1] Completed successfully.
[Tool 1] Output: output/slow_test_video.mp4
```

## Known Limitations
- The audio pitch is preserved, but extreme slow-motion factors (e.g. 0.1) can cause audio artifacts.
- It relies on FFmpeg being present on the host system.
