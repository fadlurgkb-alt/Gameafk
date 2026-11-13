import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

# Replace the buildings drawing section
buildings_regex = r'// 3\. Buildings & City Layout.*?    \}\n\}'
replacement = """// 3. Buildings & City Layout
        if (state.isUnlocked("tembok")) {
            drawWall(cx, cy, islandRadiusX * 0.9f, islandRadiusY * 0.9f)
        }
        
        withTransform({
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
            
            // 4. Characters
            val hasWeapon = state.isUnlocked("tongkat_kayu") || state.isUnlocked("pedang_besi") || state.isUnlocked("musket") || state.isUnlocked("assault_rifle") || state.isUnlocked("energy_katana") || state.isUnlocked("planet_breaker")
            val hasArmor = state.isUnlocked("kulit_hewan") || state.isUnlocked("armor_besi") || state.isUnlocked("armor_kevlar") || state.isUnlocked("exo_suit") || state.isUnlocked("nano_armor") || state.isUnlocked("cyber_body")
            drawCharacter(cx - 5f, cy, "Adam", true, hasWeapon, hasArmor)
            drawCharacter(cx + 5f, cy + 2f, "Hawa", false, false, false)
        }
        
        // Port drawn outside so we can place it on the water accurately
        if (state.isUnlocked("pelabuhan")) {
            withTransform({
                scale(2.5f, 2.5f, Offset(cx, cy))
            }) {
                // Place it at the edge of the island
                drawPort(cx + 70f, cy + 50f)
            }
        }
    }
}"""

content = re.sub(buildings_regex, replacement, content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
