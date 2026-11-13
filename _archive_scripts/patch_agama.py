import re

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    content = f.read()

old_pilih = """    fun pilihAgama(agama: String) {
        _uiState.update { state ->
            val newState = state.copy(selectedAgama = agama)
            viewModelScope.launch { repository.saveGameState(newState) }
            newState
        }
    }"""

new_pilih = """    fun pilihAgama(level: Int, agama: String) {
        _uiState.update { state ->
            val currentReligions = state.selectedReligions.toMutableMap()
            currentReligions[level] = agama
            val newReligionsStr = currentReligions.entries.joinToString(",") { "${it.key}:${it.value}" }
            
            val newState = state.copy(selectedReligionsStr = newReligionsStr)
            viewModelScope.launch { repository.saveGameState(newState) }
            newState
        }
    }
    
    fun pilihNationality(nation: String) {
        _uiState.update { state ->
            val newState = state.copy(selectedNationality = nation)
            viewModelScope.launch { repository.saveGameState(newState) }
            newState
        }
    }"""
content = content.replace(old_pilih, new_pilih)

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'w') as f:
    f.write(content)
