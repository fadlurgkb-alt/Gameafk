import re

with open("app/src/main/java/com/example/ui/game/GameScreen.kt", "r") as f:
    content = f.read()

scaffold_code = """fun DrawScope.drawScaffolding(x: Float, y: Float, era: Int, time: Float) {
    val isCyber = era >= 5
    val scaffoldColor = if (isCyber) Color(0xFF22D3EE) else Color(0xFF92400E)
    val beamColor = if (isCyber) Color(0xFF38BDF8).copy(alpha=0.5f) else Color(0xFFB45309)
    
    // Draw base foundation
    drawRect(Color(0xFF334155), topLeft = Offset(x - 5f, y + 25f), size = Size(50f, 10f))
    
    // Draw vertical and horizontal beams
    for (i in 0..4) {
        val px = x + i * 10f
        drawRect(scaffoldColor, topLeft = Offset(px, y), size = Size(2f, 25f)) // Vertical
        val py = y + i * 6f
        drawRect(scaffoldColor, topLeft = Offset(x, py), size = Size(40f, 2f)) // Horizontal
    }
    
    // Draw diagonals
    drawLine(beamColor, start = Offset(x, y), end = Offset(x + 40f, y + 25f), strokeWidth = 1.5f)
    drawLine(beamColor, start = Offset(x + 40f, y), end = Offset(x, y + 25f), strokeWidth = 1.5f)
    
    // Draw blinking hammer/wrench particles
    val blink = ((time * 5f) % 2f) > 1f
    if (blink) {
        val hammerColor = if (isCyber) Color(0xFFD946EF) else Color(0xFFFDE047)
        drawRect(hammerColor, topLeft = Offset(x + 15f, y - 10f), size = Size(10f, 4f))
        drawRect(Color(0xFF94A3B8), topLeft = Offset(x + 19f, y - 6f), size = Size(2f, 8f))
    }
}

fun DrawScope.drawTownHall"""

content = content.replace("fun DrawScope.drawTownHall", scaffold_code)

with open("app/src/main/java/com/example/ui/game/GameScreen.kt", "w") as f:
    f.write(content)

