// ============================================================================
// GENESIS PIXEL: IDLE COSMOS - COMPLETE MASTER GAME STATE & REPOSITORY (V3)
// 22 Komandan Lengkap, 12 Senjata/Armor Tempa, 26 Riset Talent Tree,
// 23 Bangunan, 10 Filsafat, 15+ Sumber Daya, Dungeon & Raid, Combat Power (CP)
// ============================================================================

// ----------------------------------------------------------------------------
// 1. DAFTAR 22 KOMANDAN LENGKAP (Mythical, SSR, SR, R)
// ----------------------------------------------------------------------------
export const COMMANDERS_DATABASE = [
  // Mythical (4)
  { id: 'zeus', name: 'Zeus (Mythical)', rarity: 'Mythical', role: 'Dewa Petir', stars: 1, level: 1, baseAtk: 50000, baseHp: 200000, skill: 'Petir Penghancur Langit', bonus: '+300% Serangan Petir & Zenith', icon: '⚡', color: '#EF4444', owned: false },
  { id: 'athena', name: 'Athena (Mythical)', rarity: 'Mythical', role: 'Dewi Kebijaksanaan', stars: 1, level: 1, baseAtk: 45000, baseHp: 220000, skill: 'Perisai Aegis Suci', bonus: '+250% Pertahanan Gerbang & Riset', icon: '🦉', color: '#EF4444', owned: false },
  { id: 'odin', name: 'Odin (Mythical)', rarity: 'Mythical', role: 'All-Father Asgard', stars: 1, level: 1, baseAtk: 48000, baseHp: 210000, skill: 'Tombak Gungnir Tak Meleset', bonus: '+280% Damage Raid & AFK Gain', icon: '👁️', color: '#EF4444', owned: false },
  { id: 'archon', name: 'CYBER-ARCHON ZERO (Mythical)', rarity: 'Mythical', role: 'Singularity Quantum AI', stars: 1, level: 1, baseAtk: 60000, baseHp: 250000, skill: 'Protokol Overdrive Kosmis', bonus: '+350% Seluruh Produksi Global', icon: '🤖', color: '#EF4444', owned: false },

  // SSR (6)
  { id: 'arthur', name: 'Arthur Pendragon (SSR)', rarity: 'SSR', role: 'Raja Camelot', stars: 1, level: 1, baseAtk: 12000, baseHp: 50000, skill: 'Excalibur Cahaya Kemenangan', bonus: '+120% Serangan Ksatria', icon: '🗡️', color: '#F59E0B', owned: false },
  { id: 'joan', name: 'Joan of Arc (SSR)', rarity: 'SSR', role: 'Ksatria Suci', stars: 1, level: 1, baseAtk: 10000, baseHp: 55000, skill: 'Panji Perjuangan Abadi', bonus: '+150% Moril & Kebahagiaan', icon: '⚜️', color: '#F59E0B', owned: false },
  { id: 'gilgamesh', name: 'Gilgamesh (SSR)', rarity: 'SSR', role: 'Raja Pertama Uruk', stars: 1, level: 1, baseAtk: 14000, baseHp: 48000, skill: 'Gerbang Khazanah Harta', bonus: '+100% Emas & Drop Dungeon', icon: '👑', color: '#F59E0B', owned: false },
  { id: 'cleopatra', name: 'Cleopatra VII (SSR)', rarity: 'SSR', role: 'Ratu Sungai Nil', stars: 1, level: 1, baseAtk: 9500, baseHp: 52000, skill: 'Pesona Diplomasi Emas', bonus: '+80% Diskon Biaya Pasar', icon: '🐍', color: '#F59E0B', owned: false },
  { id: 'genghis', name: 'Genghis Khan (SSR)', rarity: 'SSR', role: 'Kaisar Padang Stepa', stars: 1, level: 1, baseAtk: 13500, baseHp: 46000, skill: 'Serbuan Kavaleri Badai', bonus: '+110% Kecepatan Gerak Pasukan', icon: '🐎', color: '#F59E0B', owned: false },
  { id: 'tesla', name: 'Nikola Tesla (SSR)', rarity: 'SSR', role: 'Ilmuwan Petir', stars: 1, level: 1, baseAtk: 11000, baseHp: 45000, skill: 'Menara Arus Listrik AC', bonus: '+130% Daya Listrik & Titanium', icon: '💡', color: '#F59E0B', owned: false },

  // SR (7)
  { id: 'alexander', name: 'Alexander (SR)', rarity: 'SR', role: 'Penakluk Dunia', stars: 1, level: 1, baseAtk: 3000, baseHp: 12000, skill: 'Formasi Falang Tak Terkalahkan', bonus: '+60% Attack Pasukan', icon: '🛡️', color: '#8B5CF6', owned: true },
  { id: 'gajah_mada', name: 'Gajah Mada (SR)', rarity: 'SR', role: 'Mahapatih Nusantara', stars: 1, level: 1, baseAtk: 3200, baseHp: 13000, skill: 'Sumpah Palapa Pemersatu', bonus: '+70% Ketahanan Gerbang', icon: '🐘', color: '#8B5CF6', owned: true },
  { id: 'musashi', name: 'Miyamoto Musashi (SR)', rarity: 'SR', role: 'Master Pedang Ganda', stars: 1, level: 1, baseAtk: 3500, baseHp: 11000, skill: 'Tebasan Dua Langit', bonus: '+75% Serangan Kritis', icon: '⚔️', color: '#8B5CF6', owned: false },
  { id: 'sun_tzu', name: 'Sun Tzu (SR)', rarity: 'SR', role: 'Ahli Strategi Perang', stars: 1, level: 1, baseAtk: 2800, baseHp: 14000, skill: 'Seni Seni Perang', bonus: '+50% Efisiensi Pertahanan', icon: '📜', color: '#8B5CF6', owned: false },
  { id: 'leonidas', name: 'Leonidas I (SR)', rarity: 'SR', role: 'Raja Sparta', stars: 1, level: 1, baseAtk: 3100, baseHp: 15000, skill: 'Tembok 300 Sparta', bonus: '+80% HP Pasukan Gerbang', icon: '🛡️', color: '#8B5CF6', owned: false },
  { id: 'saladin', name: 'Saladin (SR)', rarity: 'SR', role: 'Sultan Kesatria', stars: 1, level: 1, baseAtk: 2900, baseHp: 13500, skill: 'Keadilan Sang Sultan', bonus: '+50% Pemulihan Rumah Sakit', icon: '🌙', color: '#8B5CF6', owned: false },
  { id: 'richard', name: 'Richard the Lionheart (SR)', rarity: 'SR', role: 'Hati Singa', stars: 1, level: 1, baseAtk: 3300, baseHp: 12500, skill: 'Auman Keberanian Singa', bonus: '+65% Damage Raid', icon: '🦁', color: '#8B5CF6', owned: false },

  // R (5)
  { id: 'gorg', name: 'Gorg Si Pemburu (R)', rarity: 'R', role: 'Pelacak Mammoth', stars: 1, level: 1, baseAtk: 600, baseHp: 2500, skill: 'Lemparan Tombak Primitif', bonus: '+25% Daging & Berry', icon: '🪓', color: '#3B82F6', owned: true },
  { id: 'shaman', name: 'Dukun Batu Kirana (R)', rarity: 'R', role: 'Tetua Ritual', stars: 1, level: 1, baseAtk: 500, baseHp: 2200, skill: 'Ritual Berkah Api Unggun', bonus: '+30% Zenith Awal', icon: '🔥', color: '#3B82F6', owned: true },
  { id: 'prajurit', name: 'Prajurit Biasa (R)', rarity: 'R', role: 'Prajurit Garis Depan', stars: 1, level: 1, baseAtk: 550, baseHp: 2600, skill: 'Tusukan Serentak', bonus: '+20% Pasukan Barak', icon: '🗡️', color: '#3B82F6', owned: true },
  { id: 'archer', name: 'Pemanah Elit (R)', rarity: 'R', role: 'Penembak Jarak Jauh', stars: 1, level: 1, baseAtk: 700, baseHp: 2000, skill: 'Hujan Panah Api', bonus: '+30% Akurasi Gerbang', icon: '🏹', color: '#3B82F6', owned: false },
  { id: 'penjaga', name: 'Penjaga Benteng (R)', rarity: 'R', role: 'Tameng Desa', stars: 1, level: 1, baseAtk: 450, baseHp: 3200, skill: 'Barikade Kayu', bonus: '+40% HP Tembok', icon: '🧱', color: '#3B82F6', owned: false }
];

