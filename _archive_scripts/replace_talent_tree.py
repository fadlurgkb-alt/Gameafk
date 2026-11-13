import re

with open("app/src/main/java/com/example/ui/game/GameScreen.kt", "r") as f:
    content = f.read()

start_idx = content.find("@Composable\nfun TalentTreeDialog")
end_idx = content.find("\n@Composable", start_idx + 1)
if end_idx == -1:
    end_idx = len(content)

new_content = """@Composable
fun TalentTreeDialog(
    state: GameState,
    viewModel: GameViewModel,
    onDismiss: () -> Unit
) {
    val treeNodes = remember {
        val nodes = allNodes.filter { it.type == "skill" }.toMutableList()
        for (i in 1 until nodes.size) {
            val node = nodes[i]
            if (node.reqs.isEmpty() && node.id != "percikan_api" && node.id != "panggil_manusia") {
                val newReqs = listOf(nodes[i - 1].id)
                nodes[i] = node.copy(reqs = newReqs)
            }
        }
        var lastY = 6
        for (i in 0 until nodes.size) {
            val node = nodes[i]
            if (node.gridY == 0 && node.gridX == 0 && node.id != "percikan_api") {
                nodes[i] = node.copy(gridY = lastY, gridX = 0)
                lastY++
            }
        }
        nodes
    }

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    
    var selectedNode by remember { mutableStateOf<TalentNode?>(null) }
    val shownStories = remember { mutableStateListOf<String>() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF090514)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                androidx.compose.foundation.Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            androidx.compose.foundation.gestures.detectDragGestures { change, dragAmount ->
                                change.consume()
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                            }
                        }
                ) {
                    val spacingX = 350f
                    val spacingY = 350f
                    val centerX = size.width / 2f + offsetX
                    val centerY = 200f + offsetY

                    treeNodes.forEach { node ->
                        node.reqs.forEach { reqId ->
                            val parent = treeNodes.find { it.id == reqId }
                            if (parent != null) {
                                val startX = centerX + parent.gridX * spacingX
                                val startY = centerY + parent.gridY * spacingY
                                val endX = centerX + node.gridX * spacingX
                                val endY = centerY + node.gridY * spacingY
                                
                                val isParentUnlocked = state.isUnlocked(parent.id)
                                val isUnlocked = state.isUnlocked(node.id)
                                
                                val lineColor = if (isUnlocked) Color(0xFFFBBF24)
                                                else if (isParentUnlocked) Color(0xFF00E5FF).copy(alpha = 0.5f)
                                                else Color.DarkGray
                                                
                                drawLine(
                                    color = lineColor,
                                    start = Offset(startX, startY),
                                    end = Offset(endX, endY),
                                    strokeWidth = if (isUnlocked) 6f else 4f
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            androidx.compose.foundation.gestures.detectDragGestures { change, dragAmount ->
                                change.consume()
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                            }
                        }
                ) {
                    val density = androidx.compose.ui.platform.LocalDensity.current
                    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
                    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
                    
                    val spacingX = 350f
                    val spacingY = 350f
                    val centerX = screenWidthPx / 2f + offsetX
                    val centerY = 200f + offsetY

                    treeNodes.forEach { node ->
                        val isUnlocked = state.isUnlocked(node.id)
                        val isAvailable = node.reqs.isEmpty() || node.reqs.all { state.isUnlocked(it) }
                        
                        val nodeColor = if (isUnlocked) Color(0xFFFBBF24)
                                        else if (isAvailable) Color(0xFF00E5FF)
                                        else Color.DarkGray

                        val xPos = centerX + node.gridX * spacingX - 100f
                        val yPos = centerY + node.gridY * spacingY - 40f

                        Box(
                            modifier = Modifier
                                .offset { androidx.compose.ui.unit.IntOffset(xPos.toInt(), yPos.toInt()) }
                                .width(200.dp)
                                .height(80.dp)
                                .clip(RoundedCornerShape(40.dp))
                                .background(nodeColor.copy(alpha = if (isUnlocked || isAvailable) 0.2f else 0.1f))
                                .border(3.dp, nodeColor, RoundedCornerShape(40.dp))
                                .clickable {
                                    selectedNode = node
                                }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isUnlocked) "✅" else if (isAvailable) "🔓" else "🔒",
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = node.name,
                                    fontSize = 12.sp,
                                    color = if (isUnlocked || isAvailable) Color.White else Color.Gray,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.TopCenter)
                        .background(Color(0xFF090514).copy(alpha = 0.8f))
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "🧬 TALENT TREE VISUAL",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFC084FC)
                        )
                        Text(
                            "Silsilah evolusi & riset peradaban",
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.1f), CircleShape)
                            .size(36.dp)
                    ) {
                        Text("❌", fontSize = 14.sp)
                    }
                }
                
                if (selectedNode != null) {
                    val node = selectedNode!!
                    val isUnlocked = state.isUnlocked(node.id)
                    val isAvailable = node.reqs.isEmpty() || node.reqs.all { state.isUnlocked(it) }
                    
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1E1B4B).copy(alpha = 0.95f))
                            .border(2.dp, Color(0xFF7C3AED), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = node.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFBBF24)
                                )
                                IconButton(onClick = { selectedNode = null }, modifier = Modifier.size(24.dp)) {
                                    Text("❌", fontSize = 12.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            if (node.story.isNotEmpty() && (!isUnlocked || !shownStories.contains(node.id))) {
                                if (isUnlocked && !shownStories.contains(node.id)) {
                                    shownStories.add(node.id)
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.Black.copy(alpha = 0.5f))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "📜 \"${node.story}\"",
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                        fontSize = 12.sp,
                                        color = Color(0xFFE2E8F0)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            
                            Text(
                                text = node.desc,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            if (isUnlocked) {
                                Text("✅ Riset telah dikuasai", color = Color(0xFF34D399), fontWeight = FontWeight.Bold)
                            } else if (!isAvailable) {
                                Text("🔒 Prasyarat belum terpenuhi", color = Color.Red, fontWeight = FontWeight.Bold)
                            } else {
                                val canAfford = canAffordNode(state, node.zenithCost, node.resourceCosts)
                                val costText = formatCost(node.zenithCost, node.resourceCosts)
                                Text("Biaya: $costText", color = if (canAfford) Color.White else Color.Red, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { viewModel.buyNode(node.id); selectedNode = null },
                                    enabled = canAfford,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                                ) {
                                    Text("Riset Sekarang")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
"""

with open("app/src/main/java/com/example/ui/game/GameScreen.kt", "w") as f:
    f.write(content[:start_idx] + new_content + content[end_idx:])

