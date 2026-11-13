import re

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    content = f.read()

god_mode_fn = """
    fun sandboxGodMode() {
        _uiState.update { currentState ->
            currentState.copy(
                totalZenith = currentState.totalZenith + 50000.0,
                kayu = currentState.kayu + 10000L,
                batu = currentState.batu + 10000L,
                besi = currentState.besi + 10000L,
                emas = currentState.emas + 10000L,
                oil = currentState.oil + 10000L,
                listrik = currentState.listrik + 10000L,
                titanium = currentState.titanium + 10000L,
                uranium = currentState.uranium + 10000L,
                daging = currentState.daging + 10000L,
                berry = currentState.berry + 10000L,
                gandum = currentState.gandum + 10000L,
                roti = currentState.roti + 10000L,
                kalengan = currentState.kalengan + 10000L,
                dungeonCoins = currentState.dungeonCoins + 5000L,
                gameCoins = currentState.gameCoins + 5000L,
                gachaTickets = currentState.gachaTickets + 50,
                maxStorage = currentState.maxStorage + 100000L // Also increase storage so they don't get capped immediately
            )
        }
    }
"""

content = content.replace("    fun clearAlertMessage() {", god_mode_fn + "\n    fun clearAlertMessage() {")

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'w') as f:
    f.write(content)
