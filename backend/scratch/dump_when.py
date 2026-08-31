import os

main_activity_path = r"C:\Users\Shivansh Tiwari\InstantDRS\InstantDRS-fronten\app\src\main\java\com\example\instantdrs_android\MainActivity.kt"

with open(main_activity_path, "r", encoding="utf-8") as f:
    lines = f.readlines()

out_path = r"C:\Users\Shivansh Tiwari\InstantDRS\InstantDRS-Backend\scratch\main_when_block.txt"
with open(out_path, "w", encoding="utf-8") as out:
    recording = False
    for i, line in enumerate(lines):
        if "when (currentScreen)" in line:
            recording = True
        if recording and "}" in line and i > 250: # just rough
            pass
        if recording:
            out.write(line)

print("Dumped when block")
