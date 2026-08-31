import os

files = [
    r"C:\Users\Shivansh Tiwari\InstantDRS\InstantDRS-fronten\app\src\main\java\com\example\instantdrs_android\MainActivity.kt",
    r"C:\Users\Shivansh Tiwari\InstantDRS\InstantDRS-fronten\app\build.gradle.kts"
]

output_file = r"C:\Users\Shivansh Tiwari\InstantDRS\InstantDRS-Backend\scratch\nav_context.txt"

with open(output_file, "w", encoding="utf-8") as out:
    for f in files:
        out.write(f"--- FILE: {f} ---\n")
        try:
            with open(f, "r", encoding="utf-8") as f_in:
                out.write(f_in.read())
        except Exception as e:
            out.write(f"ERROR: {e}\n")
        out.write("\n\n")

print(f"Dumped navigation context to {output_file}")
