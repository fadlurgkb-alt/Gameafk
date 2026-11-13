import re

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    content = f.read()

# I need to add nationality buff logic and religion buff logic
# We can do this right before the new resources are added

target = """            if (unlocked.contains("penebang_kayu")) newKayu += (currentState.getBuildingLevel("penebang_kayu") * kayuBatuBesiMult * resourceMult).toLong()"""

buffs_code = """
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
            
            baseMaxHappiness = (baseMaxHappiness * relHappiness).toLong()
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
            
            if (unlocked.contains("penebang_kayu")) newKayu += (currentState.getBuildingLevel("penebang_kayu") * kayuBatuBesiMult * resourceMult).toLong()"""

content = content.replace(target, buffs_code)
with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'w') as f:
    f.write(content)
