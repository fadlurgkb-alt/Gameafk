import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

farm_camp = """fun DrawScope.drawFarm(x: Float, y: Float, isBerry: Boolean = false, level: Int = 1) {
    val shadowColor = Color(0x33000000)
    drawOval(shadowColor, topLeft = Offset(x - 5f, y + 20f), size = Size(45f, 10f))
    
    // Ploughed land
    val dirtColor = if (level >= 9) Color(0xFF0C4A6E) else if (level >= 7) Color(0xFF4A044E) else Color(0xFF3F2718)
    drawRect(dirtColor, topLeft = Offset(x, y + 5f), size = Size(35f, 15f))
    
    val lineDirtColor = if (level >= 9) Color(0xFF0284C7) else if (level >= 7) Color(0xFFC026D3) else Color(0xFF29170A)
    drawRect(lineDirtColor, topLeft = Offset(x, y + 8f), size = Size(35f, 2f))
    drawRect(lineDirtColor, topLeft = Offset(x, y + 14f), size = Size(35f, 2f))

    // Crop color
    val cropColor = if (isBerry) {
        when {
            level >= 9 -> Color(0xFF06B6D4)
            level >= 7 -> Color(0xFFF0ABFC)
            else -> Color(0xFF9333EA) // Purple berry
        }
    } else {
        when {
            level >= 9 -> Color(0xFF38BDF8)
            level >= 7 -> Color(0xFFFDE047)
            else -> Color(0xFFEAB308) // Yellow wheat
        }
    }
    
    // Draw plants
    for (i in 0..4) {
        val px = x + 3f + (i * 6f)
        if (isBerry) {
            // Berry bush
            drawOval(Color(0xFF166534), topLeft = Offset(px - 2f, y - 2f), size = Size(8f, 10f)) // Leaves
            drawOval(cropColor, topLeft = Offset(px, y), size = Size(4f, 4f)) // Berry
        } else {
            // Wheat stalk
            drawRect(Color(0xFF166534), topLeft = Offset(px + 2f, y), size = Size(2f, 10f)) // Stem
            drawRect(cropColor, topLeft = Offset(px, y - 5f), size = Size(6f, 6f)) // Wheat head
        }
    }
}"""

content = re.sub(r'fun DrawScope\.drawFarm.*?\n\}\n', farm_camp + '\n', content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
