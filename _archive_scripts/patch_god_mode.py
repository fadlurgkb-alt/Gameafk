import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

# 1. Change HeaderSection call
call_target = "HeaderSection(state)"
call_replacement = "HeaderSection(state, viewModel)"
content = content.replace(call_target, call_replacement)

# 2. Change HeaderSection definition
def_target = """@Composable
fun HeaderSection(state: GameState) {"""
def_replacement = """@Composable
fun HeaderSection(state: GameState, viewModel: GameViewModel) {"""
content = content.replace(def_target, def_replacement)

# 3. Add God Mode button
zenith_target = """        Text(
            text = "✨ Zenith: ${state.totalZenith.toLong()} Z",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color.White)
        )"""
zenith_replacement = """        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "✨ Zenith: ${state.totalZenith.toLong()} Z",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color.White)
            )
            Button(onClick = { viewModel.sandboxGodMode() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA))) {
                Text("🛠️ Fitur Dewa", fontSize = 12.sp)
            }
        }"""
content = content.replace(zenith_target, zenith_replacement)

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
