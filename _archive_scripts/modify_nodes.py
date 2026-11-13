import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

def get_tier(node_id):
    t1 = ["town_hall", "gudang", "pertanian", "penebang_kayu", "pemecah_batu", "barter_purba"]
    t2 = ["rumah_warga", "tavern", "perkebunan", "peternakan", "market", "temple", "taman_kota", "peleburan_besi", "tambang_emas", "feodalisme"]
    t3 = ["barak_militer", "tembok", "pelabuhan", "dungeon", "merkantilisme"]
    t4 = ["pengeboran_minyak", "pembangkit_listrik", "kapitalisme"]
    t5 = ["tambang_titanium", "reaktor_uranium", "transhumanisme"]
    
    if node_id in t1: return 1
    if node_id in t2: return 2
    if node_id in t3: return 3
    if node_id in t4: return 4
    if node_id in t5: return 5
    return 1

lines = content.split('\n')
for i, line in enumerate(lines):
    if line.strip().startswith('TalentNode('):
        match = re.search(r'TalentNode\("([^"]+)"', line)
        if match:
            node_id = match.group(1)
            tier = get_tier(node_id)
            if line.endswith('),'):
                lines[i] = line[:-2] + f', tier = {tier}),'
            elif line.endswith(')'):
                lines[i] = line[:-1] + f', tier = {tier})'

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write('\n'.join(lines))

