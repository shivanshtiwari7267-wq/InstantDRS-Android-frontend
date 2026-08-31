import os

files_to_read = [
    r"C:\Users\Shivansh Tiwari\InstantDRS\InstantDRS-fronten\app\src\main\java\com\example\instantdrs_android\ui\screens\CameraScreen.kt",
    r"C:\Users\Shivansh Tiwari\InstantDRS\InstantDRS-fronten\app\build.gradle.kts",
    r"C:\Users\Shivansh Tiwari\InstantDRS\InstantDRS-fronten\app\src\main\AndroidManifest.xml"
]

output_file = r"C:\Users\Shivansh Tiwari\InstantDRS\InstantDRS-Backend\scratch\camera_context.txt"

with open(output_file, "w", encoding="utf-8") as out_f:
    for file_path in files_to_read:
        out_f.write(f"--- FILE: {file_path} ---\n")
        try:
            with open(file_path, "r", encoding="utf-8") as in_f:
                out_f.write(in_f.read())
        except Exception as e:
            out_f.write(f"ERROR: {str(e)}")
        out_f.write("\n\n")

print(f"Dumped context to {output_file}")
