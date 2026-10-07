# Tool 2: 120 FPS Video Interpolation

Tool 2 leverages the official RIFE (Real-Time Intermediate Flow Estimation for Video Frame Interpolation) framework to convert standard 30 FPS videos into 120 FPS by generating true intermediate frames.

## What Tool 2 Does
It takes a 30 FPS input video and performs a **4x frame interpolation** to output a 120 FPS video. It preserves the original resolution and audio while drastically smoothing out the motion.

## What is RIFE?
RIFE is a deep-learning-based video interpolation model. Instead of simply duplicating frames or changing the video metadata, RIFE intelligently analyzes motion between two consecutive frames and synthetically generates accurate in-between frames.

## Why Frame Interpolation is Needed
- **Smoothness:** A true 120 FPS video provides incredibly fluid motion, which is crucial for high-quality slow-motion or sports action.
- **Real Interpolation vs Metadata FPS Change:** Changing the FPS using metadata (e.g., via FFmpeg) simply duplicates frames or plays the video faster. Real interpolation (like RIFE) generates entirely *new* frames based on the movement of objects, resulting in smooth, continuous motion.

## Installation
1. Ensure you have Python installed (Note: Python 3.9 - 3.11 is highly recommended due to dependency restrictions).
2. Create a virtual environment:
   ```bash
   python -m venv venv
   source venv/bin/activate  # On Windows use: .\venv\Scripts\activate
   ```
3. Install the dependencies for RIFE:
   ```bash
   pip install -r ../rife/requirements.txt
   ```

> **Warning:** Newer versions of Python (like Python 3.12 or 3.13) are currently incompatible with `numpy<=1.23.5` specified in RIFE's `requirements.txt`. Please use Python 3.9 - 3.11 for the best compatibility.

## How to Run
Run the wrapper script, providing an input video and an output path:

```bash
python interpolate_120fps.py --input input/my_video.mp4 --output output/my_video_120fps.mp4
```

## GPU/CPU Requirements
- **GPU (Recommended):** An NVIDIA GPU with CUDA support is highly recommended. The script will automatically detect CUDA and use PyTorch with GPU acceleration, making it fast and efficient.
- **CPU:** The script will fall back to CPU if no GPU is detected, but interpolation will take significantly longer.

## Testing and Verification
The `interpolate_120fps.py` script automatically validates the output file upon completion by reading the generated video properties to ensure the output is actually 120 FPS.
