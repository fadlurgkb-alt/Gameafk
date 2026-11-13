import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

old_colors = """                                containerColor = if (isUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                disabledContainerColor = if (isUnlocked) MaterialTheme.colorScheme.primary else Color.Gray"""
new_colors = """                                containerColor = if (isUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                disabledContainerColor = Color.DarkGray,
                                disabledContentColor = Color.LightGray"""

content = content.replace(old_colors, new_colors)

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
