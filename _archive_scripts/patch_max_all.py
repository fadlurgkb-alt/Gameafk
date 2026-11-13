import re

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    content = f.read()

target = """            val bldStr = maxBuildings.entries.joinToString(",") { "${it.key}:${it.value}" }
            
            currentState.copy(
                totalZenith = 999999.0,
                kayu = 999999L, batu = 999999L, besi = 999999L, emas = 999999L,
                oil = 999999L, listrik = 999999L, titanium = 999999L, uranium = 999999L,
                daging = 999999L, berry = 999999L, gandum = 999999L, roti = 999999L,
                kalengan = 999999L, ikan = 999999L,
                dungeonCoins = 999999L, gameCoins = 999999L, gachaTickets = 999999,
                maxStorage = 9999999L,
                unlockedNodesStr = allNodeIds,
                buildingLevelsStr = bldStr
            )"""

new_code = """            val bldStr = maxBuildings.entries.joinToString(",") { "${it.key}:${it.value}" }
            val allHeroes = listOf("Zeus (Mythical):6", "Athena (Mythical):6", "Odin (Mythical):6", "Arthur (Legend):6")
            val garrisonedHeroesList = listOf("Zeus (Mythical)", "Athena (Mythical)", "Odin (Mythical)", "Arthur (Legend)")
            
            currentState.copy(
                totalZenith = 999999.0,
                kayu = 999999L, batu = 999999L, besi = 999999L, emas = 999999L,
                oil = 999999L, listrik = 999999L, titanium = 999999L, uranium = 999999L,
                daging = 999999L, berry = 999999L, gandum = 999999L, roti = 999999L,
                kalengan = 999999L, ikan = 999999L,
                dungeonCoins = 999999L, gameCoins = 999999L, gachaTickets = 999999,
                maxStorage = 9999999L,
                unlockedNodesStr = allNodeIds,
                buildingLevelsStr = bldStr,
                ownedCommandersStr = allHeroes.joinToString(","),
                garrisonedHeroesStr = garrisonedHeroesList.joinToString(","),
                garrisonedPasukan = 250L,
                currentPasukan = 1000L
            )"""

content = content.replace(target, new_code)

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'w') as f:
    f.write(content)
