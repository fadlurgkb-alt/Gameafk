import React from 'react';

/**
 * High-End Authentic Pixel-Art Visual Generator for Weapons & Armors
 * Directly uses dedicated authentic equipment PNG icons!
 */
export default function ArmoryVisual({ id, size = 42, style = {} }) {
  const weaponIcons = {
    tongkat_kayu: '/assets/ic_wp_tombak.png',
    kulit_hewan: '/assets/ic_wp_armor_kulit.png',
    pedang_besi: '/assets/ic_wp_pedang.png',
    armor_besi: '/assets/ic_wp_baju_besi.png',
    musket: '/assets/ic_wp_musket.png',
    assault_rifle: '/assets/ic_wp_assault_rifle.png',
    railgun: '/assets/ic_wp_railgun.png',
    exosuit: '/assets/ic_wp_exosuit.png',
    laser_blade: '/assets/ic_wp_laser_blade.png',
    plasma_cannon: '/assets/ic_wp_plasma_cannon.png',
    cyber_body: '/assets/ic_wp_cyber_body.png',
    nano_kirana: '/assets/ic_wp_nano_kirana.png'
  };

  const realSrc = weaponIcons[id] || (id && id.startsWith('/assets') ? id : null);

  return (
    <div style={{
      display: 'inline-flex',
      alignItems: 'center',
      justifyContent: 'center',
      width: size,
      height: size,
      backgroundColor: 'rgba(15, 23, 42, 0.9)',
      borderRadius: '8px',
      border: '1.5px solid rgba(255, 215, 0, 0.25)',
      overflow: 'hidden',
      flexShrink: 0,
      ...style
    }}>
      {realSrc ? (
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
            // Safe fallback if specific icon fails
            e.target.src = '/assets/ic_wp_pedang.png';
          }}
        />
      ) : (
        <span style={{ fontSize: `${Math.round(size * 0.55)}px` }}>⚔️</span>
      )}
    </div>
  );
}
