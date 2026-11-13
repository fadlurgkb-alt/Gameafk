import React from 'react';

/**
 * Pixel-Art SVG Visual Generator for all 22 Unique Historical & Mythical Commanders
 * Designed with specific weapons, armor, signature headdresses, and distinctive identities!
 */
export default function CommanderVisual({ id, size = 64, style = {} }) {
  let svgContent = null;

  switch (id) {
    case 'zeus':
      // Zeus (Mythical) - Dewa Petir: White beard, golden laurel, cyan lightning bolt, white-gold toga
      svgContent = (
        <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <circle cx="32" cy="32" r="30" fill="#1E1B4B" stroke="#F59E0B" strokeWidth="2" />
          <ellipse cx="32" cy="56" rx="20" ry="4" fill="rgba(0,0,0,0.4)" />
          {/* Toga Body */}
          <path d="M 20 36 L 44 36 L 46 58 L 18 58 Z" fill="#F8FAFC" />
          <path d="M 22 36 L 36 36 L 30 58 L 20 58 Z" fill="#E2E8F0" />
          <line x1="20" y1="40" x2="44" y2="40" stroke="#F59E0B" strokeWidth="2" />
          {/* Head & Skin */}
          <circle cx="32" cy="24" r="10" fill="#FDE68A" />
          {/* White Hair & Majestic Beard */}
          <path d="M 22 20 Q 32 10 42 20" fill="none" stroke="#FFFFFF" strokeWidth="5" strokeLinecap="round" />
          <polygon points="24,28 40,28 32,44" fill="#FFFFFF" />
          {/* Golden Laurel Wreath */}
          <path d="M 24 16 Q 32 12 40 16" fill="none" stroke="#F59E0B" strokeWidth="3" />
          {/* Cyan Lightning Bolt in Hand */}
          <polygon points="46,12 40,28 47,28 41,50 56,26 48,26" fill="#06B6D4" stroke="#67E8F9" strokeWidth="1" />
          {/* Glowing Eyes */}
          <circle cx="28" cy="23" r="1.5" fill="#06B6D4" />
          <circle cx="36" cy="23" r="1.5" fill="#06B6D4" />
        </svg>
      );
      break;

    case 'athena':
      // Athena (Mythical) - Golden Corinthian helmet, red crest, Aegis shield with owl, spear
      svgContent = (
        <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <circle cx="32" cy="32" r="30" fill="#1E1B4B" stroke="#F59E0B" strokeWidth="2" />
          <ellipse cx="32" cy="56" rx="20" ry="4" fill="rgba(0,0,0,0.4)" />
          {/* White & Gold Chiton */}
          <path d="M 22 36 L 42 36 L 44 58 L 20 58 Z" fill="#F8FAFC" />
          <rect x="26" y="36" width="12" height="12" fill="#F59E0B" />
          {/* Head */}
          <circle cx="32" cy="24" r="9" fill="#FDE68A" />
          {/* Golden Corinthian Helm with Red Plume */}
          <polygon points="32,4 36,16 28,16" fill="#DC2626" />
          <path d="M 22 22 L 42 22 L 40 14 L 24 14 Z" fill="#F59E0B" stroke="#B45309" strokeWidth="1" />
          {/* Spear on right */}
          <line x1="48" y1="8" x2="48" y2="58" stroke="#D1D5DB" strokeWidth="2" />
          <polygon points="48,4 51,10 45,10" fill="#F59E0B" />
          {/* Aegis Round Shield on left */}
          <circle cx="18" cy="42" r="11" fill="#F59E0B" stroke="#B45309" strokeWidth="2" />
          <circle cx="18" cy="42" r="6" fill="#3B82F6" />
          <text x="14" y="45" fontSize="7" fill="#F8FAFC">🦉</text>
        </svg>
      );
      break;

    case 'odin':
      // Odin (Mythical) - Allfather with horned helm, eye patch, grey beard, blue cloak, Gungnir
      svgContent = (
        <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <circle cx="32" cy="32" r="30" fill="#1E1B4B" stroke="#EF4444" strokeWidth="2" />
          {/* Blue Cloak */}
          <path d="M 16 34 L 48 34 L 52 58 L 12 58 Z" fill="#1E3A8A" />
          <circle cx="32" cy="24" r="10" fill="#FED7AA" />
          {/* Grey Long Beard */}
          <polygon points="24,28 40,28 32,50" fill="#94A3B8" />
          {/* Horned Viking Helmet */}
          <path d="M 22 20 Q 32 12 42 20 Z" fill="#475569" stroke="#F59E0B" strokeWidth="1.5" />
          <polygon points="18,14 24,18 20,22" fill="#F8FAFC" />
          <polygon points="46,14 40,18 44,22" fill="#F8FAFC" />
          {/* Eye Patch on Left eye */}
          <line x1="26" y1="20" x2="31" y2="25" stroke="#0F172A" strokeWidth="2.5" />
          {/* Glowing Single Eye */}
          <circle cx="35" cy="22" r="2" fill="#67E8F9" />
          {/* Gungnir Spear */}
          <line x1="50" y1="6" x2="50" y2="58" stroke="#F59E0B" strokeWidth="2.5" />
          <polygon points="50,2 53,8 47,8" fill="#F8FAFC" />
        </svg>
      );
      break;

    case 'archon':
      // CYBER-ARCHON ZERO (Mythical) - Futuristic Android Core, Neon visor, Holographic halo
      svgContent = (
        <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <circle cx="32" cy="32" r="30" fill="#090D16" stroke="#06B6D4" strokeWidth="2" />
          {/* Hologram Halo */}
          <ellipse cx="32" cy="12" rx="16" ry="4" fill="none" stroke="#06B6D4" strokeWidth="2" strokeDasharray="3 2" />
          {/* Carbon Torso */}
          <rect x="20" y="34" width="24" height="24" rx="3" fill="#0F172A" stroke="#06B6D4" strokeWidth="1.5" />
          {/* Glowing Core */}
          <circle cx="32" cy="44" r="5" fill="#06B6D4" />
          <circle cx="32" cy="44" r="2.5" fill="#F8FAFC" />
          {/* Cybernetic Head */}
          <rect x="24" y="16" width="16" height="16" rx="2" fill="#1E293B" stroke="#06B6D4" strokeWidth="1.5" />
          {/* Cyan Neon Visor Eye */}
          <rect x="26" y="21" width="12" height="4" fill="#06B6D4" />
          <line x1="22" y1="23" x2="42" y2="23" stroke="#A855F7" strokeWidth="1" />
        </svg>
      );
      break;

    case 'arthur':
      // Arthur Pendragon (SSR) - Golden crown, knight armor, glowing blue Excalibur, royal red cloak
      svgContent = (
        <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <circle cx="32" cy="32" r="30" fill="#1E293B" stroke="#F59E0B" strokeWidth="2" />
          {/* Red Cape */}
          <path d="M 16 34 L 48 34 L 52 58 L 12 58 Z" fill="#DC2626" />
          {/* Knight Silver Plate */}
          <rect x="22" y="34" width="20" height="24" rx="2" fill="#94A3B8" stroke="#475569" strokeWidth="1.5" />
          <circle cx="32" cy="22" r="9" fill="#FED7AA" />
          {/* Blonde Hair */}
          <path d="M 23 20 Q 32 14 41 20" fill="#FBBF24" />
          {/* Royal Gold Crown */}
          <polygon points="24,14 26,8 29,12 32,6 35,12 38,8 40,14" fill="#F59E0B" stroke="#B45309" strokeWidth="0.8" />
          {/* Excalibur Sword */}
          <line x1="48" y1="12" x2="48" y2="52" stroke="#60A5FA" strokeWidth="3" />
          <line x1="43" y1="20" x2="53" y2="20" stroke="#F59E0B" strokeWidth="2" />
          <circle cx="48" cy="10" r="2.5" fill="#93C5FD" />
        </svg>
      );
      break;

    case 'joan':
      // Joan of Arc (SSR) - Silver armor, holy fleur-de-lis banner, bob haircut, devout knight
      svgContent = (
        <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <circle cx="32" cy="32" r="30" fill="#1E293B" stroke="#F59E0B" strokeWidth="2" />
          <rect x="22" y="34" width="20" height="24" rx="2" fill="#E2E8F0" stroke="#64748B" strokeWidth="1.5" />
          <circle cx="32" cy="22" r="8.5" fill="#FED7AA" />
          {/* Brown Bob Haircut */}
          <path d="M 23 16 Q 32 12 41 16 L 42 25 L 22 25 Z" fill="#78350F" />
          {/* Holy Cross Banner */}
          <line x1="48" y1="6" x2="48" y2="58" stroke="#78350F" strokeWidth="2" />
          <rect x="36" y="8" width="12" height="18" fill="#F8FAFC" stroke="#F59E0B" strokeWidth="1" />
          <text x="39" y="21" fontSize="10" fill="#F59E0B">⚜️</text>
        </svg>
      );
      break;

    case 'gilgamesh':
      // Gilgamesh (SSR) - Golden Sumerian armor, crimson eyes, arrogant king of Uruk
      svgContent = (
        <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <circle cx="32" cy="32" r="30" fill="#1E293B" stroke="#F59E0B" strokeWidth="2" />
          <rect x="20" y="32" width="24" height="26" rx="2" fill="#F59E0B" stroke="#B45309" strokeWidth="1.5" />
          <circle cx="32" cy="20" r="9" fill="#FED7AA" />
          {/* Spiked Gold Crown */}
          <polygon points="23,14 26,6 32,10 38,6 41,14" fill="#F59E0B" stroke="#B45309" strokeWidth="1" />
          {/* Crimson Eyes */}
          <circle cx="28" cy="20" r="1.5" fill="#EF4444" />
          <circle cx="36" cy="20" r="1.5" fill="#EF4444" />
          {/* Golden Gate Portal Sword floating behind */}
          <line x1="12" y1="14" x2="18" y2="30" stroke="#F59E0B" strokeWidth="3" />
          <line x1="52" y1="14" x2="46" y2="30" stroke="#F59E0B" strokeWidth="3" />
        </svg>
      );
      break;

    case 'cleopatra':
      // Cleopatra VII (SSR) - Egyptian royal nemes headdress, ankh scepter, eyeliner
      svgContent = (
        <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <circle cx="32" cy="32" r="30" fill="#1E293B" stroke="#F59E0B" strokeWidth="2" />
          {/* White Linen Dress with Gold collar */}
          <path d="M 22 36 L 42 36 L 44 58 L 20 58 Z" fill="#F8FAFC" />
          <circle cx="32" cy="36" r="8" fill="#F59E0B" />
          <circle cx="32" cy="22" r="8.5" fill="#FCD34D" />
          {/* Nemes Blue-Gold Striped Headdress */}
          <path d="M 22 14 Q 32 8 42 14 L 46 32 L 38 28 L 32 30 L 26 28 L 18 32 Z" fill="#0284C7" stroke="#F59E0B" strokeWidth="1.5" />
          {/* Cobra Uraeus on forehead */}
          <circle cx="32" cy="11" r="2" fill="#F59E0B" />
          {/* Golden Ankh in Hand */}
          <circle cx="48" cy="40" r="3" fill="none" stroke="#F59E0B" strokeWidth="1.5" />
          <line x1="48" y1="43" x2="48" y2="52" stroke="#F59E0B" strokeWidth="2" />
          <line x1="44" y1="46" x2="52" y2="46" stroke="#F59E0B" strokeWidth="2" />
        </svg>
      );
      break;

    case 'genghis':
      // Genghis Khan (SSR) - Conical fur hat, Mongolian armor, recurve bow
      svgContent = (
        <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <circle cx="32" cy="32" r="30" fill="#1E293B" stroke="#F59E0B" strokeWidth="2" />
          <rect x="20" y="34" width="24" height="24" rx="2" fill="#78350F" stroke="#451A03" strokeWidth="2" />
          <circle cx="32" cy="23" r="9" fill="#FED7AA" />
          {/* Fur Hat with Conical Peak & Feather */}
          <ellipse cx="32" cy="16" rx="12" ry="4" fill="#D97706" />
          <polygon points="26,16 32,6 38,16" fill="#991B1B" />
          <line x1="32" y1="6" x2="35" y2="2" stroke="#F8FAFC" strokeWidth="2" />
          {/* Curved Mustache */}
          <path d="M 27 28 Q 32 26 37 28" fill="none" stroke="#1E293B" strokeWidth="2" />
          {/* Recurve Composite Bow */}
          <path d="M 48 20 Q 56 36 48 52" fill="none" stroke="#F59E0B" strokeWidth="3" />
        </svg>
      );
      break;

    case 'tesla':
      // Nikola Tesla (SSR) - Suit, mustache, Tesla coil electric bulbs
      svgContent = (
        <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <circle cx="32" cy="32" r="30" fill="#1E293B" stroke="#F59E0B" strokeWidth="2" />
          {/* Victorian Suit */}
          <rect x="22" y="34" width="20" height="24" rx="2" fill="#0F172A" />
          <polygon points="32,34 28,42 32,46 36,42" fill="#DC2626" />
          <circle cx="32" cy="22" r="8" fill="#FED7AA" />
          {/* Hair parted in center */}
          <path d="M 23 16 Q 32 12 41 16 L 41 22 L 23 22 Z" fill="#1E293B" />
          {/* Mustache */}
          <line x1="28" y1="26" x2="36" y2="26" stroke="#1E293B" strokeWidth="2" />
          {/* Glowing Wireless Bulb in Hand */}
          <circle cx="48" cy="36" r="6" fill="#67E8F9" stroke="#0284C7" strokeWidth="1" />
          <polygon points="46,30 49,34 47,34 50,40" fill="#FFFFFF" />
        </svg>
      );
      break;

    case 'gajah_mada':
      // Gajah Mada (SR) - Mahapatih Majapahit, destar crown, traditional sash, Keris pusaka
      svgContent = (
        <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <circle cx="32" cy="32" r="30" fill="#1E293B" stroke="#8B5CF6" strokeWidth="2" />
          {/* Muscular Torso with Gold Badong Collar */}
          <rect x="20" y="34" width="24" height="24" rx="2" fill="#B45309" stroke="#78350F" strokeWidth="1.5" />
          <polygon points="26,34 38,34 32,42" fill="#F59E0B" />
          {/* Head & Stern Face */}
          <circle cx="32" cy="22" r="9" fill="#B45309" />
          {/* Golden Destar / Udeng Crown */}
          <path d="M 21 16 Q 32 10 43 16 L 41 21 L 23 21 Z" fill="#F59E0B" stroke="#78350F" strokeWidth="1.5" />
          <polygon points="32,10 34,6 30,6" fill="#DC2626" />
          {/* Keris Naga Pusaka with Wavy Luk Blade */}
          <path d="M 48 20 Q 51 26 48 32 Q 51 38 48 44 L 48 54" fill="none" stroke="#D1D5DB" strokeWidth="3" />
          <rect x="45" y="52" width="6" height="6" rx="1" fill="#F59E0B" />
        </svg>
      );
      break;

    case 'alexander':
      // Alexander the Great (SR) - Macedonian crested bronze helm, hoplite shield
      svgContent = (
        <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <circle cx="32" cy="32" r="30" fill="#1E293B" stroke="#8B5CF6" strokeWidth="2" />
          <rect x="22" y="34" width="20" height="24" rx="2" fill="#D97706" stroke="#92400E" strokeWidth="1.5" />
          <circle cx="32" cy="22" r="9" fill="#FED7AA" />
          {/* Macedonian Crested Helmet */}
          <path d="M 23 16 Q 32 10 41 16" fill="#F59E0B" />
          <polygon points="32,2 35,14 29,14" fill="#DC2626" />
          {/* Greek Sun Shield */}
          <circle cx="16" cy="42" r="11" fill="#F59E0B" stroke="#B45309" strokeWidth="2" />
          <circle cx="16" cy="42" r="4" fill="#1E293B" />
          {/* Spear */}
          <line x1="48" y1="6" x2="48" y2="56" stroke="#E2E8F0" strokeWidth="2.5" />
        </svg>
      );
      break;

    case 'musashi':
      // Miyamoto Musashi (SR) - Samurai topknot, dual katanas
      svgContent = (
        <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <circle cx="32" cy="32" r="30" fill="#1E293B" stroke="#8B5CF6" strokeWidth="2" />
          {/* Dark Kimono */}
          <rect x="20" y="34" width="24" height="24" rx="2" fill="#1E293B" stroke="#475569" strokeWidth="1.5" />
          <circle cx="32" cy="22" r="8.5" fill="#FED7AA" />
          {/* Samurai Hair & Topknot */}
          <path d="M 23 16 Q 32 12 41 16 L 41 21 L 23 21 Z" fill="#0F172A" />
          <circle cx="32" cy="9" r="3" fill="#0F172A" />
          {/* Crossed Dual Katanas */}
          <line x1="14" y1="16" x2="30" y2="54" stroke="#E2E8F0" strokeWidth="2.5" />
          <line x1="50" y1="16" x2="34" y2="54" stroke="#E2E8F0" strokeWidth="2.5" />
        </svg>
      );
      break;

    case 'sun_tzu':
      // Sun Tzu (SR) - Chinese scholar-general robe, bamboo scroll
      svgContent = (
        <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <circle cx="32" cy="32" r="30" fill="#1E293B" stroke="#8B5CF6" strokeWidth="2" />
          <rect x="20" y="34" width="24" height="24" rx="2" fill="#065F46" stroke="#047857" strokeWidth="1.5" />
          <circle cx="32" cy="22" r="8.5" fill="#FED7AA" />
          {/* Scholar Cap */}
          <rect x="24" y="10" width="16" height="6" fill="#1E293B" />
          <rect x="28" y="6" width="8" height="4" fill="#F59E0B" />
          {/* Long Beard */}
          <polygon points="28,28 36,28 32,44" fill="#334155" />
          {/* Bamboo Scroll */}
          <rect x="44" y="36" width="12" height="14" rx="2" fill="#FDE68A" stroke="#B45309" strokeWidth="1.5" />
        </svg>
      );
      break;

    case 'leonidas':
      // Leonidas I (SR) - Spartan bronze helm, red cape, Spartan V-shield
      svgContent = (
        <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <circle cx="32" cy="32" r="30" fill="#1E293B" stroke="#8B5CF6" strokeWidth="2" />
          {/* Red Spartan Cape */}
          <path d="M 16 34 L 48 34 L 52 58 L 12 58 Z" fill="#DC2626" />
          <circle cx="32" cy="22" r="9" fill="#B45309" />
          {/* Spartan Corinthian Bronze Helm */}
          <polygon points="32,3 36,15 28,15" fill="#1E293B" />
          <path d="M 23 20 L 41 20 L 39 12 L 25 12 Z" fill="#B45309" />
          {/* Bronze Lambda Shield (Spartan V) */}
          <circle cx="18" cy="42" r="11" fill="#B45309" stroke="#78350F" strokeWidth="2" />
          <polygon points="18,34 23,46 13,46" fill="none" stroke="#F59E0B" strokeWidth="2" />
          <line x1="48" y1="8" x2="48" y2="58" stroke="#E2E8F0" strokeWidth="2.5" />
        </svg>
      );
      break;

    case 'gorg':
      // Gorg Si Pemburu (R) - Mammoth bone pelt, flint spear
      svgContent = (
        <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <circle cx="32" cy="32" r="30" fill="#1E293B" stroke="#3B82F6" strokeWidth="2" />
          <rect x="22" y="34" width="20" height="24" rx="2" fill="#78350F" />
          <circle cx="32" cy="22" r="9" fill="#FED7AA" />
          {/* Wild Caveman Hair & Bone */}
          <ellipse cx="32" cy="15" rx="11" ry="6" fill="#451A03" />
          <rect x="26" y="14" width="12" height="3" fill="#F8FAFC" rx="1.5" />
          {/* Flint Spear */}
          <line x1="48" y1="8" x2="48" y2="56" stroke="#78350F" strokeWidth="2.5" />
          <polygon points="48,4 52,11 44,11" fill="#94A3B8" />
        </svg>
      );
      break;

    case 'shaman':
      // Dukun Batu Kirana (R) - Feather headdress, glowing totem staff
      svgContent = (
        <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <circle cx="32" cy="32" r="30" fill="#1E293B" stroke="#3B82F6" strokeWidth="2" />
          <rect x="22" y="34" width="20" height="24" rx="2" fill="#451A03" />
          <circle cx="32" cy="22" r="8.5" fill="#FED7AA" />
          {/* Feather Headdress */}
          <polygon points="26,16 28,4 30,16" fill="#DC2626" />
          <polygon points="31,16 33,2 35,16" fill="#0284C7" />
          <polygon points="36,16 38,5 40,16" fill="#F59E0B" />
          {/* Totem Staff with Glowing Orb */}
          <line x1="48" y1="12" x2="48" y2="56" stroke="#78350F" strokeWidth="2.5" />
          <circle cx="48" cy="12" r="4.5" fill="#10B981" />
        </svg>
      );
      break;

    default:
      // Universal Valiant Hero Portrait
      svgContent = (
        <svg viewBox="0 0 64 64" width={size} height={size} style={{ imageRendering: 'pixelated', ...style }}>
          <circle cx="32" cy="32" r="30" fill="#1E293B" stroke="#3B82F6" strokeWidth="2" />
          <rect x="22" y="34" width="20" height="24" rx="2" fill="#475569" stroke="#64748B" strokeWidth="1.5" />
          <circle cx="32" cy="22" r="9" fill="#FED7AA" />
          <polygon points="24,14 32,8 40,14" fill="#3B82F6" />
          <line x1="48" y1="12" x2="48" y2="52" stroke="#E2E8F0" strokeWidth="2.5" />
        </svg>
      );
      break;
  }

  return (
    <div style={{ display: 'inline-flex', alignItems: 'center', justifyContent: 'center' }}>
      {svgContent}
    </div>
  );
}
