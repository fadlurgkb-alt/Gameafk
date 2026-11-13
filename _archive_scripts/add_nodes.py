import re

with open("app/src/main/java/com/example/ui/game/GameScreen.kt", "r") as f:
    content = f.read()

# 1. memask_makanan
memasak = '    TalentNode("memasak_makanan", "Memasak Makanan", 5.0, emptyMap(), "Api membuat makanan lebih aman dikonsumsi", "skill", tier = 1, reqs = listOf("percikan_api"), story = "Api bisa membuat daging tidak mentah lagi saat dimakan dan jauh lebih aman untuk dikonsumsi.", gridX = -2, gridY = 1),'

content = content.replace(
    '    TalentNode("panggil_manusia", "Panggil Manusia Pertama", 10.0, emptyMap(), "Manusia memiliki akal dan rasa ingin tahu.", "skill", tier = 1, reqs = listOf("percikan_api"), gridX = 0, gridY = 1),',
    '    TalentNode("panggil_manusia", "Panggil Manusia Pertama", 10.0, emptyMap(), "Manusia memiliki akal dan rasa ingin tahu.", "skill", tier = 1, reqs = listOf("percikan_api"), gridX = 0, gridY = 1),\n' + memasak
)

# 2. pisau_batu & kapak_gagang
pisau_kapak = '''    TalentNode("pisau_batu", "Pisau Batu", 120.0, emptyMap(), "Pisau serbaguna untuk bertahan hidup", "skill", tier = 1, reqs = listOf("peralatan_tulang"), story = "Pisau serbaguna ini digunakan untuk membangun, menyembelih, dan membuat alat lain yang membantu keseharian manusia.", gridX = -2, gridY = 4),
    TalentNode("kapak_gagang", "Kapak Gagang", 150.0, emptyMap(), "Kapak untuk menebang dan menyerang", "skill", tier = 1, reqs = listOf("peralatan_tulang"), story = "Memasangkan batu/tulang tajam ke sebatang kayu membuat pukulan manusia jauh lebih kuat untuk menebang dan bertahan hidup.", gridX = 0, gridY = 4),'''

content = content.replace(
    '    TalentNode("peralatan_tulang", "Peralatan Tulang", 50.0, emptyMap(), "Peralatan dari tulang belulang", "skill", tier = 1, reqs = listOf("perkakas_batu"), story = "Setelah manusia berhasil membunuh hewan, manusia memanfaatkan tulang belulang untuk peralatan dan senjata manusia.", gridX = -1, gridY = 3),',
    '    TalentNode("peralatan_tulang", "Peralatan Tulang", 50.0, emptyMap(), "Peralatan dari tulang belulang", "skill", tier = 1, reqs = listOf("perkakas_batu"), story = "Setelah manusia berhasil membunuh hewan, manusia memanfaatkan tulang belulang untuk peralatan dan senjata manusia.", gridX = -1, gridY = 3),\n' + pisau_kapak
)

# 3. alat_pemburu, penyamakan_kulit, pakaian_pertama
alat_kulit = '''    TalentNode("alat_pemburu", "Alat Pemburu", 200.0, emptyMap(), "Mempermudah perburuan hewan", "skill", tier = 1, reqs = listOf("naluri_berburu", "peralatan_tulang"), story = "Dengan munculnya alat pemburu, manusia dapat mandiri untuk melacak, menangkap, dan membunuh mangsanya.", gridX = 2, gridY = 4),
    TalentNode("penyamakan_kulit", "Penyamakan Kulit", 180.0, emptyMap(), "Memanfaatkan kulit buruan", "skill", tier = 1, reqs = listOf("naluri_berburu"), story = "Manusia bijak memanfaatkan semua bagian tubuh hewan, termasuk kulit hewan dengan cara menyamak hasil buruan.", gridX = 3, gridY = 4),
    TalentNode("pakaian_pertama", "Pakaian Pertama", 350.0, emptyMap(), "Kehangatan dan perlindungan ekstra", "skill", tier = 1, reqs = listOf("penyamakan_kulit"), story = "Manusia membentuk dan merajut kulit hewan sebagai pakaian pertama mereka agar tubuh semakin hangat dan lebih terlindungi dari cuaca dingin.", gridX = 3, gridY = 5),'''

content = content.replace(
    '    TalentNode("naluri_berburu", "Naluri Berburu", 100.0, emptyMap(), "Membuka produksi otomatis 🥩 Daging (+1/s)", "skill", tier = 1, reqs = listOf("otak_kognitif"), story = "Dengan otak manusia, melihat hewan yang bisa dimanfaatkan baik dari daging, kulit, maupun tulang untuk keberlangsungan hidup.", gridX = 2, gridY = 3),',
    '    TalentNode("naluri_berburu", "Naluri Berburu", 100.0, emptyMap(), "Membuka produksi otomatis 🥩 Daging (+1/s)", "skill", tier = 1, reqs = listOf("otak_kognitif"), story = "Dengan otak manusia, melihat hewan yang bisa dimanfaatkan baik dari daging, kulit, maupun tulang untuk keberlangsungan hidup.", gridX = 2, gridY = 3),\n' + alat_kulit
)

# Also update the UI to make the icon mapping correct for the new nodes
icon_mapping = '''                                            node.id.contains("api") -> "🔥"
                                            node.id.contains("manusia") || node.id.contains("kognitif") -> "👤"
                                            node.id.contains("batu") -> "🪨"
                                            node.id.contains("tulang") -> "🦴"
                                            node.id.contains("berburu") || node.id.contains("pemburu") -> "🏹"
                                            node.id.contains("kulit") -> "🐄"
                                            node.id.contains("pakaian") -> "🧥"
                                            node.id.contains("memasak") -> "🍖"
                                            node.id.contains("kapak") -> "🪓"
                                            node.id.contains("emosi") -> "❤️"'''
content = content.replace(
'''                                            node.id.contains("api") -> "🔥"
                                            node.id.contains("manusia") || node.id.contains("kognitif") -> "👤"
                                            node.id.contains("batu") -> "🪨"
                                            node.id.contains("tulang") -> "🦴"
                                            node.id.contains("berburu") -> "🏹"
                                            node.id.contains("emosi") -> "❤️"''',
icon_mapping
)

with open("app/src/main/java/com/example/ui/game/GameScreen.kt", "w") as f:
    f.write(content)

