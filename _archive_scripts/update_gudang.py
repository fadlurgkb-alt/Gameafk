import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

# Update Gudang Dialog
old_gudang = """    if (showGudangDialog) {
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
    }"""

new_gudang = """    if (showGudangDialog) {
        AlertDialog(
            onDismissRequest = { showGudangDialog = false },
            title = { Text("📦 Isi Gudang (Max: ${state.maxStorage})") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🪵 Material Dasar:", fontWeight = FontWeight.Bold)
                    Text("Kayu: ${state.kayu}/${state.maxStorage} | Batu: ${state.batu}/${state.maxStorage}\nBesi: ${state.besi}/${state.maxStorage} | Emas: ${state.emas}/${state.maxStorage}")
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("⚙️ Material Lanjut:", fontWeight = FontWeight.Bold)
                    Text("Oil: ${state.oil}/${state.maxStorage} | Listrik: ${state.listrik}/${state.maxStorage}\nTitanium: ${state.titanium}/${state.maxStorage} | Uranium: ${state.uranium}/${state.maxStorage}")
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("🍞 Makanan:", fontWeight = FontWeight.Bold)
                    Text("Daging: ${state.daging}/${state.maxStorage} | Berry: ${state.berry}/${state.maxStorage}\nGandum: ${state.gandum}/${state.maxStorage} | Roti: ${state.roti}/${state.maxStorage}\nKalengan: ${state.kalengan}/${state.maxStorage} | Ikan: ${state.ikan}/${state.maxStorage}")
                }
            },
            confirmButton = { TextButton(onClick = { showGudangDialog = false }) { Text("Tutup") } }
        )
    }"""

if old_gudang in content:
    content = content.replace(old_gudang, new_gudang)
else:
    print("Old gudang dialog not found!")

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
