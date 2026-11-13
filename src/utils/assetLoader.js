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

// Preload essential authentic game sprites with 100% COMPLETE architectures!
export const GAME_ASSETS = {
  // Buildings: Only use 100% COMPLETE houses, never partial chopped tiles!
  town_hall: '/assets/sliced/buildings/farm_house_complete_a.png',
  pertanian: '/assets/sliced/buildings/farm_house_complete_b.png',
  // Other buildings use the rich handcrafted pixel architecture to ensure full walls, roofs, and doors
  gudang: null,
  barak_militer: null,
  tembok: null,
  hospital: null,
  tavern: null,
  market: null,

  // Environment & Tiles
  tree: '/assets/sliced/tiles/maple_tree_crop_0.png',
  crops: '/assets/sliced/tiles/Spring Crops.png',

  // Characters & Heroes
  farmerHero: '/assets/sliced/heroes/farmer_hero_idle_0.png',

  // Monsters
  bloodMonster: '/assets/sliced/monsters/blood_monster_frame_0.png'
};

// Start preloading immediately in browser
if (typeof window !== 'undefined') {
  Object.values(GAME_ASSETS).forEach((src) => {
    if (src) getCachedImage(src);
  });
}
