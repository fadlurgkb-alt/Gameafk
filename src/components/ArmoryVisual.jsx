import React from 'react';

/**
 * Pixel-Art SVG Visual Generator for all 12 Armory Weapons & Armors
 */
export default function ArmoryVisual({ id, size = 40, style = {} }) {
  let svg = null;

  switch (id) {
    case 'tongkat_kayu':
      svg = (
        <svg viewBox="0 0 40 40" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <line x1="8" y1="32" x2="32" y2="8" stroke="#78350F" strokeWidth="3" strokeLinecap="round" />
          <polygon points="32,8 36,4 34,12" fill="#94A3B8" />
        </svg>
      );
      break;

    case 'kulit_hewan':
      svg = (
        <svg viewBox="0 0 40 40" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <path d="M 12 10 L 28 10 L 32 20 L 28 34 L 12 34 L 8 20 Z" fill="#92400E" stroke="#451A03" strokeWidth="1.5" />
          <line x1="12" y1="18" x2="28" y2="18" stroke="#D97706" strokeWidth="2" />
        </svg>
      );
      break;

    case 'pedang_besi':
      svg = (
        <svg viewBox="0 0 40 40" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <line x1="8" y1="32" x2="28" y2="12" stroke="#E2E8F0" strokeWidth="3.5" strokeLinecap="square" />
          <polygon points="28,12 34,6 31,15" fill="#CBD5E1" />
          <line x1="12" y1="24" x2="18" y2="30" stroke="#F59E0B" strokeWidth="2" />
          <circle cx="8" cy="32" r="2" fill="#F59E0B" />
        </svg>
      );
      break;

    case 'armor_besi':
      svg = (
        <svg viewBox="0 0 40 40" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <path d="M 10 10 L 30 10 L 34 22 L 28 34 L 12 34 L 6 22 Z" fill="#94A3B8" stroke="#475569" strokeWidth="2" />
          <polygon points="20,12 24,20 20,28 16,20" fill="#CBD5E1" />
        </svg>
      );
      break;

    case 'musket':
      svg = (
        <svg viewBox="0 0 40 40" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <line x1="6" y1="28" x2="34" y2="10" stroke="#78350F" strokeWidth="3" />
          <line x1="20" y1="19" x2="36" y2="9" stroke="#94A3B8" strokeWidth="2" />
          <circle cx="16" cy="22" r="2" fill="#F59E0B" />
        </svg>
      );
      break;

    case 'assault_rifle':
      svg = (
        <svg viewBox="0 0 40 40" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <rect x="8" y="16" width="22" height="6" fill="#1E293B" stroke="#0F172A" strokeWidth="1" />
          <line x1="28" y1="18" x2="36" y2="18" stroke="#64748B" strokeWidth="2" />
          <rect x="14" y="22" width="4" height="8" fill="#334155" />
          <rect x="6" y="18" width="6" height="8" fill="#475569" />
        </svg>
      );
      break;

    case 'railgun':
      svg = (
        <svg viewBox="0 0 40 40" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <rect x="8" y="16" width="24" height="6" fill="#0F172A" stroke="#06B6D4" strokeWidth="1" />
          <line x1="32" y1="19" x2="38" y2="19" stroke="#67E8F9" strokeWidth="2" />
          {/* Glowing Coils */}
          <line x1="14" y1="14" x2="14" y2="24" stroke="#06B6D4" strokeWidth="1.5" />
          <line x1="20" y1="14" x2="20" y2="24" stroke="#06B6D4" strokeWidth="1.5" />
          <line x1="26" y1="14" x2="26" y2="24" stroke="#06B6D4" strokeWidth="1.5" />
        </svg>
      );
      break;

    case 'exosuit':
      svg = (
        <svg viewBox="0 0 40 40" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <path d="M 8 10 L 32 10 L 36 22 L 30 36 L 10 36 L 4 22 Z" fill="#1E293B" stroke="#06B6D4" strokeWidth="2" />
          <circle cx="20" cy="22" r="4" fill="#06B6D4" />
          <line x1="12" y1="12" x2="28" y2="12" stroke="#F59E0B" strokeWidth="1.5" />
        </svg>
      );
      break;

    case 'laser_blade':
      svg = (
        <svg viewBox="0 0 40 40" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <line x1="8" y1="32" x2="14" y2="26" stroke="#475569" strokeWidth="3" />
          <line x1="14" y1="26" x2="34" y2="6" stroke="#A855F7" strokeWidth="4" strokeLinecap="round" />
          <line x1="16" y1="24" x2="32" y2="8" stroke="#F8FAFC" strokeWidth="1.5" />
        </svg>
      );
      break;

    case 'plasma_cannon':
      svg = (
        <svg viewBox="0 0 40 40" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <rect x="6" y="14" width="26" height="12" rx="3" fill="#1E1B4B" stroke="#8B5CF6" strokeWidth="1.5" />
          <circle cx="32" cy="20" r="4" fill="#C084FC" />
          <line x1="12" y1="20" x2="24" y2="20" stroke="#06B6D4" strokeWidth="2" />
        </svg>
      );
      break;

    case 'cyber_body':
      svg = (
        <svg viewBox="0 0 40 40" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <rect x="10" y="8" width="20" height="26" rx="4" fill="#020617" stroke="#38BDF8" strokeWidth="2" />
          <circle cx="20" cy="20" r="5" fill="#38BDF8" />
          <line x1="10" y1="14" x2="30" y2="14" stroke="#A855F7" strokeWidth="1" />
        </svg>
      );
      break;

    case 'nano_kirana':
      svg = (
        <svg viewBox="0 0 40 40" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <line x1="6" y1="34" x2="34" y2="6" stroke="#EC4899" strokeWidth="4" strokeLinecap="round" />
          <line x1="8" y1="32" x2="32" y2="8" stroke="#67E8F9" strokeWidth="2" />
          <circle cx="20" cy="20" r="3" fill="#FFFFFF" />
        </svg>
      );
      break;

    default:
      svg = (
        <svg viewBox="0 0 40 40" width={size} height={size}>
          <circle cx="20" cy="20" r="16" fill="#3B82F6" />
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
