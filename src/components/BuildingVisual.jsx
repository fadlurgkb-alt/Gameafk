import React from 'react';

// Color Palettes per Era for Consistent High-End Pixel Art
export const ERA_COLORS = {
  primitif: {
    roof: '#92400E',       // Thatch / wood bark
    roofHighlight: '#B45309',
    walls: '#78350F',      // Dark log wood
    wallTrim: '#451A03',
    door: '#3F1A04',
    accent: '#F59E0B',     // Amber campfire glow
    stone: '#78716C',
    banner: '#DC2626'
  },
  kerajaan: {
    roof: '#DC2626',       // Royal Crimson Clay Tiles
    roofHighlight: '#EF4444',
    walls: '#64748B',      // Sturdy Stone Castle Brick
    wallTrim: '#475569',
    door: '#1E293B',       // Heavy iron portcullis
    accent: '#FACC15',     // Gold crest
    stone: '#334155',
    banner: '#FACC15'
  },
  modern: {
    roof: '#0284C7',       // Blue tech roof / solar
    roofHighlight: '#38BDF8',
    walls: '#E2E8F0',      // White Concrete / Glass
    wallTrim: '#94A3B8',
    door: '#0369A1',       // Automatic glass door
    accent: '#10B981',     // Green energy
    stone: '#64748B',
    banner: '#2563EB'
  },
  cyberpunk: {
    roof: '#7E22CE',       // Cyber Purple / Magenta
    roofHighlight: '#C084FC',
    walls: '#0F172A',      // Dark Obsidian Carbon Plate
    wallTrim: '#3B0764',
    door: '#06B6D4',       // Cyan Plasma Door
    accent: '#06B6D4',     // Cyan Neon Glow
    stone: '#1E1B4B',
    banner: '#EC4899'
  }
};

/**
 * Standardized SVG Pixel-Art Visual Component for Any Building.
 * Rendered identically across Building Panels, RPG Modals, and Map!
 */
