import React from 'react';

/**
 * Authentic Pixel-Art Visual Generator for Shop, Market, & Inventory Items
 * Directly loads crisp 32x32 RPG icons from Raven Fantasy Icons!
 */
export default function ItemVisual({ id, size = 40, style = {} }) {
  const itemIcons = {
    // Potions & Consumables
    hp_pot: '/assets/sliced/items/potion_hp.png',
    mana_pot: '/assets/sliced/items/potion_mana.png',
    atk_pot: '/assets/sliced/items/potion_atk.png',
    regen_pot: '/assets/sliced/items/potion_regen.png',

    // Raw Materials & Resources
    wood_100: '/assets/sliced/items/pack_wood.png',
    stone_100: '/assets/sliced/items/pack_stone.png',
    iron_100: '/assets/sliced/items/pack_iron.png',
    wheat_100: '/assets/sliced/items/pack_wheat.png',
    meat_100: '/assets/sliced/items/pack_meat.png',

    // Gems & Cosmic Shards
    shard_r: '/assets/sliced/items/shard_gem_ruby.png',
    shard_ruby: '/assets/sliced/items/shard_gem_ruby.png',
    shard_sapphire: '/assets/sliced/items/shard_gem_sapphire.png',
    shard_emerald: '/assets/sliced/items/shard_gem_emerald.png',
    shard_diamond: '/assets/sliced/items/shard_diamond.png',

    // Ancient Relics
    exp_scroll: '/assets/sliced/items/scroll_exp.png',
    golden_key: '/assets/sliced/items/golden_key.png',
    gold_pouch: '/assets/sliced/items/gold_pouch.png',
    ai_core: '/assets/sliced/items/ai_core.png'
  };

  const realSrc = itemIcons[id] || (id && id.startsWith('/') ? id : '/assets/sliced/items/gold_pouch.png');

  return (
    <div style={{
      display: 'inline-flex',
      alignItems: 'center',
      justifyContent: 'center',
      width: size,
      height: size,
      backgroundColor: 'rgba(15, 23, 42, 0.85)',
      borderRadius: '8px',
      border: '1.5px solid rgba(255, 255, 255, 0.1)',
      overflow: 'hidden',
      flexShrink: 0,
      ...style
    }}>
      <img
        src={realSrc}
        alt={id}
        width={size}
        height={size}
        style={{
          imageRendering: 'pixelated',
          objectFit: 'contain',
          width: '100%',
          height: '100%',
          padding: '2px'
        }}
        onError={(e) => {
          // Never disappear! Fallback to golden coin pouch
          e.target.src = '/assets/sliced/items/gold_pouch.png';
        }}
      />
    </div>
  );
}
