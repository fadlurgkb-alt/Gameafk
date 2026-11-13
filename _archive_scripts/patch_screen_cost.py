import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

target = """            val actualZenithCost = node.zenithCost * costMultiplier
            val actualResCosts = node.resourceCosts.mapValues { 
                var cost = (it.value * costMultiplier).toLong()
                if (state.isUnlocked("metalurgi_militer") && node.type == "senjata/armor" && (it.key == "besi" || it.key == "emas")) {
                    cost = (cost * 0.85).toLong()
                }
                if (node.tier >= 3 && (node.type == "bangunan" || node.type == "filsafat")) {
                    cost = (cost * 1.8).toLong()
                }
                cost
            }"""

new_code = """            var relCostBangunan = 1.0
            if (state.selectedReligions.values.contains("Doktrin Militer Suci")) relCostBangunan += 0.15
            var natCostBangunan = 1.0
            if (state.selectedNationality == "Romawi") natCostBangunan -= 0.10
            
            val totalCostBangunanMod = if (node.type == "bangunan") relCostBangunan * natCostBangunan else 1.0

            val actualZenithCost = node.zenithCost * costMultiplier * totalCostBangunanMod
            val actualResCosts = node.resourceCosts.mapValues { 
                var cost = (it.value * costMultiplier * totalCostBangunanMod).toLong()
                if (state.isUnlocked("metalurgi_militer") && node.type == "senjata/armor" && (it.key == "besi" || it.key == "emas")) {
                    cost = (cost * 0.85).toLong()
                }
                if (node.tier >= 3 && (node.type == "bangunan" || node.type == "filsafat")) {
                    cost = (cost * 1.8).toLong()
                }
                cost
            }"""

content = content.replace(target, new_code)

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
