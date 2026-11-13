import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

# Add hospital to allNodes
# TalentNode("town_hall", "Town Hall", 0.0, mapOf("kayu" to 28L, "batu" to 28L), "Melihat statistik peradaban", "bangunan", tier = 1),
hospital_node = """    TalentNode("town_hall", "Town Hall", 0.0, mapOf("kayu" to 28L, "batu" to 28L), "Melihat statistik peradaban", "bangunan", tier = 1),
    TalentNode("hospital", "Rumah Sakit", 0.0, mapOf("kayu" to 150L, "batu" to 100L), "Menyembuhkan pasukan yang terluka", "bangunan", tier = 1),"""
content = content.replace('    TalentNode("town_hall", "Town Hall", 0.0, mapOf("kayu" to 28L, "batu" to 28L), "Melihat statistik peradaban", "bangunan", tier = 1),', hospital_node)

# In GenericTab, filter hospital based on town_hall level 2
generic_tab_regex = r'(val nodes = allNodes\.filter \{ it\.type == type \})'
filter_logic = """    val nodes = allNodes.filter { 
        if (it.type != type) return@filter false
        if (it.id == "hospital") {
            return@filter state.getBuildingLevel("town_hall") >= 2
        }
        true
    }"""
content = re.sub(generic_tab_regex, filter_logic, content)

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
