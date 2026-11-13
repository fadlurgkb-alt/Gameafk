import re

with open("app/src/main/java/com/example/ui/game/GameScreen.kt", "r") as f:
    content = f.read()

# Replace era and color logic
old_era_logic = """        // Base ground rendering by Era
                    val color = when (era) {
                        1 -> { // Prehistoric: Moss green & muddy spots
                            if (rand < 12) Color(0xFF78350F) // Mud spots
                            else if (rand < 45) Color(0xFF15803D) // Dark moss
                            else Color(0xFF22C55E) // Raw green grass
                        }
                        2 -> { // Classical: Stone paved paths & classic green grass
                            if ((x + y) % 6 == 0 && rand < 30) Color(0xFF94A3B8) // Sandstone tiles
                            else if (rand < 45) Color(0xFF10B981)
                            else Color(0xFF047857)
                        }
                        3 -> { // Medieval: Grey cobblestone pathways snaking around
                            val onCobblePath = ((x - y) % 8 == 0 || (x + y) % 8 == 0) && rand < 65
                            if (onCobblePath) Color(0xFF64748B) // Grey cobblestone paths
                            else if (rand < 50) Color(0xFF059669) // Emerald grass lawns
                            else Color(0xFF065F46)
                        }
                        4 -> { // Industrial: Smoggy coppery pavers & dark slate grids
                            if (rand < 25) Color(0xFF451A03) // Iron copper sludge
                            else if (rand < 55) Color(0xFF334155) // Dark slate
                            else Color(0xFF1E293B) // Coal soot floor
                        }
                        5 -> { // Modern: Pristine gray asphalt roads & neat lawn edges
                            val isRoad = (abs(px - cx) < 22f) || (abs(py - (cy + 20f)) < 22f)
                            if (isRoad) {
                                val isRoadLine = (px.toInt() / 20) % 2 == 0 && abs(py - (cy + 20f)) < 2f
                                if (isRoadLine) Color(0xFFFFD700) // Classic yellow road line
                                else Color(0xFF475569) // Sleek asphalt grey
                            } else {
                                if (rand < 35) Color(0xFF10B981) // Well-manicured grass lawns
                                else Color(0xFF059669)
                            }
                        }
                        else -> { // Cyberpunk: Glowing neon gridlines on dark slate obsidian background
                            val isCyberGrid = (x % 10 == 0 || y % 10 == 0)
                            if (isCyberGrid) {
                                if ((x + y) % 2 == 0) Color(0xFF22D3EE) // Bright neon cyan
                                else Color(0xFFD946EF) // Bright neon pink/magenta
                            } else {
                                Color(0xFF0B132B) // Ultra-slick deep space navy
                            }
                        }
                    }"""

new_era_logic = """        // Transisi Tanah Progresif
                    val color = when {
                        thLevel >= 9 -> {
                            // Modern/Cyberpunk: Plat metalik gelap dengan garis grid neon bersinar
                            val isCyberGrid = (x % 10 == 0 || y % 10 == 0)
                            if (isCyberGrid) {
                                if ((x + y + (time * 2f).toInt()) % 15 < 3) Color(0xFF22D3EE) else Color(0xFF38BDF8).copy(alpha = 0.3f)
                            } else {
                                Color(0xFF0F172A)
                            }
                        }
                        thLevel >= 7 -> {
                            // Industri: 50% rumput hijau, 50% ubin batu bata jalanan
                            if (rand < 50) Color(0xFF64748B) else Color(0xFF22C55E)
                        }
                        else -> {
                            // Prasejarah - Pertengahan: Rumput hijau alami tekstur semak
                            if (rand < 30) Color(0xFF15803D) else Color(0xFF22C55E)
                        }
                    }"""

content = content.replace(old_era_logic, new_era_logic)

with open("app/src/main/java/com/example/ui/game/GameScreen.kt", "w") as f:
    f.write(content)

