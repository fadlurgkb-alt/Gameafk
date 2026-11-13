import re

with open("app/src/main/java/com/example/ui/game/GameScreen.kt", "r") as f:
    content = f.read()

sig = """@Composable
fun GenericTab(
    state: GameState, 
    viewModel: GameViewModel, 
    type: String,
    onShowStat: () -> Unit = {},
    onShowAgama: () -> Unit = {},
    onShowGudang: () -> Unit = {},
    onShowGerbang: () -> Unit = {},
    onShowHospital: () -> Unit = {},
    onShowTavern: () -> Unit = {}
) {"""

new_sig = """@Composable
fun GenericTab(
    state: GameState, 
    viewModel: GameViewModel, 
    type: String,
    onShowStat: () -> Unit = {},
    onShowAgama: () -> Unit = {},
    onShowGudang: () -> Unit = {},
    onShowGerbang: () -> Unit = {},
    onShowHospital: () -> Unit = {},
    onShowTavern: () -> Unit = {}
) {
    val upgradingBuildings by viewModel.upgradingBuildings.collectAsState()"""

content = content.replace(sig, new_sig)

with open("app/src/main/java/com/example/ui/game/GameScreen.kt", "w") as f:
    f.write(content)

