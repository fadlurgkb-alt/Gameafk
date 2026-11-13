import re

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    lines = f.readlines()

out = []
in_func = False
for line in lines:
    if 'private fun generateLoot' in line:
        out.append(line)
        in_func = True
        continue
    if in_func and 'return Pair(zenithGained, message)' in line:
        out.append("""        
        // Add to inventory
        _uiState.update { st ->
            val inv = st.inventory.toMutableMap()
            if (rand < 0.02) inv["item_ssr"] = (inv["item_ssr"] ?: 0) + 1
            else if (rand < 0.17) inv["item_sr"] = (inv["item_sr"] ?: 0) + 1
            else inv["item_rare"] = (inv["item_rare"] ?: 0) + 1
            
            st.copy(inventoryStr = inv.map { "${it.key}:${it.value}" }.joinToString(","))
        }
""")
        out.append(line)
        in_func = False
        continue
    out.append(line)

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'w') as f:
    f.writelines(out)
