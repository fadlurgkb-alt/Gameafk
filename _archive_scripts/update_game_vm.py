import re

with open("app/src/main/java/com/example/ui/game/GameViewModel.kt", "r") as f:
    content = f.read()

# We need to find the block where it says:
#                                 if (node.type == "bangunan") {
#                                     val currentLevels = currentState.buildingLevels.toMutableMap()
#                                     currentLevels[id] = currentLevel + 1
#                                     
#                                     if (!isUnlocked) {

target_block = """                                if (node.type == "bangunan") {
                    val currentLevels = currentState.buildingLevels.toMutableMap()
                    currentLevels[id] = currentLevel + 1
                    
                    if (!isUnlocked) {
                        newState = newState.copy(
                            unlockedNodesStr = (currentState.unlockedNodes + id).joinToString(","),
                            buildingLevelsStr = currentLevels.map { "${it.key}:${it.value}" }.joinToString(",")
                        )
                    } else {
                        newState = newState.copy(
                            buildingLevelsStr = currentLevels.map { "${it.key}:${it.value}" }.joinToString(",")
                        )
                    }
                } else {
                    newState = newState.copy(
                        unlockedNodesStr = (currentState.unlockedNodes + id).joinToString(",")
                    )
                }
                
                viewModelScope.launch {
                    repository.saveGameState(newState)
                }
                newState"""

new_block = """                                if (node.type == "bangunan") {
                    isBuildingUpgrade = true
                    targetLevel = currentLevel + 1
                    wasUnlocked = isUnlocked
                    // DO NOT upgrade here! Just deduct cost, we will upgrade in the coroutine below!
                } else {
                    newState = newState.copy(
                        unlockedNodesStr = (currentState.unlockedNodes + id).joinToString(",")
                    )
                }
                
                viewModelScope.launch {
                    repository.saveGameState(newState)
                }
                newState"""

content = content.replace(target_block, new_block)

# Now find the end of `_uiState.update` block to launch the upgrade coroutine
end_block = """        }
    }

    fun upgradeGudang() {"""

new_end_block = """        }
        
        if (isBuildingUpgrade) {
            val endTime = System.currentTimeMillis() + 3000L
            _upgradingBuildings.value = _upgradingBuildings.value + (id to endTime)
            
            viewModelScope.launch {
                kotlinx.coroutines.delay(3000L)
                _upgradingBuildings.value = _upgradingBuildings.value - id
                
                _uiState.update { state ->
                    val currentLevels = state.buildingLevels.toMutableMap()
                    currentLevels[id] = targetLevel
                    var newState = state
                    if (!wasUnlocked) {
                        newState = newState.copy(
                            unlockedNodesStr = (state.unlockedNodes + id).joinToString(","),
                            buildingLevelsStr = currentLevels.map { "${it.key}:${it.value}" }.joinToString(",")
                        )
                    } else {
                        newState = newState.copy(
                            buildingLevelsStr = currentLevels.map { "${it.key}:${it.value}" }.joinToString(",")
                        )
                    }
                    viewModelScope.launch {
                        repository.saveGameState(newState)
                    }
                    newState
                }
            }
        }
    }

    fun upgradeGudang() {"""

content = content.replace(end_block, new_end_block)

with open("app/src/main/java/com/example/ui/game/GameViewModel.kt", "w") as f:
    f.write(content)

