import re

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    content = f.read()

raid_logic_replacement = """
            if (unlocked.contains("tembok")) {
                if (newRaidTimer > 0) {
                    newRaidTimer -= 1
                } else {
                    newRaidTimer = 180
                    var enemyAtk = newRaidLevel * 5000L
                    val wallDefense = maxGerbangHp
                    var playerCounterAtk = (garrisonDamage * 0.8).toLong()
                    
                    val currentInv = currentState.inventory.toMutableMap()
                    val trapCount = currentInv["trap_city"] ?: 0
                    if (trapCount > 0) {
                        enemyAtk = (enemyAtk * 0.9).toLong()
                    }
                    
                    val cannonCount = currentInv["cannon_gate"] ?: 0
                    if (cannonCount > 0) {
                        playerCounterAtk += (180 / 3) * 300L
                    }
                    
                    val crossbowCount = currentInv["crossbow_gate"] ?: 0
                    if (crossbowCount > 0) {
                        playerCounterAtk += 180 * 200L
                    }
                    
                    val shieldCount = currentInv["shield_1h"] ?: 0
                    
                    val finalEnemyAtk = enemyAtk - playerCounterAtk
                    if (finalEnemyAtk > wallDefense) {
                        if (shieldCount > 0) {
                            _alertMessage.value = "🚨 KOTA DISERANG! Raid Lv.$newRaidLevel gagal ditahan! (Enemy Atk: $enemyAtk vs Def: $wallDefense).\\nNamun Shield 1 Jam Kota aktif! Penjarahan dibatalkan."
                        } else {
                            newKayu = (newKayu * 0.8).toLong()
                            newBatu = (newBatu * 0.8).toLong()
                            newBesi = (newBesi * 0.8).toLong()
                            newEmas = (newEmas * 0.8).toLong()
                            newOil = (newOil * 0.8).toLong()
                            newListrik = (newListrik * 0.8).toLong()
                            newTitanium = (newTitanium * 0.8).toLong()
                            newUranium = (newUranium * 0.8).toLong()
                            newDaging = (newDaging * 0.8).toLong()
                            newBerry = (newBerry * 0.8).toLong()
                            newGandum = (newGandum * 0.8).toLong()
                            newRoti = (newRoti * 0.8).toLong()
                            newKalengan = (newKalengan * 0.8).toLong()
                            newIkan = (newIkan * 0.8).toLong()
                            
                            _alertMessage.value = "🚨 KOTA DISERANG! Raid Lv.$newRaidLevel gagal ditahan! (Enemy Atk: $enemyAtk vs Def: $wallDefense).\\nMusuh menjarah 20% seluruh sumber daya!"
                        }
                    } else {
                        newGameCoins += 100L
                        val prevRaidLevel = newRaidLevel
                        newRaidLevel += 1
                        _alertMessage.value = "🛡️ SERANGAN DITAHAN! Raid Lv.$prevRaidLevel berhasil digagalkan! (Enemy Atk: $enemyAtk vs Def: $wallDefense).\\nHadiah: +100 Game Coins!"
                    }
                }
            }
"""

content = re.sub(
    r'            if \(unlocked\.contains\("tembok"\)\) \{.*?            \}\n\n            val newState = currentState\.copy\(',
    raid_logic_replacement.strip() + '\n\n            val newState = currentState.copy(',
    content,
    flags=re.DOTALL
)

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'w') as f:
    f.write(content)