// ----------------------------------------------------------------------------
// 2. DAFTAR PERALATAN & TEMPA SENJATA (Armory)
// ----------------------------------------------------------------------------
export const ARMORY_WEAPONS = [
  { id: 'tongkat_kayu', name: 'Tombak Kayu Primitif', type: 'senjata', img: '/assets/ic_wp_tombak.png', atkBonus: 180, hpBonus: 0, cost: { kayu: 100 }, tier: 1 },
  { id: 'kulit_hewan', name: 'Armor Kulit Hewan', type: 'armor', img: '/assets/ic_wp_armor_kulit.png', atkBonus: 20, hpBonus: 200, cost: { kayu: 50, daging: 30 }, tier: 1 },
  { id: 'pedang_besi', name: 'Pedang Besi Tempa', type: 'senjata', img: '/assets/ic_wp_pedang.png', atkBonus: 980, hpBonus: 0, cost: { besi: 264 }, tier: 2 },
  { id: 'armor_besi', name: 'Baju Zirah Besi', type: 'armor', img: '/assets/ic_wp_baju_besi.png', atkBonus: 50, hpBonus: 1000, cost: { besi: 396 }, tier: 2 },
  { id: 'musket', name: 'Senapan Musket Klasik', type: 'senjata', img: '/assets/ic_wp_musket.png', atkBonus: 6000, hpBonus: 0, cost: { besi: 1100, emas: 110 }, tier: 3 },
  { id: 'assault_rifle', name: 'Assault Rifle Otomatis', type: 'senjata', img: '/assets/ic_wp_assault_rifle.png', atkBonus: 12500, hpBonus: 0, cost: { besi: 2200, oil: 200 }, tier: 3 },
  { id: 'railgun', name: 'Senapan Railgun Magnetik', type: 'senjata', img: '/assets/ic_wp_railgun.png', atkBonus: 28000, hpBonus: 0, cost: { titanium: 800, listrik: 500 }, tier: 4 },
  { id: 'exosuit', name: 'Exosuit Tempur Lapislapis', type: 'armor', img: '/assets/ic_wp_exosuit.png', atkBonus: 2000, hpBonus: 25000, cost: { titanium: 1500, listrik: 1000 }, tier: 4 },
  { id: 'laser_blade', name: 'Pedang Plasma Laser Blade', type: 'senjata', img: '/assets/ic_wp_laser_blade.png', atkBonus: 55000, hpBonus: 0, cost: { titanium: 2500, uranium: 300 }, tier: 5 },
  { id: 'plasma_cannon', name: 'Plasma Heavy Cannon', type: 'senjata', img: '/assets/ic_wp_plasma_cannon.png', atkBonus: 95000, hpBonus: 0, cost: { uranium: 1200, listrik: 3000 }, tier: 5 },
  { id: 'cyber_body', name: 'Cybernetic Nano Body', type: 'armor', img: '/assets/ic_wp_cyber_body.png', atkBonus: 10000, hpBonus: 80000, cost: { titanium: 5000, uranium: 2000 }, tier: 5 },
  { id: 'nano_kirana', name: 'Nano Kirana Singularity Blade', type: 'senjata', img: '/assets/ic_wp_nano_kirana.png', atkBonus: 250000, hpBonus: 50000, cost: { uranium: 5000, listrik: 10000 }, tier: 5 }
];

