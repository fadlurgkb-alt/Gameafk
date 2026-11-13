import re

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    content = f.read()

helpers = """    fun getGateCapacity(tembokLevel: Int): Pair<Int, Long> {
        return when (tembokLevel) {
            0 -> 0 to 0L
            1 -> 1 to 10L
            2 -> 1 to 15L
            3 -> 1 to 25L
            4 -> 1 to 40L
            5 -> 2 to 60L
            6 -> 2 to 90L
            7 -> 2 to 110L
            8 -> 3 to 150L
            9 -> 3 to 200L
            else -> 4 to 250L
        }
    }

    fun getHospitalCapacity(level: Int): Long {
        return when (level) {
            0 -> 0L
            1 -> 20L
            2 -> 30L
            3 -> 50L
            4 -> 70L
            5 -> 100L
            6 -> 120L
            7 -> 150L
            8 -> 170L
            9 -> 200L
            else -> 250L
        }
    }

    fun getHospitalBerryCost(level: Int): Long {
        return when (level) {
            1, 2 -> 200L
            3, 4 -> 190L
            5, 6, 7 -> 180L
            8, 9 -> 170L
            else -> 160L
        }
    }

    fun toggleGarrisonHero(heroName: String) {
        _uiState.update { state ->
            val tembokLevel = state.getBuildingLevel("tembok")
            val maxHeroes = getGateCapacity(tembokLevel).first
            val heroes = state.garrisonedHeroes.toMutableList()
            if (heroes.contains(heroName)) {
                heroes.remove(heroName)
            } else {
                if (heroes.size < maxHeroes) {
                    heroes.add(heroName)
                } else {
                    _alertMessage.value = "Kapasitas Hero Penuh! Maksimal $maxHeroes hero untuk Tembok level $tembokLevel."
                }
            }
            state.copy(garrisonedHeroesStr = heroes.joinToString(","))
        }
    }

    fun setGarrisonPasukan(amount: Long) {
        _uiState.update { state ->
            val tembokLevel = state.getBuildingLevel("tembok")
            val maxPasukan = getGateCapacity(tembokLevel).second
            
            // Revert existing troops back
            val activeTroops = state.currentPasukan + state.garrisonedPasukan
            val newAmount = amount.coerceAtMost(maxPasukan).coerceAtMost(activeTroops)
            
            state.copy(
                garrisonedPasukan = newAmount,
                currentPasukan = activeTroops - newAmount
            )
        }
    }

    fun healPasukan(amount: Long) {
        _uiState.update { state ->
            val level = state.getBuildingLevel("hospital")
            if (level == 0) return@update state
            
            val toHeal = amount.coerceAtMost(state.woundedPasukan)
            val costPerUnit = getHospitalBerryCost(level)
            val totalCost = toHeal * costPerUnit
            
            if (state.berry >= totalCost) {
                state.copy(
                    berry = state.berry - totalCost,
                    woundedPasukan = state.woundedPasukan - toHeal,
                    currentPasukan = state.currentPasukan + toHeal
                )
            } else {
                val affordable = state.berry / costPerUnit
                val actualHeal = affordable.coerceAtMost(toHeal)
                if (actualHeal > 0) {
                    val actualCost = actualHeal * costPerUnit
                    state.copy(
                        berry = state.berry - actualCost,
                        woundedPasukan = state.woundedPasukan - actualHeal,
                        currentPasukan = state.currentPasukan + actualHeal
                    )
                } else {
                    _alertMessage.value = "Berry tidak cukup untuk menyembuhkan pasukan!"
                    state
                }
            }
        }
    }"""

# Insert before end of class
last_bracket = content.rfind("}")
if last_bracket != -1:
    content = content[:last_bracket] + "\n" + helpers + "\n" + content[last_bracket:]

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'w') as f:
    f.write(content)
