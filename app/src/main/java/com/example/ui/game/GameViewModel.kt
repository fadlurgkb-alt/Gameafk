package com.example.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.GameRepository
import com.example.data.GameState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MonsterInfo(val name: String, val hp: Long, val atk: Long, val isBoss: Boolean)

fun getMonsterInfo(level: Int): MonsterInfo {
    val db = listOf(
        Pair(1, MonsterInfo("Serigala Liar", 360 * 3L, 24 * 2L, false)),
        Pair(5, MonsterInfo("Alpha Serigala", 2400 * 3L, 90 * 2L, true)),
        Pair(10, MonsterInfo("Raja Goblin", 13500 * 3L, 360 * 2L, true)),
        Pair(15, MonsterInfo("Panglima Orc", 54000 * 3L, 900 * 2L, true)),
        Pair(30, MonsterInfo("Raja Ogre", 255000 * 3L, 2400 * 2L, true)),
        Pair(50, MonsterInfo("Raja Iblis", 1050000 * 3L, 9000 * 2L, true)),
        Pair(100, MonsterInfo("Zenith Primordial", 15000000 * 3L, 130000 * 2L, true))
    )
    
    val exact = db.find { it.first == level }
    if (exact != null) return exact.second
    
    var lower = db[0]
    var upper = db.last()
    
    for (i in 0 until db.size - 1) {
        if (level > db[i].first && level < db[i+1].first) {
            lower = db[i]
            upper = db[i+1]
            break
        }
    }
    
    if (level > upper.first) {
        val extraLevels = level - upper.first
        return MonsterInfo("Void Entity", upper.second.hp + extraLevels * 100000 * 3L, upper.second.atk + extraLevels * 1000 * 2L, level % 5 == 0)
    }
    
    val ratio = (level - lower.first).toDouble() / (upper.first - lower.first)
    val interpHp = lower.second.hp + ((upper.second.hp - lower.second.hp) * ratio).toLong()
    val interpAtk = lower.second.atk + ((upper.second.atk - lower.second.atk) * ratio).toLong()
    val isBoss = level % 5 == 0
    
    return MonsterInfo("Prajurit Gelap", interpHp, interpAtk, isBoss)
}

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(GameState())
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()
    
    private val _dungeonLogs = MutableStateFlow<List<String>>(emptyList())
    val dungeonLogs: StateFlow<List<String>> = _dungeonLogs.asStateFlow()
    
    private val _raidLogs = MutableStateFlow<List<String>>(emptyList())
    val raidLogs: StateFlow<List<String>> = _raidLogs.asStateFlow()
    
    private val _afkResult = MutableStateFlow<String?>(null)
    val afkResult: StateFlow<String?> = _afkResult.asStateFlow()
    
    fun clearAfkResult() {
        _afkResult.value = null
    }

    private var isFirstLoad = true
    private var dungeonWaitTicks = 0
    private var saveTickCounter = 0

    init {
        viewModelScope.launch {
            repository.gameState.collect { state ->
                if (state != null) {
                    var updatedState = state
                    if (isFirstLoad) {
                        isFirstLoad = false
                        val currentTime = System.currentTimeMillis()
                        val lastTime = state.lastSavedTime
                        
                        if (lastTime > 0) {
                            val secondsOffline = (currentTime - lastTime) / 1000
                            if (secondsOffline > 60) {
                                updatedState = calculateOfflineGains(state, secondsOffline)
                                val zenithGained = (updatedState.totalZenith - state.totalZenith).toLong()
                                _afkResult.value = "✨ Selamat Datang Kembali! Anda mendapatkan hasil AFK selama ${secondsOffline / 60} menit:\n\uD83D\uDCB0 +$zenithGained Zenith dan material otomatis terisi ke gudang!"
                                repository.saveGameState(updatedState)
                            }
                        }
                    }
                    _uiState.value = updatedState
                } else {
                    if (isFirstLoad) {
                        isFirstLoad = false
                        repository.saveGameState(GameState(lastSavedTime = System.currentTimeMillis()))
                    }
                }
            }
        }

        // Game Loop
        viewModelScope.launch {
            while (true) {
                delay(1000)
                gameTick()
            }
        }
    }

    private fun calculateOfflineGains(state: GameState, secondsOffline: Long): GameState {
        val unlocked = state.unlockedNodes
        
        var currentMaxStorage = 100L
        if (unlocked.contains("gudang")) currentMaxStorage += 100L + (state.getBuildingLevel("gudang") * 500L)
        if (unlocked.contains("logistik_komputer")) currentMaxStorage += 50000L
        
        fun cap(value: Long): Long = if (value > currentMaxStorage) currentMaxStorage else value
        
        var currentHappiness = state.happiness
        var globalMultiplier = 1.0
        if (unlocked.contains("seni_memasak")) globalMultiplier *= 1.02
        if (unlocked.contains("brain_link")) globalMultiplier *= 5.0
        
        // Filsafat passives
        if (unlocked.contains("barter_purba")) globalMultiplier += 0.5
        if (unlocked.contains("kapitalisme")) globalMultiplier += 1.0
        if (unlocked.contains("transhumanisme")) globalMultiplier *= 5.0

        if (currentHappiness > 100L) {
            globalMultiplier += (currentHappiness - 100L) * 0.01
        }
        
        val resourceMult = if (unlocked.contains("overclock_tubuh")) 2.0 else 1.0
        
        var kayuBatuBesiMult = if (unlocked.contains("kerja_shift")) 1.3 else 1.0
        if (unlocked.contains("feodalisme")) kayuBatuBesiMult += 0.4
        
        val pertanianMult = if (unlocked.contains("domestikasi_hewan")) 1.25 else 1.0
        val makananMult = if (unlocked.contains("pengalengan_makanan")) 1.5 else 1.0
        
        var emasMult = 1.0
        if (unlocked.contains("merkantilisme")) emasMult += 1.0
        
        var oilListrikMult = 1.0
        if (unlocked.contains("techno_corporatism")) oilListrikMult += 1.0
        
        var baseZenith = 5.0
        if (unlocked.contains("market")) baseZenith += 2.0
        
        val offlineMult = 0.20
        
        val zenithGained = (baseZenith * globalMultiplier * secondsOffline * offlineMult)
        val newZenith = state.totalZenith + zenithGained
        
        var kayuGained = 0L
        var batuGained = 0L
        var besiGained = 0L
        var emasGained = 0L
        var oilGained = 0L
        var listrikGained = 0L
        var titaniumGained = 0L
        var uraniumGained = 0L
        
        if (unlocked.contains("penebang_kayu")) kayuGained += (state.getBuildingLevel("penebang_kayu") * kayuBatuBesiMult * resourceMult * secondsOffline * offlineMult).toLong()
        if (unlocked.contains("pemecah_batu")) batuGained += (state.getBuildingLevel("pemecah_batu") * kayuBatuBesiMult * resourceMult * secondsOffline * offlineMult).toLong()
        if (unlocked.contains("peleburan_besi")) besiGained += (state.getBuildingLevel("peleburan_besi") * 2 * kayuBatuBesiMult * resourceMult * secondsOffline * offlineMult).toLong()
        if (unlocked.contains("tambang_emas")) emasGained += (state.getBuildingLevel("tambang_emas") * 2 * emasMult * resourceMult * secondsOffline * offlineMult).toLong()
        if (unlocked.contains("pengeboran_minyak")) oilGained += (state.getBuildingLevel("pengeboran_minyak") * 5 * oilListrikMult * resourceMult * secondsOffline * offlineMult).toLong()
        if (unlocked.contains("pembangkit_listrik")) listrikGained += (state.getBuildingLevel("pembangkit_listrik") * 10 * oilListrikMult * resourceMult * secondsOffline * offlineMult).toLong()
        if (unlocked.contains("tambang_titanium")) titaniumGained += (state.getBuildingLevel("tambang_titanium") * 8 * resourceMult * secondsOffline * offlineMult).toLong()
        if (unlocked.contains("reaktor_uranium")) uraniumGained += (state.getBuildingLevel("reaktor_uranium") * 5 * resourceMult * secondsOffline * offlineMult).toLong()
        
        if (unlocked.contains("drone_swarm")) {
            oilGained += (5L * secondsOffline * offlineMult).toLong()
            titaniumGained += (5L * secondsOffline * offlineMult).toLong()
            uraniumGained += (5L * secondsOffline * offlineMult).toLong()
        }
        
        var dagingGained = 0L
        var berryGained = 0L
        var gandumGained = 0L
        var rotiGained = 0L
        var kalenganGained = 0L
        var ikanGained = 0L
        
        if (unlocked.contains("naluri_berburu")) dagingGained += (1 * makananMult * resourceMult * secondsOffline * offlineMult).toLong()
        if (unlocked.contains("peternakan")) dagingGained += (state.getBuildingLevel("peternakan") * 1 * makananMult * resourceMult * secondsOffline * offlineMult).toLong()
        if (unlocked.contains("budidaya")) berryGained += (2 * pertanianMult * makananMult * resourceMult * secondsOffline * offlineMult).toLong()
        if (unlocked.contains("perkebunan")) berryGained += (state.getBuildingLevel("perkebunan") * 2 * pertanianMult * makananMult * resourceMult * secondsOffline * offlineMult).toLong()
        if (unlocked.contains("pertanian") || unlocked.contains("pertanian_massal")) gandumGained += (state.getBuildingLevel("pertanian").coerceAtLeast(1) * 2 * pertanianMult * makananMult * resourceMult * secondsOffline * offlineMult).toLong()
        if (unlocked.contains("seni_memasak")) rotiGained += (1 * makananMult * resourceMult * secondsOffline * offlineMult).toLong()
        if (unlocked.contains("manufaktur_roti")) rotiGained += (5 * makananMult * resourceMult * secondsOffline * offlineMult).toLong()
        if (unlocked.contains("pengalengan_makanan")) kalenganGained += (5 * makananMult * resourceMult * secondsOffline * offlineMult).toLong()
        if (unlocked.contains("pelabuhan")) ikanGained += (state.getBuildingLevel("pelabuhan") * 1 * makananMult * resourceMult * secondsOffline * offlineMult).toLong()
        
        return state.copy(
            totalZenith = newZenith,
            kayu = cap(state.kayu + kayuGained),
            batu = cap(state.batu + batuGained),
            besi = cap(state.besi + besiGained),
            emas = cap(state.emas + emasGained),
            oil = cap(state.oil + oilGained),
            listrik = cap(state.listrik + listrikGained),
            titanium = cap(state.titanium + titaniumGained),
            uranium = cap(state.uranium + uraniumGained),
            daging = cap(state.daging + dagingGained),
            berry = cap(state.berry + berryGained),
            gandum = cap(state.gandum + gandumGained),
            roti = cap(state.roti + rotiGained),
            kalengan = cap(state.kalengan + kalenganGained),
            ikan = cap(state.ikan + ikanGained)
        )
    }


    private fun getHappinessAttackMultiplier(happiness: Long): Double {
        if (happiness > 100) {
            val extraHappiness = happiness - 100
            return 1.10 + (Math.floor(extraHappiness / 10.0) * 0.03)
        } else if (happiness < 100) {
            return 0.95
        }
        return 1.0
    }
    
    private fun gameTick() {

        _uiState.update { currentState ->
            val unlocked = currentState.unlockedNodes
            var newInventoryStr = currentState.inventoryStr
            val currentInv = currentState.inventory.toMutableMap()
            
            // Dynamic Global Stats
            var currentMaxStorage = 100L
            if (unlocked.contains("gudang")) currentMaxStorage += 100L + (currentState.getBuildingLevel("gudang") * 500L)
            
            var maxPasukan = 0L
            if (unlocked.contains("barak_militer")) maxPasukan += (currentState.getBuildingLevel("barak_militer") * 20L)
            
            var maxPopulasi = 0L
            if (unlocked.contains("rumah_warga")) maxPopulasi += (currentState.getBuildingLevel("rumah_warga") * 10L)
            
            var populasi = currentState.populasi
            if (populasi < maxPopulasi) {
                populasi += 1L
            }
            
            var gerbangHp = 0L
            if (unlocked.contains("tembok")) gerbangHp += 100L

            var baseMaxHappiness = 100L
            if (unlocked.contains("taman_kota")) baseMaxHappiness += 20L
            if (unlocked.contains("spiritualitas_suku")) baseMaxHappiness += 15L // +15% of 100

            val relHappinessCheck = if (currentState.selectedReligions.values.contains("Ordo Cahaya Murni")) 1.20 else if (currentState.selectedReligions.values.contains("Penyembah Setan")) 0.75 else 1.0
            var finalMaxHappiness = (baseMaxHappiness * relHappinessCheck).toLong()
            if (currentState.kerjaPaksaActive) {
                finalMaxHappiness = (finalMaxHappiness - 30L).coerceAtLeast(0L)
            }

            var currentHappiness = currentState.happiness
            if (currentHappiness < finalMaxHappiness) {
                currentHappiness += 1L
            } else if (currentHappiness > finalMaxHappiness) {
                currentHappiness = finalMaxHappiness
            }
            
            // Base Multipliers
            var globalMultiplier = 1.0
            if (unlocked.contains("seni_memasak")) globalMultiplier *= 1.02
            if (unlocked.contains("brain_link")) globalMultiplier *= 5.0
            
            if (currentHappiness > 100L) {
                globalMultiplier += (currentHappiness - 100L) * 0.01
            }
            

            val resourceMult = (if (unlocked.contains("overclock_tubuh")) 2.0 else 1.0) * (if (currentState.kerjaPaksaActive) 2.0 else 1.0)
            
            var kayuBatuBesiMult = if (unlocked.contains("kerja_shift")) 1.3 else 1.0
            if (unlocked.contains("feodalisme")) kayuBatuBesiMult += 0.4
            
            val pertanianMult = if (unlocked.contains("domestikasi_hewan")) 1.25 else 1.0
            val makananMult = if (unlocked.contains("pengalengan_makanan")) 1.5 else 1.0
            
            var emasMult = 1.0
            if (unlocked.contains("merkantilisme")) emasMult += 1.0
            
            var oilListrikMult = 1.0
            if (unlocked.contains("techno_corporatism")) oilListrikMult += 1.0

            
            var baseZenith = 5.0
            if (unlocked.contains("market")) baseZenith += 2.0 // Market bonus passive
            
            var newZenith = currentState.totalZenith + (baseZenith * globalMultiplier)
            
            if (unlocked.contains("logistik_komputer")) currentMaxStorage += 50000L
            
            fun cap(value: Long): Long = if (value > currentMaxStorage) currentMaxStorage else value
            
            var newKayu = currentState.kayu
            var newBatu = currentState.batu
            var newBesi = currentState.besi
            var newEmas = currentState.emas
            var newOil = currentState.oil
            var newListrik = currentState.listrik
            var newTitanium = currentState.titanium
            var newUranium = currentState.uranium
            

            // RELIGION & NATIONALITY EFFECTS
            val rel = currentState.selectedReligions.values.toList()
            val nat = currentState.selectedNationality
            
            var relKayu = 1.0; var relZenith = 1.0; var relBatu = 1.0; var relBarakSpeed = 1.0
            var relPangan = 1.0; var relHappiness = 1.0; var relBesiEmas = 1.0; var relPotion = 1.0
            var relAtk = 1.0; var relCostBangunan = 1.0; var relHpTembok = 1.0; var relOilUran = 1.0
            var relTitUran = 1.0; var relGudang = 1.0
            
            if (rel.contains("Agama Alam")) relKayu += 0.20
            if (rel.contains("Agama Langit")) relZenith += 0.20
            if (rel.contains("Kepercayaan Leluhur")) { relBatu += 0.30; relBarakSpeed -= 0.10 }
            if (rel.contains("Kultus Kesuburan")) { relPangan += 0.25; relZenith -= 0.05 }
            if (rel.contains("Ordo Cahaya Murni")) { relHappiness += 0.20; relBesiEmas -= 0.15 }
            if (rel.contains("Penyembah Setan")) { relZenith += 1.00; relHappiness -= 0.25 }
            if (rel.contains("Aliran Meditasi")) { relPotion += 0.30; relAtk -= 0.10 }
            if (rel.contains("Doktrin Militer Suci")) { relAtk += 0.25; relCostBangunan += 0.15 }
            if (rel.contains("Kosmisme Agung")) { relHpTembok += 0.40; relOilUran -= 0.20 }
            if (rel.contains("Sinkretisme Logam")) { relTitUran += 0.35; relGudang -= 0.10 }
            
            var natKayu = 1.0; var natAtk = 1.0; var natHpTembok = 1.0; var natEmas = 1.0
            var natCostBarak = 1.0; var natPangan = 1.0; var natTit = 1.0; var natUran = 1.0
            var natZenith = 1.0; var natBesi = 1.0; var natCostBangunan = 1.0
            
            when (nat) {
                "Nusantara" -> { natKayu += 0.50; natPangan += 0.20 }
                "Sparta" -> { natAtk += 0.50; natEmas -= 0.20 }
                "Romawi" -> { natHpTembok += 0.50; natCostBangunan -= 0.10 }
                "Mesir Kuno" -> { natZenith += 0.50; natPangan -= 0.10 }
                "Jepang Shogunate" -> { natAtk += 0.30; natBesi += 0.30 }
                "Nordik" -> { natKayu += 0.30; natEmas += 0.30 }
                "Britania" -> { natEmas += 0.50; natHpTembok += 0.20 }
                "Cyber-Neo" -> { natTit += 0.50; natUran += 0.50; natPangan -= 0.30 }
                "Atlantis" -> { natZenith += 1.00; natBesi -= 0.50 }
                "Mongol" -> { natAtk += 0.40; natCostBarak -= 0.30 }
            }
            
            baseMaxHappiness = finalMaxHappiness
            currentMaxStorage = (currentMaxStorage * relGudang).toLong()
            baseZenith *= relZenith * natZenith
            kayuBatuBesiMult *= relKayu * natKayu // Kayu spec
            val batuMult = kayuBatuBesiMult * relBatu
            val besiEmasMult = kayuBatuBesiMult * relBesiEmas
            val besiMult = besiEmasMult * natBesi
            emasMult *= relBesiEmas * natEmas
            val panganMult = makananMult * relPangan * natPangan
            oilListrikMult *= relOilUran
            val uranMult = relOilUran * relTitUran * natUran
            val titMult = relTitUran * natTit
            
            if (unlocked.contains("penebang_kayu")) newKayu += (currentState.getBuildingLevel("penebang_kayu") * kayuBatuBesiMult * resourceMult).toLong()
            if (unlocked.contains("pemecah_batu")) newBatu += (currentState.getBuildingLevel("pemecah_batu") * kayuBatuBesiMult * resourceMult).toLong()
            if (unlocked.contains("peleburan_besi")) newBesi += (currentState.getBuildingLevel("peleburan_besi") * 2 * kayuBatuBesiMult * resourceMult).toLong()
            if (unlocked.contains("tambang_emas")) newEmas += (currentState.getBuildingLevel("tambang_emas") * 2 * emasMult * resourceMult).toLong()
            if (unlocked.contains("pengeboran_minyak")) newOil += (currentState.getBuildingLevel("pengeboran_minyak") * 5 * oilListrikMult * resourceMult).toLong()
            if (unlocked.contains("pembangkit_listrik")) newListrik += (currentState.getBuildingLevel("pembangkit_listrik") * 10 * oilListrikMult * resourceMult).toLong()
            if (unlocked.contains("tambang_titanium")) newTitanium += (currentState.getBuildingLevel("tambang_titanium") * 8 * resourceMult).toLong()
            if (unlocked.contains("reaktor_uranium")) newUranium += (currentState.getBuildingLevel("reaktor_uranium") * 5 * resourceMult).toLong()
            
            if (unlocked.contains("drone_swarm")) {
                val dsMult = if (currentState.kerjaPaksaActive) 2L else 1L
                newOil += 5L * dsMult
                newTitanium += 5L * dsMult
                newUranium += 5L * dsMult
            }
            
            var newDaging = currentState.daging
            var newBerry = currentState.berry
            var newGandum = currentState.gandum
            var newRoti = currentState.roti
            var newKalengan = currentState.kalengan
            var newIkan = currentState.ikan
            
            if (unlocked.contains("naluri_berburu")) newDaging += (1 * makananMult * resourceMult).toLong()
            if (unlocked.contains("peternakan")) newDaging += (currentState.getBuildingLevel("peternakan") * 1 * makananMult * resourceMult).toLong()
            if (unlocked.contains("budidaya")) newBerry += (2 * pertanianMult * makananMult * resourceMult).toLong()
            if (unlocked.contains("perkebunan")) newBerry += (currentState.getBuildingLevel("perkebunan") * 2 * pertanianMult * makananMult * resourceMult).toLong()
            if (unlocked.contains("pertanian") || unlocked.contains("pertanian_massal")) newGandum += (currentState.getBuildingLevel("pertanian").coerceAtLeast(1) * 2 * pertanianMult * makananMult * resourceMult).toLong()
            if (unlocked.contains("seni_memasak")) newRoti += (1 * makananMult * resourceMult).toLong()
            if (unlocked.contains("manufaktur_roti")) newRoti += (5 * makananMult * resourceMult).toLong()
            if (unlocked.contains("pengalengan_makanan")) newKalengan += (5 * makananMult * resourceMult).toLong()
            if (unlocked.contains("pelabuhan")) newIkan += (currentState.getBuildingLevel("pelabuhan") * 1 * makananMult * resourceMult).toLong()
            
            // Food Consumption
            if (populasi > 0) {
                var foodNeededDouble = populasi / 5.0
                if (unlocked.contains("pengasapan_daging")) foodNeededDouble *= 0.90
                if (unlocked.contains("hukum_kodeks")) foodNeededDouble *= 0.90
                if (unlocked.contains("manufaktur_roti")) foodNeededDouble *= 0.70
                if (unlocked.contains("biokimia_sintetis")) foodNeededDouble *= 0.60
                
                var foodNeeded = foodNeededDouble.toLong()
                if (foodNeeded == 0L && Math.random() < foodNeededDouble) foodNeeded = 1L

                
                fun deductFood(amount: Long, source: Long): Pair<Long, Long> {
                    val deducted = minOf(amount, source)
                    return (amount - deducted) to (source - deducted)
                }
                
                var rem = foodNeeded
                val (r1, d1) = deductFood(rem, newRoti); rem = r1; newRoti = d1
                val (r2, d2) = deductFood(rem, newKalengan); rem = r2; newKalengan = d2
                val (r3, d3) = deductFood(rem, newDaging); rem = r3; newDaging = d3
                val (r4, d4) = deductFood(rem, newIkan); rem = r4; newIkan = d4
                val (r5, d5) = deductFood(rem, newGandum); rem = r5; newGandum = d5
                val (r6, d6) = deductFood(rem, newBerry); rem = r6; newBerry = d6
                
                if (rem > 0) {
                    currentHappiness -= 2L
                    if (currentHappiness < 0L) currentHappiness = 0L
                }
            }

            var damage = 10L
            if (unlocked.contains("pertahanan_primitif")) damage += 50L
            if (unlocked.contains("tongkat_kayu")) damage += 180L
            if (unlocked.contains("pedang_besi")) damage += 980L
            if (unlocked.contains("musket")) damage += 6000L
            if (unlocked.contains("assault_rifle")) damage += 12500L
            if (unlocked.contains("energy_katana")) damage += 76000L
            if (unlocked.contains("implan_sibernetik")) damage += 25000L
            
            damage += currentState.currentPasukan * 10L // Each soldier adds 10 damage
            if (unlocked.contains("teokrasi")) damage = (damage * 1.3).toLong()
            
            // Pasukan and Commander bonuses
            damage += currentState.pasukanDamageBonus
            
val potionAtkCount = (currentState.inventory["atk_potion"] ?: 0).toLong()
            val potionDefCount = (currentState.inventory["def_potion"] ?: 0).toLong()

            var cmdHpBonus = 0L
            var heroDamageTotal = 0L
            val happinessMod = getHappinessAttackMultiplier(currentHappiness)
            var garrisonDamage = 0L
            
val eqAtk = ((currentState.inventory["eq_wood_sword"] ?: 0) * 200L +
                        (currentState.inventory["eq_iron_sword"] ?: 0) * 700L +
                        (currentState.inventory["eq_gold_sword"] ?: 0) * 1200L +
                        (currentState.inventory["eq_pistol"] ?: 0) * 2500L +
                        (currentState.inventory["eq_ak47"] ?: 0) * 5000L +
                        (currentState.inventory["eq_carbine"] ?: 0) * 12000L +
                        (currentState.inventory["eq_laser"] ?: 0) * 25000L +
                        (currentState.inventory["eq_cyber"] ?: 0) * 50000L)

            currentState.ownedCommanders.forEach { cmd ->
                val parts = cmd.split(":")
                val heroName = parts[0]
                val stars = if (parts.size > 1) parts[1].toIntOrNull() ?: 1 else 1
                
                val weaponId = parts.getOrNull(2) ?: "none"
                val armorId = parts.getOrNull(3) ?: "none"
                
                var eqDmg = 0L
                var eqHp = 0L
                when (weaponId) {
                    "tongkat_kayu" -> eqDmg = 180L
                    "pedang_besi" -> eqDmg = 980L
                    "musket" -> eqDmg = 6000L
                    "assault_rifle" -> eqDmg = 12500L
                    "energy_katana" -> eqDmg = 76000L
                    "planet_breaker" -> eqDmg = 400000L
                }
                when (armorId) {
                    "kulit_hewan" -> eqHp = 200L
                    "armor_besi" -> eqHp = 1000L
                    "armor_kevlar" -> eqHp = 5000L
                    "exo_suit" -> eqHp = 25000L
                    "nano_armor" -> eqHp = 150000L
                    "cyber_body" -> eqHp = 1000000L
                }
                
                var heroBaseDamage = 0L
                var heroBaseHp = 0L
                if (heroName.contains("(Mythical)")) { heroBaseDamage = 50000L; heroBaseHp = 200000L }
                else if (heroName.contains("(Legend)") || heroName.contains("(SSR)")) { heroBaseDamage = 10000L; heroBaseHp = 40000L }
                else if (heroName.contains("(Very Rare)") || heroName.contains("(SR)")) { heroBaseDamage = 2500L; heroBaseHp = 10000L }
                else if (heroName.contains("(Rare)") || heroName.contains("(R)")) { heroBaseDamage = 500L; heroBaseHp = 2000L }
                else { heroBaseDamage = 50L; heroBaseHp = 200L }
                
                heroBaseDamage += eqDmg
                heroBaseHp += eqHp
                
                if (currentState.garrisonedHeroes.contains(heroName)) {
                    heroBaseDamage += eqAtk
                }
                
                val multiplier = 1.0 + (stars - 1) * 0.1
                val finalDmg = (heroBaseDamage * multiplier * (1.0 + potionAtkCount * 0.05)).toLong()
                val finalHp = (heroBaseHp * multiplier * (1.0 + potionDefCount * 0.05)).toLong()
                
                heroDamageTotal += finalDmg
                cmdHpBonus += finalHp
                
                if (currentState.garrisonedHeroes.contains(heroName)) {
                    garrisonDamage += (finalDmg * happinessMod).toLong()
                }
            }
            garrisonDamage += currentState.garrisonedPasukan * 10L

            damage += heroDamageTotal
            damage = (damage * happinessMod * relAtk * natAtk).toLong()

            var maxGerbangHp = 0L
            if (unlocked.contains("tembok")) maxGerbangHp += 100L
            if (unlocked.contains("kulit_hewan")) maxGerbangHp += 200L
            if (unlocked.contains("armor_besi")) maxGerbangHp += 1000L
            if (unlocked.contains("armor_kevlar")) maxGerbangHp += 5000L
            if (unlocked.contains("exo_suit")) maxGerbangHp += 25000L
            if (unlocked.contains("nano_armor")) maxGerbangHp += 100000L
            if (unlocked.contains("implan_sibernetik")) maxGerbangHp += 5000L
            if (unlocked.contains("nano_repair")) maxGerbangHp += 100000L
            
            maxGerbangHp += currentState.pasukanHpBonus
            maxGerbangHp += cmdHpBonus
            
            // Dungeon Logic
            var isDungeonActive = currentState.isDungeonActive
            var monsterHp = currentState.monsterHp
            var playerDungeonHp = currentState.playerDungeonHp
            var maxDungeonLevel = currentState.maxDungeonLevel
            var autoFarm = currentState.autoFarmDungeon
            var dungeonCoins = currentState.dungeonCoins
            var gachaTickets = currentState.gachaTickets
            var currentDungeonLevel = currentState.currentDungeonLevel
            var monsterMaxHp = currentState.monsterMaxHp
            
            if (isDungeonActive) {
                if (dungeonWaitTicks > 0) {
                    dungeonWaitTicks--
                    if (dungeonWaitTicks == 0 && autoFarm && monsterHp <= 0) {
                        val nextLevel = minOf(currentDungeonLevel + 1, maxDungeonLevel)
                        val nextMonster = getMonsterInfo(nextLevel)
                        currentDungeonLevel = nextLevel
                        monsterMaxHp = nextMonster.hp
                        monsterHp = nextMonster.hp
                        playerDungeonHp = maxGerbangHp
                        val nextLogs = _dungeonLogs.value.toMutableList()
                        nextLogs.add("🔄 Auto-farm lanjut ke lantai ${currentDungeonLevel}!")
                        _dungeonLogs.value = nextLogs
                        _dungeonLootResult.value = "" // Clear loot popup
                    }
                } else if (monsterHp > 0 && playerDungeonHp > 0) {
                    val monsterInfo = getMonsterInfo(currentDungeonLevel)
                    val monsterAttack = monsterInfo.atk
                    
                    monsterHp -= damage
                    playerDungeonHp -= monsterAttack
                    
                    val currentLogs = _dungeonLogs.value.toMutableList()
                    currentLogs.add("⚔️ Anda menyerang monster sebesar $damage damage! (Sisa HP: ${monsterHp.coerceAtLeast(0)})")
                    currentLogs.add("🛡️ Monster menyerang balik sebesar $monsterAttack damage! (Sisa HP Anda: ${playerDungeonHp.coerceAtLeast(0)})")
                    if (currentLogs.size > 15) {
                        currentLogs.removeAt(0)
                        currentLogs.removeAt(0)
                    }
                    _dungeonLogs.value = currentLogs
                    
                    if (monsterHp <= 0) {
                        currentLogs.add("🎉 MONSTER KALAH!")
                        _dungeonLogs.value = currentLogs
                        // Win
                        if (currentDungeonLevel >= maxDungeonLevel) {
                            maxDungeonLevel = currentDungeonLevel + 1
                        }
                        
                        if (currentDungeonLevel % 5 == 0) {
                            gachaTickets += 5
                            val baseZenithGained = (5.0 * globalMultiplier * 7200).toLong() * 5
                            newZenith += baseZenithGained
                            _dungeonLootResult.value = "👑 BOSS TERKALAHKAN!\n+5 Tiket Gacha\n+10 Jam AFK Zenith (+$baseZenithGained)"
                        } else {
                            val coinsGained = (10..50).random().toLong()
                            dungeonCoins += coinsGained
                            val (zenithLoot, msg, rarityStr) = generateLoot(currentDungeonLevel, globalMultiplier, resourceMult, coinsGained)
                            if (rarityStr.isNotEmpty()) {
                                currentInv[rarityStr] = (currentInv[rarityStr] ?: 0) + 1
                                newInventoryStr = currentInv.map { "${it.key}:${it.value}" }.joinToString(",")
                            }
                            newZenith += zenithLoot
                            _dungeonLootResult.value = msg
                        }
                        
                        if (autoFarm) {
                            if (currentInv.values.sum() >= 1000) {
                                isDungeonActive = false
                                autoFarm = false
                                val waitLogs = _dungeonLogs.value.toMutableList()
                                waitLogs.add("🛑 Inventori penuh! Auto-Farm berhenti.")
                                _dungeonLogs.value = waitLogs
                            } else {
                                dungeonWaitTicks = 2
                                val waitLogs = _dungeonLogs.value.toMutableList()
                                waitLogs.add("⏱️ Menunggu 1.5 detik...")
                                _dungeonLogs.value = waitLogs
                            }
                        } else {
                            isDungeonActive = false
                        }
                    } else if (playerDungeonHp <= 0) {
                        // Lose
                        val loseLogs = _dungeonLogs.value.toMutableList()
                        loseLogs.add("💀 ANDA TERKALAHKAN!")
                        _dungeonLogs.value = loseLogs
                        isDungeonActive = false
                        autoFarm = false
                    }
                }
            }


            var newRaidTimer = currentState.raidTimer
            var newGameCoins = currentState.gameCoins
            var newRaidLevel = currentState.raidLevel
            var newConsecutiveLosses = currentState.consecutiveLosses
            
            var isRaidBattleActive = currentState.isRaidBattleActive
            var raidEnemyHp = currentState.raidEnemyHp
            var raidEnemyMaxHp = currentState.raidEnemyMaxHp
            var raidPlayerHp = currentState.raidPlayerHp
            var newWoundedPasukan = currentState.woundedPasukan
            var newGarrisonedPasukan = currentState.garrisonedPasukan
            
            if (unlocked.contains("tembok")) {
                if (isRaidBattleActive) {
                    val enemyAtk = (newRaidLevel * 500L).coerceAtLeast(100L)
                    var playerCounterAtk = (garrisonDamage * 0.25).toLong().coerceAtLeast(10L)
                    
                    val currentInv = currentState.inventory.toMutableMap()
                    if (currentInv.containsKey("cannon_gate")) playerCounterAtk += 300L
                    if (currentInv.containsKey("crossbow_gate")) playerCounterAtk += 200L
                    
                    raidEnemyHp -= playerCounterAtk
                    raidPlayerHp -= enemyAtk
                    
                    val rLogs = _raidLogs.value.toMutableList()
                    rLogs.add("⚔️ Gerbang bertahan: Menyerang balik musuh sebesar -$playerCounterAtk DMG. (HP Musuh: $raidEnemyHp)")
                    rLogs.add("💥 Serangan musuh: Gerbang menerima kerusakan -$enemyAtk HP. (HP Gerbang: $raidPlayerHp)")
                    _raidLogs.value = rLogs
                    
                    if (raidEnemyHp <= 0) {
                        isRaidBattleActive = false
                        newConsecutiveLosses = 0
                        newGameCoins += 100L
                        val prevRaidLevel = newRaidLevel
                        newRaidLevel += 1
                        newRaidTimer = 180
                        _alertMessage.value = "🛡️ SERANGAN DITAHAN! Raid Lv.$prevRaidLevel berhasil digagalkan! Hadiah: +100 Game Coins!"
                        
                        val endLogs = _raidLogs.value.toMutableList()
                        endLogs.add("🎉 Kemenangan! Serangan Raid Lv.$prevRaidLevel berhasil digagalkan!")
                        _raidLogs.value = endLogs
                    } else if (raidPlayerHp <= 0) {
                        isRaidBattleActive = false
                        val shieldCount = currentInv["shield_1h"] ?: 0
                        if (shieldCount > 0) {
                            currentInv["shield_1h"] = shieldCount - 1
                            newInventoryStr = currentInv.map { "${it.key}:${it.value}" }.joinToString(",")
                            _alertMessage.value = "🚨 KOTA DISERANG! Raid Lv.$newRaidLevel gagal ditahan! Namun Shield 1 Jam Kota aktif! Penjarahan dibatalkan."
                        } else {
                            val totalGarrison = newGarrisonedPasukan
                            val newlyWounded = (totalGarrison * 0.7).toLong()
                            val newlyDead = (totalGarrison * 0.3).toLong()
                            
                            val hospitalLvl = currentState.getBuildingLevel("hospital")
                            val hospitalCap = getHospitalCapacity(hospitalLvl)
                            val allowedWounded = (hospitalCap - newWoundedPasukan).coerceAtLeast(0L)
                            val actualWounded = newlyWounded.coerceAtMost(allowedWounded)
                            val deadPasukan = newlyDead + (newlyWounded - actualWounded)
                            
                            newWoundedPasukan += actualWounded
                            newGarrisonedPasukan = 0L
                            
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
                            
                            _alertMessage.value = "🚨 KOTA DISERANG! Raid Lv.$newRaidLevel gagal ditahan! Musuh menjarah 20% sumber daya!\n$newlyWounded prajurit terluka ($deadPasukan tewas karena kapasitas Rumah Sakit penuh)."
                        }
                        newRaidTimer = 180
                    }
                } else {
                    if (newRaidTimer > 0) {
                        newRaidTimer -= 1
                    } else {
                        isRaidBattleActive = true
                        val enemyAtk = newRaidLevel * 5000L
                        raidEnemyMaxHp = enemyAtk
                        
                        val currentInv = currentState.inventory.toMutableMap()
                        val trapCount = currentInv["trap_city"] ?: 0
                        val initialLogs = mutableListOf<String>()
                        initialLogs.add("🚨 WARNING: Gelombang Musuh Raid Lv. $newRaidLevel menyerang gerbang kota!")
                        
                        if (trapCount > 0) {
                            raidEnemyHp = (enemyAtk * 0.9).toLong()
                            currentInv["trap_city"] = trapCount - 1
                            newInventoryStr = currentInv.map { "${it.key}:${it.value}" }.joinToString(",")
                            initialLogs.add("💥 Jebakan Kota meledak! Mengurangi HP awal bos raid sebesar -10%!")
                        } else {
                            raidEnemyHp = enemyAtk
                        }
                        
                        initialLogs.add("🛡️ Mengaktifkan sistem pertahanan gerbang...")
                        _raidLogs.value = initialLogs
                        raidPlayerHp = maxGerbangHp
                    }
                }
            }

            val newState = currentState.copy(
                totalZenith = newZenith,
                maxStorage = currentMaxStorage,
                happiness = currentHappiness,
                maxPasukan = maxPasukan,
                populasi = populasi,
                maxPopulasi = maxPopulasi,
                gerbangHp = gerbangHp,
                maxGerbangHp = maxGerbangHp,
                damage = damage,
                lastSavedTime = System.currentTimeMillis(),
                inventoryStr = newInventoryStr,
                isDungeonActive = isDungeonActive,
                monsterHp = monsterHp,
                monsterMaxHp = monsterMaxHp,
                currentDungeonLevel = currentDungeonLevel,
                playerDungeonHp = playerDungeonHp,
                maxDungeonLevel = maxDungeonLevel,
                autoFarmDungeon = autoFarm,
                raidTimer = newRaidTimer,
                gameCoins = newGameCoins,
                raidLevel = newRaidLevel,
                consecutiveLosses = newConsecutiveLosses,
                dungeonCoins = dungeonCoins,
                gachaTickets = gachaTickets,
                isRaidBattleActive = isRaidBattleActive,
                raidEnemyHp = raidEnemyHp,
                raidEnemyMaxHp = raidEnemyMaxHp,
                raidPlayerHp = raidPlayerHp,
                woundedPasukan = newWoundedPasukan,
                garrisonedPasukan = newGarrisonedPasukan,
                kayu = cap(newKayu), batu = cap(newBatu), besi = cap(newBesi), emas = cap(newEmas),
                oil = cap(newOil), listrik = cap(newListrik), titanium = cap(newTitanium), uranium = cap(newUranium),
                daging = cap(newDaging), berry = cap(newBerry), gandum = cap(newGandum), roti = cap(newRoti),
                kalengan = cap(newKalengan), ikan = cap(newIkan)
            )
            
            viewModelScope.launch {
                // Auto-save hanya setiap 20 detik untuk efisiensi baterai & storage
            }
            
            newState
        }

        // Throttled save: simpan ke DB hanya setiap 20 detik
        saveTickCounter++
        if (saveTickCounter >= 20) {
            saveTickCounter = 0
            val currentState = _uiState.value
            viewModelScope.launch { repository.saveGameState(currentState) }
        }
    }

    private val _alertMessage = MutableStateFlow<String?>(null)
    val alertMessage: StateFlow<String?> = _alertMessage.asStateFlow()
    private val _upgradingBuildings = MutableStateFlow<Map<String, Long>>(emptyMap())
    val upgradingBuildings: StateFlow<Map<String, Long>> = _upgradingBuildings.asStateFlow()


    fun activateMaxAllMode() {
        _uiState.update { currentState ->
            val allNodeIds = allNodes.map { it.id }.joinToString(",")
            val maxBuildings = allNodes.filter { it.type == "bangunan" }.associate {
                val maxLv = when (it.id) {
                    "temple" -> 5
                    "market" -> 4
                    else -> 10
                }
                it.id to maxLv
            }
            val bldStr = maxBuildings.entries.joinToString(",") { "${it.key}:${it.value}" }
            val allHeroes = listOf("Zeus (Mythical):6", "Athena (Mythical):6", "Odin (Mythical):6", "Arthur (Legend):6")
            val garrisonedHeroesList = listOf("Zeus (Mythical)", "Athena (Mythical)", "Odin (Mythical)", "Arthur (Legend)")
            
            currentState.copy(
                totalZenith = 999999.0,
                kayu = 999999L, batu = 999999L, besi = 999999L, emas = 999999L,
                oil = 999999L, listrik = 999999L, titanium = 999999L, uranium = 999999L,
                daging = 999999L, berry = 999999L, gandum = 999999L, roti = 999999L,
                kalengan = 999999L, ikan = 999999L,
                dungeonCoins = 999999L, gameCoins = 999999L, gachaTickets = 999999,
                maxStorage = 9999999L,
                unlockedNodesStr = allNodeIds,
                buildingLevelsStr = bldStr,
                ownedCommandersStr = allHeroes.joinToString(","),
                garrisonedHeroesStr = garrisonedHeroesList.joinToString(","),
                garrisonedPasukan = 250L,
                currentPasukan = 1000L,
                gamePhase = "main_game"
            )
        }
    }

    fun sandboxGodMode() {
        _uiState.update { currentState ->
            currentState.copy(
                totalZenith = currentState.totalZenith + 50000.0,
                kayu = currentState.kayu + 10000L,
                batu = currentState.batu + 10000L,
                besi = currentState.besi + 10000L,
                emas = currentState.emas + 10000L,
                oil = currentState.oil + 10000L,
                listrik = currentState.listrik + 10000L,
                titanium = currentState.titanium + 10000L,
                uranium = currentState.uranium + 10000L,
                daging = currentState.daging + 10000L,
                berry = currentState.berry + 10000L,
                gandum = currentState.gandum + 10000L,
                roti = currentState.roti + 10000L,
                kalengan = currentState.kalengan + 10000L,
                dungeonCoins = currentState.dungeonCoins + 5000L,
                gameCoins = currentState.gameCoins + 5000L,
                gachaTickets = currentState.gachaTickets + 50,
                maxStorage = currentState.maxStorage + 100000L // Also increase storage so they don't get capped immediately
            )
        }
    }

    fun clearAlertMessage() {
        _alertMessage.value = null
    }

    fun showAlert(message: String) {
        _alertMessage.value = message
    }

    fun toggleKerjaPaksa() {
        _uiState.update { currentState ->
            val newValue = !currentState.kerjaPaksaActive
            val newState = currentState.copy(kerjaPaksaActive = newValue)
            viewModelScope.launch {
                repository.saveGameState(newState)
            }
            newState
        }
    }

    fun advancePrologToAnimasi() {
        _uiState.update { currentState ->
            val newState = currentState.copy(gamePhase = "animasi_api")
            viewModelScope.launch {
                repository.saveGameState(newState)
            }
            newState
        }
    }

    fun startMainGame() {
        _uiState.update { currentState ->
            val newState = currentState.copy(gamePhase = "main_game")
            viewModelScope.launch {
                repository.saveGameState(newState)
            }
            newState
        }
    }

    fun buyNode(id: String) {
        if (_upgradingBuildings.value.containsKey(id)) {
            _alertMessage.value = "🚧 Bangunan sedang ditingkatkan!"
            return
        }
        
        var isBuildingUpgrade = false
        var targetLevel = 0
        var wasUnlocked = false

        _uiState.update { currentState ->
            val node = allNodes.find { it.id == id } ?: return@update currentState
            val isUnlocked = currentState.isUnlocked(id)
            if (isUnlocked && node.type != "bangunan") return@update currentState
            
            // Cek prasyarat (hanya untuk tipe non-bangunan, jika belum kebuka)
            if (!isUnlocked && node.reqs.isNotEmpty()) {
                val missingReqs = node.reqs.filter { !currentState.isUnlocked(it) }
                if (missingReqs.isNotEmpty()) {
                    _alertMessage.value = "❌ Gagal! Anda harus membuka prasyarat terlebih dahulu."
                    return@update currentState
                }
            }
            
            // Kunci Town Hall awal jika prasyarat cerita belum kelar
            if (node.id == "town_hall" && !isUnlocked) {
                if (!currentState.isUnlocked("penyelesaian_masalah")) {
                    _alertMessage.value = "❌ Gagal! Balai Kota membutuhkan riset 'Penyelesaian Masalah'."
                    return@update currentState
                }
            }

            val currentLevel = currentState.getBuildingLevel(id)
            
var currentChapter = 1
            if (currentState.isUnlocked("transhumanisme")) currentChapter = 11
            else if (currentState.isUnlocked("techno_corporatism")) currentChapter = 10
            else if (currentState.isUnlocked("sosialisme")) currentChapter = 9
            else if (currentState.isUnlocked("kapitalisme")) currentChapter = 8
            else if (currentState.isUnlocked("teokrasi")) currentChapter = 7
            else if (currentState.isUnlocked("merkantilisme")) currentChapter = 6
            else if (currentState.isUnlocked("feodalisme")) currentChapter = 5
            else if (currentState.isUnlocked("hukum_kodeks")) currentChapter = 4
            else if (currentState.isUnlocked("spiritualitas_suku")) currentChapter = 3
            else if (currentState.isUnlocked("barter_purba")) currentChapter = 2

            if (node.type == "bangunan" && id == "temple") {
                if (currentLevel >= 5) {
                    _alertMessage.value = "❌ Gagal! Kuil sudah mencapai level maksimal (Lv 5)."
                    return@update currentState
                }
                val thLevel = currentState.getBuildingLevel("town_hall")
                val reqTh = (currentLevel + 1) * 2 - 1
                if (thLevel < reqTh) {
                    _alertMessage.value = "❌ Gagal! Kuil Lv ${currentLevel + 1} membutuhkan Town Hall Lv $reqTh."
                    return@update currentState
                }
            }

            if (node.type == "bangunan" && id == "market") {
                if (currentLevel >= 4) {
                    _alertMessage.value = "❌ Gagal! Market sudah mencapai level maksimal (Lv 4)."
                    return@update currentState
                }
                if (currentLevel == 1 && currentChapter < 3) {
                    _alertMessage.value = "❌ Gagal! Market Lv 2 membutuhkan pencapaian Chapter 3 (Filsafat Level 2+)."
                    return@update currentState
                }
                if (currentLevel == 2 && currentChapter < 5) {
                    _alertMessage.value = "❌ Gagal! Market Lv 3 membutuhkan pencapaian Chapter 5 (Filsafat Level 4+)."
                    return@update currentState
                }
                if (currentLevel == 3 && currentChapter < 8) {
                    _alertMessage.value = "❌ Gagal! Market Lv 4 membutuhkan pencapaian Chapter 8 (Filsafat Level 7+)."
                    return@update currentState
                }
            }

            if (node.type == "bangunan" && id != "town_hall") {
                val townHallLvl = currentState.getBuildingLevel("town_hall")
                if (currentLevel >= townHallLvl) {
                    _alertMessage.value = "❌ Gagal! 🔒 Town Hall Level ${currentLevel + 1} Diperlukan!"
                    return@update currentState
                }
            }
            
var costMultiplier = if (isUnlocked) Math.pow(1.5, currentLevel.toDouble()) else 1.0
            
            if (node.type == "bangunan" && isUnlocked) {
                val targetLevel = currentLevel + 1
                if (targetLevel == 2) {
                    costMultiplier *= 1.4
                } else if (targetLevel >= 3) {
                    costMultiplier *= 1.8
                }
            }
            
            val actualZenithCost = node.zenithCost * costMultiplier
            if (currentState.totalZenith < actualZenithCost) return@update currentState
            
            var canAfford = true
            val actualResCosts = node.resourceCosts.mapValues {
                var cost = (it.value * costMultiplier).toLong()
                if (currentState.isUnlocked("metalurgi_militer") && node.type == "senjata/armor" && (it.key == "besi" || it.key == "emas")) {
                    cost = (cost * 0.85).toLong()
                }
                if (node.tier >= 3 && (node.type == "bangunan" || node.type == "filsafat")) {
                    cost = (cost * 1.8).toLong()
                }
                cost
            }
            
            val currentInv = currentState.inventory.toMutableMap()
            
            for ((res, amount) in actualResCosts) {
                val currentAmount = when(res) {
                    "kayu" -> currentState.kayu
                    "batu" -> currentState.batu
                    "besi" -> currentState.besi
                    "emas" -> currentState.emas
                    "oil" -> currentState.oil
                    "listrik" -> currentState.listrik
                    "titanium" -> currentState.titanium
                    "uranium" -> currentState.uranium
                    "item_rare" -> (currentInv["item_rare"] ?: 0).toLong()
                    "item_sr" -> (currentInv["item_sr"] ?: 0).toLong()
                    "item_ssr" -> (currentInv["item_ssr"] ?: 0).toLong()
                    else -> 0L
                }
                if (currentAmount < amount) {
                    canAfford = false
                    break
                }
            }
            
            if (canAfford) {
                var newState = currentState.copy(totalZenith = currentState.totalZenith - actualZenithCost)
                for ((res, amount) in actualResCosts) {
                    newState = when(res) {
                        "kayu" -> newState.copy(kayu = newState.kayu - amount)
                        "batu" -> newState.copy(batu = newState.batu - amount)
                        "besi" -> newState.copy(besi = newState.besi - amount)
                        "emas" -> newState.copy(emas = newState.emas - amount)
                        "oil" -> newState.copy(oil = newState.oil - amount)
                        "listrik" -> newState.copy(listrik = newState.listrik - amount)
                        "titanium" -> newState.copy(titanium = newState.titanium - amount)
                        "uranium" -> newState.copy(uranium = newState.uranium - amount)
                        "item_rare" -> {
                            currentInv["item_rare"] = (currentInv["item_rare"] ?: 0) - amount.toInt()
                            newState
                        }
                        "item_sr" -> {
                            currentInv["item_sr"] = (currentInv["item_sr"] ?: 0) - amount.toInt()
                            newState
                        }
                        "item_ssr" -> {
                            currentInv["item_ssr"] = (currentInv["item_ssr"] ?: 0) - amount.toInt()
                            newState
                        }
                        else -> newState
                    }
                }
                
                newState = newState.copy(inventoryStr = currentInv.map { "${it.key}:${it.value}" }.joinToString(","))
                
                if (node.type == "bangunan") {
                    val currentLevels = currentState.buildingLevels.toMutableMap()
                    currentLevels[id] = currentLevel + 1
                    
                    if (!isUnlocked) {
                        newState = newState.copy(
                            unlockedNodesStr = (currentState.unlockedNodes + id).joinToString(","),
                            buildingLevelsStr = currentLevels.map { "${it.key}:${it.value}" }.joinToString(",")
                        )
                    } else {
                        newState = newState.copy(
                            buildingLevelsStr = currentLevels.map { "${it.key}:${it.value}" }.joinToString(",")
                        )
                    }
                } else {
                    newState = newState.copy(
                        unlockedNodesStr = (currentState.unlockedNodes + id).joinToString(",")
                    )
                }
                
                viewModelScope.launch {
                    repository.saveGameState(newState)
                }
                newState
            } else {
                currentState
            }
        }
        
        if (isBuildingUpgrade) {
            val endTime = System.currentTimeMillis() + 3000L
            _upgradingBuildings.value = _upgradingBuildings.value + (id to endTime)
            
            viewModelScope.launch {
                kotlinx.coroutines.delay(3000L)
                _upgradingBuildings.value = _upgradingBuildings.value - id
                
                _uiState.update { state ->
                    val currentLevels = state.buildingLevels.toMutableMap()
                    currentLevels[id] = targetLevel
                    var newState = state
                    if (!wasUnlocked) {
                        newState = newState.copy(
                            unlockedNodesStr = (state.unlockedNodes + id).joinToString(","),
                            buildingLevelsStr = currentLevels.map { "${it.key}:${it.value}" }.joinToString(",")
                        )
                    } else {
                        newState = newState.copy(
                            buildingLevelsStr = currentLevels.map { "${it.key}:${it.value}" }.joinToString(",")
                        )
                    }
                    viewModelScope.launch {
                        repository.saveGameState(newState)
                    }
                    newState
                }
            }
        }
    }

    fun upgradeGudang() {
        _uiState.update { state ->
            if (state.kayu >= 200 && state.batu >= 200) {
                val newState = state.copy(
                    kayu = state.kayu - 200,
                    batu = state.batu - 200,
                    gudangLevel = state.gudangLevel + 1
                )
                viewModelScope.launch { repository.saveGameState(newState) }
                newState
            } else {
                state
            }
        }
    }

    fun latihPasukan() {
        _uiState.update { state ->
            if (state.currentPasukan < state.maxPasukan) {
                val barakLevel = state.getBuildingLevel("barak_militer")
                val thLevel = state.getBuildingLevel("town_hall")
                var costDaging = 25L
                var costKayu = 15L
                var costBesi = 0L
                var costUranium = 0L
                var costListrik = 0L
                
                if (barakLevel >= 10 || thLevel >= 10) {
                    costDaging = 500L
                    costKayu = 0L
                    costBesi = 0L
                    costUranium = 100L
                    costListrik = 50L
                } else if (barakLevel >= 5 || thLevel >= 5) {
                    costDaging = 150L
                    costKayu = 0L
                    costBesi = 50L
                } else if (barakLevel >= 3 || thLevel >= 3) {
                    costDaging = 60L
                    costKayu = 20L
                    costBesi = 15L
                } else {
                    // Prehistoric Era: Pasukan Kapak Batu & Pasukan Tombak
                    costDaging = 25L
                    costKayu = 15L
                    costBesi = 0L
                }
                
                var natCostBarak = 1.0
                if (state.selectedNationality == "Mongol") natCostBarak -= 0.30
                
                costDaging = (costDaging * natCostBarak).toLong()
                costKayu = (costKayu * natCostBarak).toLong()
                costBesi = (costBesi * natCostBarak).toLong()
                costUranium = (costUranium * natCostBarak).toLong()
                costListrik = (costListrik * natCostBarak).toLong()

                if (state.daging >= costDaging && state.kayu >= costKayu && state.besi >= costBesi && state.uranium >= costUranium && state.listrik >= costListrik) {
                    val newState = state.copy(
                        daging = state.daging - costDaging,
                        kayu = state.kayu - costKayu,
                        besi = state.besi - costBesi,
                        uranium = state.uranium - costUranium,
                        listrik = state.listrik - costListrik,
                        currentPasukan = state.currentPasukan + 1
                    )
                    viewModelScope.launch { repository.saveGameState(newState) }
                    newState
                } else {
                    state
                }
            } else {
                state
            }
        }
    }
    
    fun upgradeGenMiliter() {
        _uiState.update { state ->
            if (state.dungeonCoins >= 100L) {
                val newState = state.copy(
                    dungeonCoins = state.dungeonCoins - 100L,
                    pasukanDamageBonus = state.pasukanDamageBonus + 100L,
                    pasukanHpBonus = state.pasukanHpBonus + 100L
                )
                viewModelScope.launch { repository.saveGameState(newState) }
                newState
            } else {
                state
            }
        }
    }

    fun pilihAgama(level: Int, agama: String) {
        _uiState.update { state ->
            val currentReligions = state.selectedReligions.toMutableMap()
            currentReligions[level] = agama
            val newReligionsStr = currentReligions.entries.joinToString(",") { "${it.key}:${it.value}" }
            
            val newState = state.copy(selectedReligionsStr = newReligionsStr)
            viewModelScope.launch { repository.saveGameState(newState) }
            newState
        }
    }
    
    fun pilihNationality(nation: String) {
        _uiState.update { state ->
            val newState = state.copy(selectedNationality = nation)
            viewModelScope.launch { repository.saveGameState(newState) }
            newState
        }
    }

    private val _gachaResult = MutableStateFlow<String?>(null)
    val gachaResult: StateFlow<String?> = _gachaResult.asStateFlow()
    
    private val _gachaResultList = MutableStateFlow<List<GachaRollResult>?>(null)
    val gachaResultList: StateFlow<List<GachaRollResult>?> = _gachaResultList.asStateFlow()
    

    fun setGarrisonHero(heroName: String) {
        _uiState.update { state ->
            val newState = state.copy(garrisonedHero = heroName)
            viewModelScope.launch { repository.saveGameState(newState) }
            newState
        }
    }
    
    fun clearGachaResult() {
        _gachaResult.value = null
    }

    fun clearGachaResultList() {
        _gachaResultList.value = null
    }

    fun gachaPahlawan(pullCount: Int = 1) {
        _uiState.update { state ->
            val cost = if (pullCount == 10) 9 else 1
            if (state.gachaTickets >= cost) {
                val currentCommanders = state.ownedCommandersStr.split(",").filter { it.isNotEmpty() }.toMutableList()
                val results = mutableListOf<GachaRollResult>()
                var totalShardsAdded = 0
                
                repeat(pullCount) {
                    val rand = Math.random() * 100
                    val heroName = when {
                        rand <= 1.5 -> listOf("Zeus (Mythical)", "Athena (Mythical)", "Odin (Mythical)").random()
                        rand <= 10.0 -> listOf("Arthur (SSR)", "Joan of Arc (SSR)").random()
                        rand <= 40.0 -> listOf("Alexander (SR)", "Gajah Mada (SR)").random()
                        else -> "Prajurit Biasa (R)"
                    }
                    
                    val existingIdx = currentCommanders.indexOfFirst { it.startsWith(heroName) }
                    var isNew = false
                    var stars = 1
                    var shardTokensToAdd = 0
                    
                    if (existingIdx != -1) {
                        val existing = currentCommanders[existingIdx]
                        val parts = existing.split(":")
                        val currentStars = if (parts.size > 1) parts[1].toIntOrNull() ?: 1 else 1
                        
                        if (currentStars < 6) {
                            stars = currentStars + 1
                            // Preserve weapon and armor if equipped
                            val currentWeapon = parts.getOrNull(2) ?: "none"
                            val currentArmor = parts.getOrNull(3) ?: "none"
                            currentCommanders[existingIdx] = "$heroName:$stars:$currentWeapon:$currentArmor"
                        } else {
                            stars = 6
                            shardTokensToAdd = when {
                                heroName.contains("(Mythical)") -> 50
                                heroName.contains("(SSR)") -> 20
                                heroName.contains("(SR)") -> 10
                                else -> 1
                            }
                            totalShardsAdded += shardTokensToAdd
                        }
                    } else {
                        isNew = true
                        currentCommanders.add("$heroName:1")
                    }
                    
                    results.add(GachaRollResult(
                        heroName = heroName,
                        isNew = isNew,
                        stars = stars,
                        convertedShards = shardTokensToAdd
                    ))
                }
                
                val newState = state.copy(
                    gachaTickets = state.gachaTickets - cost,
                    shardTokens = state.shardTokens + totalShardsAdded,
                    ownedCommandersStr = currentCommanders.joinToString(",")
                )
                
                _gachaResultList.value = results
                viewModelScope.launch { repository.saveGameState(newState) }
                newState
            } else {
                state
            }
        }
    }
    
    fun exchangeCoinsForMaterials() {
        _uiState.update { state ->
            if (state.dungeonCoins >= 200L) {
                val maxStore = state.maxStorage
                fun cap(value: Long) = if (value > maxStore) maxStore else value
                
                val newState = state.copy(
                    dungeonCoins = state.dungeonCoins - 200L,
                    kayu = cap(state.kayu + 1000L),
                    batu = cap(state.batu + 1000L),
                    besi = cap(state.besi + 1000L),
                    emas = cap(state.emas + 1000L)
                )
                
                viewModelScope.launch { repository.saveGameState(newState) }
                newState
            } else {
                state
            }
        }
    }

    fun buyHpPotion() {
        _uiState.update { state ->
            if (state.dungeonCoins >= 50L) {
                val newState = state.copy(
                    dungeonCoins = state.dungeonCoins - 50L,
                    hpPotions = state.hpPotions + 1
                )
                viewModelScope.launch { repository.saveGameState(newState) }
                newState
            } else {
                state
            }
        }
    }

    fun exchangeShardForHero() {
        _uiState.update { state ->
            if (state.shardTokens >= 50) {
                val heroName = "Gajah Mada (Very Rare)"
                
                val currentCommanders = state.ownedCommandersStr.split(",").filter { it.isNotEmpty() }.toMutableList()
                val existingIdx = currentCommanders.indexOfFirst { it.startsWith(heroName) }
                
                var shardTokensToAdd = 0
                if (existingIdx != -1) {
                    val existing = currentCommanders[existingIdx]
                    val parts = existing.split(":")
                    val stars = if (parts.size > 1) parts[1].toIntOrNull() ?: 1 else 1
                    
                    if (stars < 6) {
                        currentCommanders[existingIdx] = "$heroName:${stars + 1}"
                        _gachaResult.value = "Ascension! $heroName naik ke Bintang ${stars + 1}"
                    } else {
                        shardTokensToAdd = when {
                            heroName.contains("(Mythical)") || heroName.contains("(Legend)") -> 20
                            heroName.contains("(Very Rare)") -> 10
                            heroName.contains("(Rare)") -> 1
                            else -> 1
                        }
                        _gachaResult.value = "$heroName (Maksimal B6) -> Dikonversi menjadi $shardTokensToAdd Shard Token!"
                    }
                } else {
                    currentCommanders.add("$heroName:1")
                    _gachaResult.value = "$heroName (Baru!)"
                }
                
                val newState = state.copy(
                    shardTokens = state.shardTokens - 50 + shardTokensToAdd,
                    ownedCommandersStr = currentCommanders.joinToString(",")
                )
                
                viewModelScope.launch { repository.saveGameState(newState) }
                newState
            } else {
                state
            }
        }
    }

    private val _dungeonLootResult = MutableStateFlow<String?>(null)
    val dungeonLootResult: StateFlow<String?> = _dungeonLootResult.asStateFlow()
    
    
    fun jualItemDungeon(tier: String) {
        _uiState.update { state ->
            val inv = state.inventory.toMutableMap()
            val count = inv[tier] ?: 0
            if (count > 0) {
                inv[tier] = count - 1
                val coinsToAdd = when (tier) {
                    "item_rare" -> 10L
                    "item_sr" -> 50L
                    "item_ssr" -> 150L
                    else -> 0L
                }
                state.copy(
                    inventoryStr = inv.map { "${it.key}:${it.value}" }.joinToString(","),
                    dungeonCoins = state.dungeonCoins + coinsToAdd
                )
            } else {
                state
            }
        }
    }