// ----------------------------------------------------------------------------
// 3. POHON RISET TALENT TREE KOSMIS (26 Cabang Terstruktur Berkonstelasi)
// ----------------------------------------------------------------------------
export const COSMIC_RESEARCH_TREE = [
  // TIER 1: ERA PRIMITIF (Y: 50 - 250)
  { id: 'percikan_api', name: 'Nyalakan Api Pertama', tier: 1, costZenith: 1, reqs: [], x: 400, y: 70, desc: 'Satu percikan api mengusir kegelapan awal zaman.', icon: '🔥' },
  { id: 'memasak_makanan', name: 'Memasak Makanan', tier: 1, costZenith: 5, reqs: ['percikan_api'], x: 250, y: 150, desc: 'Api membuat daging buruan aman dikonsumsi suku.', icon: '🍳' },
  { id: 'panggil_manusia', name: 'Panggil Manusia', tier: 1, costZenith: 10, reqs: ['percikan_api'], x: 550, y: 150, desc: 'Manusia pertama hadir membawa akal budi dan rasa ingin tahu.', icon: '🚶' },
  { id: 'perkakas_batu', name: 'Perkakas Batu', tier: 1, costZenith: 20, reqs: ['memasak_makanan'], x: 160, y: 230, desc: 'Peralatan batu obsidian membantu bertahan hidup.', icon: '🪨' },
  { id: 'budidaya', name: 'Budidaya Semak', tier: 1, costZenith: 108, reqs: ['perkakas_batu'], x: 120, y: 310, desc: 'Membuka produksi otomatis 🧺 Berry (+2/s).', icon: '🫐' },
  { id: 'pengasapan_daging', name: 'Pengasapan Daging', tier: 1, costZenith: 324, reqs: ['budidaya'], x: 120, y: 390, desc: 'Mengurangi konsumsi makanan warga sebesar 10%.', icon: '🥓' },
  { id: 'pertahanan_primitif', name: 'Pertahanan Primitif', tier: 1, costZenith: 756, reqs: ['pengasapan_daging'], x: 120, y: 470, desc: 'Menambah Damage tempur dasar sebesar +50.', icon: '🛡️' },
  { id: 'otak_kognitif', name: 'Otak Kognitif', tier: 1, costZenith: 30, reqs: ['panggil_manusia'], x: 550, y: 230, desc: 'Lonjakan kapasitas berpikir dan memecahkan teka-teki.', icon: '🧠' },
  { id: 'emosi_jiwa', name: 'Emosi & Jiwa', tier: 1, costZenith: 80, reqs: ['otak_kognitif'], x: 650, y: 310, desc: 'Kasih sayang dan empati mempererat kelompok suku.', icon: '❤️' },
  { id: 'moralitas_komunitas', name: 'Moralitas Komunitas', tier: 1, costZenith: 150, reqs: ['emosi_jiwa'], x: 650, y: 390, desc: 'Norma sosial mengatur ketertiban masyarakat.', icon: '🤝' },
  { id: 'penyelesaian_masalah', name: 'Penyelesaian Masalah', tier: 1, costZenith: 300, reqs: ['moralitas_komunitas'], x: 650, y: 470, desc: 'Merencanakan pembangunan desa secara sistematis (Syarat Town Hall).', icon: '💡' },
  { id: 'naluri_berburu', name: 'Naluri Berburu', tier: 1, costZenith: 100, reqs: ['otak_kognitif'], x: 380, y: 270, desc: 'Membuka produksi otomatis 🥩 Daging (+1/s).', icon: '🏹' },

  // TIER 2: ERA KERAJAAN AGRARIS (Y: 530 - 680)
  { id: 'domestikasi_hewan', name: 'Domestikasi Hewan', tier: 2, costZenith: 2592, reqs: ['naluri_berburu', 'pertahanan_primitif'], x: 260, y: 550, desc: 'Produksi Pertanian & Perkebunan naik +25%.', icon: '🐕' },
  { id: 'seni_memasak', name: 'Seni Memasak Klasik', tier: 2, costZenith: 7560, reqs: ['domestikasi_hewan'], x: 380, y: 620, desc: 'Olah Makanan Matang (+2% Zenith Global).', icon: '🍲' },
  { id: 'manufaktur_roti', name: 'Manufaktur Roti', tier: 2, costZenith: 18360, reqs: ['seni_memasak'], x: 380, y: 700, desc: 'Gandum diolah menjadi Roti bergizi tinggi.', icon: '🍞' },
  { id: 'spiritualitas_suku', name: 'Spiritualitas Suku', tier: 2, costZenith: 32400, reqs: ['penyelesaian_masalah'], x: 620, y: 620, desc: 'Meningkatkan batas maksimal Kebahagiaan peradaban +15%.', icon: '✨' },

  // TIER 3: ERA INDUSTRI & KEDAULATAN (Y: 760 - 920)
  { id: 'pengalengan_makanan', name: 'Pengalengan Makanan', tier: 3, costZenith: 140400, reqs: ['manufaktur_roti'], x: 200, y: 780, desc: 'Pabrik makanan kaleng kedap udara, output naik +50%.', icon: '🥫' },
  { id: 'kerja_shift', name: 'Manajemen Kerja Shift', tier: 3, costZenith: 324000, reqs: ['manufaktur_roti'], x: 320, y: 780, desc: 'Kecepatan ekstraksi Kayu, Batu, Besi naik +30%.', icon: '⏱️' },
  { id: 'metalurgi_militer', name: 'Metalurgi Militer', tier: 3, costZenith: 756000, reqs: ['manufaktur_roti'], x: 440, y: 780, desc: 'Diskon 15% bahan logam saat menempa senjata militer.', icon: '🗡️' },
  { id: 'kedaulatan_bangsa', name: 'Kedaulatan Bangsa', tier: 3, costZenith: 3000000, reqs: ['spiritualitas_suku'], x: 580, y: 780, desc: 'Membuka Fitur Kebangsaan & Diplomasi Dunia.', icon: '👑' },
  { id: 'overclock_tubuh', name: 'Overclock Tubuh', tier: 3, costZenith: 9720000, reqs: ['pengalengan_makanan', 'kerja_shift'], x: 260, y: 870, desc: 'Stimulan medis memicu seluruh produksi bahan mentah 2x lipat.', icon: '⚡' },
  { id: 'logistik_komputer', name: 'Logistik Komputerisasi', tier: 3, costZenith: 25920000, reqs: ['kerja_shift', 'metalurgi_militer'], x: 400, y: 870, desc: 'Kapasitas gudang otomatis bertambah +50.000 unit.', icon: '💻' },
  { id: 'drone_swarm', name: 'Drone Swarm Gathering', tier: 3, costZenith: 97200000, reqs: ['metalurgi_militer', 'kedaulatan_bangsa'], x: 560, y: 870, desc: 'Drone memanen otomatis Minyak, Titanium, Uranium +5/s.', icon: '🛸' },

  // TIER 4 & 5: ERA CYBERPUNK & KOSMIS (Y: 960 - 1100)
  { id: 'implan_sibernetik', name: 'Implan Sibernetik', tier: 4, costZenith: 324000000, reqs: ['overclock_tubuh'], x: 260, y: 970, desc: 'Damage tempur +25.000 dan HP gerbang benteng +5.000.', icon: '🦾' },
  { id: 'nano_repair', name: 'Nano Repair System', tier: 4, costZenith: 1296000000, reqs: ['logistik_komputer'], x: 400, y: 970, desc: 'Nanobot menyembuhkan sel tubuh, HP melonjak +100.000.', icon: '💉' },
  { id: 'brain_link', name: 'Singularity Brain Link', tier: 5, costZenith: 5400000000, reqs: ['drone_swarm', 'implan_sibernetik', 'nano_repair'], x: 400, y: 1080, desc: 'Kesadaran kosmis menyatu! Seluruh pendapatan Zenith dikali 5x lipat!', icon: '🌌' }
];

