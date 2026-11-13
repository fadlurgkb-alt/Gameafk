import re

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    content = f.read()

target = """if (unlocked.contains("tembok")) {
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
                            _alertMessage.value = "🚨 KOTA DISERANG! Raid Lv.$newRaidLevel gagal ditahan! (Enemy Atk: $enemyAtk vs Def: $wallDefense).\nNamun Shield 1 Jam Kota aktif! Penjarahan dibatalkan."
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
                            
                            newConsecutiveLosses += 1
                            if (newConsecutiveLosses == 1) {
                                currentHappiness -= 3
                            } else if (newConsecutiveLosses >= 2) {
                                currentHappiness -= 6
                            }
                            if (currentHappiness < 0) currentHappiness = 0
                            
                            _alertMessage.value = "🚨 KOTA DISERANG! Raid Lv.$newRaidLevel gagal ditahan! (Enemy Atk: $enemyAtk vs Def: $wallDefense).\nMusuh menjarah 20% sumber daya! Happiness turun karena kalah beruntun ($newConsecutiveLosses x)."
                        }
                    } else {
                        newConsecutiveLosses = 0
                        newGameCoins += 100L
                        val prevRaidLevel = newRaidLevel
                        newRaidLevel += 1
                        _alertMessage.value = "🛡️ SERANGAN DITAHAN! Raid Lv.$prevRaidLevel berhasil digagalkan! (Enemy Atk: $enemyAtk vs Def: $wallDefense).\nHadiah: +100 Game Coins!"
                    }
                }
            }"""

new_code = """var newIsRaidBattleActive = currentState.isRaidBattleActive
            var newRaidEnemyHp = currentState.raidEnemyHp
            var newRaidEnemyMaxHp = currentState.raidEnemyMaxHp
            var newRaidPlayerHp = currentState.raidPlayerHp
            var newWoundedPasukan = currentState.woundedPasukan
            var newGarrisonedPasukan = currentState.garrisonedPasukan
            
            if (unlocked.contains("tembok")) {
                if (newRaidTimer > 0 && !newIsRaidBattleActive) {
                    newRaidTimer -= 1
                } else if (newRaidTimer <= 0 && !newIsRaidBattleActive) {
                    newIsRaidBattleActive = true
                    newRaidEnemyMaxHp = newRaidLevel * 5000L
                    
                    val currentInv = currentState.inventory.toMutableMap()
                    val trapCount = currentInv["trap_city"] ?: 0
                    if (trapCount > 0) {
                        newRaidEnemyMaxHp = (newRaidEnemyMaxHp * 0.9).toLong()
                    }
                    newRaidEnemyHp = newRaidEnemyMaxHp
                    newRaidPlayerHp = maxGerbangHp
                } else if (newIsRaidBattleActive) {
                    var playerCounterAtk = (garrisonDamage * 0.8).toLong()
                    
                    val currentInv = currentState.inventory.toMutableMap()
                    val cannonCount = currentInv["cannon_gate"] ?: 0
                    if (cannonCount > 0) playerCounterAtk += 300L
                    val crossbowCount = currentInv["crossbow_gate"] ?: 0
                    if (crossbowCount > 0) playerCounterAtk += 200L
                    
                    playerCounterAtk += currentState.garrisonedPasukan * 5L
                    val enemyAtkPerTick = newRaidLevel * 100L
                    
                    newRaidEnemyHp -= playerCounterAtk
                    newRaidPlayerHp -= enemyAtkPerTick
                    
                    if (newRaidEnemyHp <= 0 || newRaidPlayerHp <= 0) {
                        newIsRaidBattleActive = false
                        newRaidTimer = 180
                        
                        if (newRaidPlayerHp <= 0 && newRaidEnemyHp > 0) {
                            val shieldCount = currentInv["shield_1h"] ?: 0
                            if (shieldCount > 0) {
                                _alertMessage.value = "🚨 KOTA DISERANG! Raid Lv.$newRaidLevel gagal ditahan!\nNamun Shield 1 Jam Kota aktif! Penjarahan dibatalkan."
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
                                
                                newConsecutiveLosses += 1
                                if (newConsecutiveLosses == 1) currentHappiness -= 3 else if (newConsecutiveLosses >= 2) currentHappiness -= 6
                                if (currentHappiness < 0) currentHappiness = 0
                                
                                val deadPasukan = (newGarrisonedPasukan * 0.3).toLong()
                                val wounded = newGarrisonedPasukan - deadPasukan
                                newGarrisonedPasukan = 0L
                                
                                val hospitalCap = getHospitalCapacity(currentState.getBuildingLevel("hospital"))
                                val totalWounded = newWoundedPasukan + wounded
                                newWoundedPasukan = totalWounded.coerceAtMost(hospitalCap)
                                
                                _alertMessage.value = "🚨 KOTA DISERANG! Raid Lv.$newRaidLevel gagal ditahan!\nMusuh menjarah 20% sumber daya!\n$deadPasukan Pasukan Gugur, $wounded Terluka."
                            }
                        } else {
                            newConsecutiveLosses = 0
                            newGameCoins += 100L
                            val prevRaidLevel = newRaidLevel
                            newRaidLevel += 1
                            _alertMessage.value = "🛡️ SERANGAN DITAHAN! Raid Lv.$prevRaidLevel berhasil digagalkan!\nHadiah: +100 Game Coins!"
                        }
                    }
                }
            }"""

content = content.replace(target, new_code)

# Now update the state copy
state_copy_target = """                dungeonCoins = dungeonCoins,
                gachaTickets = gachaTickets,
                kayu = newKayu,"""

new_state_copy = """                dungeonCoins = dungeonCoins,
                gachaTickets = gachaTickets,
                isRaidBattleActive = newIsRaidBattleActive,
                raidEnemyHp = newRaidEnemyHp,
                raidEnemyMaxHp = newRaidEnemyMaxHp,
                raidPlayerHp = newRaidPlayerHp,
                woundedPasukan = newWoundedPasukan,
                garrisonedPasukan = newGarrisonedPasukan,
                kayu = newKayu,"""

content = content.replace(state_copy_target, new_state_copy)

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'w') as f:
    f.write(content)
