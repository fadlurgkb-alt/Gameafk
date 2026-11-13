import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

target = """            if (state.isDungeonActive) {
                DungeonCanvas(
                    state = state,
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF111827))
                )
            } else {"""

new_code = """            if (state.isDungeonActive) {
                DungeonCanvas(
                    state = state,
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF111827))
                )
            } else if (state.isRaidBattleActive) {
                RaidCanvas(
                    state = state,
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF1E1B4B))
                )
            } else {"""

content = content.replace(target, new_code)

raid_canvas_code = """
@Composable
fun RaidCanvas(state: GameState, viewModel: GameViewModel, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val time by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing), RepeatMode.Restart)
    )
    
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2
            val cy = size.height / 2
            
            // Draw raid background
            drawRect(Color(0xFF2E1025), size = size)
            
            // Draw Tembok (Gate) on left
            drawRect(Color(0xFF4B5563), topLeft = Offset(50f, cy - 100f), size = Size(80f, 200f))
            drawRect(Color(0xFF9CA3AF), topLeft = Offset(60f, cy - 110f), size = Size(60f, 20f))
            
            // Heroes and Pasukan on top or beside the wall
            state.garrisonedHeroes.forEachIndexed { idx, hero ->
                val heroX = 150f + (idx * 40f)
                val heroY = cy + (idx % 2 * 30f)
                drawCircle(Color.Green, radius = 15f, center = Offset(heroX, heroY))
                drawRect(Color.White, topLeft = Offset(heroX - 5f, heroY), size = Size(10f, 20f))
            }
            
            val offsetT = (time * 50) % 200
            
            // Draw enemies coming from right
            for (i in 0 until 5) {
                val enemyX = size.width - 100f - offsetT + (i * 30f)
                val enemyY = cy + (i * 20f) - 40f
                drawCircle(Color.Red, radius = 20f, center = Offset(enemyX, enemyY))
                drawCircle(Color.Black, radius = 5f, center = Offset(enemyX - 5f, enemyY - 5f))
            }
            
            // Projectiles from left to right
            val projX = 200f + offsetT * 3
            if (projX < size.width) {
                drawCircle(Color.Yellow, radius = 5f, center = Offset(projX, cy))
                drawCircle(Color.Yellow, radius = 5f, center = Offset(projX - 20f, cy + 20f))
            }
        }
        
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("🚨 KOTA DISERANG: RAID LV ${state.raidLevel} 🚨", style = MaterialTheme.typography.headlineSmall, color = Color.Red, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Gerbang Kota", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("HP: ${state.raidPlayerHp} / ${state.maxGerbangHp}", color = Color.Green)
                    Text("Pasukan: ${state.garrisonedPasukan}", color = Color.LightGray)
                    Text("Heroes: ${state.garrisonedHeroes.size}", color = Color.LightGray)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Pasukan Musuh", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("HP: ${state.raidEnemyHp} / ${state.raidEnemyMaxHp}", color = Color.Red)
                }
            }
        }
    }
}
"""

content += raid_canvas_code

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
