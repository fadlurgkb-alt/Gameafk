// ============================================================================
// GENESIS PIXEL: IDLE COSMOS - COMPLETE MASTER GAME STATE & REPOSITORY
// Berisi seluruh data, logika, formula, 15+ sumber daya, bangunan, riset,
// filsafat, komandan, dungeon, raid, gerbang pertahanan, dan rumah sakit.
// ============================================================================

export const INITIAL_GAME_STATE = {
  // 15+ Rantai Sumber Daya
  totalZenith: 150.0,
  kayu: 50,
  batu: 50,
  besi: 0,
  emas: 0,
  oil: 0,
  listrik: 0,
  titanium: 0,
  uranium: 0,
  daging: 20,
  berry: 30,
  gandum: 0,
  roti: 0,
  kalengan: 0,
  ikan: 0,

  // Parameter & Kapasitas Kota
  maxStorage: 100,
  gudangLevel: 0,
  happiness: 100,
  maxPasukan: 20,
  currentPasukan: 5,
  woundedPasukan: 0,
  populasi: 10,
  maxPopulasi: 20,
  damage: 25,
  gerbangHp: 500,
  maxGerbangHp: 500,
  garrisonedPasukan: 0,
  garrisonedHeroes: [],

  // Budaya & Bangsa
  selectedNationality: 'Suku Primordial',
  selectedReligions: { 1: 'Pemuja Api' },
  gamePhase: 'main_game', // atau 'prolog_api'
  kerjaPaksaActive: false,

  // Mata Uang & Token Tempur
  gachaTickets: 5,
  dungeonCoins: 50,
  gameCoins: 100,
  shardTokens: 0,
  hpPotions: 3,

  // Progres Dungeon & Raid
  autoFarmDungeon: false,
  currentDungeonLevel: 1,
  maxDungeonLevel: 1,
  monsterHp: 1080,
  monsterMaxHp: 1080,
  playerDungeonHp: 500,
  playerDungeonMaxHp: 500,
  isDungeonActive: false,

  raidTimer: 180,
  raidLevel: 1,
  consecutiveLosses: 0,
  isRaidBattleActive: false,
  raidEnemyHp: 3000,
  raidEnemyMaxHp: 3000,
  raidPlayerHp: 2500,

  // Node & Bangunan yang telah dibuka (ID array)
  unlockedNodes: [
    'percikan_api', 'memasak_makanan', 'panggil_manusia', 'perkakas_batu',
    'budidaya', 'naluri_berburu', 'town_hall', 'rumah_warga', 'penebang_kayu',
    'pemecah_batu', 'gudang'
  ],

  // Tingkat Bangunan: { [nodeId]: level }
  buildingLevels: {
    town_hall: 1,
    rumah_warga: 1,
    gudang: 1,
    penebang_kayu: 1,
    pemecah_batu: 1
  },

  // Komandan Dimiliki: { [commanderId]: { level, stars, owned } }
  commanders: [
    { id: 'c_gorg', name: 'Gorg Si Pemburu Mammoth', rarity: 'Common', stars: 2, level: 3, role: 'Pemburu', bonus: '+25% Daging & Berry', icon: '🪓', owned: true },
    { id: 'c_shaman', name: 'Dukun Batu Kirana', rarity: 'Rare', stars: 2, level: 2, role: 'Spiritual', bonus: '+30% Zenith & Happiness', icon: '🔥', owned: true },
    { id: 'c_richard', name: 'Ksatria Pedang Richard', rarity: 'Epic', stars: 1, level: 1, role: 'Komandan Perang', bonus: '+50% Attack Gerbang', icon: '⚔️', owned: false },
    { id: 'c_katherine', name: 'Dr. Katherine Tesla', rarity: 'Epic', stars: 1, level: 1, role: 'Peneliti', bonus: '+75% Listrik & Riset', icon: '💡', owned: false },
    { id: 'c_archon', name: 'CYBER-ARCHON ZERO', rarity: 'Mythical', stars: 3, level: 5, role: 'Singularity AI', bonus: '+300% Semua Produksi Global', icon: '🤖', owned: false }
  ],

  lastSavedTime: Date.now()
};

