import re

with open("app/src/main/java/com/example/ui/game/GameScreen.kt", "r") as f:
    content = f.read()

# Fix GenericTab signature
old_sig = """@Composable
fun GenericTab(
    state: GameState, 
    viewModel: GameViewModel, 
    type: String,"""

new_sig = """@Composable
fun GenericTab(
    state: GameState, 
    viewModel: GameViewModel, 
    type: String,
    upgradingBuildings: Map<String, Long> = emptyMap(),"""

content = content.replace(old_sig, new_sig)

# Now we need to pass upgradingBuildings to GenericTab from TabsSection
# We first need to get upgradingBuildings in TabsSection, or pass it to TabsSection.
# Wait, let's just collect it in GameScreen and pass it down.
# In GameScreen:
#     val state by viewModel.uiState.collectAsStateWithLifecycle()
#     val upgradingBuildings by viewModel.upgradingBuildings.collectAsStateWithLifecycle()
# Then we pass it to TabsSection.

