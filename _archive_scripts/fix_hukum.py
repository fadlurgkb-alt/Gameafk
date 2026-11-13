import re

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'if (unlocked.contains("pengasapan_daging")) foodNeededDouble *= 0.90',
    'if (unlocked.contains("pengasapan_daging")) foodNeededDouble *= 0.90\n                if (unlocked.contains("hukum_kodeks")) foodNeededDouble *= 0.90'
)

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'w') as f:
    f.write(content)