// ============================================================================
// DAFTAR LENGKAP BANGUNAN (23 Bangunan Asli)
// ============================================================================
export const ALL_BUILDINGS = [
  {
    id: 'town_hall',
    name: 'Balai Kota (Town Hall)',
    desc: 'Pusat peradaban & penentu zaman. Membuka fitur kebangsaan di level 6.',
    type: 'bangunan',
    tier: 1,
    cost: { kayu: 28, batu: 28 },
    costMult: 1.35,
    zenithPerSec: 5.0,
    icon: '🏛️',
    plotX: 0.50, plotY: 0.48 // Posisi visual di peta
  },
  {
    id: 'rumah_warga',
    name: 'Rumah Rakyat',
    desc: 'Tempat tinggal warga. Meningkatkan kapasitas populasi maksimal +10.',
    type: 'bangunan',
    tier: 1,
    cost: { kayu: 40, batu: 30 },
    costMult: 1.25,
    zenithPerSec: 1.5,
    icon: '🛖',
    plotX: 0.38, plotY: 0.42
  },
  {
    id: 'penebang_kayu',
    name: 'Kamp Pemburu Kayu',
    desc: 'Menebang pohon hutan untuk pasokan kayu gelondongan (+1/s per level).',
    type: 'bangunan',
    tier: 1,
    cost: { kayu: 12 },
    costMult: 1.20,
    produces: 'kayu',
    ratePerSec: 1.0,
    zenithPerSec: 1.0,
    icon: '🪓',
    plotX: 0.22, plotY: 0.35
  },
  {
    id: 'pemecah_batu',
    name: 'Area Penambang Batu',
    desc: 'Menambang batu obsidian & granit perbukitan (+1/s per level).',
    type: 'bangunan',
    tier: 1,
    cost: { batu: 12 },
    costMult: 1.20,
    produces: 'batu',
    ratePerSec: 1.0,
    zenithPerSec: 1.0,
    icon: '⛏️',
    plotX: 0.78, plotY: 0.36
  },
  {
    id: 'gudang',
    name: 'Gudang Logistik',
    desc: 'Menyimpan hasil alam dan bahan baku. Kapasitas penyimpanan +500 per level.',
    type: 'bangunan',
    tier: 1,
    cost: { kayu: 36, batu: 20 },
    costMult: 1.28,
    zenithPerSec: 0.5,
    icon: '📦',
    plotX: 0.62, plotY: 0.42
  },
  {
    id: 'pertanian',
    name: 'Ladang Pertanian',
    desc: 'Menanam gandum sebagai bahan baku roti makanan rakyat (+2/s per level).',
    type: 'bangunan',
    tier: 1,
    cost: { kayu: 48, batu: 17 },
    costMult: 1.22,
    produces: 'gandum',
    ratePerSec: 2.0,
    zenithPerSec: 2.0,
    icon: '🌾',
    plotX: 0.28, plotY: 0.62
  },
  {
    id: 'perkebunan',
    name: 'Kebun Buah & Berry',
    desc: 'Menghasilkan berry manis untuk nutrisi dan obat luka (+2/s per level).',
    type: 'bangunan',
    tier: 1,
    cost: { kayu: 36, batu: 12 },
    costMult: 1.22,
    produces: 'berry',
    ratePerSec: 2.0,
    zenithPerSec: 1.5,
    icon: '🫐',
    plotX: 0.72, plotY: 0.60
  },
  {
    id: 'peternakan',
    name: 'Padang Peternakan',
    desc: 'Memelihara sapi & bison untuk daging segar bergizi (+1/s per level).',
    type: 'bangunan',
    tier: 2,
    cost: { kayu: 60, batu: 36 },
    costMult: 1.25,
    produces: 'daging',
    ratePerSec: 1.0,
    zenithPerSec: 3.0,
    icon: '🥩',
    plotX: 0.18, plotY: 0.52
  },
  {
    id: 'hospital',
    name: 'Balai Pengobatan (Hospital)',
    desc: 'Menyembuhkan prajurit yang terluka pasca raid menggunakan ramuan berry.',
    type: 'bangunan',
    tier: 1,
    cost: { kayu: 150, batu: 100 },
    costMult: 1.30,
    zenithPerSec: 1.0,
    icon: '🏥',
    plotX: 0.35, plotY: 0.28
  },
  {
    id: 'barak_militer',
    name: 'Barak Militer',
    desc: 'Melatih prajurit suku & ksatria benteng. Menambah kapasitas pasukan +20.',
    type: 'bangunan',
    tier: 1,
    cost: { kayu: 40, batu: 20 },
    costMult: 1.26,
    zenithPerSec: 2.5,
    icon: '🛡️',
    plotX: 0.65, plotY: 0.28
  },
  {
    id: 'tembok',
    name: 'Tembok & Gerbang Pertahanan',
    desc: 'Benteng utama penahan raid musuh. Meningkatkan HP Gerbang +250 per level.',
    type: 'bangunan',
    tier: 2,
    cost: { batu: 60, besi: 24 },
    costMult: 1.30,
    zenithPerSec: 1.0,
    icon: '🏰',
    plotX: 0.50, plotY: 0.20
  },
  {
    id: 'peleburan_besi',
    name: 'Peleburan Besi',
    desc: 'Tungku pembakaran bijih besi menjadi batangan logam kokoh (+2/s per level).',
    type: 'bangunan',
    tier: 2,
    cost: { batu: 60, kayu: 60 },
    costMult: 1.28,
    produces: 'besi',
    ratePerSec: 2.0,
    zenithPerSec: 4.0,
    icon: '⛓️',
    plotX: 0.82, plotY: 0.48
  },
  {
    id: 'tambang_emas',
    name: 'Tambang Emas',
    desc: 'Menggali urat emas murni untuk perhiasan, doktrin kuil, dan pasar (+2/s per level).',
    type: 'bangunan',
    tier: 2,
    cost: { besi: 60, kayu: 60 },
    costMult: 1.30,
    produces: 'emas',
    ratePerSec: 2.0,
    zenithPerSec: 8.0,
    icon: '🪙',
    plotX: 0.88, plotY: 0.28
  },
  {
    id: 'market',
    name: 'Pasar Rakyat (Market)',
    desc: 'Pusat perdagangan barter dan toko koin peradaban (+2 Zenith/s).',
    type: 'bangunan',
    tier: 2,
    cost: { kayu: 145, besi: 72 },
    costMult: 1.35,
    zenithPerSec: 10.0,
    icon: '🏪',
    plotX: 0.48, plotY: 0.66
  },
  {
    id: 'temple',
    name: 'Kuil / Katedral (Altar)',
    desc: 'Pusat spiritual peradaban, membuka doktrin agama dan berkah langit.',
    type: 'bangunan',
    tier: 2,
    cost: { batu: 181, emas: 60 },
    costMult: 1.35,
    zenithPerSec: 12.0,
    icon: '⛪',
    plotX: 0.50, plotY: 0.35
  },
  {
    id: 'pelabuhan',
    name: 'Pelabuhan & Dermaga',
    desc: 'Perahu nelayan berlayar menangkap ikan segar bergizi di laut (+2/s per level).',
    type: 'bangunan',
    tier: 2,
    cost: { kayu: 96, batu: 48 },
    costMult: 1.25,
    produces: 'ikan',
    ratePerSec: 2.0,
    zenithPerSec: 5.0,
    icon: '⛵',
    plotX: 0.12, plotY: 0.70
  },
  {
    id: 'dungeon',
    name: 'Portal Dungeon Mistis',
    desc: 'Pintu gerbang menuju sarang monster bawah tanah untuk merebut koin kuno.',
    type: 'bangunan',
    tier: 2,
    cost: { batu: 242, besi: 60 },
    costMult: 1.40,
    zenithPerSec: 15.0,
    icon: '🌀',
    plotX: 0.85, plotY: 0.72
  },
  {
    id: 'tavern',
    name: 'Kedai Pahlawan (Tavern)',
    desc: 'Tempat berkumpulnya petualang hebat. Membuka gacha pahlawan & komandan.',
    type: 'bangunan',
    tier: 2,
    cost: { kayu: 275, besi: 165, emas: 55 },
    costMult: 1.40,
    zenithPerSec: 18.0,
    icon: '🍻',
    plotX: 0.62, plotY: 0.56
  },
  {
    id: 'pengeboran_minyak',
    name: 'Pengeboran Minyak Bumi',
    desc: 'Menghisap minyak bumi untuk bahan bakar mesin industri modern (+5/s per level).',
    type: 'bangunan',
    tier: 3,
    cost: { besi: 242, batu: 121 },
    costMult: 1.32,
    produces: 'oil',
    ratePerSec: 5.0,
    zenithPerSec: 35.0,
    icon: '🛢️',
    plotX: 0.15, plotY: 0.22
  },
  {
    id: 'pembangkit_listrik',
    name: 'Pembangkit Listrik Turbin',
    desc: 'Mengalirkan daya listrik tegangan tinggi ke seluruh kota (+10/s per level).',
    type: 'bangunan',
    tier: 3,
    cost: { besi: 605, emas: 121 },
    costMult: 1.35,
    produces: 'listrik',
    ratePerSec: 10.0,
    zenithPerSec: 80.0,
    icon: '⚡',
    plotX: 0.32, plotY: 0.16
  },
  {
    id: 'tambang_titanium',
    name: 'Tambang Titanium Bawah Tanah',
    desc: 'Mengekstrak logam super-kuat untuk pesawat & armor cyber (+8/s per level).',
    type: 'bangunan',
    tier: 4,
    cost: { listrik: 1210, besi: 1210 },
    costMult: 1.40,
    produces: 'titanium',
    ratePerSec: 8.0,
    zenithPerSec: 250.0,
    icon: '🦾',
    plotX: 0.68, plotY: 0.16
  },
  {
    id: 'reaktor_uranium',
    name: 'Reaktor Fisi Uranium',
    desc: 'Membelah inti atom untuk daya kosmik tak terbatas (+5/s per level).',
    type: 'bangunan',
    tier: 4,
    cost: { titanium: 6050, listrik: 6050 },
    costMult: 1.45,
    produces: 'uranium',
    ratePerSec: 5.0,
    zenithPerSec: 800.0,
    icon: '☢️',
    plotX: 0.86, plotY: 0.18
  }
];

