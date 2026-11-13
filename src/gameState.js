export const ERAS = [
  { id: 'primitif', name: 'Zaman Batu', img: '/assets/img_era_primitif.png', minZenith: 0, color: '#FFB300' },
  { id: 'kerajaan', name: 'Zaman Kerajaan', img: '/assets/img_era_kerajaan.png', minZenith: 5000, color: '#00E5FF' },
  { id: 'modern', name: 'Zaman Modern', img: '/assets/img_era_modern.png', minZenith: 50000, color: '#00E676' },
  { id: 'cyberpunk', name: 'Era Cyberpunk', img: '/assets/img_era_cyberpunk.png', minZenith: 500000, color: '#B388FF' }
];

export const INITIAL_RESOURCES = {
  zenith: 150,
  batu: 50,
  kayu: 50,
  daging: 30,
  besi: 0
};

export const INITIAL_NODES = [
  // Era Primitif
  {
    id: 'batu_tenda',
    eraId: 'primitif',
    name: 'Gua & Tenda Primitif',
    desc: 'Tempat berteduh para pemburu awal. Menghasilkan Zenith & Daging.',
    type: 'bangunan',
    level: 1,
    baseCostZenith: 20,
    costMultiplier: 1.15,
    zenithPerSec: 1.5,
    resourceType: 'daging',
    resourcePerSec: 0.8,
    unlocked: true,
    maxLevel: 100,
    icon: '⛺'
  },
  {
    id: 'batu_pemburu',
    eraId: 'primitif',
    name: 'Kelompok Pemburu',
    desc: 'Berburu mammoth dan bison di padang rumput.',
    type: 'bangunan',
    level: 0,
    baseCostZenith: 75,
    costMultiplier: 1.18,
    zenithPerSec: 4.0,
    resourceType: 'daging',
    resourcePerSec: 2.5,
    unlocked: true,
    maxLevel: 100,
    icon: '🏹'
  },
  {
    id: 'batu_tambang',
    eraId: 'primitif',
    name: 'Galian Batu Obsidian',
    desc: 'Menghancurkan batu gunung untuk perkakas tajam.',
    type: 'bangunan',
    level: 0,
    baseCostZenith: 150,
    costMultiplier: 1.20,
    zenithPerSec: 8.0,
    resourceType: 'batu',
    resourcePerSec: 4.0,
    unlocked: true,
    maxLevel: 100,
    icon: '⛏️'
  },
  {
    id: 'batu_penebang',
    eraId: 'primitif',
    name: 'Penebang Hutan Liar',
    desc: 'Mengumpulkan kayu gelondongan untuk konstruksi.',
    type: 'bangunan',
    level: 0,
    baseCostZenith: 300,
    costMultiplier: 1.22,
    zenithPerSec: 15.0,
    resourceType: 'kayu',
    resourcePerSec: 6.0,
    unlocked: true,
    maxLevel: 100,
    icon: '🪓'
  },
  {
    id: 'riset_api',
    eraId: 'primitif',
    name: 'Penemuan Api Unggun',
    desc: 'Riset penting untuk menghangatkan suku dan memasak.',
    type: 'riset',
    level: 0,
    baseCostZenith: 500,
    costMultiplier: 2.0,
    zenithPerSec: 25.0,
    resourceType: null,
    resourcePerSec: 0,
    unlocked: true,
    maxLevel: 10,
    icon: '🔥'
  },
  {
    id: 'riset_roda',
    eraId: 'primitif',
    name: 'Roda Batu Pengangkut',
    desc: 'Mempercepat distribusi hasil sumber daya.',
    type: 'riset',
    level: 0,
    baseCostZenith: 1200,
    costMultiplier: 2.2,
    zenithPerSec: 60.0,
    resourceType: null,
    resourcePerSec: 0,
    unlocked: true,
    maxLevel: 10,
    icon: '⚙️'
  },

  // Era Kerajaan
  {
    id: 'kerajaan_istana',
    eraId: 'kerajaan',
    name: 'Kastil & Balai Kota',
    desc: 'Pusat pemerintahan feodal dan upeti rakyat.',
    type: 'bangunan',
    level: 0,
    baseCostZenith: 5500,
    costMultiplier: 1.25,
    zenithPerSec: 250.0,
    resourceType: 'besi',
    resourcePerSec: 15.0,
    unlocked: false,
    maxLevel: 100,
    icon: '🏰'
  },
  {
    id: 'kerajaan_barak',
    eraId: 'kerajaan',
    name: 'Barak Prajurit Pedang',
    desc: 'Melatih ksatria pelindung peradaban dari serangan monster.',
    type: 'bangunan',
    level: 0,
    baseCostZenith: 10000,
    costMultiplier: 1.26,
    zenithPerSec: 500.0,
    resourceType: 'besi',
    resourcePerSec: 30.0,
    unlocked: false,
    maxLevel: 100,
    icon: '⚔️'
  },
  {
    id: 'kerajaan_ladang',
    eraId: 'kerajaan',
    name: 'Ladang Gandum & Peternakan',
    desc: 'Pertanian terstruktur penopang populasi besar.',
    type: 'bangunan',
    level: 0,
    baseCostZenith: 22000,
    costMultiplier: 1.28,
    zenithPerSec: 1200.0,
    resourceType: 'daging',
    resourcePerSec: 80.0,
    unlocked: false,
    maxLevel: 100,
    icon: '🌾'
  },

  // Era Modern
  {
    id: 'modern_pabrik',
    eraId: 'modern',
    name: 'Kompleks Pabrik Uap & Baja',
    desc: 'Industrialisasi massal memicu revolusi ekonomi.',
    type: 'bangunan',
    level: 0,
    baseCostZenith: 60000,
    costMultiplier: 1.30,
    zenithPerSec: 3500.0,
    resourceType: 'besi',
    resourcePerSec: 200.0,
    unlocked: false,
    maxLevel: 100,
    icon: '🏭'
  },
  {
    id: 'modern_laboratorium',
    eraId: 'modern',
    name: 'Pusat Riset Nuklir & Listrik',
    desc: 'Energi mutakhir untuk percepatan peradaban.',
    type: 'riset',
    level: 0,
    baseCostZenith: 150000,
    costMultiplier: 1.35,
    zenithPerSec: 9000.0,
    resourceType: null,
    resourcePerSec: 0,
    unlocked: false,
    maxLevel: 100,
    icon: '⚡'
  },

  // Era Cyberpunk
  {
    id: 'cyber_ai',
    eraId: 'cyberpunk',
    name: 'Quantum AI Core Nexus',
    desc: 'Kecerdasan buatan otonom mengelola seluruh kosmos.',
    type: 'bangunan',
    level: 0,
    baseCostZenith: 600000,
    costMultiplier: 1.40,
    zenithPerSec: 40000.0,
    resourceType: 'besi',
    resourcePerSec: 1000.0,
    unlocked: false,
    maxLevel: 100,
    icon: '🔮'
  },
  {
    id: 'cyber_dyson',
    eraId: 'cyberpunk',
    name: 'Dyson Swarm Harvester',
    desc: 'Menghisap energi bintang untuk Zenith tak terbatas.',
    type: 'bangunan',
    level: 0,
    baseCostZenith: 2500000,
    costMultiplier: 1.45,
    zenithPerSec: 180000.0,
    resourceType: null,
    resourcePerSec: 0,
    unlocked: false,
    maxLevel: 100,
    icon: '🌌'
  }
];

