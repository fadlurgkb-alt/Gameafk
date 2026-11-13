import React from 'react';

/**
 * Authentic Pixel-Art Visual Generator for Shop & Market Items
 * Uses real sliced PNG icons from itch.io packs with seamless SVG fallbacks!
 */
export default function ItemVisual({ id, size = 40, style = {} }) {
  // Map item IDs to real sliced icon PNG files
  const itemIcons = {
    hp_pot: '/assets/sliced/items/fa1.png',
    atk_pot: '/assets/sliced/items/fa10.png',
    wood_100: '/assets/sliced/cut_icons/icon_1_5.png',
    stone_100: '/assets/sliced/cut_icons/icon_2_10.png',
    shard_r: '/assets/sliced/items/fa1000.png',
    ai_core: '/assets/sliced/items/fa1005.png'
  };

  const realSrc = itemIcons[id];

  return (
    <div style={{ display: 'inline-flex', alignItems: 'center', justifyContent: 'center', width: size, height: size }}>
      {realSrc ? (
        <img
          src={realSrc}
          alt={id}
          width={size}
          height={size}
          style={{ imageRendering: 'pixelated', objectFit: 'contain', ...style }}
          onError={(e) => {
            // Fallback if specific icon not found
            e.target.style.display = 'none';
          }}
        />
      ) : (
        <svg viewBox="0 0 40 40" width={size} height={size}>
          <circle cx="20" cy="20" r="14" fill="#F59E0B" />
        </svg>
      )}
    </div>
  );
}
