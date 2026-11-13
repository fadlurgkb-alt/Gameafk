/**
 * Global Real Asset Image Cache & Loader
 * Caches HTML5 Image objects for 60fps canvas draw without lag or flickering!
 */

const imageCache = {};

export function getCachedImage(src) {
  if (!src) return null;
  if (imageCache[src]) {
    return imageCache[src].complete ? imageCache[src] : null;
  }

  const img = new Image();
  img.src = src;
  imageCache[src] = img;
  return null;
}

// Preload essential authentic game sprites
export const GAME_ASSETS = {
  // Buildings (Real Farmhouse variations)
  town_hall: '/assets/sliced/buildings/farm_house_0.png',
  gudang: '/assets/sliced/buildings/farm_house_1.png',
  barak_militer: '/assets/sliced/buildings/farm_house_2.png',
  tembok: '/assets/sliced/buildings/farm_house_3.png',
  hospital: '/assets/sliced/buildings/farm_house_4.png',
  tavern: '/assets/sliced/buildings/farm_house_5.png',
  market: '/assets/sliced/buildings/farm_house_6.png',
  pertanian: '/assets/sliced/buildings/farm_house_7.png',

  // Environment & Tiles
  tree: '/assets/sliced/tiles/maple_tree_crop_0.png',
  treeAlt: '/assets/sliced/tiles/maple_tree_crop_1.png',
  crops: '/assets/sliced/tiles/Spring Crops.png',
  fence: "/assets/sliced/tiles/Fence's copiar.png",
  road: '/assets/sliced/tiles/Road copiar.png',

  // Characters & Heroes
  farmerHero: '/assets/sliced/heroes/farmer_hero_idle_0.png',
  heroIdle1: '/assets/sliced/heroes/farmer_hero_idle_1.png',

  // Monsters
  bloodMonster: '/assets/sliced/monsters/blood_monster_frame_0.png',
  bloodMonster1: '/assets/sliced/monsters/blood_monster_frame_1.png',
  bloodMonster2: '/assets/sliced/monsters/blood_monster_frame_2.png',
  demon: '/assets/sliced/monsters/Demon_A.png'
};

// Start preloading immediately in browser
if (typeof window !== 'undefined') {
  Object.values(GAME_ASSETS).forEach((src) => {
    getCachedImage(src);
  });
}
