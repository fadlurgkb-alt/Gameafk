import re

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    content = f.read()

target = """                // Restore 10% HP based on maxGerbangHp
                val healAmount = (state.maxGerbangHp * 0.1).toLong()
                val newHp = (state.playerDungeonHp + healAmount).coerceAtMost(state.maxGerbangHp)"""

new_code = """                // Restore 10% HP based on maxGerbangHp
                var relPotion = 1.0
                if (state.selectedReligions.values.contains("Aliran Meditasi")) relPotion += 0.30
                val healAmount = (state.maxGerbangHp * 0.1 * relPotion).toLong()
                val newHp = (state.playerDungeonHp + healAmount).coerceAtMost(state.maxGerbangHp)"""

content = content.replace(target, new_code)

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'w') as f:
    f.write(content)
