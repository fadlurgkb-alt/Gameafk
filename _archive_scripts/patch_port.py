import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

# Replace drawPort positioning
port_regex = r"""        // Port drawn outside so we can place it on the water accurately
        if \(state\.isUnlocked\("pelabuhan"\)\) \{
            withTransform\(\{
                scale\(2\.5f, 2\.5f, Offset\(cx, cy\)\)
            \}\) \{
                // Place it at the edge of the island
                drawPort\(cx \+ 70f, cy \+ 50f\)
            \}
        \}"""
        
new_port = """        // Port drawn outside so we can place it on the water accurately
        if (state.isUnlocked("pelabuhan")) {
            withTransform({
                scale(2.5f, 2.5f, Offset(cx, cy))
            }) {
                // Place it far at the edge of the island on the water
                drawPort(cx + (islandRadiusX / 2.5f) * 0.9f, cy + (islandRadiusY / 2.5f) * 0.8f)
            }
        }"""
        
content = re.sub(port_regex, new_port, content, flags=re.MULTILINE|re.DOTALL)

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
