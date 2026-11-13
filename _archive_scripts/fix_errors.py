import re

with open("app/src/main/java/com/example/ui/game/GameScreen.kt", "r") as f:
    content = f.read()

# Add imports
imports = """
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
"""
content = content.replace("import androidx.compose.runtime.*", "import androidx.compose.runtime.*\n" + imports)

# Fix drawChibiHero
old_chibi = """fun androidx.compose.ui.graphics.drawscope.DrawScope.drawChibiHero(
    x: Float, y: Float, name: String, time: Float
) {"""
new_chibi = """fun androidx.compose.ui.graphics.drawscope.DrawScope.drawChibiHero(
    name: String, isAlly: Boolean, time: Float
) {
    val x = 0f
    val y = 0f
"""
content = content.replace(old_chibi, new_chibi)

# Fix syntax error in text string
# The bad string was text = "📜 "${node.story}"",
# We want text = "📜 \"${node.story}\"",
content = content.replace('text = "📜 "${node.story}""', 'text = "📜 \\"${node.story}\\""')

with open("app/src/main/java/com/example/ui/game/GameScreen.kt", "w") as f:
    f.write(content)