fun buyCoinShopItem(itemId: String, costCoins: Long) {
        _uiState.update { state ->
            if (state.gameCoins < costCoins) return@update state
            var newState = state.copy(gameCoins = state.gameCoins - costCoins)
            val inv = newState.inventory.toMutableMap()
            inv[itemId] = (inv[itemId] ?: 0) + 1
            newState = newState.copy(inventoryStr = inv.map { "${it.key}:${it.value}" }.joinToString(","))
            viewModelScope.launch { repository.saveGameState(newState) }
            newState
        }
    }
    fun buyMarketItem(itemId: String, costCoins: Long) {
        _uiState.update { state ->
            if (state.dungeonCoins < costCoins) return@update state
            var newState = state.copy(dungeonCoins = state.dungeonCoins - costCoins)
            when (itemId) {
                "hp_potion" -> newState = newState.copy(hpPotions = newState.hpPotions + 1)
                "atk_potion" -> {
                    val inv = newState.inventory.toMutableMap()
                    inv["atk_potion"] = (inv["atk_potion"] ?: 0) + 1
                    newState = newState.copy(inventoryStr = inv.map { "${it.key}:${it.value}" }.joinToString(","))
                }
                "def_potion" -> {
                    val inv = newState.inventory.toMutableMap()
                    inv["def_potion"] = (inv["def_potion"] ?: 0) + 1
                    newState = newState.copy(inventoryStr = inv.map { "${it.key}:${it.value}" }.joinToString(","))
                }
                "kayu_100" -> newState = newState.copy(kayu = newState.kayu + 100)
                "batu_100" -> newState = newState.copy(batu = newState.batu + 100)
                "daging_100" -> newState = newState.copy(daging = newState.daging + 100)
                
                "kayu_250" -> newState = newState.copy(kayu = newState.kayu + 250)
                "batu_250" -> newState = newState.copy(batu = newState.batu + 250)
                "daging_150" -> newState = newState.copy(daging = newState.daging + 150)
                "besi_100" -> newState = newState.copy(besi = newState.besi + 100)
                "emas_75" -> newState = newState.copy(emas = newState.emas + 75)
                
                "kayu_500" -> newState = newState.copy(kayu = newState.kayu + 500)
                "batu_500" -> newState = newState.copy(batu = newState.batu + 500)
                "daging_300" -> newState = newState.copy(daging = newState.daging + 300)
                
                "shard_sr" -> {
                    val inv = newState.inventory.toMutableMap()
                    inv["shard_sr"] = (inv["shard_sr"] ?: 0) + 1
                    newState = newState.copy(inventoryStr = inv.map { "${it.key}:${it.value}" }.joinToString(","))
                }
                "shard_ssr" -> {
                    val inv = newState.inventory.toMutableMap()
                    inv["shard_ssr"] = (inv["shard_ssr"] ?: 0) + 1
                    newState = newState.copy(inventoryStr = inv.map { "${it.key}:${it.value}" }.joinToString(","))
                }
            }
            newState
        }
    }


    fun combineShards(tier: String) {
        _uiState.update { state ->
            val inv = state.inventory.toMutableMap()
            val (shardKey, reqCount, resultPool) = when(tier) {
                "r" -> Triple("shard_r", 20, listOf("Arthur (Rare)", "Leonidas (Rare)", "Robin (Rare)", "Spartacus (Rare)"))
                "sr" -> Triple("shard_sr", 50, listOf("Joan of Arc (Very Rare)", "Miyamoto (Very Rare)", "Merlin (Very Rare)"))
                "ssr" -> Triple("shard_ssr", 100, listOf("Gilgamesh (Legend)", "Odin (Legend)", "Amaterasu (Legend)"))
                "ur" -> Triple("shard_ur", 150, listOf("Zeus (Mythical)", "Ra (Mythical)", "Sun Wukong (Mythical)"))
                else -> return@update state
            }
            
            val count = inv[shardKey] ?: 0
            if (count < reqCount) {
                _alertMessage.value = "❌ Gagal! Anda membutuhkan $reqCount Shard (Saat ini: $count)."
                return@update state
            }
            
            inv[shardKey] = count - reqCount
            
            val newHero = resultPool.random()
            val cmds = state.ownedCommanders.toMutableList()
            val idx = cmds.indexOfFirst { it.startsWith(newHero) }
            if (idx != -1) {
                val p = cmds[idx].split(":")
                val currentStars = if (p.size > 1) p[1].toIntOrNull() ?: 1 else 1
                cmds[idx] = "$newHero:${currentStars + 1}"
                _alertMessage.value = "🎉 Combine Berhasil! Duplikat $newHero didapat. Bintang naik jadi ${currentStars + 1}!"
            } else {
                cmds.add("$newHero:1")
                _alertMessage.value = "🎉 Combine Berhasil! Anda mendapatkan Hero Baru: $newHero!"
            }
            
            state.copy(
                inventoryStr = inv.map { "${it.key}:${it.value}" }.joinToString(","),
                ownedCommandersStr = cmds.joinToString(",")
            )
        }
    }

    fun exchangeShardToken(target: String, costTokens: Int) {
        _uiState.update { state ->
            if (state.shardTokens < costTokens) return@update state
            
            val inv = state.inventory.toMutableMap()
            when (target) {
                "shard_r_random" -> inv["shard_r"] = (inv["shard_r"] ?: 0) + 1
                "shard_sr_random" -> inv["shard_sr"] = (inv["shard_sr"] ?: 0) + 1
                "shard_ssr_random" -> inv["shard_ssr"] = (inv["shard_ssr"] ?: 0) + 1
                "shard_ur_random" -> inv["shard_ur"] = (inv["shard_ur"] ?: 0) + 1
            }
            
            state.copy(
                shardTokens = state.shardTokens - costTokens,
                inventoryStr = inv.map { "${it.key}:${it.value}" }.joinToString(",")
            )
        }
    }

    fun clearDungeonLootResult() {
        _dungeonLootResult.value = null
    }

    private fun generateLoot(level: Int, globalMultiplier: Double, resourceMult: Double, coins: Long): Triple<Long, String, String> {
        val rand = Math.random()
        var message = ""
        var rarityStr = ""
        var zenithGained = 0L
        
        if (rand < 0.02) {
            // SSR Tier (2%)
            rarityStr = "item_ssr"
            zenithGained = (5.0 * globalMultiplier * 2 * 3600).toLong() // 2 hours of base Zenith
            message = "\uD83C\uDF1F SSR LOOT! Tiket Gacha & +2 Jam Zenith (+$zenithGained)\nMaterial Eksotis: Cosmic Crystal, Dark Matter Shard\n💰 +$coins Koin Dungeon"
        } else if (rand < 0.17) {
            // SR Tier (15%)
            rarityStr = "item_sr"
            zenithGained = (5.0 * globalMultiplier * 1 * 3600).toLong() // 1 hours
            message = "✨ SR LOOT! +1 Jam Zenith (+$zenithGained)\nItem Langka: Kulit Monster, Kristal Alam\n💰 +$coins Koin Dungeon"
        } else {
            // Rare Tier (83%)
            rarityStr = "item_rare"
            zenithGained = (5.0 * globalMultiplier * 1800).toLong() // 30 minutes
            message = "\uD83D\uDD39 Rare LOOT! +30 Menit Zenith (+$zenithGained)\nMaterial Purba: Tulang, Besi Karatan\n💰 +$coins Koin Dungeon"
        }
        
        
        return Triple(zenithGained, message, rarityStr)
    }
    
    fun startDungeonBattle(level: Int, autoFarm: Boolean = false) {
        _dungeonLogs.value = listOf("Mulai pertempuran Lantai $level...")
        val monster = getMonsterInfo(level)
        _uiState.update { state ->
            state.copy(
                isDungeonActive = true,
                currentDungeonLevel = level,
                monsterMaxHp = monster.hp,
                monsterHp = monster.hp,
                playerDungeonHp = state.maxGerbangHp,
                autoFarmDungeon = autoFarm
            )
        }
    }
    
    fun stopDungeonBattle() {
        _uiState.update { state ->
            state.copy(isDungeonActive = false, autoFarmDungeon = false)
        }
    }
    
    fun toggleAutoFarm(enabled: Boolean) {
        _uiState.update { state ->
            state.copy(autoFarmDungeon = enabled)
        }
    }
    
    fun useHpPotion() {
        _uiState.update { state ->
            if (state.hpPotions > 0 && state.isDungeonActive) {
                // Restore 10% HP based on maxGerbangHp
                var relPotion = 1.0
                if (state.selectedReligions.values.contains("Aliran Meditasi")) relPotion += 0.30
                val healAmount = (state.maxGerbangHp * 0.1 * relPotion).toLong()
                val newHp = (state.playerDungeonHp + healAmount).coerceAtMost(state.maxGerbangHp)
                
                val currentLogs = _dungeonLogs.value.toMutableList()
                currentLogs.add("🧪 Menggunakan Potion! Memulihkan $healAmount HP.")
                _dungeonLogs.value = currentLogs
                
                val newState = state.copy(
                    hpPotions = state.hpPotions - 1,
                    playerDungeonHp = newHp
                )
                viewModelScope.launch { repository.saveGameState(newState) }
                newState
            } else {
                state
            }
        }
    }

    class Factory(private val repository: GameRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
                return GameViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    fun getGateCapacity(tembokLevel: Int): Pair<Int, Long> {
        return when (tembokLevel) {
            0 -> 0 to 0L
            1 -> 1 to 10L
            2 -> 1 to 15L
            3 -> 1 to 25L
            4 -> 1 to 40L
            5 -> 2 to 60L
            6 -> 2 to 90L
            7 -> 2 to 110L
            8 -> 3 to 150L
            9 -> 3 to 200L
            else -> 4 to 250L
        }
    }

    fun getHospitalCapacity(level: Int): Long {
        return when (level) {
            0 -> 0L
            1 -> 20L
            2 -> 30L
            3 -> 50L
            4 -> 70L
            5 -> 100L
            6 -> 120L
            7 -> 150L
            8 -> 170L
            9 -> 200L
            else -> 250L
        }
    }

    fun getHospitalBerryCost(level: Int): Long {
        return when (level) {
            1, 2 -> 200L
            3, 4 -> 190L
            5, 6, 7 -> 180L
            8, 9 -> 170L
            else -> 160L
        }
    }

    fun toggleGarrisonHero(heroName: String) {
        _uiState.update { state ->
            val tembokLevel = state.getBuildingLevel("tembok")
            val maxHeroes = getGateCapacity(tembokLevel).first
            val heroes = state.garrisonedHeroes.toMutableList()
            if (heroes.contains(heroName)) {
                heroes.remove(heroName)
            } else {
                if (heroes.size < maxHeroes) {
                    heroes.add(heroName)
                } else {
                    _alertMessage.value = "Kapasitas Hero Penuh! Maksimal $maxHeroes hero untuk Tembok level $tembokLevel."
                }
            }
            state.copy(garrisonedHeroesStr = heroes.joinToString(","))
        }
    }

    fun setGarrisonPasukan(amount: Long) {
        _uiState.update { state ->
            val tembokLevel = state.getBuildingLevel("tembok")
            val maxPasukan = getGateCapacity(tembokLevel).second
            
            // Revert existing troops back
            val activeTroops = state.currentPasukan + state.garrisonedPasukan
            val newAmount = amount.coerceAtMost(maxPasukan).coerceAtMost(activeTroops)
            
            state.copy(
                garrisonedPasukan = newAmount,
                currentPasukan = activeTroops - newAmount
            )
        }
    }

    fun healPasukan(amount: Long) {
        _uiState.update { state ->
            val level = state.getBuildingLevel("hospital")
            if (level == 0) return@update state
            
            val toHeal = amount.coerceAtMost(state.woundedPasukan)
            val costPerUnit = getHospitalBerryCost(level)
            val totalCost = toHeal * costPerUnit
            
            if (state.berry >= totalCost) {
                state.copy(
                    berry = state.berry - totalCost,
                    woundedPasukan = state.woundedPasukan - toHeal,
                    currentPasukan = state.currentPasukan + toHeal
                )
            } else {
                val affordable = state.berry / costPerUnit
                val actualHeal = affordable.coerceAtMost(toHeal)
                if (actualHeal > 0) {
                    val actualCost = actualHeal * costPerUnit
                    state.copy(
                        berry = state.berry - actualCost,
                        woundedPasukan = state.woundedPasukan - actualHeal,
                        currentPasukan = state.currentPasukan + actualHeal
                    )
                } else {
                    _alertMessage.value = "Berry tidak cukup untuk menyembuhkan pasukan!"
                    state
                }
            }
        }
    }

    fun equipItem(heroName: String, itemId: String) {
        _uiState.update { currentState ->
            val list = currentState.ownedCommandersStr.split(",").filter { it.isNotEmpty() }.toMutableList()
            val index = list.indexOfFirst { it.startsWith("$heroName:") }
            if (index == -1) return@update currentState
            
            val entry = list[index]
            val parts = entry.split(":")
            val stars = parts.getOrNull(1) ?: "1"
            var currentWeapon = parts.getOrNull(2) ?: "none"
            var currentArmor = parts.getOrNull(3) ?: "none"
            
            val node = allNodes.find { it.id == itemId } ?: return@update currentState
            if (node.type != "senjata/armor") return@update currentState
            
            // Check if player has unlocked this weapon/armor
            if (!currentState.isUnlocked(itemId)) {
                _alertMessage.value = "❌ Gagal! Anda belum menempa ${node.name}."
                return@update currentState
            }
            
            if (node.name.contains("Armor") || node.id.contains("armor") || node.id == "kulit_hewan" || node.id == "exo_suit" || node.id == "cyber_body") {
                currentArmor = itemId
            } else {
                currentWeapon = itemId
            }
            
            val newEntry = "$heroName:$stars:$currentWeapon:$currentArmor"
            list[index] = newEntry
            
            _alertMessage.value = "✅ Berhasil memasangkan ${node.name} ke $heroName!"
            
            currentState.copy(
                ownedCommandersStr = list.joinToString(",")
            )
        }
    }

    fun unequipItem(heroName: String, isArmor: Boolean) {
        _uiState.update { currentState ->
            val list = currentState.ownedCommandersStr.split(",").filter { it.isNotEmpty() }.toMutableList()
            val index = list.indexOfFirst { it.startsWith("$heroName:") }
            if (index == -1) return@update currentState
            
            val entry = list[index]
            val parts = entry.split(":")
            val stars = parts.getOrNull(1) ?: "1"
            var currentWeapon = parts.getOrNull(2) ?: "none"
            var currentArmor = parts.getOrNull(3) ?: "none"
            
            if (isArmor) {
                currentArmor = "none"
                _alertMessage.value = "🛡️ Berhasil melepas armor dari $heroName."
            } else {
                currentWeapon = "none"
                _alertMessage.value = "🗡️ Berhasil melepas senjata dari $heroName."
            }
            
            val newEntry = "$heroName:$stars:$currentWeapon:$currentArmor"
            list[index] = newEntry
            
            currentState.copy(
                ownedCommandersStr = list.joinToString(",")
            )
        }
    }
}

data class GachaRollResult(
    val heroName: String,
    val isNew: Boolean,
    val stars: Int,
    val convertedShards: Int
)
