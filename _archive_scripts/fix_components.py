import re

with open("app/src/main/java/com/example/ui/game/GameScreen.kt", "r") as f:
    content = f.read()

# In NodeList:
# Collect upgradingBuildings inside the function body
node_list_start = """fun NodeList(
    state: GameState, 
    viewModel: GameViewModel, 
    type: String,
    upgradingBuildings: Map<String, Long> = emptyMap(),
    onShowStat: () -> Unit = {},"""
    
node_list_fixed = """fun NodeList(
    state: GameState, 
    viewModel: GameViewModel, 
    type: String,
    onShowStat: () -> Unit = {},"""

content = content.replace(node_list_start, node_list_fixed)

node_list_body = """fun NodeList(
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

node_list_body_new = """fun NodeList(
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

content = content.replace(node_list_body, node_list_body_new)

# In GenericTab:
generic_tab_start = """@Composable
fun GenericTab(
    state: GameState, 
    viewModel: GameViewModel, 
    type: String,
    upgradingBuildings: Map<String, Long> = emptyMap(),
    onShowStat: () -> Unit = {},"""

generic_tab_fixed = """@Composable
fun GenericTab(
    state: GameState, 
    viewModel: GameViewModel, 
    type: String,
    onShowStat: () -> Unit = {},"""

content = content.replace(generic_tab_start, generic_tab_fixed)

# In GameCanvas, we need to collect upgradingBuildings and add the scaffolding visual.
game_canvas_start = """@Composable
fun GameCanvas(state: GameState, modifier: Modifier = Modifier) {"""

game_canvas_new = """@Composable
fun GameCanvas(state: GameState, viewModel: GameViewModel, modifier: Modifier = Modifier) {
    val upgradingBuildings by viewModel.upgradingBuildings.collectAsState()"""

# Wait, `GameCanvas` call needs to be updated in `GameScreen`
# I will do this in another step if needed, but wait! GameCanvas is only called once. Let's just pass viewModel.

content = content.replace(game_canvas_start, game_canvas_new)

with open("app/src/main/java/com/example/ui/game/GameScreen.kt", "w") as f:
    f.write(content)

