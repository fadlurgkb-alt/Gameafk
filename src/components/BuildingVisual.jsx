import React from 'react';

/**
 * High-End Authentic 2000s Retro Farming & Village Pixel Art Building Renderer
 * Styled with warm wood planks, layered roof shingles, cobblestone base,
 * glowing cozy windows, brick chimneys with smoke, and rustic accents!
 */
export const ERA_COLORS = {
  primitif: {
    roof: '#B45309',         // Warm rustic wood / thatch
    roofDark: '#78350F',     // Dark wood shingle shadow
    roofLight: '#F59E0B',    // Golden straw highlight
    walls: '#854D0E',        // Cozy wooden log planks
    wallsDark: '#542807',    // Timber beam shadow
    cobble: '#78716C',       // Riverstone foundation
    cobbleDark: '#44403C',
    door: '#451A03',
    windowGlow: '#FEF08A',   // Warm cozy lantern light
    windowFrame: '#3F1A04',
    chimney: '#991B1B',      // Red brick chimney
    accent: '#22C55E'        // Ivy / Flower greenery
  },
  kerajaan: {
    roof: '#DC2626',         // Terracotta Red Castle Shingles
    roofDark: '#991B1B',     // Deep brick shadow
    roofLight: '#F87171',    // Sunlit roof ridge
    walls: '#64748B',        // Sturdy Chiseled Stone Brick
    wallsDark: '#334155',    // Mortar groove shadow
    cobble: '#475569',
    cobbleDark: '#1E293B',
    door: '#1E293B',         // Heavy oak with iron band
    windowGlow: '#FDE047',
    windowFrame: '#0F172A',
    chimney: '#7F1D1D',
    accent: '#FACC15'        // Royal gold crest
  },
  modern: {
    roof: '#0284C7',         // Solar blue tile
    roofDark: '#0369A1',
    roofLight: '#38BDF8',
    walls: '#CBD5E1',        // Concrete brick
    wallsDark: '#64748B',
    cobble: '#475569',
    cobbleDark: '#334155',
    door: '#0284C7',
    windowGlow: '#67E8F9',
    windowFrame: '#1E293B',
    chimney: '#475569',
    accent: '#10B981'
  },
  cyberpunk: {
    roof: '#7E22CE',         // Cybernetic neon canopy
    roofDark: '#3B0764',
    roofLight: '#C084FC',
    walls: '#0F172A',        // Dark alloy plating
    wallsDark: '#020617',
    cobble: '#1E1B4B',
    cobbleDark: '#0F0E2A',
    door: '#06B6D4',
    windowGlow: '#06B6D4',
    windowFrame: '#3B0764',
    chimney: '#4C1D95',
    accent: '#EC4899'
  }
};

