import re

with open("app/src/main/java/com/example/ui/game/GameScreen.kt", "r") as f:
    content = f.read()

content = content.replace("import detectDragGestures", "import androidx.compose.foundation.gestures.detectDragGestures")

with open("app/src/main/java/com/example/ui/game/GameScreen.kt", "w") as f:
    f.write(content)

