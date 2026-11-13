import re

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    content = f.read()

buyNode_new = """    private val _alertMessage = MutableStateFlow<String?>(null)
    val alertMessage: StateFlow<String?> = _alertMessage.asStateFlow()

    fun clearAlertMessage() {
        _alertMessage.value = null
    }

    fun buyNode(id: String) {
        _uiState.update { currentState ->
            val node = allNodes.find { it.id == id } ?: return@update currentState
            val isUnlocked = currentState.isUnlocked(id)
            if (isUnlocked && node.type != "bangunan") return@update currentState
            
            val currentLevel = currentState.getBuildingLevel(id)
            
            if (node.type == "bangunan" && id != "town_hall") {
                val townHallLvl = currentState.getBuildingLevel("town_hall")
                if (currentLevel >= townHallLvl) {
                    _alertMessage.value = "❌ Gagal! Anda harus meningkatkan level Town Hall terlebih dahulu untuk mematok batas level bangunan ini!"
                    return@update currentState
                }
            }
            
            val costMultiplier = if (isUnlocked) Math.pow(1.5, currentLevel.toDouble()) else 1.0
            
            val actualZenithCost = node.zenithCost * costMultiplier
            if (currentState.totalZenith < actualZenithCost) return@update currentState
            
            var canAfford = true
            val actualResCosts = node.resourceCosts.mapValues {
                var cost = (it.value * costMultiplier).toLong()
                if (currentState.isUnlocked("metalurgi_militer") && node.type == "senjata/armor" && (it.key == "besi" || it.key == "emas")) {
                    cost = (cost * 0.85).toLong()
                }
                if (node.tier >= 3 && (node.type == "bangunan" || node.type == "filsafat")) {
                    cost = (cost * 1.8).toLong()
                }
                cost
            }
            
            val currentInv = currentState.inventory.toMutableMap()
            
            for ((res, amount) in actualResCosts) {
                val currentAmount = when(res) {
                    "kayu" -> currentState.kayu
                    "batu" -> currentState.batu
                    "besi" -> currentState.besi
                    "emas" -> currentState.emas
                    "oil" -> currentState.oil
                    "listrik" -> currentState.listrik
                    "titanium" -> currentState.titanium
                    "uranium" -> currentState.uranium
                    "item_rare" -> (currentInv["item_rare"] ?: 0).toLong()
                    "item_sr" -> (currentInv["item_sr"] ?: 0).toLong()
                    "item_ssr" -> (currentInv["item_ssr"] ?: 0).toLong()
                    else -> 0L
                }
                if (currentAmount < amount) {
                    canAfford = false
                    break
                }
            }
            
            if (canAfford) {
                var newState = currentState.copy(totalZenith = currentState.totalZenith - actualZenithCost)
                for ((res, amount) in actualResCosts) {
                    newState = when(res) {
                        "kayu" -> newState.copy(kayu = newState.kayu - amount)
                        "batu" -> newState.copy(batu = newState.batu - amount)
                        "besi" -> newState.copy(besi = newState.besi - amount)
                        "emas" -> newState.copy(emas = newState.emas - amount)
                        "oil" -> newState.copy(oil = newState.oil - amount)
                        "listrik" -> newState.copy(listrik = newState.listrik - amount)
                        "titanium" -> newState.copy(titanium = newState.titanium - amount)
                        "uranium" -> newState.copy(uranium = newState.uranium - amount)
                        "item_rare" -> {
                            currentInv["item_rare"] = (currentInv["item_rare"] ?: 0) - amount.toInt()
                            newState
                        }
                        "item_sr" -> {
                            currentInv["item_sr"] = (currentInv["item_sr"] ?: 0) - amount.toInt()
                            newState
                        }
                        "item_ssr" -> {
                            currentInv["item_ssr"] = (currentInv["item_ssr"] ?: 0) - amount.toInt()
                            newState
                        }
                        else -> newState
                    }
                }
                
                newState = newState.copy(inventoryStr = currentInv.map { "${it.key}:${it.value}" }.joinToString(","))
                
                if (node.type == "bangunan") {
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
                return@update newState
            }
            
            currentState
        }
    }"""

pattern = r'    fun buyNode\(id: String\) \{.*?            currentState\n        \}\n    \}'
new_content = re.sub(pattern, buyNode_new, content, flags=re.DOTALL)

if new_content == content:
    print("Match failed!")
    import sys
    sys.exit(1)

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'w') as f:
    f.write(new_content)
print("Updated successfully")
