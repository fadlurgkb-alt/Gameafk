package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_state")
data class GameState(
    @PrimaryKey val id: Int = 1,
    val totalZenith: Double = 100.0,
    val kayu: Long = 50,
    val batu: Long = 50,
    val besi: Long = 0,
    val emas: Long = 0,
    val oil: Long = 0,
    val listrik: Long = 0,
    val titanium: Long = 0,
    val uranium: Long = 0,
    val daging: Long = 0,
    val berry: Long = 0,
    val gandum: Long = 0,
    val roti: Long = 0,
    val kalengan: Long = 0,
    val ikan: Long = 0,
    
    val maxStorage: Long = 100,
    val happiness: Long = 100,
    val maxPasukan: Long = 0,
    val currentPasukan: Long = 0,
    val selectedReligionsStr: String = "",
    val selectedNationality: String = "Belum Memilih",
    val gerbangHp: Long = 0,
    val maxGerbangHp: Long = 0,
    val damage: Long = 10,
    val gudangLevel: Int = 0,
    
    val unlockedNodesStr: String = "",
    val populasi: Long = 0,
    val maxPopulasi: Long = 0,
    val buildingLevelsStr: String = "",
    
    val lastSavedTime: Long = 0L,
    val inventoryStr: String = "",
    val autoFarmDungeon: Boolean = false,
    val maxDungeonLevel: Int = 1,
    
    val gachaTickets: Int = 0,
    val dungeonCoins: Long = 0L,
    
    val shardTokens: Int = 0,
    val hpPotions: Int = 0,
    
    val ownedCommandersStr: String = "",
    val pasukanDamageBonus: Long = 0L,
    val pasukanHpBonus: Long = 0L,
    

    // Raid & Coins
    val gameCoins: Long = 0L,
    val raidTimer: Int = 180,
    val raidLevel: Int = 1,
    val consecutiveLosses: Int = 0,
    val garrisonedHero: String = "",
    val garrisonedHeroesStr: String = "",
    val garrisonedPasukan: Long = 0L,
    val woundedPasukan: Long = 0L,
    val isRaidBattleActive: Boolean = false,
    val raidEnemyHp: Long = 0L,
    val raidEnemyMaxHp: Long = 0L,
    val raidPlayerHp: Long = 0L,
    
    // Dungeon Battle State
    val isDungeonActive: Boolean = false,
    val currentDungeonLevel: Int = 1,
    val monsterHp: Long = 0L,
    val monsterMaxHp: Long = 0L,
    val playerDungeonHp: Long = 0L,
    val kerjaPaksaActive: Boolean = false,
    val gamePhase: String = "prolog_api"
) {
    val unlockedNodes: List<String>
        get() = unlockedNodesStr.split(",").filter { it.isNotEmpty() }
        
    val buildingLevels: Map<String, Int>
        get() = buildingLevelsStr.split(",").filter { it.isNotEmpty() }.associate { 
            val parts = it.split(":")
            parts[0] to parts[1].toInt()
        }
        
    val inventory: Map<String, Int>
        get() = inventoryStr.split(",").filter { it.isNotEmpty() }.associate {
            val parts = it.split(":")
            parts[0] to parts[1].toInt()
        }
        
    val ownedCommanders: List<String>
        get() = ownedCommandersStr.split(",").filter { it.isNotEmpty() }
        
    val garrisonedHeroes: List<String>
        get() = garrisonedHeroesStr.split(",").filter { it.isNotEmpty() }
        
    val selectedReligions: Map<Int, String>
        get() = selectedReligionsStr.split(",").filter { it.isNotEmpty() }.associate {
            val parts = it.split(":")
            parts[0].toInt() to parts[1]
        }
        
    fun getCommanderStars(commanderName: String): Int {
        val entry = ownedCommanders.find { it.startsWith(commanderName) } ?: return 0
        val parts = entry.split(":")
        return if (parts.size > 1) parts[1].toIntOrNull() ?: 1 else 1
    }

    fun isUnlocked(nodeId: String): Boolean = unlockedNodes.contains(nodeId)
    
    fun getBuildingLevel(id: String): Int = buildingLevels[id] ?: if (isUnlocked(id)) 1 else 0
}
