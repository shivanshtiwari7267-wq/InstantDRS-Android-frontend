import argparse
import os
import subprocess
import sys
import cv2

def is_gpu_available():
    try:
        import torch
        return torch.cuda.is_available()
    except ImportError:
        return False

def validate_fps(video_path):
    if not os.path.exists(video_path):
        return None
    cap = cv2.VideoCapture(video_path)
    if not cap.isOpened():
        return None
    fps = cap.get(cv2.CAP_PROP_FPS)
    cap.release()
    return fps

def main():
    parser = argparse.ArgumentParser(description="Tool 2: Real 120 FPS Frame Interpolation using RIFE")
    parser.add_argument("--input", required=True, help="Path to input video (e.g., input/video.mp4)")
    parser.add_argument("--output", required=True, help="Path to save output video (e.g., output/video_120fps.mp4)")
    args = parser.parse_args()

    input_path = os.path.abspath(args.input)
    output_path = os.path.abspath(args.output)
    
    if not os.path.exists(input_path):
        print(f"Error: Input file does not exist at {input_path}")
        sys.exit(1)

    rife_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", "rife"))
    inference_script = os.path.join(rife_dir, "inference_video.py")
    
    if not os.path.exists(inference_script):
        print(f"Error: RIFE script not found at {inference_script}")
        sys.exit(1)

    print("Checking system requirements...")
    gpu_available = is_gpu_available()
    if gpu_available:
        print("-> GPU detected. RIFE will run using CUDA for faster interpolation.")
    else:
        print("-> WARNING: GPU not detected or PyTorch not configured for CUDA.")
        print("-> RIFE will run using CPU, which may take significantly longer.")

    os.makedirs(os.path.dirname(output_path), exist_ok=True)
    
    print(f"\nStarting 4x interpolation (30 FPS -> 120 FPS)")
    print(f"Input: {input_path}")
    print(f"Output: {output_path}\n")

    cmd = [
        sys.executable,
        inference_script,
        "--video", input_path,
        "--output", output_path,
        "--exp", "2"
    ]
    
    try:
        subprocess.run(cmd, check=True, cwd=rife_dir)
    except subprocess.CalledProcessError:
        print(f"\nError: RIFE interpolation failed. Please check the logs above.")
        sys.exit(1)
        
    print("\nInterpolation complete. Validating output FPS...")
    
    output_fps = validate_fps(output_path)
    if output_fps is None:
        print("Error: Could not validate the output file. It may be missing or corrupted.")
        sys.exit(1)
        
    print(f"Output FPS: {output_fps:.2f}")
    
    if output_fps >= 115:
        print("SUCCESS: Video successfully interpolated to ~120 FPS!")
    else:
        print(f"WARNING: Output FPS ({output_fps:.2f}) does not match expected ~120 FPS.")

if __name__ == "__main__":
    main()
