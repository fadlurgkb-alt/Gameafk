import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

target1 = """    var showStatDialog by remember { mutableStateOf(false) }
    var showAgamaDialog by remember { mutableStateOf(false) }
    var showNationalityDialog by remember { mutableStateOf(false) }
    var showGudangDialog by remember { mutableStateOf(false) }"""

new_code1 = """    var showStatDialog by remember { mutableStateOf(false) }
    var showAgamaDialog by remember { mutableStateOf(false) }
    var showNationalityDialog by remember { mutableStateOf(false) }
    var showGudangDialog by remember { mutableStateOf(false) }
    var showGerbangDialog by remember { mutableStateOf(false) }
    var showHospitalDialog by remember { mutableStateOf(false) }"""

content = content.replace(target1, new_code1)

target2 = """            GenericTab(state, viewModel, currentType, onShowStat = { showStatDialog = true }, onShowAgama = { showAgamaDialog = true }, onShowGudang = { showGudangDialog = true })"""

new_code2 = """            GenericTab(state, viewModel, currentType, 
                onShowStat = { showStatDialog = true }, 
                onShowAgama = { showAgamaDialog = true }, 
                onShowGudang = { showGudangDialog = true },
                onShowGerbang = { showGerbangDialog = true },
                onShowHospital = { showHospitalDialog = true })"""

content = content.replace(target2, new_code2)

target3 = """fun GenericTab(
    state: GameState, 
    viewModel: GameViewModel, 
    type: String,
    onShowStat: () -> Unit = {},
    onShowAgama: () -> Unit = {},
    onShowGudang: () -> Unit = {}
) {"""

new_code3 = """fun GenericTab(
    state: GameState, 
    viewModel: GameViewModel, 
    type: String,
    onShowStat: () -> Unit = {},
    onShowAgama: () -> Unit = {},
    onShowGudang: () -> Unit = {},
    onShowGerbang: () -> Unit = {},
    onShowHospital: () -> Unit = {}
) {"""

content = content.replace(target3, new_code3)

target4 = """                    "dungeon" -> {
                        actionText = "⚔️ Masuk Dungeon (Lv.${state.maxDungeonLevel})"
                        onActionClick = { viewModel.startDungeonBattle(state.maxDungeonLevel) }
                    }
                }"""

new_code4 = """                    "dungeon" -> {
                        actionText = "⚔️ Masuk Dungeon (Lv.${state.maxDungeonLevel})"
                        onActionClick = { viewModel.startDungeonBattle(state.maxDungeonLevel) }
                    }
                    "tembok" -> {
                        actionText = "🛡️ Atur Pertahanan Gerbang"
                        onActionClick = onShowGerbang
                    }
                    "hospital" -> {
                        actionText = "🏥 Rumah Sakit (${state.woundedPasukan} Terluka)"
                        onActionClick = onShowHospital
                    }
                }"""

content = content.replace(target4, new_code4)

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