// ----------------------------------------------------------------------------
// 4. DAFTAR 23 BANGUNAN ASLI
// ----------------------------------------------------------------------------
export const ALL_BUILDINGS = [
  { id: 'town_hall', name: 'Balai Kota (Town Hall)', cost: { kayu: 28, batu: 28 }, costMult: 1.35, zenithPerSec: 5.0, icon: '🏛️', plotX: 400, plotY: 280, desc: 'Pusat pemerintahan peradaban.' },
  { id: 'rumah_warga', name: 'Rumah Rakyat', cost: { kayu: 40, batu: 30 }, costMult: 1.25, zenithPerSec: 1.5, icon: '🛖', plotX: 300, plotY: 240, desc: 'Kapasitas populasi +10 per level.' },
  { id: 'penebang_kayu', name: 'Kamp Kayu', cost: { kayu: 12 }, costMult: 1.20, produces: 'kayu', ratePerSec: 1.0, zenithPerSec: 1.0, icon: '🪓', plotX: 180, plotY: 200, desc: 'Produksi Kayu (+1/s).' },
  { id: 'pemecah_batu', name: 'Penambang Batu', cost: { batu: 12 }, costMult: 1.20, produces: 'batu', ratePerSec: 1.0, zenithPerSec: 1.0, icon: '⛏️', plotX: 620, plotY: 200, desc: 'Produksi Batu (+1/s).' },
  { id: 'gudang', name: 'Gudang Logistik', cost: { kayu: 36, batu: 20 }, costMult: 1.28, zenithPerSec: 0.5, icon: '📦', plotX: 500, plotY: 240, desc: 'Kapasitas penyimpanan +500.' },
  { id: 'pertanian', name: 'Ladang Gandum', cost: { kayu: 48, batu: 17 }, costMult: 1.22, produces: 'gandum', ratePerSec: 2.0, zenithPerSec: 2.0, icon: '🌾', plotX: 240, plotY: 360, desc: 'Produksi Gandum (+2/s).' },
  { id: 'perkebunan', name: 'Kebun Berry', cost: { kayu: 36, batu: 12 }, costMult: 1.22, produces: 'berry', ratePerSec: 2.0, zenithPerSec: 1.5, icon: '🫐', plotX: 560, plotY: 360, desc: 'Produksi Berry (+2/s).' },
  { id: 'peternakan', name: 'Padang Peternakan', cost: { kayu: 60, batu: 36 }, costMult: 1.25, produces: 'daging', ratePerSec: 1.0, zenithPerSec: 3.0, icon: '🥩', plotX: 160, plotY: 300, desc: 'Produksi Daging (+1/s).' },
  { id: 'hospital', name: 'Balai Pengobatan', cost: { kayu: 150, batu: 100 }, costMult: 1.30, zenithPerSec: 1.0, icon: '🏥', plotX: 280, plotY: 170, desc: 'Sembuhkan prajurit terluka.' },
  { id: 'barak_militer', name: 'Barak Militer', cost: { kayu: 40, batu: 20 }, costMult: 1.26, zenithPerSec: 2.5, icon: '🛡️', plotX: 520, plotY: 170, desc: 'Latih prajurit (Max Pasukan +20).' },
  { id: 'tembok', name: 'Gerbang Benteng', cost: { batu: 60, besi: 24 }, costMult: 1.30, zenithPerSec: 1.0, icon: '🏰', plotX: 400, plotY: 130, desc: 'Pertahanan raid (HP Gerbang +250).' },
  { id: 'peleburan_besi', name: 'Peleburan Besi', cost: { batu: 60, kayu: 60 }, costMult: 1.28, produces: 'besi', ratePerSec: 2.0, zenithPerSec: 4.0, icon: '⛓️', plotX: 660, plotY: 280, desc: 'Produksi Besi (+2/s).' },
  { id: 'tambang_emas', name: 'Tambang Emas', cost: { besi: 60, kayu: 60 }, costMult: 1.30, produces: 'emas', ratePerSec: 2.0, zenithPerSec: 8.0, icon: '🪙', plotX: 700, plotY: 170, desc: 'Produksi Emas (+2/s).' },
  { id: 'market', name: 'Pasar (Market)', cost: { kayu: 145, besi: 72 }, costMult: 1.35, zenithPerSec: 10.0, icon: '🏪', plotX: 380, plotY: 390, desc: 'Pusat barter & toko koin.' },
  { id: 'temple', name: 'Kuil / Altar', cost: { batu: 181, emas: 60 }, costMult: 1.35, zenithPerSec: 12.0, icon: '⛪', plotX: 400, plotY: 200, desc: 'Doktrin agama & spiritual.' },
  { id: 'pelabuhan', name: 'Pelabuhan Dermaga', cost: { kayu: 96, batu: 48 }, costMult: 1.25, produces: 'ikan', ratePerSec: 2.0, zenithPerSec: 5.0, icon: '⛵', plotX: 120, plotY: 420, desc: 'Produksi Ikan (+2/s).' },
  { id: 'dungeon', name: 'Portal Dungeon', cost: { batu: 242, besi: 60 }, costMult: 1.40, zenithPerSec: 15.0, icon: '🌀', plotX: 680, plotY: 420, desc: 'Akses pertempuran monster.' },
  { id: 'tavern', name: 'Kedai (Tavern)', cost: { kayu: 275, besi: 165, emas: 55 }, costMult: 1.40, zenithPerSec: 18.0, icon: '🍻', plotX: 490, plotY: 330, desc: 'Rekrutmen pahlawan & gacha.' },
  { id: 'pengeboran_minyak', name: 'Pengeboran Minyak', cost: { besi: 242, batu: 121 }, costMult: 1.32, produces: 'oil', ratePerSec: 5.0, zenithPerSec: 35.0, icon: '🛢️', plotX: 130, plotY: 130, desc: 'Produksi Minyak (+5/s).' },
  { id: 'pembangkit_listrik', name: 'Pembangkit Listrik', cost: { besi: 605, emas: 121 }, costMult: 1.35, produces: 'listrik', ratePerSec: 10.0, zenithPerSec: 80.0, icon: '⚡', plotX: 250, plotY: 90, desc: 'Produksi Listrik (+10/s).' },
  { id: 'tambang_titanium', name: 'Tambang Titanium', cost: { listrik: 1210, besi: 1210 }, costMult: 1.40, produces: 'titanium', ratePerSec: 8.0, zenithPerSec: 250.0, icon: '🦾', plotX: 550, plotY: 90, desc: 'Produksi Titanium (+8/s).' },
  { id: 'reaktor_uranium', name: 'Reaktor Uranium', cost: { titanium: 6050, listrik: 6050 }, costMult: 1.45, produces: 'uranium', ratePerSec: 5.0, zenithPerSec: 800.0, icon: '☢️', plotX: 690, plotY: 100, desc: 'Produksi Uranium (+5/s).' },
  { id: 'taman_kota', name: 'Taman Kota Peradaban', cost: { kayu: 84, batu: 84 }, costMult: 1.25, zenithPerSec: 8.0, icon: '⛲', plotX: 310, plotY: 330, desc: 'Kebahagiaan warga +20%.' }
];

