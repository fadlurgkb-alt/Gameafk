import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

target = """                        Text("Pasif: +$actualDamage Attack, +$actualHp HP", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }"""

new_code = """                        Text("Pasif: +$actualDamage Attack, +$actualHp HP", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        val isGarrisoned = state.garrisonedHeroes.contains(heroName)
                        Button(
                            onClick = { viewModel.toggleGarrisonHero(heroName) },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isGarrisoned) Color.Red else Color.Green)
                        ) {
                            Text(if (isGarrisoned) "Lepas Jaga" else "Tugaskan Jaga")
                        }
                    }
                }
            }"""

content = content.replace(target, new_code)

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
