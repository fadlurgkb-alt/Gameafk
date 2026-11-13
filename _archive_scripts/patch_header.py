import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

target = """            Button(onClick = { viewModel.sandboxGodMode() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA))) {
                Text("🛠️ Fitur Dewa", fontSize = 12.sp)
            }
        }"""

new_buttons = """            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Button(onClick = { viewModel.sandboxGodMode() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA)), contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)) {
                    Text("🛠️ Dewa", fontSize = 10.sp)
                }
                Button(onClick = { viewModel.activateMaxAllMode() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)), contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)) {
                    Text("⚡ MAX ALL", fontSize = 10.sp, color = Color.White)
                }
            }
        }"""

content = content.replace(target, new_buttons)

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
