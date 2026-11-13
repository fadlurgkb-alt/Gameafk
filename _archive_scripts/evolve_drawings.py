import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

# Helper for styles
# Lv 1-2: Gaya Primitif/Kayu Mentah.
# Lv 3-4: Gaya Kerajaan Kuno/Batu/Semen Awal.
# Lv 5-6: Gaya Industri Modern/Baja.
# Lv 7-8: Gaya Cyberpunk Awal/Neon.
# Lv 9-10: Gaya Futuristik Cosmos/Reaktor Quantum.

# Replace function signatures
content = content.replace("fun DrawScope.drawWoodCamp(x: Float, y: Float)", "fun DrawScope.drawWoodCamp(x: Float, y: Float, level: Int = 1)")
content = content.replace("fun DrawScope.drawStoneCamp(x: Float, y: Float)", "fun DrawScope.drawStoneCamp(x: Float, y: Float, level: Int = 1)")
content = content.replace("fun DrawScope.drawTownHall(x: Float, y: Float)", "fun DrawScope.drawTownHall(x: Float, y: Float, level: Int = 1)")
content = content.replace("fun DrawScope.drawGudang(x: Float, y: Float)", "fun DrawScope.drawGudang(x: Float, y: Float, level: Int = 1)")
content = content.replace("fun DrawScope.drawTemple(x: Float, y: Float)", "fun DrawScope.drawTemple(x: Float, y: Float, level: Int = 1)")
content = content.replace("fun DrawScope.drawFarm(x: Float, y: Float, isBerry: Boolean = false)", "fun DrawScope.drawFarm(x: Float, y: Float, isBerry: Boolean = false, level: Int = 1)")
content = content.replace("fun DrawScope.drawBarak(x: Float, y: Float)", "fun DrawScope.drawBarak(x: Float, y: Float, level: Int = 1)")
content = content.replace("fun DrawScope.drawHouse(x: Float, y: Float)", "fun DrawScope.drawHouse(x: Float, y: Float, level: Int = 1)")
content = content.replace("fun DrawScope.drawTavern(x: Float, y: Float)", "fun DrawScope.drawTavern(x: Float, y: Float, level: Int = 1)")
content = content.replace("fun DrawScope.drawHospital(x: Float, y: Float)", "fun DrawScope.drawHospital(x: Float, y: Float, level: Int = 1)")

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
