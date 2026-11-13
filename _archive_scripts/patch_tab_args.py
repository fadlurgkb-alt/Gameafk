import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

target = 'GenericTab(state, viewModel, currentType, onShowStat = { showStatDialog = true }, onShowAgama = { showAgamaDialog = true })'
replacement = 'GenericTab(state, viewModel, currentType, onShowStat = { showStatDialog = true }, onShowAgama = { showAgamaDialog = true }, onShowGudang = { showGudangDialog = true })'
content = content.replace(target, replacement)

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