// ----------------------------------------------------------------------------
// 5. DAFTAR 10 MAZHAB FILSAFAT
// ----------------------------------------------------------------------------
export const ALL_PHILOSOPHIES = [
  { id: 'barter_purba', name: 'Perdagangan Suku', costZenith: 40, effect: '+50% Pendapatan pasif Zenith', img: '/assets/ic_fil_perdagangan.png', icon: '🐚', tier: 1 },
  { id: 'hukum_kodeks', name: 'Hukum & Tata Pura', costZenith: 1760, effect: 'Konsumsi makanan warga berkurang 10%', img: '/assets/ic_fil_hukum.png', icon: '📜', tier: 1 },
  { id: 'monarki', name: 'Monarki Kodoks', costZenith: 4400, effect: 'Batas Kebahagiaan peradaban naik +15%', img: '/assets/ic_fil_monarki.png', icon: '👑', tier: 2 },
  { id: 'feodalisme', name: 'Feodalisme', costZenith: 7040, effect: 'Produksi Kayu & Batu meningkat +40%', img: '/assets/ic_fil_feodalisme.png', icon: '🏰', tier: 2 },
  { id: 'meritokrasi', name: 'Meritokrasi', costZenith: 26400, effect: 'Produksi Emas & efisiensi pejabat +100%', img: '/assets/ic_fil_meritokrasi.png', icon: '⚖️', tier: 3 },
  { id: 'teokrasi', name: 'Teokrasi', costZenith: 99000, effect: 'Seluruh Damage tempur militer +30%', img: '/assets/ic_fil_teokrasi.png', icon: '📿', tier: 3 },
  { id: 'kapitalisme', name: 'Kapitalisme', costZenith: 330000, effect: 'Pendapatan Zenith per detik melonjak +100%', img: '/assets/ic_fil_kapitalisme.png', icon: '💰', tier: 4 },
  { id: 'sosialisme', name: 'Sosialisme', costZenith: 1320000, effect: 'Gizi pangan optimal, kapasitas warga +50', img: '/assets/ic_fil_sosialisme.png', icon: '🚩', tier: 4 },
  { id: 'otoritarianisme', name: 'Otoritarianisme', costZenith: 5500000, effect: 'Produksi Minyak & Listrik berlipat +100%', img: '/assets/ic_fil_otoritarianisme.png', icon: '🦅', tier: 5 },
  { id: 'transhumanisme', name: 'Transhumanisme', costZenith: 22000000, effect: 'Pengali evolusi peradaban dikali 5x lipat!', img: '/assets/ic_fil_transhumanisme.png', icon: '🧬', tier: 5 }
];

