import re

with open("app/src/main/java/com/example/ui/game/GameScreen.kt", "r") as f:
    content = f.read()

particles_code = """fun DrawScope.drawSmokeParticles(x: Float, y: Float, time: Float) {
    for (i in 0..2) {
        val offset = (time * 2f + i * 15f) % 40f
        val alpha = kotlin.math.max(0f, 1f - (offset / 40f))
        val driftX = kotlin.math.sin(time + i) * 5f
        drawCircle(
            color = Color.DarkGray.copy(alpha = alpha * 0.5f),
            radius = 3f + (offset * 0.2f),
            center = Offset(x + 10f + driftX, y - offset)
        )
    }
}

fun DrawScope.drawCyberTraffic(cx: Float, cy: Float, time: Float, size: Size) {
    // 3 lanes of traffic
    for (i in 0..2) {
        val y = cy - 20f + (i * 30f)
        val speed = if (i % 2 == 0) 300f else -250f
        val px = (time * speed) % (size.width + 100f) - 50f
        
        val color = if (i % 2 == 0) Color(0xFF22D3EE) else Color(0xFFD946EF)
        
        // Draw the vehicle body
        drawRect(color, topLeft = Offset(px, y), size = Size(6f, 3f))
        // Draw the trail
        drawRect(color.copy(alpha=0.3f), topLeft = Offset(px + (if (speed > 0) -8f else 6f), y), size = Size(8f, 3f))
    }
}

fun DrawScope.drawScaffolding"""

content = content.replace("fun DrawScope.drawScaffolding", particles_code)

with open("app/src/main/java/com/example/ui/game/GameScreen.kt", "w") as f:
    f.write(content)

