import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

# Filsafat block to replace
filsafat_block = """    // Filsafat
    TalentNode("barter_purba", "Sistem Barter Purba", 40.0, emptyMap(), "Pendapatan pasif Zenith (+50%)", "filsafat", tier = 1),
    TalentNode("spiritualitas_suku", "Spiritualitas Suku", 440.0, emptyMap(), "Batas Happiness global +15%", "filsafat", tier = 2),
    TalentNode("hukum_kodeks", "Hukum Kodeks", 1760.0, emptyMap(), "Konsumsi makanan warga -10%", "filsafat", tier = 3),
    TalentNode("feodalisme", "Feodalisme", 7040.0, emptyMap(), "+40% produksi Kayu dan Batu", "filsafat", tier = 4),
    TalentNode("merkantilisme", "Merkantilisme", 26400.0, emptyMap(), "+100% produksi Emas", "filsafat", tier = 5),
    TalentNode("teokrasi", "Teokrasi", 99000.0, emptyMap(), "Semua damage militer +30%", "filsafat", tier = 6),
    TalentNode("kapitalisme", "Kapitalisme", 330000.0, emptyMap(), "+100% pendapatan Zenith", "filsafat", tier = 7),
    TalentNode("sosialisme", "Sosialisme", 1320000.0, emptyMap(), "Gizi makanan sangat optimal, populasi cap +50", "filsafat", tier = 8),
    TalentNode("techno_corporatism", "Techno-Corporatism", 5500000.0, emptyMap(), "Produksi Oil & Listrik +100%", "filsafat", tier = 9),
    TalentNode("transhumanisme", "Transhumanisme", 22000000.0, emptyMap(), "Multiplier global dikali 5", "filsafat", tier = 10),

    // Senjata/Armor
    TalentNode("tongkat_kayu", "Tier 1: Tongkat Kayu", 30.0, mapOf("kayu" to 100L), "Damage: 180", "senjata/armor", tier = 1),
    TalentNode("kulit_hewan", "Armor 1: Kulit Hewan", 45.0, mapOf("kayu" to 50L), "Defense +20, HP +200", "senjata/armor", tier = 1),
    TalentNode("pedang_besi", "Tier 2: Pedang Besi", 440.0, mapOf("besi" to 264L), "Damage: 980", "senjata/armor", tier = 2),
    TalentNode("armor_besi", "Armor 2: Baju Besi", 660.0, mapOf("besi" to 396L), "Defense +150, HP +1000", "senjata/armor", tier = 2),
    TalentNode("musket", "Tier 3: Musket", 5500.0, mapOf("besi" to 1100L, "emas" to 110L), "Damage: 6000", "senjata/armor", tier = 3),
    TalentNode("armor_kevlar", "Armor 3: Rompi Kevlar", 8800.0, mapOf("besi" to 2200L, "emas" to 220L), "Defense +1000, HP +5000", "senjata/armor", tier = 3),
    TalentNode("assault_rifle", "Tier 4: Assault Rifle", 49500.0, mapOf("besi" to 20000L, "emas" to 5000L, "item_rare" to 10L), "Damage: 12500", "senjata/armor", tier = 4),
    TalentNode("exo_suit", "Armor 4: Exo-Suit", 66000.0, mapOf("besi" to 25000L, "emas" to 7000L, "item_rare" to 15L), "Defense +8000, HP +25000", "senjata/armor", tier = 4),
    TalentNode("energy_katana", "Tier 5: Energy Katana", 330000.0, mapOf("besi" to 100000L, "titanium" to 5000L, "item_sr" to 5L), "Damage: 76000", "senjata/armor", tier = 5),
    TalentNode("nano_armor", "Armor 5: Nano Armor", 495000.0, mapOf("besi" to 150000L, "titanium" to 8000L, "item_sr" to 8L), "Defense +50000, HP +150000", "senjata/armor", tier = 5),
    TalentNode("planet_breaker", "Tier 6: Planet Breaker", 8800000.0, mapOf("titanium" to 50000L, "uranium" to 10000L, "item_ssr" to 2L), "Damage: 400000", "senjata/armor", tier = 6),
    TalentNode("cyber_body", "Armor 6: Cyber Body", 13200000.0, mapOf("titanium" to 80000L, "uranium" to 15000L, "item_ssr" to 3L), "Defense +300000, HP +1000000", "senjata/armor", tier = 6)
)"""

# Regex to find from "// Filsafat" up to the end of the list ")"
content = re.sub(r'// Filsafat.*?cyber_body.*?\n\)', filsafat_block, content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
