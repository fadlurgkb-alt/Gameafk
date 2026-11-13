import React from 'react';

/**
 * Authentic Pixel-Art Visual Generator for all 12 Armory Weapons & Armors
 * Uses real sliced PNG weapon/armor icons from Raven Fantasy Icons!
 */
export default function ArmoryVisual({ id, size = 40, style = {} }) {
  const weaponIcons = {
    tongkat_kayu: '/assets/sliced/items/fa100.png',
    kulit_hewan: '/assets/sliced/items/fa101.png',
    pedang_besi: '/assets/sliced/items/fa102.png',
    armor_besi: '/assets/sliced/items/fa103.png',
    musket: '/assets/sliced/items/fa104.png',
    assault_rifle: '/assets/sliced/items/fa105.png',
    railgun: '/assets/sliced/items/fa106.png',
    exosuit: '/assets/sliced/items/fa107.png',
    laser_blade: '/assets/sliced/items/fa108.png',
    plasma_cannon: '/assets/sliced/items/fa109.png',
    cyber_body: '/assets/sliced/items/fa110.png',
    nano_kirana: '/assets/sliced/items/fa111.png'
  };

  const realSrc = weaponIcons[id];

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
            e.target.style.display = 'none';
          }}
        />
      ) : (
        <svg viewBox="0 0 40 40" width={size} height={size}>
          <circle cx="20" cy="20" r="14" fill="#3B82F6" />
        </svg>
      )}
    </div>
  );
}
