import re

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    content = f.read()

new_fn = """
    fun buyCoinShopItem(itemId: String, costCoins: Long) {
        _uiState.update { state ->
            if (state.gameCoins < costCoins) return@update state
            var newState = state.copy(gameCoins = state.gameCoins - costCoins)
            val inv = newState.inventory.toMutableMap()
            inv[itemId] = (inv[itemId] ?: 0) + 1
            newState = newState.copy(inventoryStr = inv.map { "${it.key}:${it.value}" }.joinToString(","))
            viewModelScope.launch { repository.saveGameState(newState) }
            newState
        }
    }
"""

content = re.sub(
    r'    fun buyExoticMarketItem.*?    \}',
    new_fn.strip(),
    content,
    flags=re.DOTALL
)

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'w') as f:
    f.write(content)
