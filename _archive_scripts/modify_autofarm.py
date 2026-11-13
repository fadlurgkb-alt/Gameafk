import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'if (!state.autoFarmDungeon && state.maxDungeonLevel > state.currentDungeonLevel)',
    'if (!state.autoFarmDungeon)'
)

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'if (autoFarm && maxDungeonLevel > currentDungeonLevel)',
    'if (autoFarm)'
)

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'w') as f:
    f.write(content)
