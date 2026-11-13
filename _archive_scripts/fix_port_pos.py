import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

old_port = """        if (state.isUnlocked("pelabuhan")) {
            withTransform({
                scale(2.5f, 2.5f, Offset(cx, cy))
            }) {
                // Place it far at the edge of the island on the water
                drawPort(cx + (islandRadiusX / 2.5f) * 0.9f, cy + (islandRadiusY / 2.5f) * 0.8f)
            }
        }"""

new_port = """        if (state.isUnlocked("pelabuhan")) {
            withTransform({
                scale(2.0f, 2.0f, Offset(cx, cy))
            }) {
                // Place it far at the edge of the island on the water
                drawPort(cx - (islandRadiusX / 2.0f) * 1.1f, cy + (islandRadiusY / 2.0f) * 0.5f)
            }
        }"""

content = content.replace(old_port, new_port)

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
