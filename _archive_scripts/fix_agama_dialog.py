import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

target = """    if (showAgamaDialog) {
        AlertDialog(
            onDismissRequest = { showAgamaDialog = false },
            title = { Text("Pilih Agama") },
            text = { 
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { viewModel.pilihAgama("Agama Alam"); showAgamaDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Agama Alam (+20% Kayu)") }
                    Button(onClick = { viewModel.pilihAgama("Agama Langit"); showAgamaDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Agama Langit (+20% Zenith)") }
                }

    if (showGudangDialog) {"""

new_code = """    if (showAgamaDialog) {
        val levelTemple = state.getBuildingLevel("temple")
        AlertDialog(
            onDismissRequest = { showAgamaDialog = false },
            title = { Text("Pilih Agama") },
            text = { 
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (levelTemple >= 1) {
                        Text("Kuil Lv 1:", fontWeight = FontWeight.Bold)
                        Button(onClick = { viewModel.pilihAgama(1, "Agama Alam"); showAgamaDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Agama Alam (+20% Kayu)") }
                        Button(onClick = { viewModel.pilihAgama(1, "Agama Langit"); showAgamaDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Agama Langit (+20% Zenith)") }
                    }
                    if (levelTemple >= 2) {
                        Text("Kuil Lv 2:", fontWeight = FontWeight.Bold)
                        Button(onClick = { viewModel.pilihAgama(2, "Kepercayaan Leluhur"); showAgamaDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Kepercayaan Leluhur (+30% Batu, -10% Speed Barak)") }
                        Button(onClick = { viewModel.pilihAgama(2, "Kultus Kesuburan"); showAgamaDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Kultus Kesuburan (+25% Pangan, -5% Zenith)") }
                    }
                    if (levelTemple >= 3) {
                        Text("Kuil Lv 3:", fontWeight = FontWeight.Bold)
                        Button(onClick = { viewModel.pilihAgama(3, "Ordo Cahaya Murni"); showAgamaDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Ordo Cahaya Murni (+20% Happiness, -15% Besi/Emas)") }
                        Button(onClick = { viewModel.pilihAgama(3, "Penyembah Setan"); showAgamaDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Penyembah Setan (+100% Zenith, -25% Happiness)") }
                    }
                    if (levelTemple >= 4) {
                        Text("Kuil Lv 4:", fontWeight = FontWeight.Bold)
                        Button(onClick = { viewModel.pilihAgama(4, "Aliran Meditasi"); showAgamaDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Aliran Meditasi (+30% Potion, -10% Atk)") }
                        Button(onClick = { viewModel.pilihAgama(4, "Doktrin Militer Suci"); showAgamaDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Doktrin Militer Suci (+25% Atk, +15% Cost)") }
                    }
                    if (levelTemple >= 5) {
                        Text("Kuil Lv 5:", fontWeight = FontWeight.Bold)
                        Button(onClick = { viewModel.pilihAgama(5, "Kosmisme Agung"); showAgamaDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Kosmisme Agung (+40% HP Tembok, -20% Oil/Uranium)") }
                        Button(onClick = { viewModel.pilihAgama(5, "Sinkretisme Logam"); showAgamaDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Sinkretisme Logam (+35% Titanium/Uranium, -10% Gudang)") }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showAgamaDialog = false }) { Text("Batal") } }
        )
    }

    if (showGudangDialog) {"""

content = content.replace(target, new_code)

# Clean up leftover confirm button from previous bad bracket
target2 = """            },
            confirmButton = { TextButton(onClick = { showAgamaDialog = false }) { Text("Batal") } }
        )
    }
    
    Column(modifier = modifier.fillMaxSize()) {"""

new_code2 = """    
    Column(modifier = modifier.fillMaxSize()) {"""

content = content.replace(target2, new_code2)


with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