// ============================================================================
// DAFTAR LENGKAP RISET & TEKNOLOGI (25+ Cabang Asli)
// ============================================================================
export const ALL_RESEARCH = [
  { id: 'percikan_api', name: 'Nyalakan Api Pertama', costZenith: 1, reqs: [], desc: 'Satu percikan untuk mengusir kegelapan awal zaman.', tier: 1, icon: '🔥' },
  { id: 'memasak_makanan', name: 'Memasak Makanan', costZenith: 5, reqs: ['percikan_api'], desc: 'Api membuat makanan lebih aman dikonsumsi.', tier: 1, icon: '🍳' },
  { id: 'panggil_manusia', name: 'Panggil Manusia Pertama', costZenith: 10, reqs: ['percikan_api'], desc: 'Manusia pertama hadir membawa akal budi.', tier: 1, icon: '🚶' },
  { id: 'perkakas_batu', name: 'Perkakas Batu', costZenith: 20, reqs: ['memasak_makanan'], desc: 'Pembuatan alat dasar dari batu obsidian & granit.', tier: 1, icon: '🪨' },
  { id: 'budidaya', name: 'Budidaya Semak', costZenith: 108, reqs: ['perkakas_batu'], desc: 'Membuka produksi otomatis 🧺 Berry (+2/s).', tier: 1, icon: '🫐' },
  { id: 'pengasapan_daging', name: 'Pengasapan Daging', costZenith: 324, reqs: ['budidaya'], desc: 'Mengurangi konsumsi makanan warga sebesar 10%.', tier: 1, icon: '🥓' },
  { id: 'pertahanan_primitif', name: 'Pertahanan Primitif', costZenith: 756, reqs: ['pengasapan_daging'], desc: 'Menambah Damage tempur dasar sebesar +50 secara instan.', tier: 1, icon: '🛡️' },
  { id: 'otak_kognitif', name: 'Otak Kognitif', costZenith: 30, reqs: ['panggil_manusia'], desc: 'Evolusi kapasitas berpikir Homo sapiens.', tier: 1, icon: '🧠' },
  { id: 'emosi_jiwa', name: 'Emosi & Jiwa', costZenith: 80, reqs: ['otak_kognitif'], desc: 'Rasa empati dan kasih sayang mempererat kelompok suku.', tier: 1, icon: '❤️' },
  { id: 'moralitas_komunitas', name: 'Moralitas Komunitas', costZenith: 150, reqs: ['emosi_jiwa'], desc: 'Norma sosial mengatur ketertiban masyarakat.', tier: 1, icon: '🤝' },
  { id: 'penyelesaian_masalah', name: 'Penyelesaian Masalah', costZenith: 300, reqs: ['moralitas_komunitas'], desc: 'Perencanaan pembangunan desa secara terstruktur (Syarat Balai Kota).', tier: 1, icon: '💡' },
  { id: 'naluri_berburu', name: 'Naluri Berburu', costZenith: 100, reqs: ['otak_kognitif'], desc: 'Membuka produksi otomatis 🥩 Daging (+1/s).', tier: 1, icon: '🏹' },
  { id: 'domestikasi_hewan', name: 'Domestikasi Hewan', costZenith: 2592, reqs: ['naluri_berburu', 'pertahanan_primitif'], desc: 'Produksi Pertanian & Perkebunan naik +25%.', tier: 2, icon: '🐕' },
  { id: 'seni_memasak', name: 'Seni Memasak Klasik', costZenith: 7560, reqs: ['domestikasi_hewan'], desc: 'Olah Makanan Matang (+2% Zenith Global).', tier: 2, icon: '🍲' },
  { id: 'manufaktur_roti', name: 'Manufaktur Roti', costZenith: 18360, reqs: ['seni_memasak'], desc: 'Gandum diolah menjadi Roti lezat (+30% efisiensi gizi).', tier: 2, icon: '🍞' },
  { id: 'spiritualitas_suku', name: 'Spiritualitas Suku', costZenith: 32400, reqs: ['penyelesaian_masalah'], desc: 'Meningkatkan batas maksimal Kebahagiaan peradaban +15%.', tier: 2, icon: '✨' },
  { id: 'pengalengan_makanan', name: 'Pengalengan Makanan', costZenith: 140400, reqs: ['manufaktur_roti'], desc: 'Pabrik makanan kaleng kedap udara, output melonjak +50%.', tier: 3, icon: '🥫' },
  { id: 'kerja_shift', name: 'Manajemen Kerja Shift', costZenith: 324000, reqs: ['manufaktur_roti'], desc: 'Kecepatan ekstraksi Kayu, Batu, dan Besi naik +30%.', tier: 3, icon: '⏱️' },
  { id: 'metalurgi_militer', name: 'Metalurgi Militer', costZenith: 756000, reqs: ['manufaktur_roti'], desc: 'Diskon 15% bahan logam saat menempa senjata militer.', tier: 3, icon: '🗡️' },
  { id: 'kedaulatan_bangsa', name: 'Kedaulatan Bangsa', costZenith: 3000000, reqs: ['spiritualitas_suku'], desc: 'Membuka Fitur Kebangsaan & Diplomasi Dunia.', tier: 3, icon: '👑' },
  { id: 'overclock_tubuh', name: 'Overclock Tubuh', costZenith: 9720000, reqs: ['pengalengan_makanan', 'kerja_shift'], desc: 'Stimulan medis memicu seluruh produksi bahan baku berlipat 2x.', tier: 3, icon: '⚡' },
  { id: 'logistik_komputer', name: 'Logistik Komputerisasi', costZenith: 25920000, reqs: ['kerja_shift', 'metalurgi_militer'], desc: 'Kapasitas gudang otomatis bertambah +50.000 unit.', tier: 3, icon: '💻' },
  { id: 'drone_swarm', name: 'Drone Swarm Gathering', costZenith: 97200000, reqs: ['metalurgi_militer', 'kedaulatan_bangsa'], desc: 'Kawanan drone memanen otomatis Minyak, Titanium, Uranium +5/s.', tier: 4, icon: '🛸' },
  { id: 'implan_sibernetik', name: 'Implan Sibernetik', costZenith: 324000000, reqs: ['overclock_tubuh'], desc: 'Damage tempur +25.000 dan HP gerbang benteng +5.000.', tier: 4, icon: '🦾' },
  { id: 'nano_repair', name: 'Nano Repair System', costZenith: 1296000000, reqs: ['logistik_komputer'], desc: 'Nanobot menyembuhkan sel tubuh, HP maksimal melonjak +100.000.', tier: 4, icon: '💉' },
  { id: 'brain_link', name: 'Singularity Brain Link', costZenith: 5400000000, reqs: ['drone_swarm', 'implan_sibernetik', 'nano_repair'], desc: 'Kesadaran kosmis menyatu! Seluruh Zenith dikali 5x lipat!', tier: 5, icon: '🌌' }
];

