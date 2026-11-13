/**
 * Global Real Asset Image Cache & Loader
 * Caches HTML5 Image objects for 60fps canvas draw without lag or flickering!
 */

const imageCache = {};

export function getCachedImage(src) {
  if (!src) return null;
  if (imageCache[src]) {
    return imageCache[src].complete && imageCache[src].naturalWidth > 0 ? imageCache[src] : null;
  }

  const img = new Image();
  img.src = src;
  imageCache[src] = img;
  return null;
}

// Preload essential authentic game sprites with 100% COMPLETE architectures!
export const GAME_ASSETS = {
  // Buildings: 100% COMPLETE authentic pixel art architectures!
  town_hall: '/assets/sliced/buildings/building_town_hall.png',
  pertanian: '/assets/sliced/buildings/building_pertanian.png',
  barak_militer: '/assets/sliced/buildings/building_barak_militer.png',
  gudang: '/assets/sliced/buildings/building_gudang.png',
  hospital: '/assets/sliced/buildings/building_hospital.png',
  tavern: '/assets/sliced/buildings/building_tavern.png',
  market: '/assets/sliced/buildings/building_market.png',
  tembok: '/assets/sliced/buildings/building_tembok.png',
  dungeon: '/assets/sliced/buildings/building_dungeon.png',
  pertambangan: '/assets/sliced/buildings/building_pertambangan.png',

  // Trees & Environment
  treeGreen: '/assets/sliced/tiles/tree_green.png',
  treeAutumn: '/assets/sliced/tiles/tree_autumn.png',
  treeGolden: '/assets/sliced/tiles/tree_golden.png',
  treeLush: '/assets/sliced/tiles/tree_lush.png',
  treePine: '/assets/sliced/tiles/tree_pine.png',
  crops: '/assets/sliced/tiles/Spring Crops.png',

  // Animals
  cowBrown: '/assets/sliced/tiles/cow_brown.png',
  chickenRed: '/assets/sliced/tiles/chicken_red.png',

  // Characters & Patrolling Troops (32x32 animated walk cycles)
  soldier0: '/assets/sliced/heroes/troop_soldier_0.png',
  soldier1: '/assets/sliced/heroes/troop_soldier_1.png',
  soldier2: '/assets/sliced/heroes/troop_soldier_2.png',
  soldier3: '/assets/sliced/heroes/troop_soldier_3.png',

  archer0: '/assets/sliced/heroes/troop_archer_0.png',
  archer1: '/assets/sliced/heroes/troop_archer_1.png',
  archer2: '/assets/sliced/heroes/troop_archer_2.png',
  archer3: '/assets/sliced/heroes/troop_archer_3.png',

  leader0: '/assets/sliced/heroes/troop_leader_0.png',
  leader1: '/assets/sliced/heroes/troop_leader_1.png',
  leader2: '/assets/sliced/heroes/troop_leader_2.png',
  leader3: '/assets/sliced/heroes/troop_leader_3.png',

  farmer0: '/assets/sliced/heroes/troop_farmer_0.png',
  farmer1: '/assets/sliced/heroes/troop_farmer_1.png',
  farmer2: '/assets/sliced/heroes/troop_farmer_2.png',
  farmer3: '/assets/sliced/heroes/troop_farmer_3.png',

  // Monsters (Tiny RPG Character Asset Pack 02)
  bloodMonsterIdle: '/assets/sliced/monsters/blood_monster_idle.png',
  bloodMonsterAttack: '/assets/sliced/monsters/blood_monster_attack.png',
  bloodMonsterHurt: '/assets/sliced/monsters/blood_monster_hurt.png',
  bloodMonsterDeath: '/assets/sliced/monsters/blood_monster_death.png',

  demonIdle: '/assets/sliced/monsters/demon_idle.png',
  demonAttack: '/assets/sliced/monsters/demon_attack.png',
  demonHurt: '/assets/sliced/monsters/demon_hurt.png',
  demonDeath: '/assets/sliced/monsters/demon_death.png'
};

// Start preloading immediately in browser
if (typeof window !== 'undefined') {
  Object.values(GAME_ASSETS).forEach((src) => {
    if (src) getCachedImage(src);
  });
}
