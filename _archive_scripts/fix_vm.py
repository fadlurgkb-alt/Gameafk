import re

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    content = f.read()

# Fix the duplicate lines from patch_vm_shop.py
bad_dup = """            newState = newState.copy(inventoryStr = inv.map { "${it.key}:${it.value}" }.joinToString(","))
            viewModelScope.launch { repository.saveGameState(newState) }
            newState
        }
    }"""
content = content.replace(bad_dup + "\n" + bad_dup, bad_dup)

# Fix broken strings
content = content.replace('Def: $wallDefense).\nNamun', 'Def: $wallDefense).\\nNamun')
content = content.replace('dibatal\nkan."', 'dibatalkan."') # Just in case

content = content.replace('Def: $wallDefense).\nMusuh', 'Def: $wallDefense).\\nMusuh')
content = content.replace('Def: $wallDefense).\nHadiah', 'Def: $wallDefense).\\nHadiah')

# Another error: e: file:///app/src/main/java/com/example/MainActivity.kt:20:33 Unresolved reference 'Factory'.
# Actually GameViewModel.Factory is at the end.
# In my GameViewModel, it has `class Factory...` inside `companion object`? No, it's inside GameViewModel.
# Wait, `GameViewModel.Factory` unresolved? Let's check `MainActivity.kt`.

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'w') as f:
    f.write(content)
