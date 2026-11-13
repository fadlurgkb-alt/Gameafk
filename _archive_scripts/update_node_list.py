import re

with open("app/src/main/java/com/example/ui/game/GameScreen.kt", "r") as f:
    content = f.read()

# Add upgradingBuildings to NodeList
old_sig = """fun NodeList(
    state: GameState, 
    viewModel: GameViewModel, 
    type: String,"""

new_sig = """fun NodeList(
    state: GameState, 
    viewModel: GameViewModel, 
    type: String,
    upgradingBuildings: Map<String, Long> = emptyMap(),"""

content = content.replace(old_sig, new_sig)

old_card = """            val isHighlighted = (node.id == "percikan_api" && state.gamePhase == "prolog_api") || 
                                (node.id == "panggil_manusia" && state.gamePhase == "animasi_api")
            
            NodeCard(
                title = displayTitle,"""

new_card = """            val isHighlighted = (node.id == "percikan_api" && state.gamePhase == "prolog_api") || 
                                (node.id == "panggil_manusia" && state.gamePhase == "animasi_api")
            val isUpgrading = upgradingBuildings.containsKey(node.id)
            val upgradeEndTime = upgradingBuildings[node.id] ?: 0L
            
            NodeCard(
                title = displayTitle,"""
                
content = content.replace(old_card, new_card)

with open("app/src/main/java/com/example/ui/game/GameScreen.kt", "w") as f:
    f.write(content)

