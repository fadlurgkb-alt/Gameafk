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
            val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition()
            val pulseAlpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = androidx.compose.animation.core.infiniteRepeatable(
                    animation = androidx.compose.animation.core.tween(1000, easing = androidx.compose.animation.core.LinearEasing),
                    repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
                )
            )

            Box(modifier = Modifier.fillMaxSize()) {
                androidx.compose.foundation.Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                            }
                        }
                ) {
                    val spacingX = 220f
                    val spacingY = 220f
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
                                                else if (isParentUnlocked) Color(0xFF00E5FF).copy(alpha = 0.8f)
                                                else Color(0xFF333333).copy(alpha = 0.5f)
                                                
                                drawLine(
                                    color = lineColor,
                                    start = Offset(startX, startY),
                                    end = Offset(endX, endY),
                                    strokeWidth = if (isUnlocked) 3f else 1.5f
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                            }
                        }
                ) {
                    val density = androidx.compose.ui.platform.LocalDensity.current
                    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
                    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
                    
                    val spacingX = 220f
                    val spacingY = 220f
                    val centerX = screenWidthPx / 2f + offsetX
                    val centerY = 200f + offsetY

                    treeNodes.forEach { node ->
                        val isUnlocked = state.isUnlocked(node.id)
                        val isAvailable = node.reqs.isEmpty() || node.reqs.all { state.isUnlocked(it) }
                        
                        val nodeColor = if (isUnlocked) Color(0xFFFBBF24)
                                        else if (isAvailable) Color(0xFF00E5FF)
                                        else Color(0xFF333333)

                        val xPos = centerX + node.gridX * spacingX
                        val yPos = centerY + node.gridY * spacingY

                        Box(
                            modifier = Modifier
                                .offset { androidx.compose.ui.unit.IntOffset((xPos - 80f).toInt(), (yPos - 60f).toInt()) }
                                .width(160.dp)
                                .height(120.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isUnlocked) Color(0xFF451A03).copy(alpha = 0.8f)
                                            else if (isAvailable) Color(0xFF082F49).copy(alpha = 0.8f)
                                            else Color(0xFF1E293B).copy(alpha = 0.8f)
                                        )
                                        .border(
                                            width = if (isUnlocked) 1.5.dp else if (isAvailable) 2.dp else 1.dp,
                                            color = if (isAvailable && !isUnlocked) nodeColor.copy(alpha = pulseAlpha) else nodeColor,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            selectedNode = node
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!isUnlocked && !isAvailable) {
                                        Text("🔒", fontSize = 16.sp)
                                    } else {
                                        val icon = when {
                                            node.id.contains("api") -> "🔥"
                                            node.id.contains("manusia") || node.id.contains("kognitif") -> "👤"
                                            node.id.contains("batu") -> "🪨"
                                            node.id.contains("tulang") -> "🦴"
                                            node.id.contains("berburu") -> "🏹"
                                            node.id.contains("emosi") -> "❤️"
                                            node.id.contains("moralitas") -> "⚖️"
                                            node.id.contains("masalah") -> "🧠"
                                            node.id.contains("budidaya") -> "🧺"
                                            node.id.contains("hewan") -> "🐂"
                                            node.id.contains("roti") -> "🍞"
                                            node.id.contains("medis") || node.id.contains("biokimia") -> "💊"
                                            node.id.contains("komputer") || node.id.contains("link") -> "💻"
                                            else -> "💡"
                                        }
                                        Text(icon, fontSize = 24.sp)
                                        
                                        if (isUnlocked) {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.BottomEnd
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .offset(x = 6.dp, y = 6.dp)
                                                        .size(16.dp)
                                                        .background(Color(0xFF34D399), CircleShape)
                                                        .border(1.dp, Color.Black, CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text("✓", fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.ExtraBold)
                                                }
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = node.name,
                                    fontSize = 10.sp,
                                    color = if (isUnlocked) Color(0xFFFBBF24) else if (isAvailable) Color.White else Color.Gray,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 12.sp,
                                    modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 2.dp)
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
                            "🌌 JEJARING KOSMIS",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF00E5FF)
                        )
                        Text(
                            "Evolusi peradaban & riset",
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
                            .border(2.dp, Color(0xFF00E5FF), RoundedCornerShape(16.dp))
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
                                    color = Color(0xFF00E5FF)
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
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color.Black)
                                ) {
                                    Text("Riset Sekarang", fontWeight = FontWeight.Bold)
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

