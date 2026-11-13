import React from 'react';
import { GAME_ASSETS } from '../utils/assetLoader';

export const ERA_COLORS = {
  primitif: {
    roof: '#B45309',
    walls: '#854D0E',
    cobble: '#78716C',
    door: '#451A03',
    windowGlow: '#FEF08A'
  },
  kerajaan: {
    roof: '#DC2626',
    walls: '#64748B',
    cobble: '#475569',
    door: '#1E293B',
    windowGlow: '#FDE047'
  },
  modern: {
    roof: '#0284C7',
    walls: '#CBD5E1',
    cobble: '#475569',
    door: '#0284C7',
    windowGlow: '#67E8F9'
  },
  cyberpunk: {
    roof: '#7E22CE',
    walls: '#0F172A',
    cobble: '#1E1B4B',
    door: '#06B6D4',
    windowGlow: '#06B6D4'
  }
};

export default function BuildingVisual({
  buildingId,
  eraId = 'primitif',
  size = 64,
  style = {}
}) {
  // Directly map to authentic complete pixel art PNG
  const realImgSrc = GAME_ASSETS[buildingId] || `/assets/sliced/buildings/building_${buildingId}.png`;

  return (
    <div style={{
      width: size,
      height: size,
      display: 'inline-flex',
      alignItems: 'center',
      justifyContent: 'center',
      overflow: 'hidden',
      ...style
    }}>
      <img
        src={realImgSrc}
        alt={buildingId}
        width={size}
        height={size}
        style={{
          width: '100%',
          height: '100%',
          imageRendering: 'pixelated',
          objectFit: 'contain',
          display: 'block'
        }}
        onError={(e) => {
          // Fallback if specific file missing
          e.target.src = '/assets/sliced/buildings/building_town_hall.png';
        }}
      />
    </div>
  );
}
