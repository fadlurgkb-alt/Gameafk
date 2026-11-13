import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

# Modify Town Hall base cost
def town_hall_repl(m):
    return f'TalentNode("town_hall", "Town Hall", 0.0, mapOf("kayu" to 28L, "batu" to 28L),'
content = re.sub(r'TalentNode\("town_hall", "Town Hall", 0.0, mapOf\("kayu" to 24L, "batu" to 24L\),', town_hall_repl, content)

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
