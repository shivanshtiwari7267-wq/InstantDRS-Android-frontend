import os
import re

main_activity_path = r"C:\Users\Shivansh Tiwari\InstantDRS\InstantDRS-fronten\app\src\main\java\com\example\instantdrs_android\MainActivity.kt"

with open(main_activity_path, "r", encoding="utf-8") as f:
    content = f.read()

# Fix Screen enum
content = re.sub(
    r'enum class Screen \{\s+([^}]+)\s+\}',
    lambda m: f"enum class Screen {{\n    {m.group(1).replace(', RecordedVideos, VideoPlayer', '')}, RecordedVideos, VideoPlayer\n}}",
    content
)

with open(main_activity_path, "w", encoding="utf-8") as f:
    f.write(content)
print("MainActivity updated")
