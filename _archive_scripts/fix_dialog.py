import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

stat_dialog = """
    if (showStatDialog) {
        AlertDialog(
            onDismissRequest = { showStatDialog = false },
            title = { Text("Statistik Peradaban") },
            text = { 
                val keamanan = (state.currentPasukan * 2 + state.maxGerbangHp / 50).coerceIn(0, 100)
                Text("Populasi: ${state.currentPasukan}/${state.maxPasukan}\\nHappiness: ${state.happiness}%\\nKapasitas Gudang: ${state.maxStorage}\\nTingkat Keamanan Kota: $keamanan%") 
            },
            confirmButton = { TextButton(onClick = { showStatDialog = false }) { Text("Tutup") } }
        )
    }
"""
content = re.sub(
    r'    if \(showStatDialog\) \{.*?    \}',
    stat_dialog.strip(),
    content,
    flags=re.DOTALL,
    count=1
)

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