export const INITIAL_COMMANDERS = [
  { id: 'c1', name: 'Gorg si Pemburu Mammoth', rarity: 'Common', bonus: '+15% Daging', icon: '🪓', level: 1, owned: true },
  { id: 'c2', name: 'Dukun Batu Api', rarity: 'Rare', bonus: '+25% Zenith', icon: '🔥', level: 1, owned: true },
  { id: 'c3', name: 'Panglima Richard', rarity: 'Epic', bonus: '+50% Serangan Militer', icon: '🛡️', level: 0, owned: false },
  { id: 'c4', name: 'Dr. Katherine Tesla', rarity: 'Epic', bonus: '+75% Riset & Listrik', icon: '💡', level: 0, owned: false },
  { id: 'c5', name: 'CYBER-ARCHON ZERO', rarity: 'Mythical', bonus: '+200% Semua Produksi', icon: '🤖', level: 0, owned: false }
];

export function formatNumber(num) {
  if (num === null || num === undefined || isNaN(num)) return '0';
  if (num < 1000) return Math.floor(num).toLocaleString('id-ID');
  
  const suffixes = ['', 'K', 'M', 'B', 'T', 'Qa', 'Qi', 'Sx', 'Sp', 'Oc', 'No', 'Dc'];
  const i = Math.floor(Math.log10(Math.max(1, num)) / 3);
  const formatted = (num / Math.pow(10, i * 3)).toFixed(i > 0 ? 1 : 0);
  return `${formatted}${suffixes[Math.min(i, suffixes.length - 1)]}`;
}

export function calculateNodeCost(node) {
  return Math.floor(node.baseCostZenith * Math.pow(node.costMultiplier, node.level));
}
