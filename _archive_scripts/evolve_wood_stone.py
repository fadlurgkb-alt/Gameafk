import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

wood_camp = """fun DrawScope.drawWoodCamp(x: Float, y: Float, level: Int = 1) {
    val shadowColor = Color(0x33000000)
    drawOval(shadowColor, topLeft = Offset(x - 5f, y + 10f), size = Size(60f, 15f))

    // Base colors depending on level
    val logColor = when {
        level >= 9 -> Color(0xFF0284C7) // Quantum blue logs
        level >= 7 -> Color(0xFFC026D3) // Neon purple logs
        level >= 5 -> Color(0xFF475569) // Steel logs
        level >= 3 -> Color(0xFF6B7280) // Stone logs
        else -> Color(0xFF78350F) // Wood logs
    }
    val logInner = when {
        level >= 9 -> Color(0xFF38BDF8)
        level >= 7 -> Color(0xFFE879F9)
        level >= 5 -> Color(0xFF94A3B8)
        level >= 3 -> Color(0xFF9CA3AF)
        else -> Color(0xFFD2B48C)
    }

    // Pile of logs
    for (i in 0..3) {
        val logY = y + 10f - (i * 6f)
        val offset = if (i % 2 == 0) 0f else 5f
        val width = 30f - (i * 4f)
        drawRect(logColor, topLeft = Offset(x + offset, logY), size = Size(width, 6f))
        drawRect(logInner, topLeft = Offset(x + offset, logY + 1f), size = Size(2f, 4f))
    }

    // Large Axe
    val handleColor = if (level >= 5) Color(0xFF334155) else Color(0xFF4A3728)
    val headColor = when {
        level >= 9 -> Color(0xFF0EA5E9) // Quantum axe
        level >= 7 -> Color(0xFFF0ABFC) // Cyber axe
        level >= 5 -> Color(0xFFF1F5F9) // Steel axe
        level >= 3 -> Color(0xFFE5E7EB) // Iron axe
        else -> Color(0xFF9CA3AF) // Stone axe
    }
    
    // Axe Handle
    drawRect(handleColor, topLeft = Offset(x + 35f, y - 5f), size = Size(3f, 20f))
    
    // Axe Head
    val axePath = Path().apply {
        moveTo(x + 35f, y - 5f)
        lineTo(x + 45f, y - 10f)
        lineTo(x + 45f, y + 5f)
        lineTo(x + 35f, y)
        close()
    }
    drawPath(axePath, headColor)
}"""

stone_camp = """fun DrawScope.drawStoneCamp(x: Float, y: Float, level: Int = 1) {
    val shadowColor = Color(0x33000000)
    drawOval(shadowColor, topLeft = Offset(x - 10f, y + 15f), size = Size(70f, 20f))

    val baseRockColor = when {
        level >= 9 -> Color(0xFF0F172A)
        level >= 7 -> Color(0xFF2E1065)
        level >= 5 -> Color(0xFF1E293B)
        level >= 3 -> Color(0xFF374151)
        else -> Color(0xFF4B5563)
    }
    val highlightColor = when {
        level >= 9 -> Color(0xFF38BDF8)
        level >= 7 -> Color(0xFFD946EF)
        level >= 5 -> Color(0xFFCBD5E1)
        level >= 3 -> Color(0xFF9CA3AF)
        else -> Color(0xFF6B7280)
    }

    // Rocky digging area (crater / stones)
    drawOval(Color(0xFF3F3F46), topLeft = Offset(x - 5f, y + 10f), size = Size(50f, 20f)) // Hole
    
    // Rocks around
    drawRect(baseRockColor, topLeft = Offset(x, y - 5f), size = Size(20f, 15f))
    drawRect(highlightColor, topLeft = Offset(x + 2f, y - 2f), size = Size(10f, 8f))
    
    drawRect(baseRockColor, topLeft = Offset(x + 25f, y + 5f), size = Size(15f, 10f))
    drawRect(highlightColor, topLeft = Offset(x + 27f, y + 7f), size = Size(8f, 5f))

    // Sledgehammer
    val handleColor = if (level >= 5) Color(0xFF475569) else Color(0xFF8B4513)
    val headColor = when {
        level >= 9 -> Color(0xFF7DD3FC)
        level >= 7 -> Color(0xFFF5D0FE)
        level >= 5 -> Color(0xFFF8FAFC)
        level >= 3 -> Color(0xFFD1D5DB)
        else -> Color(0xFF9CA3AF)
    }

    drawRect(handleColor, topLeft = Offset(x + 15f, y - 15f), size = Size(3f, 20f))
    drawRect(headColor, topLeft = Offset(x + 10f, y - 18f), size = Size(13f, 6f))
}"""

content = re.sub(r'fun DrawScope\.drawWoodCamp.*?\n\}\n', wood_camp + '\n', content, flags=re.DOTALL)
content = re.sub(r'fun DrawScope\.drawStoneCamp.*?\n\}\n', stone_camp + '\n', content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