// ----------------------------------------------------------------------------
// 6. BOS DUNGEON RESMI
// ----------------------------------------------------------------------------
export const DUNGEON_MONSTERS = [
  { level: 1, name: 'Serigala Liar', hp: 1080, atk: 48, isBoss: false, icon: '🐺', color: '#4B5563' },
  { level: 5, name: 'Alpha Serigala Purba', hp: 7200, atk: 180, isBoss: true, icon: '🐺', color: '#DC2626' },
  { level: 10, name: 'Raja Goblin Gua', hp: 40500, atk: 720, isBoss: true, icon: '👺', color: '#16A34A' },
  { level: 15, name: 'Panglima Orc Lembah', hp: 162000, atk: 1800, isBoss: true, icon: '👹', color: '#EA580C' },
  { level: 30, name: 'Raja Ogre Berkepala Dua', hp: 765000, atk: 4800, isBoss: true, icon: '🧌', color: '#7C3AED' },
  { level: 50, name: 'Raja Iblis Kegelapan', hp: 3150000, atk: 18000, isBoss: true, icon: '👿', color: '#B91C1C' },
  { level: 100, name: 'Zenith Primordial Kosmis', hp: 45000000, atk: 260000, isBoss: true, icon: '🐉', color: '#F59E0B' }
];

