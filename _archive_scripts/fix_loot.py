import re

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    content = f.read()

# Remove the broken generateLoot block I added
content = re.sub(r'        // Add to inventory.*?        }\n', '', content, flags=re.DOTALL)

# Modify generateLoot signature and return
content = content.replace(
    'private fun generateLoot(level: Int, globalMultiplier: Double, resourceMult: Double, coins: Long): Pair<Long, String> {',
    'private fun generateLoot(level: Int, globalMultiplier: Double, resourceMult: Double, coins: Long): Triple<Long, String, String> {'
)
content = content.replace(
    'var message = ""',
    'var message = ""\n        var rarityStr = ""'
)
content = content.replace(
    '// SSR Tier (2%)',
    '// SSR Tier (2%)\n            rarityStr = "item_ssr"'
)
content = content.replace(
    '// SR Tier (15%)',
    '// SR Tier (15%)\n            rarityStr = "item_sr"'
)
content = content.replace(
    '// Rare Tier (83%)',
    '// Rare Tier (83%)\n            rarityStr = "item_rare"'
)
content = content.replace(
    'return Pair(zenithGained, message)',
    'return Triple(zenithGained, message, rarityStr)'
)

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'w') as f:
    f.write(content)
