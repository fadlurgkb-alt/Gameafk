import re

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    content = f.read()

target = """    fun sandboxGodMode() {"""

max_all = """    fun activateMaxAllMode() {
        _uiState.update { currentState ->
            val allNodeIds = allNodes.map { it.id }.joinToString(",")
            val maxBuildings = allNodes.filter { it.type == "bangunan" }.associate {
                val maxLv = when (it.id) {
                    "temple" -> 5
                    "market" -> 4
                    else -> 10
                }
                it.id to maxLv
            }
            val bldStr = maxBuildings.entries.joinToString(",") { "${it.key}:${it.value}" }
            
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
            )
        }
    }

    fun sandboxGodMode() {"""

content = content.replace(target, max_all)

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'w') as f:
    f.write(content)