// ----------------------------------------------------------------------------
// 7. INITIAL GAME STATE LENGKAP
// ----------------------------------------------------------------------------
export const INITIAL_GAME_STATE = {
  // Player Profile
  playerName: 'Penguasa Kosmis',
  avatarId: 'zeus',

  // 15+ Sumber Daya
  totalZenith: 150.0,
  kayu: 50,
  batu: 50,
  besi: 0,
  emas: 0,
  oil: 0,
  listrik: 0,
  titanium: 0,
  uranium: 0,
  daging: 30,
  berry: 30,
  gandum: 0,
  roti: 0,
  kalengan: 0,
  ikan: 0,

  // Pasukan & Pertahanan
  maxStorage: 100,
  gudangLevel: 0,
  happiness: 100,
  maxPasukan: 20,
  currentPasukan: 8,
  woundedPasukan: 0,
  garrisonedPasukan: 4,
  garrisonedHeroes: ['Alexander (SR)'],
  gerbangHp: 500,
  maxGerbangHp: 500,
  damage: 100,

  // Mata Uang Khusus
  gachaTickets: 10,
  dungeonCoins: 120,
  gameCoins: 200,
  shard_r: 10,
  shard_sr: 5,
  shard_ssr: 1,
  shard_ur: 0,

  // Dungeon & Raid State
  currentDungeonLevel: 1,
  monsterHp: 1080,
  monsterMaxHp: 1080,
  autoFarmDungeon: false,
  isDungeonBattleActive: false,
  raidTimer: 180,
  raidLevel: 1,

  // Unlocked Node IDs
  unlockedNodes: [
    'percikan_api', 'memasak_makanan', 'panggil_manusia', 'perkakas_batu',
    'budidaya', 'naluri_berburu', 'town_hall', 'rumah_warga', 'penebang_kayu',
    'pemecah_batu', 'gudang', 'barter_purba', 'tongkat_kayu'
  ],

  // Building Levels
  buildingLevels: {
    town_hall: 1,
    rumah_warga: 1,
    gudang: 1,
    penebang_kayu: 1,
    pemecah_batu: 1
  },

  // Commanders List
  commanders: COMMANDERS_DATABASE,

  // Armory Forged Weapons (IDs of forged weapons)
  forgedWeapons: ['tongkat_kayu'],

  lastSavedTime: Date.now()
};

