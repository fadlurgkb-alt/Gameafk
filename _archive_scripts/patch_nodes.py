import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

target = """    TalentNode("biokimia_sintetis", "Nutrisi Biokimia Sintetis", 2592000.0, emptyMap(), "Pil suplemen mengurangi konsumsi pangan warga sebesar 40%", "skill", tier = 1),"""

new_node = """    TalentNode("biokimia_sintetis", "Nutrisi Biokimia Sintetis", 2592000.0, emptyMap(), "Pil suplemen mengurangi konsumsi pangan warga sebesar 40%", "skill", tier = 1),
    TalentNode("kedaulatan_bangsa", "Kedaulatan Bangsa", 3000000.0, emptyMap(), "Membuka Fitur Kebangsaan di Town Hall level 6", "skill", tier = 1),"""

content = content.replace(target, new_node)

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
