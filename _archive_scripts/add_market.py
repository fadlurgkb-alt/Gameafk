import re

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    content = f.read()

functions_to_add = """
    fun jualItemDungeon(tier: String) {
        _uiState.update { state ->
            val inv = state.inventory.toMutableMap()
            val count = inv[tier] ?: 0
            if (count > 0) {
                inv[tier] = count - 1
                val coinsToAdd = when (tier) {
                    "item_rare" -> 10L
                    "item_sr" -> 50L
                    "item_ssr" -> 150L
                    else -> 0L
                }
                state.copy(
                    inventoryStr = inv.map { "${it.key}:${it.value}" }.joinToString(","),
                    dungeonCoins = state.dungeonCoins + coinsToAdd
                )
            } else {
                state
            }
        }
    }

    fun buyMarketItem(itemId: String, costCoins: Long) {
        _uiState.update { state ->
            if (state.dungeonCoins < costCoins) return@update state
            var newState = state.copy(dungeonCoins = state.dungeonCoins - costCoins)
            when (itemId) {
                "hp_potion" -> newState = newState.copy(hpPotions = newState.hpPotions + 1)
                "atk_potion" -> {
                    val inv = newState.inventory.toMutableMap()
                    inv["atk_potion"] = (inv["atk_potion"] ?: 0) + 1
                    newState = newState.copy(inventoryStr = inv.map { "${it.key}:${it.value}" }.joinToString(","))
                }
                "def_potion" -> {
                    val inv = newState.inventory.toMutableMap()
                    inv["def_potion"] = (inv["def_potion"] ?: 0) + 1
                    newState = newState.copy(inventoryStr = inv.map { "${it.key}:${it.value}" }.joinToString(","))
                }
                "kayu_100" -> newState = newState.copy(kayu = newState.kayu + 100)
                "batu_100" -> newState = newState.copy(batu = newState.batu + 100)
                "daging_100" -> newState = newState.copy(daging = newState.daging + 100)
                
                "kayu_250" -> newState = newState.copy(kayu = newState.kayu + 250)
                "batu_250" -> newState = newState.copy(batu = newState.batu + 250)
                "daging_150" -> newState = newState.copy(daging = newState.daging + 150)
                "besi_100" -> newState = newState.copy(besi = newState.besi + 100)
                "emas_75" -> newState = newState.copy(emas = newState.emas + 75)
                
                "kayu_500" -> newState = newState.copy(kayu = newState.kayu + 500)
                "batu_500" -> newState = newState.copy(batu = newState.batu + 500)
                "daging_300" -> newState = newState.copy(daging = newState.daging + 300)
                
                "shard_sr" -> {
                    val inv = newState.inventory.toMutableMap()
                    inv["shard_sr"] = (inv["shard_sr"] ?: 0) + 1
                    newState = newState.copy(inventoryStr = inv.map { "${it.key}:${it.value}" }.joinToString(","))
                }
                "shard_ssr" -> {
                    val inv = newState.inventory.toMutableMap()
                    inv["shard_ssr"] = (inv["shard_ssr"] ?: 0) + 1
                    newState = newState.copy(inventoryStr = inv.map { "${it.key}:${it.value}" }.joinToString(","))
                }
            }
            newState
        }
    }

    fun exchangeShardToken(target: String, costTokens: Int) {
        _uiState.update { state ->
            if (state.shardTokens < costTokens) return@update state
            
            val inv = state.inventory.toMutableMap()
            when (target) {
                "shard_sr_random" -> inv["shard_sr"] = (inv["shard_sr"] ?: 0) + 1
                "shard_ssr_random" -> inv["shard_ssr"] = (inv["shard_ssr"] ?: 0) + 1
                "shard_ur_random" -> inv["shard_ur"] = (inv["shard_ur"] ?: 0) + 1
            }
            
            state.copy(
                shardTokens = state.shardTokens - costTokens,
                inventoryStr = inv.map { "${it.key}:${it.value}" }.joinToString(",")
            )
        }
    }
"""

content = content.replace(
    'fun clearDungeonLootResult() {',
    functions_to_add + '\n    fun clearDungeonLootResult() {'
)

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'w') as f:
    f.write(content)
