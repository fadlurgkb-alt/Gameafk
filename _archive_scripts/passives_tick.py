import re

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    content = f.read()

tick_mult = """
            val resourceMult = if (unlocked.contains("overclock_tubuh")) 2.0 else 1.0
            
            var kayuBatuBesiMult = if (unlocked.contains("kerja_shift")) 1.3 else 1.0
            if (unlocked.contains("feodalisme")) kayuBatuBesiMult += 0.4
            
            val pertanianMult = if (unlocked.contains("domestikasi_hewan")) 1.25 else 1.0
            val makananMult = if (unlocked.contains("pengalengan_makanan")) 1.5 else 1.0
            
            var emasMult = 1.0
            if (unlocked.contains("merkantilisme")) emasMult += 1.0
            
            var oilListrikMult = 1.0
            if (unlocked.contains("techno_corporatism")) oilListrikMult += 1.0
"""

content = re.sub(
    r'            val resourceMult.*?            val makananMult = if \(unlocked.contains\("pengalengan_makanan"\)\) 1.5 else 1.0',
    tick_mult,
    content,
    flags=re.DOTALL
)

# Replace the specific generations to use these multipliers
content = content.replace(
    'if (unlocked.contains("tambang_emas")) newEmas += (currentState.getBuildingLevel("tambang_emas") * 2 * resourceMult).toLong()',
    'if (unlocked.contains("tambang_emas")) newEmas += (currentState.getBuildingLevel("tambang_emas") * 2 * emasMult * resourceMult).toLong()'
)

content = content.replace(
    'if (unlocked.contains("pengeboran_minyak")) newOil += (currentState.getBuildingLevel("pengeboran_minyak") * 5 * resourceMult).toLong()',
    'if (unlocked.contains("pengeboran_minyak")) newOil += (currentState.getBuildingLevel("pengeboran_minyak") * 5 * oilListrikMult * resourceMult).toLong()'
)

content = content.replace(
    'if (unlocked.contains("pembangkit_listrik")) newListrik += (currentState.getBuildingLevel("pembangkit_listrik") * 10 * resourceMult).toLong()',
    'if (unlocked.contains("pembangkit_listrik")) newListrik += (currentState.getBuildingLevel("pembangkit_listrik") * 10 * oilListrikMult * resourceMult).toLong()'
)

# And for teokrasi: damage + 30%
content = content.replace(
    'damage += currentState.currentPasukan * 10L // Each soldier adds 10 damage',
    'damage += currentState.currentPasukan * 10L // Each soldier adds 10 damage\n            if (unlocked.contains("teokrasi")) damage = (damage * 1.3).toLong()'
)

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'w') as f:
    f.write(content)
