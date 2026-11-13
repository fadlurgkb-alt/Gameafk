import re

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    content = f.read()

# 1. Add getHappinessAttackMultiplier helper
helper = """
    private fun getHappinessAttackMultiplier(happiness: Long): Double {
        if (happiness > 100) {
            val extraHappiness = happiness - 100
            return 1.10 + (Math.floor(extraHappiness / 10.0) * 0.03)
        } else if (happiness < 100) {
            return 0.95
        }
        return 1.0
    }
    
    fun onTick() {
"""
content = content.replace("    fun onTick() {", helper)

# 2. Multiply damage
damage_target = """            // Pasukan and Commander bonuses
            damage += currentState.pasukanDamageBonus
            
            val potionAtkCount = (currentState.inventory["atk_potion"] ?: 0).toLong()"""

damage_replacement = """            // Pasukan and Commander bonuses
            damage += currentState.pasukanDamageBonus
            
            val happinessMod = getHappinessAttackMultiplier(currentHappiness)
            damage = (damage * happinessMod).toLong()
            
            val potionAtkCount = (currentState.inventory["atk_potion"] ?: 0).toLong()"""
content = content.replace(damage_target, damage_replacement)

# 3. Multiply garrisonDamage
garrison_target = """                if (heroName == currentState.garrisonedHero) {
                    garrisonDamage = finalDmg
                }"""

garrison_replacement = """                if (heroName == currentState.garrisonedHero) {
                    garrisonDamage = (finalDmg * happinessMod).toLong()
                }"""
content = content.replace(garrison_target, garrison_replacement)


with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'w') as f:
    f.write(content)
