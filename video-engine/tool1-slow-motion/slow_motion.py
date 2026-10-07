import argparse
import subprocess
import sys
import os

def check_ffmpeg():
    try:
        subprocess.run(['ffmpeg', '-version'], stdout=subprocess.PIPE, stderr=subprocess.PIPE, check=True)
        return True
    except (subprocess.CalledProcessError, FileNotFoundError):
        return False

def process_video(input_path, output_path, speed_factor):
    print(f"[Tool 1] Starting slow-motion processing...")
    print(f"[Tool 1] Input: {input_path}")
    print(f"[Tool 1] Speed factor: {speed_factor}")
    
    if not os.path.exists(input_path):
        print(f"[Tool 1] Error: Input file '{input_path}' does not exist.")
        sys.exit(1)
        
    if not check_ffmpeg():
        print("[Tool 1] Error: FFmpeg is not installed or not available in the system PATH.")
        sys.exit(1)
        
    if speed_factor <= 0:
        print("[Tool 1] Error: Speed factor must be greater than 0.")
        sys.exit(1)
        
    # Ensure output directory exists
    output_dir = os.path.dirname(output_path)
    if output_dir and not os.path.exists(output_dir):
        os.makedirs(output_dir)

    pts_factor = 1.0 / speed_factor
    
    # Probe for audio stream
    probe_cmd = ['ffprobe', '-i', input_path, '-show_streams', '-select_streams', 'a', '-loglevel', 'error']
    try:
        probe_result = subprocess.run(probe_cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
        has_audio = bool(probe_result.stdout.strip())
    except Exception as e:
        print(f"[Tool 1] Warning: Could not probe for audio ({e}). Assuming video only.")
        has_audio = False

    print("[Tool 1] Processing...")
    
    if has_audio:
        tempo_filters = []
        current_tempo = speed_factor
        while current_tempo < 0.5:
            tempo_filters.append("atempo=0.5")
            current_tempo /= 0.5
        if current_tempo != 1.0:
            tempo_filters.append(f"atempo={current_tempo}")
            
        audio_filter = ",".join(tempo_filters)
        
        # If speed_factor is exactly 1.0, audio_filter might be empty, but we handled current_tempo != 1.0.
        # Let's make sure it's valid if someone passes exactly 1.0.
        if speed_factor == 1.0:
            audio_filter = "atempo=1.0"
        elif not audio_filter:
            audio_filter = "atempo=1.0"
        
        cmd = [
            'ffmpeg',
            '-y',
            '-i', input_path,
            '-filter_complex', f"[0:v]setpts={pts_factor}*PTS[v];[0:a]{audio_filter}[a]",
            '-map', '[v]',
            '-map', '[a]',
            output_path
        ]
    else:
        cmd = [
            'ffmpeg',
            '-y',
            '-i', input_path,
            '-filter:v', f"setpts={pts_factor}*PTS",
            output_path
        ]
        
    try:
        result = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
        if result.returncode != 0:
            print(f"[Tool 1] Error during FFmpeg processing:\n{result.stderr}")
            sys.exit(1)
    except Exception as e:
        print(f"[Tool 1] Unexpected error: {e}")
        sys.exit(1)

    print("[Tool 1] Completed successfully.")
    print(f"[Tool 1] Output: {output_path}")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Slow down a video using FFmpeg.")
    parser.add_argument("input", help="Path to input video file")
    parser.add_argument("output", help="Path to output video file")
    parser.add_argument("speed", type=float, help="Speed factor (e.g., 0.5 for half speed)")
    
    args = parser.parse_args()
    process_video(args.input, args.output, args.speed)