export default function BuildingVisual({
  buildingId,
  eraId = 'primitif',
  size = 64,
  style = {}
}) {
  const p = ERA_COLORS[eraId] || ERA_COLORS.primitif;

  // Custom Building SVG Silhouette based on Building Type & Era
  let content = null;

  if (buildingId === 'town_hall') {
    content = (
      <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
        {/* Drop shadow */}
        <ellipse cx="32" cy="58" rx="28" ry="5" fill="rgba(0,0,0,0.4)" />
        {/* Foundation */}
        <rect x="6" y="32" width="52" height="26" rx="2" fill={p.walls} stroke={p.wallTrim} strokeWidth="2" />
        {/* Columns / Bricks */}
        <rect x="12" y="34" width="6" height="24" fill={p.wallTrim} />
        <rect x="46" y="34" width="6" height="24" fill={p.wallTrim} />
        {/* Tier 1 Roof */}
        <polygon points="32,8 60,32 4,32" fill={p.roof} stroke={p.wallTrim} strokeWidth="2" />
        <polygon points="32,10 56,30 8,30" fill={p.roofHighlight} opacity="0.6" />
        {/* Grand Arch Door */}
        <path d="M 26 58 L 26 40 A 6 6 0 0 1 38 40 L 38 58 Z" fill={p.door} stroke={p.wallTrim} strokeWidth="1.5" />
        {/* Royal Crest / Flag */}
        <rect x="31" y="2" width="2" height="8" fill={p.accent} />
        <polygon points="33,2 43,5 33,8" fill={p.banner} />
        {/* Windows */}
        <rect x="14" y="20" width="6" height="6" fill={p.accent} opacity="0.9" />
        <rect x="44" y="20" width="6" height="6" fill={p.accent} opacity="0.9" />
      </svg>
    );
  } else if (buildingId === 'barak_militer') {
    content = (
      <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
        <ellipse cx="32" cy="58" rx="26" ry="5" fill="rgba(0,0,0,0.4)" />
        {/* Barracks Base */}
        <rect x="8" y="28" width="48" height="30" fill={p.walls} stroke={p.wallTrim} strokeWidth="2" />
        {/* Fortified Roof */}
        <polygon points="32,12 58,28 6,28" fill={p.roof} stroke={p.wallTrim} strokeWidth="2" />
        {/* Archery Target prop */}
        <circle cx="52" cy="48" r="8" fill="#F8FAFC" stroke="#DC2626" strokeWidth="2" />
        <circle cx="52" cy="48" r="4" fill="#DC2626" />
        {/* Weapon Rack / Crossed Swords */}
        <line x1="14" y1="44" x2="22" y2="52" stroke="#E2E8F0" strokeWidth="2" />
        <line x1="22" y1="44" x2="14" y2="52" stroke="#E2E8F0" strokeWidth="2" />
        {/* Entrance */}
        <rect x="28" y="42" width="10" height="16" fill={p.door} stroke={p.wallTrim} strokeWidth="1.5" />
        {/* Red Barracks Banner */}
        <polygon points="30,8 38,12 30,16" fill="#EF4444" />
        <rect x="29" y="8" width="2" height="10" fill={p.accent} />
      </svg>
    );
  } else if (buildingId === 'gudang') {
    content = (
      <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
        <ellipse cx="32" cy="58" rx="26" ry="5" fill="rgba(0,0,0,0.4)" />
        {/* Vault Barn Structure */}
        <rect x="8" y="26" width="48" height="32" fill={p.walls} stroke={p.wallTrim} strokeWidth="2" />
        {/* Rounded Vault Roof */}
        <path d="M 6 26 Q 32 8 58 26 Z" fill={p.roof} stroke={p.wallTrim} strokeWidth="2" />
        {/* Massive Iron/Wood Vault Doors with Cross Planks */}
        <rect x="24" y="38" width="16" height="20" fill={p.door} stroke={p.wallTrim} strokeWidth="2" />
        <line x1="24" y1="38" x2="40" y2="58" stroke={p.accent} strokeWidth="1.5" />
        <line x1="40" y1="38" x2="24" y2="58" stroke={p.accent} strokeWidth="1.5" />
        {/* Barrel props outside */}
        <rect x="10" y="46" width="10" height="12" rx="2" fill="#B45309" stroke="#451A03" strokeWidth="1" />
        <rect x="46" y="48" width="8" height="10" rx="2" fill="#D97706" stroke="#451A03" strokeWidth="1" />
      </svg>
    );
  } else if (buildingId === 'tembok') {
    content = (
      <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
        <ellipse cx="32" cy="58" rx="28" ry="5" fill="rgba(0,0,0,0.4)" />
        {/* Stone Ramparts */}
        <rect x="4" y="24" width="56" height="34" fill={p.walls} stroke={p.wallTrim} strokeWidth="2" />
        {/* Battlements (Crenellations) */}
        <rect x="4" y="16" width="10" height="8" fill={p.wallTrim} />
        <rect x="18" y="16" width="10" height="8" fill={p.wallTrim} />
        <rect x="36" y="16" width="10" height="8" fill={p.wallTrim} />
        <rect x="50" y="16" width="10" height="8" fill={p.wallTrim} />
        {/* High Portcullis Arch */}
        <path d="M 22 58 L 22 36 A 10 10 0 0 1 42 36 L 42 58 Z" fill="#0F172A" stroke={p.wallTrim} strokeWidth="2" />
        {/* Iron Grate Portcullis */}
        <line x1="26" y1="32" x2="26" y2="58" stroke="#94A3B8" strokeWidth="1.5" />
        <line x1="32" y1="28" x2="32" y2="58" stroke="#94A3B8" strokeWidth="1.5" />
        <line x1="38" y1="32" x2="38" y2="58" stroke="#94A3B8" strokeWidth="1.5" />
        <line x1="22" y1="42" x2="42" y2="42" stroke="#94A3B8" strokeWidth="1.5" />
        <line x1="22" y1="50" x2="42" y2="50" stroke="#94A3B8" strokeWidth="1.5" />
      </svg>
    );
  } else if (buildingId === 'hospital') {
    content = (
      <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
        <ellipse cx="32" cy="58" rx="26" ry="5" fill="rgba(0,0,0,0.4)" />
        {/* Clinic Base */}
        <rect x="10" y="26" width="44" height="32" fill="#F8FAFC" stroke={p.wallTrim} strokeWidth="2" />
        {/* Green/Clay Roof */}
        <polygon points="32,10 58,26 6,26" fill={eraId === 'kerajaan' ? '#059669' : p.roof} stroke={p.wallTrim} strokeWidth="2" />
        {/* Prominent Red Medical Cross */}
        <rect x="29" y="32" width="6" height="16" fill="#EF4444" />
        <rect x="24" y="37" width="16" height="6" fill="#EF4444" />
        {/* Door */}
        <rect x="28" y="48" width="8" height="10" fill={p.door} />
        {/* Window with sunlight */}
        <rect x="14" y="34" width="6" height="6" fill="#38BDF8" opacity="0.8" />
        <rect x="44" y="34" width="6" height="6" fill="#38BDF8" opacity="0.8" />
      </svg>
    );
  } else if (buildingId === 'tavern') {
    content = (
      <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
        <ellipse cx="32" cy="58" rx="26" ry="5" fill="rgba(0,0,0,0.4)" />
        <rect x="8" y="28" width="48" height="30" fill={p.walls} stroke={p.wallTrim} strokeWidth="2" />
        <polygon points="32,12 56,28 8,28" fill={p.roof} stroke={p.wallTrim} strokeWidth="2" />
        {/* Hanging Lantern with warm light */}
        <circle cx="16" cy="38" r="4" fill="#FACC15" />
        <circle cx="16" cy="38" r="7" fill="#FACC15" opacity="0.25" />
        {/* Tavern Sign Board with Beer Mug */}
        <rect x="46" y="32" width="10" height="8" rx="1" fill="#78350F" stroke="#FACC15" strokeWidth="1" />
        <text x="48" y="38" fontSize="6" fill="#FACC15">🍺</text>
        {/* Double Dutch Door */}
        <rect x="26" y="40" width="12" height="18" fill={p.door} stroke={p.wallTrim} strokeWidth="1.5" />
      </svg>
    );
  } else if (buildingId === 'dungeon') {
    content = (
      <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
        <ellipse cx="32" cy="58" rx="26" ry="5" fill="rgba(0,0,0,0.4)" />
        {/* Dark Obsidian Spire Pillars */}
        <polygon points="12,18 20,58 6,58" fill="#1E1B4B" stroke="#4C1D95" strokeWidth="2" />
        <polygon points="52,18 58,58 44,58" fill="#1E1B4B" stroke="#4C1D95" strokeWidth="2" />
        {/* Glowing Swirling Void Center */}
        <circle cx="32" cy="38" r="16" fill="#4C1D95" stroke="#8B5CF6" strokeWidth="2" />
        <circle cx="32" cy="38" r="10" fill="#7C3AED" />
        <circle cx="32" cy="38" r="5" fill="#C084FC" />
        {/* Skull rune at top */}
        <text x="27" y="16" fontSize="11" fill="#C084FC">💀</text>
      </svg>
    );
  } else if (buildingId === 'market') {
    content = (
      <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
        <ellipse cx="32" cy="58" rx="26" ry="5" fill="rgba(0,0,0,0.4)" />
        {/* Merchant Stall Counter */}
        <rect x="8" y="38" width="48" height="20" fill={p.walls} stroke={p.wallTrim} strokeWidth="2" />
        {/* Striped Canopy Awning */}
        <polygon points="4,22 60,22 52,36 12,36" fill="#DC2626" stroke={p.wallTrim} strokeWidth="1.5" />
        <polygon points="16,22 28,22 24,36 12,36" fill="#F8FAFC" />
        <polygon points="36,22 48,22 44,36 32,36" fill="#F8FAFC" />
        {/* Crates & Scales */}
        <rect x="12" y="44" width="8" height="8" fill="#D97706" />
        <rect x="44" y="44" width="8" height="8" fill="#F59E0B" />
        <text x="26" y="52" fontSize="9" fill="#FACC15">⚖️</text>
      </svg>
    );
  } else if (buildingId === 'pertanian') {
    content = (
      <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
        <ellipse cx="32" cy="58" rx="28" ry="5" fill="rgba(0,0,0,0.4)" />
        {/* Farm Soil Beds */}
        <rect x="6" y="36" width="52" height="22" rx="3" fill="#451A03" stroke="#78350F" strokeWidth="2" />
        {/* Golden Wheat Rows */}
        <line x1="10" y1="42" x2="54" y2="42" stroke="#FBBF24" strokeWidth="3" strokeDasharray="4 2" />
        <line x1="10" y1="48" x2="54" y2="48" stroke="#F59E0B" strokeWidth="3" strokeDasharray="4 2" />
        <line x1="10" y1="54" x2="54" y2="54" stroke="#D97706" strokeWidth="3" strokeDasharray="4 2" />
        {/* Cute Windmill / Silo in background */}
        <rect x="24" y="16" width="16" height="20" fill={p.walls} stroke={p.wallTrim} strokeWidth="1.5" />
        <polygon points="32,6 42,16 22,16" fill={p.roof} stroke={p.wallTrim} strokeWidth="1" />
        <line x1="32" y1="16" x2="20" y2="10" stroke="#F8FAFC" strokeWidth="1.5" />
        <line x1="32" y1="16" x2="44" y2="22" stroke="#F8FAFC" strokeWidth="1.5" />
      </svg>
    );
  } else if (buildingId === 'pertambangan') {
    content = (
      <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
        <ellipse cx="32" cy="58" rx="26" ry="5" fill="rgba(0,0,0,0.4)" />
        {/* Rocky Cliff Entrance */}
        <polygon points="32,10 60,56 4,56" fill="#57534E" stroke="#292524" strokeWidth="2" />
        <polygon points="32,14 54,54 10,54" fill="#78716C" opacity="0.6" />
        {/* Dark Mine Cave Hole */}
        <ellipse cx="32" cy="46" rx="12" ry="10" fill="#0C0A09" />
        {/* Minecart Rails & Cart */}
        <line x1="20" y1="56" x2="44" y2="56" stroke="#A8A29E" strokeWidth="2" />
        <rect x="26" y="48" width="12" height="8" rx="1" fill="#44403C" stroke="#A8A29E" strokeWidth="1" />
        {/* Ore Glint */}
        <circle cx="30" cy="49" r="1.5" fill="#FACC15" />
        <circle cx="34" cy="50" r="1.5" fill="#38BDF8" />
      </svg>
    );
  } else {
    // Universal Default Building
    content = (
      <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
        <ellipse cx="32" cy="58" rx="26" ry="5" fill="rgba(0,0,0,0.4)" />
        <rect x="10" y="28" width="44" height="30" rx="2" fill={p.walls} stroke={p.wallTrim} strokeWidth="2" />
        <polygon points="32,10 58,28 6,28" fill={p.roof} stroke={p.wallTrim} strokeWidth="2" />
        <rect x="27" y="42" width="10" height="16" fill={p.door} stroke={p.wallTrim} strokeWidth="1" />
        <rect x="16" y="34" width="6" height="6" fill={p.accent} />
        <rect x="42" y="34" width="6" height="6" fill={p.accent} />
      </svg>
    );
  }

  return (
    <div style={{ display: 'inline-flex', alignItems: 'center', justifyContent: 'center' }}>
      {content}
    </div>
  );
}
