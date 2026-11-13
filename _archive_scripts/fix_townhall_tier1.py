import re

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    content = f.read()

# Fix town hall level constraint
old_req = """            if (node.type == "bangunan" && id != "town_hall") {
                val townHallLvl = currentState.getBuildingLevel("town_hall")
                if (townHallLvl > 0 && currentLevel >= townHallLvl) {"""
new_req = """            if (node.type == "bangunan" && id != "town_hall") {
                val townHallLvl = currentState.getBuildingLevel("town_hall")
                // By pass restriction for tier 1 buildings
                val isTier1 = node.tier == 1
                if (!isTier1 && townHallLvl > 0 && currentLevel >= townHallLvl) {"""
content = content.replace(old_req, new_req)

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'w') as f:
    f.write(content)
