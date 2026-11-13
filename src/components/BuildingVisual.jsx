import React from 'react';
import { GAME_ASSETS } from '../utils/assetLoader';

export const ERA_COLORS = {
  primitif: {
    roof: '#B45309',
    roofDark: '#78350F',
    roofLight: '#F59E0B',
    walls: '#854D0E',
    wallsDark: '#542807',
    cobble: '#78716C',
    cobbleDark: '#44403C',
    door: '#451A03',
    windowGlow: '#FEF08A',
    windowFrame: '#3F1A04'
  },
  kerajaan: {
    roof: '#DC2626',
    roofDark: '#991B1B',
    roofLight: '#F87171',
    walls: '#64748B',
    wallsDark: '#334155',
    cobble: '#475569',
    cobbleDark: '#1E293B',
    door: '#1E293B',
    windowGlow: '#FDE047',
    windowFrame: '#0F172A'
  },
  modern: {
    roof: '#0284C7',
    roofDark: '#0369A1',
    roofLight: '#38BDF8',
    walls: '#CBD5E1',
    wallsDark: '#64748B',
    cobble: '#475569',
    cobbleDark: '#334155',
    door: '#0284C7',
    windowGlow: '#67E8F9',
    windowFrame: '#1E293B'
  },
  cyberpunk: {
    roof: '#7E22CE',
    roofDark: '#3B0764',
    roofLight: '#C084FC',
    walls: '#0F172A',
    wallsDark: '#020617',
    cobble: '#1E1B4B',
    cobbleDark: '#0F0E2A',
    door: '#06B6D4',
    windowGlow: '#06B6D4',
    windowFrame: '#3B0764'
  }
};

export default function BuildingVisual({
  buildingId,
  eraId = 'primitif',
  size = 64,
  style = {}
}) {
  const p = ERA_COLORS[eraId] || ERA_COLORS.primitif;

  // 1. If Real Extracted Asset Sprite exists for this building, render authentic PNG!
  const realImgSrc = GAME_ASSETS[buildingId] || (buildingId.includes('pertanian') ? GAME_ASSETS.pertanian : null);
  if (realImgSrc) {
    return (
      <img
        src={realImgSrc}
        alt={buildingId}
        width={size}
        height={size}
        style={{
          imageRendering: 'pixelated',
          objectFit: 'contain',
          display: 'block',
          ...style
        }}
      />
    );
  }

  // 2. Fallback High-End Pixel Art SVG
  return (
    <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
      <ellipse cx="32" cy="58" rx="26" ry="5" fill="rgba(0,0,0,0.4)" />
      <rect x="10" y="46" width="44" height="10" fill={p.cobble} />
      <rect x="12" y="26" width="40" height="21" fill={p.walls} />
      <polygon points="32,8 56,26 8,26" fill={p.roof} />
      <rect x="27" y="38" width="10" height="18" fill={p.door} rx="2" />
      <rect x="16" y="33" width="7" height="7" fill={p.windowGlow} stroke={p.windowFrame} strokeWidth="1" />
    </svg>
  );
}
