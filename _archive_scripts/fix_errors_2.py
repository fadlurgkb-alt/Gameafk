import re

with open("app/src/main/java/com/example/ui/game/GameScreen.kt", "r") as f:
    content = f.read()

# Fix time parameter
content = content.replace("time: Float\n) {", "time: Float = 0f\n) {")

# Fix detectDragGestures (remove fully qualified name since it's an extension function)
content = content.replace("androidx.compose.foundation.gestures.detectDragGestures", "detectDragGestures")

with open("app/src/main/java/com/example/ui/game/GameScreen.kt", "w") as f:
    f.write(content)

