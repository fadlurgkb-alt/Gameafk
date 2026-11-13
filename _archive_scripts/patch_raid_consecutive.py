import re

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    content = f.read()

# 1. Read consecutiveLosses from state
target1 = "var newRaidLevel = currentState.raidLevel"
replacement1 = "var newRaidLevel = currentState.raidLevel\n            var newConsecutiveLosses = currentState.consecutiveLosses"
content = content.replace(target1, replacement1)

# 2. Add penalty
target2 = """                            _alertMessage.value = "🚨 KOTA DISERANG! Raid Lv.$newRaidLevel gagal ditahan! (Enemy Atk: $enemyAtk vs Def: $wallDefense).\\nMusuh menjarah 20% seluruh sumber daya!\""""
replacement2 = """                            newConsecutiveLosses += 1
                            if (newConsecutiveLosses == 1) {
                                currentHappiness -= 3
                            } else if (newConsecutiveLosses >= 2) {
                                currentHappiness -= 6
                            }
                            if (currentHappiness < 0) currentHappiness = 0
                            
                            _alertMessage.value = "🚨 KOTA DISERANG! Raid Lv.$newRaidLevel gagal ditahan! (Enemy Atk: $enemyAtk vs Def: $wallDefense).\\nMusuh menjarah 20% sumber daya! Happiness turun karena kalah beruntun ($newConsecutiveLosses x).\""""
content = content.replace(target2, replacement2)

# 3. Reset penalty on win
target3 = """                        newGameCoins += 100L
                        val prevRaidLevel = newRaidLevel"""
replacement3 = """                        newConsecutiveLosses = 0
                        newGameCoins += 100L
                        val prevRaidLevel = newRaidLevel"""
content = content.replace(target3, replacement3)

# 4. Save back to state
target4 = "                raidLevel = newRaidLevel,"
replacement4 = "                raidLevel = newRaidLevel,\n                consecutiveLosses = newConsecutiveLosses,"
content = content.replace(target4, replacement4)


with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'w') as f:
    f.write(content)
