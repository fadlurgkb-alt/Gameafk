import re

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    content = f.read()

offline_mods = """        var globalMultiplier = 1.0
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
        if (unlocked.contains("techno_corporatism")) oilListrikMult += 1.0"""

content = re.sub(
    r'        var globalMultiplier = 1\.0.*?        val makananMult = if \(unlocked.contains\("pengalengan_makanan"\)\) 1.5 else 1.0',
    offline_mods,
    content,
    flags=re.DOTALL,
    count=1
)

# Fix offline emas & oil & listrik calculation
content = content.replace(
    'if (unlocked.contains("tambang_emas")) emasGained += (state.getBuildingLevel("tambang_emas") * 2 * resourceMult * secondsOffline * offlineMult).toLong()',
    'if (unlocked.contains("tambang_emas")) emasGained += (state.getBuildingLevel("tambang_emas") * 2 * emasMult * resourceMult * secondsOffline * offlineMult).toLong()'
)
content = content.replace(
    'if (unlocked.contains("pengeboran_minyak")) oilGained += (state.getBuildingLevel("pengeboran_minyak") * 5 * resourceMult * secondsOffline * offlineMult).toLong()',
    'if (unlocked.contains("pengeboran_minyak")) oilGained += (state.getBuildingLevel("pengeboran_minyak") * 5 * oilListrikMult * resourceMult * secondsOffline * offlineMult).toLong()'
)
content = content.replace(
    'if (unlocked.contains("pembangkit_listrik")) listrikGained += (state.getBuildingLevel("pembangkit_listrik") * 10 * resourceMult * secondsOffline * offlineMult).toLong()',
    'if (unlocked.contains("pembangkit_listrik")) listrikGained += (state.getBuildingLevel("pembangkit_listrik") * 10 * oilListrikMult * resourceMult * secondsOffline * offlineMult).toLong()'
)

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'w') as f:
    f.write(content)
