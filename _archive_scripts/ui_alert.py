import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

# I will hook alertMessage to AlertDialog just below gachaResult
alert_block = """    val gachaResult by viewModel.gachaResult.collectAsState()
    val alertMessage by viewModel.alertMessage.collectAsState()
    
    if (alertMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearAlertMessage() },
            title = { Text("⚠️ Peringatan") },
            text = { Text(alertMessage ?: "", fontWeight = FontWeight.Bold) },
            confirmButton = { TextButton(onClick = { viewModel.clearAlertMessage() }) { Text("OK") } }
        )
    }"""

content = content.replace(
    'val gachaResult by viewModel.gachaResult.collectAsState()',
    alert_block
)

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
