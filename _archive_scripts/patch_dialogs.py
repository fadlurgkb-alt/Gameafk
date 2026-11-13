import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

target = """    Column(modifier = modifier.fillMaxSize()) {"""

new_code = """    if (showHospitalDialog) {
        val level = state.getBuildingLevel("hospital")
        val cap = viewModel.getHospitalCapacity(level)
        val cost = viewModel.getHospitalBerryCost(level)
        
        AlertDialog(
            onDismissRequest = { showHospitalDialog = false },
            title = { Text("🏥 Rumah Sakit (Lv. $level)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Kapasitas: $cap Pasukan Terluka")
                    Text("Biaya Perawatan: $cost Berry / Pasukan")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Jumlah Terluka: ${state.woundedPasukan}", fontWeight = FontWeight.Bold, color = Color.Red)
                    Text("Berry Anda: ${state.berry}", fontWeight = FontWeight.Bold, color = Color.Green)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = { viewModel.healPasukan(1) },
                        enabled = state.woundedPasukan >= 1 && state.berry >= cost,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Sembuhkan 1 Pasukan") }
                    
                    Button(
                        onClick = { viewModel.healPasukan(10) },
                        enabled = state.woundedPasukan >= 10 && state.berry >= cost * 10,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Sembuhkan 10 Pasukan") }
                    
                    Button(
                        onClick = { viewModel.healPasukan(state.woundedPasukan) },
                        enabled = state.woundedPasukan > 0 && state.berry >= cost,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Sembuhkan Maksimal (Sesuai Berry)") }
                }
            },
            confirmButton = { TextButton(onClick = { showHospitalDialog = false }) { Text("Tutup") } }
        )
    }

    if (showGerbangDialog) {
        val level = state.getBuildingLevel("tembok")
        val capacity = viewModel.getGateCapacity(level)
        val maxHeroes = capacity.first
        val maxPasukan = capacity.second
        
        var sliderValue by remember { mutableFloatStateOf(state.garrisonedPasukan.toFloat()) }
        
        AlertDialog(
            onDismissRequest = { showGerbangDialog = false },
            title = { Text("🛡️ Penugasan Gerbang") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Slot Hero: ${state.garrisonedHeroes.size} / $maxHeroes")
                    Text("Hero Bertugas: ${state.garrisonedHeroesStr.ifEmpty { "Tidak ada" }}")
                    Text("(Tugaskan Hero melalui tab Komandan Suku)", fontSize = 12.sp, color = Color.Gray)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text("Pasukan Gerbang: ${state.garrisonedPasukan} / $maxPasukan")
                    Text("Pasukan Aktif (Barak): ${state.currentPasukan}")
                    
                    val maxPossible = (state.currentPasukan + state.garrisonedPasukan).coerceAtMost(maxPasukan)
                    
                    Slider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        valueRange = 0f..(maxPossible.toFloat().coerceAtLeast(1f)),
                        steps = if (maxPossible > 1) (maxPossible - 1).toInt() else 0
                    )
                    Text("Atur Pasukan: ${sliderValue.toLong()}", fontWeight = FontWeight.Bold)
                    
                    Button(
                        onClick = { 
                            viewModel.setGarrisonPasukan(sliderValue.toLong()) 
                            showGerbangDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Simpan Penugasan") }
                }
            },
            confirmButton = { TextButton(onClick = { showGerbangDialog = false }) { Text("Batal") } }
        )
    }

    Column(modifier = modifier.fillMaxSize()) {"""

content = content.replace(target, new_code)

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
