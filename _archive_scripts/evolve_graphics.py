import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

# Remove drawWall call and its function definition
content = re.sub(r'        if \(state\.isUnlocked\("tembok"\)\) \{\n            drawWall\(cx, cy, islandRadiusX \* 0\.9f, islandRadiusY \* 0\.9f\)\n        \}\n', '', content)
content = re.sub(r'fun DrawScope\.drawWall.*?\n\}\n', '', content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
