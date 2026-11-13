import re

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    content = f.read()

# Add passives to gameTick and calculateOfflineProgress

offline_mod = """
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
        
        var maxHappiness = 100L
        if (unlocked.contains("spiritualitas_suku")) maxHappiness += 15L
        
        var maxPopulasi = 0L
        if (unlocked.contains("rumah_warga")) maxPopulasi += (state.getBuildingLevel("rumah_warga") * 10L)
        if (unlocked.contains("sosialisme")) maxPopulasi += 50L
"""

content = re.sub(
    r'        var globalMultiplier = 1\.0.*?        \}',
    offline_mod,
    content,
    flags=re.DOTALL,
    count=1
)

tick_mod = """
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
"""

content = re.sub(
    r'            var globalMultiplier = 1\.0.*?            \}',
    tick_mod,
    content,
    flags=re.DOTALL,
    count=1
)

content = content.replace(
    'if (unlocked.contains("spiritualitas_suku")) maxHappiness += 15L',
    'if (unlocked.contains("spiritualitas_suku")) maxHappiness += 15L\n            if (unlocked.contains("spiritualitas_suku_2")) maxHappiness += 0L // hack to avoid matching again'
)

# gameTick has its own maxPopulasi handling:
content = content.replace(
    'if (unlocked.contains("rumah_warga")) maxPopulasi += (currentState.getBuildingLevel("rumah_warga") * 10L)',
    'if (unlocked.contains("rumah_warga")) maxPopulasi += (currentState.getBuildingLevel("rumah_warga") * 10L)\n            if (unlocked.contains("sosialisme")) maxPopulasi += 50L'
)
content = content.replace(
    'if (unlocked.contains("pengasapan_daging")) foodNeededDouble *= 0.90',
    'if (unlocked.contains("pengasapan_daging")) foodNeededDouble *= 0.90\n                if (unlocked.contains("hukum_kodeks")) foodNeededDouble *= 0.90'
)

# For feodalisme, merkantilisme, techno_corporatism we need to multiply resources in the generator logic.
# Wait, we can just modify resourceMult or individual multipliers.
# Let's search for how resources are added.
