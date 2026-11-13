import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

# Add nationality dialog state
target = """    var showAgamaDialog by remember { mutableStateOf(false) }"""
new_code = """    var showAgamaDialog by remember { mutableStateOf(false) }
    var showNationalityDialog by remember { mutableStateOf(false) }"""
content = content.replace(target, new_code)

# Add UI for nationality
target2 = """    if (showAgamaDialog) {"""
new_code2 = """    if (showNationalityDialog) {
        AlertDialog(
            onDismissRequest = { showNationalityDialog = false },
            title = { Text("Pilih Bangsa") },
            text = { 
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        Button(onClick = { viewModel.pilihNationality("Nusantara"); showNationalityDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Nusantara (+50% Kayu, +20% Pangan)") }
                        Button(onClick = { viewModel.pilihNationality("Sparta"); showNationalityDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Sparta (+50% Atk, -20% Emas)") }
                        Button(onClick = { viewModel.pilihNationality("Romawi"); showNationalityDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Romawi (+50% HP Tembok, -10% Cost Bangunan)") }
                        Button(onClick = { viewModel.pilihNationality("Mesir Kuno"); showNationalityDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Mesir Kuno (+50% Zenith, -10% Pangan)") }
                        Button(onClick = { viewModel.pilihNationality("Jepang Shogunate"); showNationalityDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Jepang Shogunate (+30% Atk, +30% Besi)") }
                        Button(onClick = { viewModel.pilihNationality("Nordik"); showNationalityDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Nordik (+30% Kayu, +30% Emas)") }
                        Button(onClick = { viewModel.pilihNationality("Britania"); showNationalityDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Britania (+50% Emas, +20% HP Tembok)") }
                        Button(onClick = { viewModel.pilihNationality("Cyber-Neo"); showNationalityDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Cyber-Neo (+50% Titanium, +50% Uranium, -30% Pangan)") }
                        Button(onClick = { viewModel.pilihNationality("Atlantis"); showNationalityDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Atlantis (+100% Zenith, -50% Besi)") }
                        Button(onClick = { viewModel.pilihNationality("Mongol"); showNationalityDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Mongol (+40% Atk, -30% Cost Barak)") }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showNationalityDialog = false }) { Text("Batal") } }
        )
    }

    if (showAgamaDialog) {"""
content = content.replace(target2, new_code2)

# Add Tab
target3 = """    if (state.isUnlocked("tavern")) {
        tabs.add("Komandan Suku")
    }"""
new_code3 = """    if (state.isUnlocked("tavern")) {
        tabs.add("Komandan Suku")
    }
    if (state.isUnlocked("kedaulatan_bangsa") && state.getBuildingLevel("town_hall") >= 6) {
        tabs.add("🌐 Kebangsaan")
    }"""
content = content.replace(target3, new_code3)

# Handle Tab content
target4 = """        } else if (currentType == "komandan suku") {
            KomandanTab(state, viewModel)"""
new_code4 = """        } else if (currentType == "komandan suku") {
            KomandanTab(state, viewModel)
        } else if (currentType == "🌐 kebangsaan") {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text("Bangsa Saat Ini: ${state.selectedNationality}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { showNationalityDialog = true }) {
                    Text("Pilih / Ubah Bangsa")
                }
            }"""
content = content.replace(target4, new_code4)

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
