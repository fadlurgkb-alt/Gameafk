import re

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    content = f.read()

# I will just place val happinessMod = getHappinessAttackMultiplier(currentHappiness) right before garrisonDamage
target1 = "var garrisonDamage = 0L"
replacement1 = "val happinessMod = getHappinessAttackMultiplier(currentHappiness)\n            var garrisonDamage = 0L"
content = content.replace(target1, replacement1)

# Now I need to multiply damage by happinessMod. Where does damage finish calculating?
# Line 415: damage += heroDamageTotal
target2 = "damage += heroDamageTotal"
replacement2 = "damage += heroDamageTotal\n            damage = (damage * happinessMod).toLong()"
content = content.replace(target2, replacement2)

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'w') as f:
    f.write(content)
