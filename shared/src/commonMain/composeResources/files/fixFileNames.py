import os
import re
from pathlib import Path

def rename(name):
    return name.lower().replace("%27","").replace("%28","").replace("%29","")

def renameFiles(basePath):
        files = os.listdir(Path(basePath))
        for filename in files:
            old_path = os.path.join(basePath, filename)
            newName = rename(filename)
            new_path = os.path.join(basePath, newName)
            os.rename(old_path, new_path)
            

def renameJsonImageUrlByRegex(pathStr):
    path = Path(pathStr)
    content = path.read_text(encoding="utf-8")

    pattern = r'("imageUrl"\s*:\s*")([^"]+)(")'
    updated_content = re.sub(pattern, lambda m: f"{m.group(1)}{rename(m.group(2))}{m.group(3)}", content)
    path.write_text(updated_content, encoding="utf-8")

if __name__ == '__main__':
    imagePath = "./images"
    jsonPath  = "./items.json"
    renameFiles(imagePath)
    renameJsonImageUrlByRegex(jsonPath)