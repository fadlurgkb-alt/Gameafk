import re

with open("app/src/main/java/com/example/ui/game/GameScreen.kt", "r") as f:
    content = f.read()

# The pattern we want to replace
old_button = """                        if (state.isUnlocked("dungeon")) {
                            IconButton(
                                onClick = { viewModel.startDungeonBattle(state.maxDungeonLevel) },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(end = 12.dp, bottom = 80.dp)
                                    .size(56.dp)
                                    .background(Color(0xFF3B82F6), CircleShape)
                                    .border(2.dp, Color(0xFF06B6D4), CircleShape)
                                    .testTag("floating_dungeon_btn")
                            ) {
                                Text("🌀", fontSize = 24.sp)
                            }
                        }"""

new_button = """                        if (state.isUnlocked("dungeon")) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(end = 12.dp, bottom = 80.dp)
                                    .size(64.dp)
                                    .clickable { viewModel.startDungeonBattle(state.maxDungeonLevel) }
                                    .testTag("floating_dungeon_btn"),
                                contentAlignment = Alignment.Center
                            ) {
                                val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition()
                                val rotation by infiniteTransition.animateFloat(
                                    initialValue = 0f,
                                    targetValue = 360f,
                                    animationSpec = androidx.compose.animation.core.infiniteRepeatable(
                                        animation = androidx.compose.animation.core.tween(3000, easing = androidx.compose.animation.core.LinearEasing),
                                        repeatMode = androidx.compose.animation.core.RepeatMode.Restart
                                    )
                                )
                                val pulse by infiniteTransition.animateFloat(
                                    initialValue = 0.8f,
                                    targetValue = 1.2f,
                                    animationSpec = androidx.compose.animation.core.infiniteRepeatable(
                                        animation = androidx.compose.animation.core.tween(1500, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                                        repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
                                    )
                                )

                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val cx = size.width / 2f
                                    val cy = size.height / 2f
                                    val radius = size.width / 2f - 4f
                                    
                                    // Outer metallic ring
                                    drawCircle(
                                        color = Color(0xFF334155),
                                        radius = radius,
                                        center = Offset(cx, cy),
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6f)
                                    )
                                    drawCircle(
                                        color = Color(0xFF94A3B8),
                                        radius = radius - 2f,
                                        center = Offset(cx, cy),
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                                    )
                                    
                                    // Vortex
                                    withTransform({
                                        rotate(rotation, Offset(cx, cy))
                                    }) {
                                        for (i in 0 until 5) {
                                            val ang = i * (360f / 5f)
                                            val rad = Math.toRadians(ang.toDouble())
                                            val px = cx + kotlin.math.cos(rad).toFloat() * (radius * 0.4f)
                                            val py = cy + kotlin.math.sin(rad).toFloat() * (radius * 0.4f)
                                            drawCircle(
                                                color = Color(0xFF8B5CF6).copy(alpha = 0.6f),
                                                radius = radius * 0.6f * pulse,
                                                center = Offset(px, py)
                                            )
                                            drawCircle(
                                                color = Color(0xFF06B6D4).copy(alpha = 0.8f),
                                                radius = radius * 0.3f * pulse,
                                                center = Offset(px, py)
                                            )
                                        }
                                        drawCircle(
                                            color = Color.Black,
                                            radius = radius * 0.2f,
                                            center = Offset(cx, cy)
                                        )
                                    }
                                    
                                    // Lightning particles
                                    val t = System.currentTimeMillis() / 1000f
                                    for (i in 0 until 6) {
                                        val offset = (i * 13f) % 360f
                                        val speed = 2f
                                        val ang = (t * speed * 50f + offset) % 360f
                                        val rad = Math.toRadians(ang.toDouble())
                                        val dist = radius * 0.8f * (0.5f + 0.5f * kotlin.math.sin(t * 3f + i))
                                        val px = cx + kotlin.math.cos(rad).toFloat() * dist
                                        val py = cy + kotlin.math.sin(rad).toFloat() * dist
                                        drawCircle(
                                            color = Color(0xFF38BDF8),
                                            radius = 3f * pulse,
                                            center = Offset(px, py)
                                        )
                                    }
                                }
                            }
                        }"""

content = content.replace(old_button, new_button)

with open("app/src/main/java/com/example/ui/game/GameScreen.kt", "w") as f:
    f.write(content)