// ============================================================================
// DAFTAR LENGKAP 10 MAZHAB FILSAFAT
// ============================================================================
export const ALL_PHILOSOPHIES = [
  { id: 'barter_purba', name: 'Perdagangan Suku', costZenith: 40, effect: '+50% Pendapatan pasif Zenith', icon: '🐚', tier: 1 },
  { id: 'hukum_kodeks', name: 'Hukum & Tata Pura', costZenith: 1760, effect: 'Konsumsi makanan warga berkurang 10%', icon: '📜', tier: 1 },
  { id: 'monarki', name: 'Monarki Kodoks', costZenith: 4400, effect: 'Batas Kebahagiaan peradaban naik +15%', icon: '👑', tier: 2 },
  { id: 'feodalisme', name: 'Feodalisme', costZenith: 7040, effect: 'Produksi Kayu & Batu meningkat +40%', icon: '🏰', tier: 2 },
  { id: 'meritokrasi', name: 'Meritokrasi', costZenith: 26400, effect: 'Produksi Emas & efisiensi pejabat +100%', icon: '⚖️', tier: 3 },
  { id: 'teokrasi', name: 'Teokrasi', costZenith: 99000, effect: 'Seluruh Damage tempur militer +30%', icon: '📿', tier: 3 },
  { id: 'kapitalisme', name: 'Kapitalisme', costZenith: 330000, effect: 'Pendapatan Zenith per detik melonjak +100%', icon: '💰', tier: 4 },
  { id: 'sosialisme', name: 'Sosialisme', costZenith: 1320000, effect: 'Gizi pangan optimal, kapasitas warga +50', icon: '🚩', tier: 4 },
  { id: 'otoritarianisme', name: 'Otoritarianisme', costZenith: 5500000, effect: 'Produksi Minyak & Listrik berlipat +100%', icon: '🦅', tier: 5 },
  { id: 'transhumanisme', name: 'Transhumanisme', costZenith: 22000000, effect: 'Pengali evolusi peradaban dikali 5x lipat!', icon: '🧬', tier: 5 }
];

// ============================================================================
// DAFTAR BOS DUNGEON RESMI DARI DATABASE GAMEVIEWMODEL
// ============================================================================
export const DUNGEON_MONSTERS = [
  { level: 1, name: 'Serigala Liar', hp: 1080, atk: 48, isBoss: false, icon: '🐺' },
  { level: 5, name: 'Alpha Serigala Purba', hp: 7200, atk: 180, isBoss: true, icon: '🐺' },
  { level: 10, name: 'Raja Goblin Gua', hp: 40500, atk: 720, isBoss: true, icon: '👺' },
  { level: 15, name: 'Panglima Orc Lembah', hp: 162000, atk: 1800, isBoss: true, icon: '👹' },
  { level: 30, name: 'Raja Ogre Berkepala Dua', hp: 765000, atk: 4800, isBoss: true, icon: '🧌' },
  { level: 50, name: 'Raja Iblis Kegelapan', hp: 3150000, atk: 18000, isBoss: true, icon: '👿' },
  { level: 100, name: 'Zenith Primordial Kosmis', hp: 45000000, atk: 260000, isBoss: true, icon: '🐉' }
];

// ============================================================================
// HELPER FORMULA BIAYA & FORMAT ANGKA
// ============================================================================
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
