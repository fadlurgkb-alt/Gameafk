import re

with open("app/src/main/java/com/example/ui/game/GameScreen.kt", "r") as f:
    content = f.read()

old_buildings_block = """        // 3. Buildings & City Layout (1.5f Scale with Generous Spacing for Megapolitan Feel)
        withTransform({
            scale(1.5f, 1.5f, Offset(cx, cy))
        }) {
            if (state.isUnlocked("barter_purba")) {
                drawCampfire(cx - 10f, cy - 10f, time, era)
            }
            if (state.isUnlocked("town_hall")) {
                drawTownHall(cx - 20f, cy - 120f, state.getBuildingLevel("town_hall"), era)
            }
            if (state.isUnlocked("rumah_warga")) {
                val level = state.getBuildingLevel("rumah_warga")
                drawHouse(cx - 80f, cy - 50f, level, era, time)
                if (level >= 2) drawHouse(cx + 40f, cy - 50f, level, era, time)
                if (level >= 3) drawHouse(cx - 80f, cy - 10f, level, era, time)
                if (level >= 4) drawHouse(cx + 40f, cy - 10f, level, era, time)
            }
            if (state.isUnlocked("hospital")) {
                drawHospital(cx + 140f, cy - 120f, state.getBuildingLevel("hospital"), era, time)
            }
            if (state.isUnlocked("gudang")) {
                drawGudang(cx - 110f, cy - 120f, state.getBuildingLevel("gudang"), era, time)
            }
            // Industry to the Far West
            if (state.isUnlocked("penebang_kayu")) {
                drawWoodCamp(cx - 160f, cy - 10f, state.getBuildingLevel("penebang_kayu"), era)
            }
            if (state.isUnlocked("pemecah_batu")) {
                drawStoneCamp(cx - 160f, cy + 50f, state.getBuildingLevel("pemecah_batu"), era)
            }
            // Barracks/Tavern to the East
            if (state.isUnlocked("tavern")) {
                drawTavern(cx + 110f, cy - 10f, state.getBuildingLevel("tavern"), era, time)
            }
            if (state.isUnlocked("barak_militer")) {
                drawBarak(cx + 120f, cy + 55f, state.getBuildingLevel("barak_militer"), era)
            }
            if (state.isUnlocked("temple")) {
                drawTemple(cx - 20f, cy + 120f, state.getBuildingLevel("temple"), era)
            }
        }"""

new_buildings_block = """        // 3. Buildings & City Layout (1.5f Scale with Generous Spacing for Megapolitan Feel)
        withTransform({
            scale(1.5f, 1.5f, Offset(cx, cy))
        }) {
            if (era >= 6) {
                drawCyberTraffic(cx, cy, time, size)
            }
        
            if (state.isUnlocked("barter_purba")) {
                drawCampfire(cx - 10f, cy - 10f, time, era)
            }
            if (state.isUnlocked("town_hall")) {
                if (upgradingBuildings.containsKey("town_hall")) drawScaffolding(cx - 20f, cy - 120f, era, time)
                else drawTownHall(cx - 20f, cy - 120f, state.getBuildingLevel("town_hall"), era)
            }
            if (state.isUnlocked("rumah_warga")) {
                val level = state.getBuildingLevel("rumah_warga")
                if (upgradingBuildings.containsKey("rumah_warga")) {
                    drawScaffolding(cx - 80f, cy - 50f, era, time)
                    if (level >= 2) drawScaffolding(cx + 40f, cy - 50f, era, time)
                    if (level >= 3) drawScaffolding(cx - 80f, cy - 10f, era, time)
                    if (level >= 4) drawScaffolding(cx + 40f, cy - 10f, era, time)
                } else {
                    drawHouse(cx - 80f, cy - 50f, level, era, time)
                    if (level >= 2) drawHouse(cx + 40f, cy - 50f, level, era, time)
                    if (level >= 3) drawHouse(cx - 80f, cy - 10f, level, era, time)
                    if (level >= 4) drawHouse(cx + 40f, cy - 10f, level, era, time)
                }
            }
            if (state.isUnlocked("hospital")) {
                if (upgradingBuildings.containsKey("hospital")) drawScaffolding(cx + 140f, cy - 120f, era, time)
                else drawHospital(cx + 140f, cy - 120f, state.getBuildingLevel("hospital"), era, time)
            }
            if (state.isUnlocked("gudang")) {
                if (upgradingBuildings.containsKey("gudang")) drawScaffolding(cx - 110f, cy - 120f, era, time)
                else drawGudang(cx - 110f, cy - 120f, state.getBuildingLevel("gudang"), era, time)
            }
            // Industry to the Far West
            if (state.isUnlocked("penebang_kayu")) {
                if (upgradingBuildings.containsKey("penebang_kayu")) drawScaffolding(cx - 160f, cy - 10f, era, time)
                else {
                    drawWoodCamp(cx - 160f, cy - 10f, state.getBuildingLevel("penebang_kayu"), era)
                    if (era >= 4) drawSmokeParticles(cx - 160f, cy - 10f, time)
                }
            }
            if (state.isUnlocked("pemecah_batu")) {
                if (upgradingBuildings.containsKey("pemecah_batu")) drawScaffolding(cx - 160f, cy + 50f, era, time)
                else {
                    drawStoneCamp(cx - 160f, cy + 50f, state.getBuildingLevel("pemecah_batu"), era)
                    if (era >= 4) drawSmokeParticles(cx - 160f, cy + 50f, time)
                }
            }
            // Barracks/Tavern to the East
            if (state.isUnlocked("tavern")) {
                if (upgradingBuildings.containsKey("tavern")) drawScaffolding(cx + 110f, cy - 10f, era, time)
                else drawTavern(cx + 110f, cy - 10f, state.getBuildingLevel("tavern"), era, time)
            }
            if (state.isUnlocked("barak_militer")) {
                if (upgradingBuildings.containsKey("barak_militer")) drawScaffolding(cx + 120f, cy + 55f, era, time)
                else drawBarak(cx + 120f, cy + 55f, state.getBuildingLevel("barak_militer"), era)
            }
            if (state.isUnlocked("temple")) {
                if (upgradingBuildings.containsKey("temple")) drawScaffolding(cx - 20f, cy + 120f, era, time)
                else drawTemple(cx - 20f, cy + 120f, state.getBuildingLevel("temple"), era)
            }
        }"""

content = content.replace(old_buildings_block, new_buildings_block)

with open("app/src/main/java/com/example/ui/game/GameScreen.kt", "w") as f:
    f.write(content)

