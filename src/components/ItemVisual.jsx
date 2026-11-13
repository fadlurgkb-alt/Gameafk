import React from 'react';

/**
 * Pixel-Art SVG Visual Generator for Shop & Market Items
 */
export default function ItemVisual({ id, size = 40, style = {} }) {
  let svg = null;

  switch (id) {
    case 'hp_pot':
      svg = (
        <svg viewBox="0 0 40 40" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <ellipse cx="20" cy="34" rx="10" ry="3" fill="rgba(0,0,0,0.3)" />
          {/* Flask Body */}
          <path d="M 17 12 L 23 12 L 28 22 A 10 10 0 1 1 12 22 Z" fill="#EF4444" stroke="#B91C1C" strokeWidth="1.5" />
          {/* Liquid Shimmer */}
          <ellipse cx="20" cy="24" rx="6" ry="2" fill="#FCA5A5" />
          <circle cx="18" cy="28" r="1.5" fill="#FFFFFF" />
          {/* Cork Stopper */}
          <rect x="17" y="8" width="6" height="4" fill="#D97706" rx="1" />
        </svg>
      );
      break;

    case 'atk_pot':
      svg = (
        <svg viewBox="0 0 40 40" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <ellipse cx="20" cy="34" rx="10" ry="3" fill="rgba(0,0,0,0.3)" />
          <path d="M 17 12 L 23 12 L 28 22 A 10 10 0 1 1 12 22 Z" fill="#0284C7" stroke="#0369A1" strokeWidth="1.5" />
          {/* Lightning inside */}
          <polygon points="21,18 18,24 22,24 19,30 24,23 20,23" fill="#FDE047" />
          <rect x="17" y="8" width="6" height="4" fill="#D97706" rx="1" />
        </svg>
      );
      break;

    case 'wood_100':
      svg = (
        <svg viewBox="0 0 40 40" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <ellipse cx="20" cy="32" rx="12" ry="4" fill="rgba(0,0,0,0.3)" />
          {/* Timber Logs Stack */}
          <rect x="8" y="20" width="24" height="8" rx="2" fill="#78350F" stroke="#451A03" strokeWidth="1.5" />
          <rect x="11" y="14" width="18" height="7" rx="2" fill="#92400E" stroke="#451A03" strokeWidth="1.5" />
          {/* Rope Binding */}
          <line x1="16" y1="14" x2="16" y2="28" stroke="#F59E0B" strokeWidth="2" />
          <line x1="24" y1="14" x2="24" y2="28" stroke="#F59E0B" strokeWidth="2" />
        </svg>
      );
      break;

    case 'stone_100':
      svg = (
        <svg viewBox="0 0 40 40" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <ellipse cx="20" cy="32" rx="12" ry="4" fill="rgba(0,0,0,0.3)" />
          {/* Heavy Stone Blocks */}
          <polygon points="12,14 26,10 32,22 22,28 10,24" fill="#64748B" stroke="#334155" strokeWidth="1.5" />
          <polygon points="8,22 18,18 24,28 12,32" fill="#94A3B8" stroke="#475569" strokeWidth="1.5" />
        </svg>
      );
      break;

    case 'shard_r':
      svg = (
        <svg viewBox="0 0 40 40" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <ellipse cx="20" cy="34" rx="8" ry="3" fill="rgba(0,0,0,0.4)" />
          {/* Glowing Crystal Gem */}
          <polygon points="20,6 28,14 24,30 16,30 12,14" fill="#06B6D4" stroke="#22D3EE" strokeWidth="1.5" />
          <polygon points="20,6 24,14 20,30 16,14" fill="#67E8F9" opacity="0.8" />
          <circle cx="20" cy="14" r="2" fill="#FFFFFF" />
        </svg>
      );
      break;

    case 'ai_core':
      svg = (
        <svg viewBox="0 0 40 40" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <rect x="10" y="10" width="20" height="20" rx="3" fill="#0F172A" stroke="#A855F7" strokeWidth="2" />
          <circle cx="20" cy="20" r="5" fill="#A855F7" />
          <circle cx="20" cy="20" r="2.5" fill="#67E8F9" />
          {/* Gold Pin connectors */}
          <line x1="6" y1="15" x2="10" y2="15" stroke="#F59E0B" strokeWidth="1.5" />
          <line x1="6" y1="25" x2="10" y2="25" stroke="#F59E0B" strokeWidth="1.5" />
          <line x1="30" y1="15" x2="34" y2="15" stroke="#F59E0B" strokeWidth="1.5" />
          <line x1="30" y1="25" x2="34" y2="25" stroke="#F59E0B" strokeWidth="1.5" />
        </svg>
      );
      break;

    default:
      svg = (
        <svg viewBox="0 0 40 40" width={size} height={size}>
          <circle cx="20" cy="20" r="14" fill="#F59E0B" />
        </svg>
      );
      break;
  }

  return (
    <div style={{ display: 'inline-flex', alignItems: 'center', justifyContent: 'center' }}>
      {svg}
    </div>
  );
}
