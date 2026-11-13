import re

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    content = f.read()

target = """            damage += heroDamageTotal
            damage = (damage * happinessMod).toLong()

            var maxGerbangHp = 0L"""

new_code = """            damage += heroDamageTotal
            damage = (damage * happinessMod * relAtk * natAtk).toLong()

            var maxGerbangHp = 0L"""

content = content.replace(target, new_code)

target2 = """            maxGerbangHp += currentState.pasukanHpBonus
            maxGerbangHp += cmdHpBonus

            // Dungeon Logic"""

new_code2 = """            maxGerbangHp += currentState.pasukanHpBonus
            maxGerbangHp += cmdHpBonus
            
            maxGerbangHp = (maxGerbangHp * relHpTembok * natHpTembok).toLong()

            // Dungeon Logic"""

content = content.replace(target2, new_code2)

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'w') as f:
    f.write(content)
