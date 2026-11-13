import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

# 1. Update TabsSection to include showGudangDialog
tabs_regex = r'(var showAgamaDialog by remember \{ mutableStateOf\(false\) \})'
content = re.sub(tabs_regex, r'\1\n    var showGudangDialog by remember { mutableStateOf(false) }', content)

gudang_dialog = """
    if (showGudangDialog) {
        AlertDialog(
            onDismissRequest = { showGudangDialog = false },
            title = { Text("📦 Isi Gudang Penyimpanan") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🪵 Material Dasar:", fontWeight = FontWeight.Bold)
                    Text("Kayu: ${state.kayu} | Batu: ${state.batu} | Besi: ${state.besi} | Emas: ${state.emas}")
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("⚙️ Material Lanjut:", fontWeight = FontWeight.Bold)
                    Text("Oil: ${state.oil} | Listrik: ${state.listrik} | Titanium: ${state.titanium} | Uranium: ${state.uranium}")
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("🍞 Makanan:", fontWeight = FontWeight.Bold)
                    Text("Daging: ${state.daging} | Berry: ${state.berry} | Gandum: ${state.gandum} | Roti: ${state.roti} | Kalengan: ${state.kalengan} | Ikan: ${state.ikan}")
                }
            },
            confirmButton = { TextButton(onClick = { showGudangDialog = false }) { Text("Tutup") } }
        )
    }
"""

tabs_content_regex = r'(if \(showAgamaDialog\) \{.*?^\s*\}\n)'
content = re.sub(tabs_content_regex, r'\1' + gudang_dialog, content, flags=re.MULTILINE|re.DOTALL)

# Update GenericTab call
call_regex = r'GenericTab\(state, viewModel, currentType, onShowStat = \{ showStatDialog = true \}, onShowAgama = \{ showAgamaDialog = true \}\)'
content = content.replace(call_regex, 'GenericTab(state, viewModel, currentType, onShowStat = { showStatDialog = true }, onShowAgama = { showAgamaDialog = true }, onShowGudang = { showGudangDialog = true })')

# 2. Update GenericTab signature
generic_tab_regex = r'(fun GenericTab\(\s*state: GameState,\s*viewModel: GameViewModel,\s*type: String,\s*onShowStat: \(\) -> Unit = \{\},\s*onShowAgama: \(\) -> Unit = \{\}\s*\))'
generic_tab_replacement = """fun GenericTab(
    state: GameState, 
    viewModel: GameViewModel, 
    type: String,
    onShowStat: () -> Unit = {},
    onShowAgama: () -> Unit = {},
    onShowGudang: () -> Unit = {}
)"""
content = re.sub(generic_tab_regex, generic_tab_replacement, content)

# 3. Add Gudang button
town_hall_action = """                    "town_hall" -> {
                        actionText = "📊 Buka Stat Peradaban"
                        onActionClick = onShowStat
                    }
                    "gudang" -> {
                        actionText = "📦 Cek Isi Gudang"
                        onActionClick = onShowGudang
                    }"""
content = content.replace('                    "town_hall" -> {\n                        actionText = "\\uD83D\\uDCCA Buka Stat Peradaban"\n                        onActionClick = onShowStat\n                    }', town_hall_action)

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
