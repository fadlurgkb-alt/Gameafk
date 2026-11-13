import re

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    content = f.read()

target = """                if (heroName == currentState.garrisonedHero) {
                    heroBaseDamage += eqAtk
                }
                
                val multiplier = 1.0 + (stars - 1) * 0.1
                val finalDmg = (heroBaseDamage * multiplier * (1.0 + potionAtkCount * 0.05)).toLong()
                val finalHp = (heroBaseHp * multiplier * (1.0 + potionDefCount * 0.05)).toLong()
                
                heroDamageTotal += finalDmg
                cmdHpBonus += finalHp
                
                if (heroName == currentState.garrisonedHero) {
                    garrisonDamage = (finalDmg * happinessMod).toLong()
                }"""

new_code = """                if (currentState.garrisonedHeroes.contains(heroName)) {
                    heroBaseDamage += eqAtk
                }
                
                val multiplier = 1.0 + (stars - 1) * 0.1
                val finalDmg = (heroBaseDamage * multiplier * (1.0 + potionAtkCount * 0.05)).toLong()
                val finalHp = (heroBaseHp * multiplier * (1.0 + potionDefCount * 0.05)).toLong()
                
                heroDamageTotal += finalDmg
                cmdHpBonus += finalHp
                
                if (currentState.garrisonedHeroes.contains(heroName)) {
                    garrisonDamage += (finalDmg * happinessMod).toLong()
                }"""

content = content.replace(target, new_code)

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'w') as f:
    f.write(content)
