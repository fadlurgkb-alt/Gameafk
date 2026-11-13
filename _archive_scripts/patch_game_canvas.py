import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

# Update GameCanvas building positions and add level parameter
old_canvas_block = """        withTransform({
            scale(2.5f, 2.5f, Offset(cx, cy))
        }) {
            if (state.isUnlocked("penebang_kayu")) {
                drawWoodCamp(cx - 50f, cy - 20f)
            }
            if (state.isUnlocked("pemecah_batu")) {
                drawStoneCamp(cx - 50f, cy + 10f)
            }
            if (state.isUnlocked("barter_purba")) {
                drawCampfire(cx, cy + 10f, time)
            }
            if (state.isUnlocked("town_hall")) {
                drawTownHall(cx - 10f, cy - 40f)
            }
            if (state.isUnlocked("hospital")) {
                drawHospital(cx + 30f, cy - 40f)
            }
            if (state.isUnlocked("rumah_warga")) {
                val level = state.getBuildingLevel("rumah_warga")
                drawHouse(cx - 30f, cy - 25f)
                if (level >= 2) drawHouse(cx + 15f, cy - 25f)
                if (level >= 3) drawHouse(cx - 40f, cy - 5f)
                if (level >= 4) drawHouse(cx + 25f, cy - 5f)
            }
            if (state.isUnlocked("gudang")) {
                drawGudang(cx - 30f, cy + 10f)
            }
            if (state.isUnlocked("temple")) {
                drawTemple(cx + 35f, cy - 35f)
            }
            if (state.isUnlocked("tavern")) {
                drawTavern(cx + 40f, cy - 10f)
            }
            if (state.isUnlocked("barak_militer")) {
                drawBarak(cx + 45f, cy + 15f)
            }
            if (state.isUnlocked("pertanian")) {
                drawFarm(cx - 20f, cy + 30f, isBerry = false)
            }
            if (state.isUnlocked("perkebunan")) {
                drawFarm(cx + 10f, cy + 30f, isBerry = true)
            }
            
            // 4. Characters"""

new_canvas_block = """        withTransform({
            scale(2.0f, 2.0f, Offset(cx, cy))
        }) {
            if (state.isUnlocked("barter_purba")) {
                drawCampfire(cx, cy - 10f, time)
            }
            if (state.isUnlocked("town_hall")) {
                drawTownHall(cx - 15f, cy - 60f, state.getBuildingLevel("town_hall"))
            }
            if (state.isUnlocked("rumah_warga")) {
                val level = state.getBuildingLevel("rumah_warga")
                drawHouse(cx - 35f, cy - 45f, level)
                if (level >= 2) drawHouse(cx + 15f, cy - 45f, level)
                if (level >= 3) drawHouse(cx - 45f, cy - 20f, level)
                if (level >= 4) drawHouse(cx + 25f, cy - 20f, level)
            }
            if (state.isUnlocked("hospital")) {
                drawHospital(cx + 45f, cy - 60f, state.getBuildingLevel("hospital"))
            }
            if (state.isUnlocked("gudang")) {
                drawGudang(cx - 45f, cy - 70f, state.getBuildingLevel("gudang"))
            }

            // Industry to the West
            if (state.isUnlocked("penebang_kayu")) {
                drawWoodCamp(cx - 80f, cy - 10f, state.getBuildingLevel("penebang_kayu"))
            }
            if (state.isUnlocked("pemecah_batu")) {
                drawStoneCamp(cx - 85f, cy + 30f, state.getBuildingLevel("pemecah_batu"))
            }

            // Barracks/Tavern to the East
            if (state.isUnlocked("tavern")) {
                drawTavern(cx + 70f, cy - 20f, state.getBuildingLevel("tavern"))
            }
            if (state.isUnlocked("barak_militer")) {
                drawBarak(cx + 80f, cy + 15f, state.getBuildingLevel("barak_militer"))
            }
            if (state.isUnlocked("temple")) {
                drawTemple(cx + 60f, cy - 50f, state.getBuildingLevel("temple"))
            }

            // Farming to the South
            if (state.isUnlocked("pertanian")) {
                drawFarm(cx - 30f, cy + 50f, isBerry = false, state.getBuildingLevel("pertanian"))
            }
            if (state.isUnlocked("perkebunan")) {
                drawFarm(cx + 20f, cy + 50f, isBerry = true, state.getBuildingLevel("perkebunan"))
            }
            
            // 4. Characters"""

content = content.replace(old_canvas_block, new_canvas_block)

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