// ----------------------------------------------------------------------------
// 8. FORMULA COMBAT POWER (CP) TOTAL
// ----------------------------------------------------------------------------
export function calculateCombatPower(state) {
  let cp = 0;

  // Pasukan
  cp += (state.currentPasukan + state.garrisonedPasukan) * 150;
  cp += state.maxGerbangHp * 2;

  // Komandan Dimiliki
  state.commanders.forEach((c) => {
    if (c.owned) {
      cp += (c.baseAtk * (c.stars || 1)) + (c.baseHp * 0.1);
    }
  });

  // Senjata Ditempa
  state.forgedWeapons.forEach((wId) => {
    const w = ARMORY_WEAPONS.find((item) => item.id === wId);
    if (w) cp += (w.atkBonus * 5) + (w.hpBonus * 2);
  });

  // Level Balai Kota
  const thLvl = state.buildingLevels.town_hall || 1;
  cp += thLvl * 5000;

  return Math.floor(cp);
}

// ----------------------------------------------------------------------------
// 9. HELPER FUNCTIONS
// ----------------------------------------------------------------------------
export function formatNumber(num) {
  if (num === null || num === undefined || isNaN(num)) return '0';
  if (num < 1000) return Math.floor(num).toLocaleString('id-ID');
  
  const suffixes = ['', 'K', 'M', 'B', 'T', 'Qa', 'Qi', 'Sx', 'Sp', 'Oc', 'No', 'Dc'];
  const i = Math.floor(Math.log10(Math.max(1, num)) / 3);
  const formatted = (num / Math.pow(10, i * 3)).toFixed(i > 0 ? 1 : 0);
  return `${formatted}${suffixes[Math.min(i, suffixes.length - 1)]}`;
}

export function calculateBuildingCost(building, currentLevel = 0) {
  const mult = Math.pow(building.costMult, currentLevel);
  const result = {};
  for (const [resKey, baseCost] of Object.entries(building.cost)) {
    result[resKey] = Math.floor(baseCost * mult);
  }
  return result;
}
