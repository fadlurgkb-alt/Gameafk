import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

def get_colors_block(name_base, name_highlight):
    return f"""
    val {name_base} = when {{
        level >= 9 -> Color(0xFF1E3A8A) // Quantum blue
        level >= 7 -> Color(0xFF581C87) // Cyber purple
        level >= 5 -> Color(0xFF334155) // Steel gray
        level >= 3 -> Color(0xFF4B5563) // Stone gray
        else -> Color(0xFF8B4513) // Primitive wood
    }}
    val {name_highlight} = when {{
        level >= 9 -> Color(0xFF38BDF8) // Quantum cyan
        level >= 7 -> Color(0xFFE879F9) // Cyber pink
        level >= 5 -> Color(0xFF94A3B8) // Steel light
        level >= 3 -> Color(0xFF9CA3AF) // Stone light
        else -> Color(0xFFD2B48C) // Wood light
    }}
"""

# Replace in drawHouse
house_old = r'fun DrawScope\.drawHouse\(x: Float, y: Float, level: Int = 1\) \{.*?\n\}\n'
house_new = f"""fun DrawScope.drawHouse(x: Float, y: Float, level: Int = 1) {{
    val shadowColor = Color(0x33000000)
    drawOval(shadowColor, topLeft = Offset(x - 5f, y + 20f), size = Size(40f, 12f))
    {get_colors_block('base', 'highlight')}
    drawRect(base, topLeft = Offset(x, y), size = Size(30f, 20f))
    drawRect(highlight, topLeft = Offset(x + 5f, y + 5f), size = Size(8f, 8f)) // Window
    drawRect(Color(0xFF000000).copy(alpha=0.5f), topLeft = Offset(x + 18f, y + 10f), size = Size(8f, 10f)) // Door
    val roofPath = Path().apply {{
        moveTo(x - 5f, y)
        lineTo(x + 15f, y - 15f)
        lineTo(x + 35f, y)
        close()
    }}
    drawPath(roofPath, highlight)
}}
"""
content = re.sub(house_old, house_new, content, flags=re.DOTALL)

# Let's do the rest simply
def replace_simple(func_name, w, h):
    old = r'fun DrawScope\.' + func_name + r'\(x: Float, y: Float, level: Int = 1\) \{.*?\n\}\n'
    new = f"""fun DrawScope.{func_name}(x: Float, y: Float, level: Int = 1) {{
    val shadowColor = Color(0x33000000)
    drawOval(shadowColor, topLeft = Offset(x - 5f, y + {h}f), size = Size({w + 10}f, 15f))
    {get_colors_block('base', 'highlight')}
    drawRect(base, topLeft = Offset(x, y), size = Size({w}f, {h}f))
    drawRect(highlight, topLeft = Offset(x, y - 10f), size = Size({w}f, 10f)) // Roof/Top
    drawRect(Color(0xFF000000).copy(alpha=0.5f), topLeft = Offset(x + {w/2 - 5}f, y + {h - 10}f), size = Size(10f, 10f)) // Door
}}
"""
    return re.sub(old, new, content, flags=re.DOTALL)

content = replace_simple('drawTownHall', 50, 40)
content = replace_simple('drawGudang', 40, 30)
content = replace_simple('drawTemple', 45, 45)
content = replace_simple('drawBarak', 45, 25)
content = replace_simple('drawTavern', 40, 30)
content = replace_simple('drawHospital', 40, 35)


with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