export default function BuildingVisual({
  buildingId,
  eraId = 'primitif',
  size = 64,
  style = {}
}) {
  const p = ERA_COLORS[eraId] || ERA_COLORS.primitif;

  // 1. TOWN HALL / BALAI DESA (Grand 2-Story Farmhouse / Village Manor)
  if (buildingId === 'town_hall') {
    return (
      <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
        {/* Soft Isometric Ground Shadow */}
        <ellipse cx="32" cy="58" rx="28" ry="5" fill="rgba(0,0,0,0.45)" />

        {/* Chimney with Smoke Puff */}
        <rect x="44" y="6" width="6" height="14" fill={p.chimney} />
        <rect x="43" y="5" width="8" height="2" fill={p.roofDark} />
        <circle cx="48" cy="2" r="3" fill="#E2E8F0" opacity="0.75" />
        <circle cx="51" cy="-2" r="2.5" fill="#E2E8F0" opacity="0.5" />

        {/* Stone Cobblestone Base Foundation */}
        <rect x="8" y="46" width="48" height="12" fill={p.cobble} />
        <line x1="8" y1="52" x2="56" y2="52" stroke={p.cobbleDark} strokeWidth="1" strokeDasharray="4 2" />
        <rect x="8" y="46" width="48" height="2" fill={p.cobbleDark} />

        {/* Wooden Plank Wall Body (2nd Floor & 1st Floor) */}
        <rect x="10" y="24" width="44" height="23" fill={p.walls} />
        {/* Horizontal Wood Plank Lines */}
        <line x1="10" y1="29" x2="54" y2="29" stroke={p.wallsDark} strokeWidth="1" />
        <line x1="10" y1="35" x2="54" y2="35" stroke={p.wallsDark} strokeWidth="1" />
        <line x1="10" y1="41" x2="54" y2="41" stroke={p.wallsDark} strokeWidth="1" />

        {/* Side Timber Corner Beams */}
        <rect x="8" y="24" width="4" height="24" fill={p.wallsDark} />
        <rect x="52" y="24" width="4" height="24" fill={p.wallsDark} />

        {/* Layered Rustic Roof with Shingle Rows */}
        <polygon points="32,6 58,24 6,24" fill={p.roof} />
        {/* Roof Shingle Texture Rows */}
        <line x1="13" y1="20" x2="51" y2="20" stroke={p.roofDark} strokeWidth="1.5" />
        <line x1="20" y1="15" x2="44" y2="15" stroke={p.roofDark} strokeWidth="1.5" />
        <line x1="27" y1="10" x2="37" y2="10" stroke={p.roofDark} strokeWidth="1.5" />
        {/* Roof Ridge Light */}
        <line x1="32" y1="6" x2="32" y2="24" stroke={p.roofLight} strokeWidth="1" />
        <polygon points="32,6 35,8 6,24 6,23" fill={p.roofLight} opacity="0.6" />

        {/* Dormer Window on Attic Roof */}
        <rect x="27" y="12" width="10" height="9" fill={p.walls} />
        <polygon points="32,9 38,13 26,13" fill={p.roofDark} />
        <rect x="29" y="14" width="6" height="5" fill={p.windowGlow} />
        <line x1="32" y1="14" x2="32" y2="19" stroke={p.windowFrame} strokeWidth="0.8" />
        <line x1="29" y1="16.5" x2="35" y2="16.5" stroke={p.windowFrame} strokeWidth="0.8" />

        {/* Arched Cozy Wooden Door with Iron Straps */}
        <rect x="28" y="42" width="9" height="15" fill={p.door} rx="2" />
        <line x1="28" y1="46" x2="37" y2="46" stroke="#1E293B" strokeWidth="1" />
        <line x1="28" y1="52" x2="37" y2="52" stroke="#1E293B" strokeWidth="1" />
        <circle cx="35" cy="50" r="1" fill="#FACC15" /> {/* Brass knob */}

        {/* Windows with Warm Golden Glowing Light */}
        <rect x="14" y="32" width="8" height="8" fill={p.windowGlow} />
        <rect x="14" y="32" width="8" height="8" fill="none" stroke={p.windowFrame} strokeWidth="1" />
        <line x1="18" y1="32" x2="18" y2="40" stroke={p.windowFrame} strokeWidth="0.8" />
        <line x1="14" y1="36" x2="22" y2="36" stroke={p.windowFrame} strokeWidth="0.8" />
        {/* Flower Box under window */}
        <rect x="13" y="40" width="10" height="2.5" fill="#78350F" />
        <circle cx="15" cy="40" r="1.5" fill="#EF4444" />
        <circle cx="18" cy="40" r="1.5" fill="#FACC15" />
        <circle cx="21" cy="40" r="1.5" fill="#3B82F6" />

        {/* Right Window */}
        <rect x="42" y="32" width="8" height="8" fill={p.windowGlow} />
        <rect x="42" y="32" width="8" height="8" fill="none" stroke={p.windowFrame} strokeWidth="1" />
        <line x1="46" y1="32" x2="46" y2="40" stroke={p.windowFrame} strokeWidth="0.8" />
        <line x1="42" y1="36" x2="50" y2="36" stroke={p.windowFrame} strokeWidth="0.8" />
        <rect x="41" y="40" width="10" height="2.5" fill="#78350F" />
        <circle cx="43" cy="40" r="1.5" fill="#EF4444" />
        <circle cx="46" cy="40" r="1.5" fill="#FACC15" />
        <circle cx="49" cy="40" r="1.5" fill="#3B82F6" />

        {/* Wooden Porch Steps & Hanging Lantern */}
        <rect x="25" y="56" width="15" height="2" fill={p.wallsDark} />
        <rect x="26" y="54" width="13" height="2" fill={p.walls} />
        <circle cx="25" cy="44" r="2" fill="#FACC15" /> {/* Lantern glow */}
      </svg>
    );
  }

  // 2. LUMBUNG PANGAN / GUDANG (Classic Red Rustic Barn & Silo)
  if (buildingId === 'lumbung_pangan' || buildingId === 'gudang') {
    return (
      <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
        <ellipse cx="32" cy="58" rx="27" ry="5" fill="rgba(0,0,0,0.45)" />

        {/* Silo on the Left (Round Tower with Metal Dome) */}
        <rect x="8" y="16" width="14" height="40" fill="#94A3B8" />
        <line x1="8" y1="24" x2="22" y2="24" stroke="#64748B" strokeWidth="1" />
        <line x1="8" y1="34" x2="22" y2="34" stroke="#64748B" strokeWidth="1" />
        <line x1="8" y1="44" x2="22" y2="44" stroke="#64748B" strokeWidth="1" />
        {/* Silo Dome Roof */}
        <path d="M 8 16 Q 15 8 22 16 Z" fill="#DC2626" />
        <rect x="13" y="6" width="4" height="4" fill="#64748B" />

        {/* Classic Rustic Barn Body */}
        <rect x="22" y="24" width="36" height="32" fill="#B91C1C" />
        {/* Vertical Board & Batten Lines */}
        <line x1="28" y1="24" x2="28" y2="56" stroke="#991B1B" strokeWidth="1" />
        <line x1="34" y1="24" x2="34" y2="56" stroke="#991B1B" strokeWidth="1" />
        <line x1="40" y1="24" x2="40" y2="56" stroke="#991B1B" strokeWidth="1" />
        <line x1="46" y1="24" x2="46" y2="56" stroke="#991B1B" strokeWidth="1" />
        <line x1="52" y1="24" x2="52" y2="56" stroke="#991B1B" strokeWidth="1" />

        {/* Gambrel Barn Roof */}
        <polygon points="40,8 59,20 60,25 20,25 21,20" fill="#78350F" />
        <polygon points="40,9 58,20 22,20" fill="#92400E" />

        {/* Hay Loft Door on 2nd Floor with Pulley Beam */}
        <rect x="36" y="27" width="8" height="8" fill="#451A03" />
        <line x1="36" y1="27" x2="44" y2="35" stroke="#FFFFFF" strokeWidth="0.8" />
        <line x1="44" y1="27" x2="36" y2="35" stroke="#FFFFFF" strokeWidth="0.8" />
        <line x1="40" y1="20" x2="40" y2="26" stroke="#FBBF24" strokeWidth="1.5" /> {/* Pulley rope */}

        {/* Large Sliding Barn Doors with 'X' Cross Braces */}
        <rect x="30" y="40" width="20" height="16" fill="#F8FAFC" />
        <rect x="32" y="42" width="7" height="13" fill="#B91C1C" />
        <line x1="32" y1="42" x2="39" y2="55" stroke="#F8FAFC" strokeWidth="1.5" />
        <line x1="39" y1="42" x2="32" y2="55" stroke="#F8FAFC" strokeWidth="1.5" />

        <rect x="41" y="42" width="7" height="13" fill="#B91C1C" />
        <line x1="41" y1="42" x2="48" y2="55" stroke="#F8FAFC" strokeWidth="1.5" />
        <line x1="48" y1="42" x2="41" y2="55" stroke="#F8FAFC" strokeWidth="1.5" />

        {/* Hay Bale Prop on right */}
        <rect x="52" y="50" width="8" height="6" rx="1" fill="#FACC15" stroke="#CA8A04" strokeWidth="1" />
        <line x1="52" y1="52" x2="60" y2="52" stroke="#713F12" strokeWidth="0.8" />
        <line x1="52" y1="54" x2="60" y2="54" stroke="#713F12" strokeWidth="0.8" />
      </svg>
    );
  }

  // 3. BARAK MILITER (Blacksmith & Guard Garrison with Forge Chimney)
  if (buildingId === 'barak_militer') {
    return (
      <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
        <ellipse cx="32" cy="58" rx="27" ry="5" fill="rgba(0,0,0,0.45)" />

        {/* Sturdy Cobblestone Wall Body */}
        <rect x="10" y="24" width="44" height="32" fill={p.cobble} />
        {/* Stone Brick Textures */}
        <line x1="10" y1="32" x2="54" y2="32" stroke={p.cobbleDark} strokeWidth="1" strokeDasharray="6 3" />
        <line x1="10" y1="40" x2="54" y2="40" stroke={p.cobbleDark} strokeWidth="1" strokeDasharray="4 4" />
        <line x1="10" y1="48" x2="54" y2="48" stroke={p.cobbleDark} strokeWidth="1" strokeDasharray="5 3" />

        {/* Massive Stone Chimney with Forge Smoke */}
        <rect x="42" y="8" width="8" height="18" fill="#475569" stroke="#1E293B" strokeWidth="1" />
        <circle cx="46" cy="4" r="3" fill="#64748B" opacity="0.8" />
        <circle cx="49" cy="0" r="2" fill="#94A3B8" opacity="0.5" />

        {/* Fortified Timber Roof */}
        <polygon points="32,8 58,24 6,24" fill={p.roof} />
        <line x1="10" y1="21" x2="54" y2="21" stroke={p.roofDark} strokeWidth="1.5" />
        <line x1="18" y1="16" x2="46" y2="16" stroke={p.roofDark} strokeWidth="1.5" />

        {/* Heavy Iron-Reinforced Door */}
        <rect x="27" y="38" width="10" height="18" fill="#334155" />
        <line x1="27" y1="43" x2="37" y2="43" stroke="#0F172A" strokeWidth="1.5" />
        <line x1="27" y1="50" x2="37" y2="50" stroke="#0F172A" strokeWidth="1.5" />
        <circle cx="35" cy="47" r="1" fill="#FACC15" />

        {/* Anvil / Tempa Besi Prop on Left */}
        <path d="M 12 50 L 22 50 L 20 54 L 22 56 L 12 56 L 14 54 Z" fill="#1E293B" stroke="#0F172A" strokeWidth="0.8" />
        <circle cx="17" cy="49" r="1.5" fill="#FACC15" /> {/* Spark on anvil */}

        {/* Archery Target Prop on Right */}
        <circle cx="50" cy="46" r="6" fill="#F8FAFC" stroke="#DC2626" strokeWidth="1.5" />
        <circle cx="50" cy="46" r="3.5" fill="#DC2626" />
        <circle cx="50" cy="46" r="1.5" fill="#FBBF24" />

        {/* Crossed Spears on Banner */}
        <line x1="18" y1="27" x2="26" y2="35" stroke="#CBD5E1" strokeWidth="1.5" />
        <line x1="26" y1="27" x2="18" y2="35" stroke="#CBD5E1" strokeWidth="1.5" />
        <circle cx="22" cy="31" r="3" fill="#DC2626" />
      </svg>
    );
  }

  // 4. GERBANG BENTENG (Wooden Palisade & Watchtower)
  if (buildingId === 'gate') {
    return (
      <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
        <ellipse cx="32" cy="58" rx="28" ry="5" fill="rgba(0,0,0,0.45)" />

        {/* Log Palisade Fence on left & right */}
        {[-8, -4, 0, 4, 36, 40, 44, 48].map((ox, i) => (
          <g key={i}>
            <polygon points={`${16 + ox},56 ${20 + ox},56 ${18 + ox},34`} fill="#78350F" />
            <polygon points={`${16 + ox},56 ${18 + ox},56 ${17 + ox},34`} fill="#92400E" />
          </g>
        ))}

        {/* Central Wooden Watchtower Frame */}
        <rect x="22" y="16" width="20" height="40" fill="#854D0E" />
        <line x1="22" y1="26" x2="42" y2="26" stroke="#451A03" strokeWidth="1.5" />
        <line x1="22" y1="38" x2="42" y2="38" stroke="#451A03" strokeWidth="1.5" />

        {/* Diagonal X-Braces */}
        <line x1="22" y1="26" x2="42" y2="38" stroke="#542807" strokeWidth="1.2" />
        <line x1="42" y1="26" x2="22" y2="38" stroke="#542807" strokeWidth="1.2" />

        {/* Watchtower Lookout Platform & Shingle Roof */}
        <polygon points="32,4 46,16 18,16" fill={p.roof} />
        <line x1="20" y1="14" x2="44" y2="14" stroke={p.roofDark} strokeWidth="1" />
        <rect x="20" y="15" width="24" height="4" fill="#542807" />

        {/* Alert Bell in Tower */}
        <circle cx="32" cy="20" r="2.5" fill="#FACC15" />

        {/* Fortified Portcullis / Iron Gate Grate */}
        <rect x="26" y="42" width="12" height="15" fill="#1E293B" />
        <line x1="29" y1="42" x2="29" y2="57" stroke="#94A3B8" strokeWidth="1" />
        <line x1="32" y1="42" x2="32" y2="57" stroke="#94A3B8" strokeWidth="1" />
        <line x1="35" y1="42" x2="35" y2="57" stroke="#94A3B8" strokeWidth="1" />
        <line x1="26" y1="46" x2="38" y2="46" stroke="#94A3B8" strokeWidth="1" />
        <line x1="26" y1="51" x2="38" y2="51" stroke="#94A3B8" strokeWidth="1" />

        {/* Red Guard Banners on Tower */}
        <polygon points="43,8 51,11 43,14" fill="#DC2626" />
        <line x1="43" y1="6" x2="43" y2="15" stroke="#FACC15" strokeWidth="1" />
      </svg>
    );
  }

  // 5. RUMAH WARGA / COZY COTTAGE (Pondok Kayu Pedesaan)
  if (buildingId === 'rumah_warga' || buildingId === 'perumahan') {
    return (
      <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
        <ellipse cx="32" cy="58" rx="25" ry="5" fill="rgba(0,0,0,0.4)" />

        {/* Small Red Brick Chimney */}
        <rect x="38" y="10" width="5" height="12" fill="#B91C1C" />
        <circle cx="40.5" cy="7" r="2.5" fill="#E2E8F0" opacity="0.7" />

        {/* Wooden Cottage Wall */}
        <rect x="14" y="28" width="36" height="28" fill="#854D0E" />
        <line x1="14" y1="35" x2="50" y2="35" stroke="#542807" strokeWidth="1" />
        <line x1="14" y1="42" x2="50" y2="42" stroke="#542807" strokeWidth="1" />
        <line x1="14" y1="49" x2="50" y2="49" stroke="#542807" strokeWidth="1" />

        {/* Cozy Gabled Roof */}
        <polygon points="32,12 54,28 10,28" fill={p.roof} />
        <line x1="14" y1="25" x2="50" y2="25" stroke={p.roofDark} strokeWidth="1.5" />
        <line x1="20" y1="20" x2="44" y2="20" stroke={p.roofDark} strokeWidth="1.5" />

        {/* Cozy Door */}
        <rect x="20" y="42" width="8" height="14" fill="#451A03" rx="1.5" />
        <circle cx="26" cy="49" r="0.8" fill="#FACC15" />

        {/* Glowing Window with Red Curtains */}
        <rect x="34" y="36" width="10" height="9" fill={p.windowGlow} />
        <rect x="34" y="36" width="10" height="9" fill="none" stroke="#451A03" strokeWidth="1" />
        <line x1="39" y1="36" x2="39" y2="45" stroke="#451A03" strokeWidth="0.8" />
        <line x1="34" y1="40.5" x2="44" y2="40.5" stroke="#451A03" strokeWidth="0.8" />
        {/* Flower Pot */}
        <rect x="33" y="45" width="12" height="2" fill="#78350F" />
        <circle cx="35" cy="44.5" r="1.2" fill="#EF4444" />
        <circle cx="39" cy="44.5" r="1.2" fill="#FACC15" />
        <circle cx="43" cy="44.5" r="1.2" fill="#EC4899" />
      </svg>
    );
  }

  // 6. DEFAULT / ALL OTHER BUILDINGS (High-Detail Retro Pixel Architecture)
  return (
    <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
      <ellipse cx="32" cy="58" rx="26" ry="5" fill="rgba(0,0,0,0.4)" />
      {/* Stone Foundation */}
      <rect x="10" y="46" width="44" height="10" fill={p.cobble} />
      <line x1="10" y1="51" x2="54" y2="51" stroke={p.cobbleDark} strokeWidth="1" strokeDasharray="4 2" />
      {/* Wooden Main Body */}
      <rect x="12" y="26" width="40" height="21" fill={p.walls} />
      <line x1="12" y1="33" x2="52" y2="33" stroke={p.wallsDark} strokeWidth="1" />
      <line x1="12" y1="40" x2="52" y2="40" stroke={p.wallsDark} strokeWidth="1" />
      {/* Gabled Shingle Roof */}
      <polygon points="32,8 56,26 8,26" fill={p.roof} />
      <line x1="12" y1="23" x2="52" y2="23" stroke={p.roofDark} strokeWidth="1.5" />
      <line x1="18" y1="18" x2="46" y2="18" stroke={p.roofDark} strokeWidth="1.5" />
      {/* Entrance Door */}
      <rect x="27" y="38" width="10" height="18" fill={p.door} rx="2" />
      <circle cx="34" cy="47" r="1" fill="#FACC15" />
      {/* Cozy Window */}
      <rect x="16" y="33" width="7" height="7" fill={p.windowGlow} stroke={p.windowFrame} strokeWidth="1" />
      <line x1="19.5" y1="33" x2="19.5" y2="40" stroke={p.windowFrame} strokeWidth="0.8" />
      <line x1="16" y1="36.5" x2="23" y2="36.5" stroke={p.windowFrame} strokeWidth="0.8" />
      {/* Right Prop (Barrel / Plant) */}
      <rect x="42" y="44" width="7" height="9" rx="1.5" fill="#78350F" stroke="#451A03" strokeWidth="1" />
      <line x1="42" y1="47" x2="49" y2="47" stroke="#1E293B" strokeWidth="0.8" />
      <line x1="42" y1="50" x2="49" y2="50" stroke="#1E293B" strokeWidth="0.8" />
    </svg>
  );
}
