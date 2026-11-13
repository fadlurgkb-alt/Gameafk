import React from 'react';

/**
 * Authentic 16-Bit Retro Chibi Pixel Art Commander Visuals (All 22 Historical & Mythical Heroes)
 * Clean, proportional, high-contrast palette, zero distorted vector curves or raw emojis!
 */
export default function CommanderVisual({ id, size = 64, style = {} }) {
  // Helper to draw a pixel rect
  const r = (x, y, w, h, fill) => (
    <rect key={`${x}-${y}-${w}-${h}`} x={x} y={y} width={w} height={h} fill={fill} />
  );

  let pixels = null;

  switch (id) {
    case 'zeus':
      // Zeus (Mythical) - Dewa Petir: White Beard, Golden Laurel, Cyan Lightning Bolt
      pixels = (
        <g>
          {/* Circular Glow Aura */}
          <circle cx="16" cy="16" r="15" fill="#1E1B4B" stroke="#F59E0B" strokeWidth="1" />
          {/* White & Gold Toga Body */}
          {r(10, 18, 12, 11, '#F8FAFC')}
          {r(10, 20, 12, 2, '#F59E0B')}
          {r(9, 21, 2, 7, '#E2E8F0')}
          {r(21, 21, 2, 7, '#E2E8F0')}
          {/* Head & Skin */}
          {r(11, 8, 10, 8, '#FED7AA')}
          {/* White Hair & Majestic Beard */}
          {r(9, 6, 14, 4, '#FFFFFF')}
          {r(9, 8, 3, 5, '#FFFFFF')}
          {r(20, 8, 3, 5, '#FFFFFF')}
          {r(10, 13, 12, 6, '#FFFFFF')}
          {r(12, 19, 8, 3, '#FFFFFF')}
          {/* Golden Laurel Crown */}
          {r(11, 6, 10, 2, '#F59E0B')}
          {r(10, 7, 2, 2, '#FDE047')}
          {r(20, 7, 2, 2, '#FDE047')}
          {/* Cyan Glowing Eyes */}
          {r(12, 10, 2, 2, '#06B6D4')}
          {r(18, 10, 2, 2, '#06B6D4')}
          {/* Cyan Lightning Bolt in Hand */}
          {r(23, 5, 2, 6, '#06B6D4')}
          {r(21, 9, 3, 2, '#67E8F9')}
          {r(21, 11, 2, 6, '#06B6D4')}
          {r(19, 15, 3, 2, '#67E8F9')}
          {r(19, 17, 2, 8, '#06B6D4')}
        </g>
      );
      break;

    case 'athena':
      // Athena (Mythical) - Dewi Kebijaksanaan & Perang: Corinthian Helm, Red Plume, Spear & Shield
      pixels = (
        <g>
          <circle cx="16" cy="16" r="15" fill="#1E1B4B" stroke="#FACC15" strokeWidth="1" />
          {/* Golden Armor Torso */}
          {r(10, 18, 12, 11, '#F59E0B')}
          {r(12, 19, 8, 8, '#FDE047')}
          {r(11, 27, 10, 2, '#B45309')}
          {/* Face */}
          {r(12, 9, 8, 7, '#FED7AA')}
          {/* Golden Corinthian Helmet */}
          {r(10, 7, 12, 4, '#F59E0B')}
          {r(9, 9, 3, 6, '#D97706')}
          {r(20, 9, 3, 6, '#D97706')}
          {/* Red Crest / Plume */}
          {r(15, 2, 3, 6, '#DC2626')}
          {r(14, 4, 5, 3, '#EF4444')}
          {/* Eyes */}
          {r(13, 11, 2, 2, '#1E293B')}
          {r(17, 11, 2, 2, '#1E293B')}
          {/* Spear in Right Hand */}
          {r(24, 2, 2, 27, '#CBD5E1')}
          {r(23, 2, 4, 3, '#FACC15')}
          {/* Round Aegis Shield in Left Hand */}
          {r(4, 15, 7, 9, '#F59E0B')}
          {r(5, 16, 5, 7, '#3B82F6')}
          {r(6, 18, 3, 3, '#FACC15')}
        </g>
      );
      break;

    case 'odin':
      // Odin (Mythical) - Allfather: Horned Helm, Eye Patch, Grey Beard, Blue Cloak, Spear
      pixels = (
        <g>
          <circle cx="16" cy="16" r="15" fill="#0F172A" stroke="#3B82F6" strokeWidth="1" />
          {/* Blue Cloak */}
          {r(9, 17, 14, 12, '#1E3A8A')}
          {r(11, 19, 10, 10, '#1D4ED8')}
          {/* Face */}
          {r(11, 9, 10, 7, '#FED7AA')}
          {/* Long Grey Beard */}
          {r(10, 14, 12, 6, '#94A3B8')}
          {r(12, 19, 8, 4, '#94A3B8')}
          {r(13, 23, 6, 3, '#CBD5E1')}
          {/* Viking Horned Helm */}
          {r(10, 6, 12, 4, '#475569')}
          {r(7, 3, 3, 5, '#F8FAFC')} {/* Left Horn */}
          {r(22, 3, 3, 5, '#F8FAFC')} {/* Right Horn */}
          {/* Eye Patch (Left Eye) & Glowing Blue Right Eye */}
          {r(12, 11, 3, 2, '#020617')}
          {r(17, 11, 2, 2, '#38BDF8')}
          {/* Gungnir Golden Spear */}
          {r(25, 2, 2, 27, '#F59E0B')}
          {r(24, 1, 4, 3, '#FDE047')}
        </g>
      );
      break;

    case 'archon':
      // Cyber-Archon (Mythical) - Android Overlord: Neon Visor, Plasma Core, Obsidian Armor
      pixels = (
        <g>
          <circle cx="16" cy="16" r="15" fill="#020617" stroke="#06B6D4" strokeWidth="1" />
          {/* Obsidian Body Armor */}
          {r(10, 17, 12, 12, '#0F172A')}
          {r(12, 19, 8, 7, '#1E293B')}
          {/* Glowing Plasma Core */}
          {r(14, 21, 4, 4, '#06B6D4')}
          {r(15, 22, 2, 2, '#FFFFFF')}
          {/* Cybernetic Helmet */}
          {r(10, 7, 12, 9, '#1E293B')}
          {r(9, 8, 2, 6, '#06B6D4')}
          {r(21, 8, 2, 6, '#06B6D4')}
          {/* Neon Cyan Visor */}
          {r(11, 10, 10, 3, '#06B6D4')}
          {r(12, 11, 8, 1, '#FFFFFF')}
          {/* Holographic Halo Ring */}
          {r(11, 3, 10, 2, '#06B6D4')}
          {r(8, 4, 3, 2, '#06B6D4')}
          {r(21, 4, 3, 2, '#06B6D4')}
        </g>
      );
      break;

    case 'arthur':
      // Arthur Pendragon (SSR) - Raja Britania: Golden Crown, Blue Tunic, Shining Excalibur
      pixels = (
        <g>
          <circle cx="16" cy="16" r="15" fill="#1E293B" stroke="#FACC15" strokeWidth="1" />
          {/* Royal Blue Tunic & Armor */}
          {r(10, 17, 12, 12, '#2563EB')}
          {r(12, 19, 8, 8, '#3B82F6')}
          {r(10, 24, 12, 2, '#FACC15')} {/* Gold belt */}
          {/* Face */}
          {r(11, 9, 10, 7, '#FED7AA')}
          {/* Blonde Hair */}
          {r(9, 8, 3, 6, '#FDE047')}
          {r(20, 8, 3, 6, '#FDE047')}
          {/* Golden Crown */}
          {r(10, 6, 12, 3, '#FACC15')}
          {r(10, 4, 3, 2, '#FDE047')}
          {r(14, 3, 4, 3, '#FDE047')}
          {r(19, 4, 3, 2, '#FDE047')}
          {/* Eyes */}
          {r(13, 11, 2, 2, '#1E3A8A')}
          {r(17, 11, 2, 2, '#1E3A8A')}
          {/* Excalibur Sword */}
          {r(23, 6, 2, 18, '#F8FAFC')}
          {r(21, 18, 6, 2, '#FACC15')}
          {r(23, 20, 2, 4, '#78350F')}
        </g>
      );
      break;

    case 'joan':
      // Joan of Arc (SSR) - Ksatria Suci: Silver Plate Armor, White-Gold Banner
      pixels = (
        <g>
          <circle cx="16" cy="16" r="15" fill="#1E293B" stroke="#E2E8F0" strokeWidth="1" />
          {/* Silver Plate Armor */}
          {r(10, 17, 12, 12, '#94A3B8')}
          {r(12, 18, 8, 9, '#CBD5E1')}
          {/* Face */}
          {r(11, 9, 10, 7, '#FED7AA')}
          {/* Blonde Bob Haircut */}
          {r(9, 7, 14, 4, '#FDE047')}
          {r(8, 9, 3, 6, '#FACC15')}
          {r(21, 9, 3, 6, '#FACC15')}
          {/* Eyes */}
          {r(13, 11, 2, 2, '#2563EB')}
          {r(17, 11, 2, 2, '#2563EB')}
          {/* Fleur-de-lis Banner in Right Hand */}
          {r(23, 3, 2, 26, '#78350F')}
          {r(24, 4, 6, 8, '#F8FAFC')}
          {r(26, 6, 2, 4, '#FACC15')}
        </g>
      );
      break;

    case 'gilgamesh':
      // Gilgamesh (SSR) - Raja Uruk: Golden Full Armor, Crimson Cape, Jeweled Crown
      pixels = (
        <g>
          <circle cx="16" cy="16" r="15" fill="#3B0764" stroke="#FACC15" strokeWidth="1" />
          {/* Crimson Cape behind */}
          {r(7, 16, 18, 13, '#991B1B')}
          {/* Shining Gold Armor */}
          {r(10, 17, 12, 12, '#F59E0B')}
          {r(12, 18, 8, 9, '#FDE047')}
          {/* Face */}
          {r(11, 9, 10, 7, '#FED7AA')}
          {/* Golden Spiky Hair */}
          {r(9, 5, 14, 4, '#FACC15')}
          {r(8, 8, 3, 5, '#FDE047')}
          {r(21, 8, 3, 5, '#FDE047')}
          {/* Piercing Crimson Eyes */}
          {r(13, 11, 2, 2, '#DC2626')}
          {r(17, 11, 2, 2, '#DC2626')}
        </g>
      );
      break;

    case 'cleopatra':
      // Cleopatra (SSR) - Ratu Mesir: Nemes Headdress, Golden Collar, Turquoise Gown
      pixels = (
        <g>
          <circle cx="16" cy="16" r="15" fill="#0C4A6E" stroke="#FACC15" strokeWidth="1" />
          {/* Turquoise Gown */}
          {r(10, 18, 12, 11, '#06B6D4')}
          {r(9, 17, 14, 3, '#FACC15')} {/* Golden collar */}
          {/* Face */}
          {r(11, 9, 10, 7, '#FED7AA')}
          {/* Black Egyptian Hair & Headdress with Gold Bands */}
          {r(8, 7, 16, 4, '#0F172A')}
          {r(7, 9, 4, 9, '#0F172A')}
          {r(21, 9, 4, 9, '#0F172A')}
          {r(7, 12, 4, 1.5, '#FACC15')}
          {r(21, 12, 4, 1.5, '#FACC15')}
          {/* Golden Cobra Headdress */}
          {r(15, 5, 2, 3, '#FACC15')}
          {/* Kohl-lined Eyes */}
          {r(12, 11, 3, 2, '#0F172A')}
          {r(17, 11, 3, 2, '#0F172A')}
        </g>
      );
      break;

    case 'genghis':
      // Genghis Khan (SSR) - Panglima Stepa: Fur Nomadic Hat, Leather Armor, Recurve Bow
      pixels = (
        <g>
          <circle cx="16" cy="16" r="15" fill="#1C1917" stroke="#EA580C" strokeWidth="1" />
          {/* Leather Nomad Armor */}
          {r(10, 17, 12, 12, '#78350F')}
          {r(12, 19, 8, 8, '#92400E')}
          {/* Face */}
          {r(11, 9, 10, 7, '#FED7AA')}
          {/* Steppe Fur Hat with Feather */}
          {r(9, 5, 14, 5, '#A8A29E')}
          {r(8, 9, 16, 2, '#78350F')}
          {r(15, 2, 2, 4, '#F8FAFC')} {/* Feather */}
          {/* Mustache */}
          {r(12, 13, 8, 2, '#1C1917')}
          {/* Eyes */}
          {r(13, 11, 2, 1, '#1C1917')}
          {r(17, 11, 2, 1, '#1C1917')}
          {/* Recurve Bow on Left */}
          {r(5, 10, 2, 16, '#92400E')}
          {r(7, 12, 1, 12, '#FFFFFF')}
        </g>
      );
      break;

    case 'tesla':
      // Nikola Tesla (SSR) - Maestro Kilat: Dark Suit, Mustache, Lightning Coil in Hand
      pixels = (
        <g>
          <circle cx="16" cy="16" r="15" fill="#0F172A" stroke="#38BDF8" strokeWidth="1" />
          {/* Victorian Suit */}
          {r(10, 17, 12, 12, '#1E293B')}
          {r(14, 18, 4, 6, '#F8FAFC')}
          {r(15, 20, 2, 4, '#DC2626')} {/* Red tie */}
          {/* Face */}
          {r(11, 9, 10, 7, '#FED7AA')}
          {/* Sleek Black Hair */}
          {r(9, 6, 14, 4, '#0F172A')}
          {r(8, 8, 3, 5, '#0F172A')}
          {r(21, 8, 3, 5, '#0F172A')}
          {/* Signature Mustache */}
          {r(13, 13, 6, 1.5, '#0F172A')}
          {/* Eyes */}
          {r(13, 11, 2, 2, '#0F172A')}
          {r(17, 11, 2, 2, '#0F172A')}
          {/* Mini Tesla Coil in Hand with Sparks */}
          {r(23, 17, 4, 8, '#94A3B8')}
          {r(22, 15, 6, 2, '#F59E0B')}
          {r(23, 12, 3, 3, '#38BDF8')}
        </g>
      );
      break;

    case 'gajah_mada':
      // Gajah Mada (SR) - Mahapatih Majapahit: Destar Emas Jawa, Kain Batik, Keris Luk Emas
      pixels = (
        <g>
          <circle cx="16" cy="16" r="15" fill="#451A03" stroke="#FACC15" strokeWidth="1" />
          {/* Bronze Torso Armor & Batik Sash */}
          {r(10, 17, 12, 12, '#B45309')}
          {r(10, 24, 12, 5, '#78350F')}
          {r(11, 18, 3, 11, '#FACC15')} {/* Golden sash */}
          {/* Face */}
          {r(11, 9, 10, 7, '#D97706')}
          {/* Traditional Javanese Golden Destar Headdress */}
          {r(9, 6, 14, 4, '#78350F')}
          {r(10, 8, 12, 2, '#FACC15')}
          {r(15, 4, 2, 3, '#FDE047')}
          {/* Fierce Eyes & Mustache */}
          {r(13, 11, 2, 2, '#1E293B')}
          {r(17, 11, 2, 2, '#1E293B')}
          {r(12, 13, 8, 2, '#1E293B')}
          {/* Golden Wavy Keris */}
          {r(23, 12, 3, 4, '#FDE047')}
          {r(24, 16, 2, 3, '#FACC15')}
          {r(23, 19, 3, 4, '#FDE047')}
          {r(24, 23, 2, 4, '#78350F')} {/* Handle */}
        </g>
      );
      break;

    case 'alexander':
      // Alexander the Great (SR) - Penakluk Makedonia: Lion Helm, Bronze Cuirass, Kopis Sword
      pixels = (
        <g>
          <circle cx="16" cy="16" r="15" fill="#1E293B" stroke="#F59E0B" strokeWidth="1" />
          {/* Bronze Muscle Cuirass */}
          {r(10, 17, 12, 12, '#D97706')}
          {r(12, 18, 8, 9, '#F59E0B')}
          {/* Face */}
          {r(11, 9, 10, 7, '#FED7AA')}
          {/* Lion Crest Bronze Helmet */}
          {r(10, 6, 12, 4, '#F59E0B')}
          {r(9, 8, 3, 6, '#B45309')}
          {r(20, 8, 3, 6, '#B45309')}
          {r(14, 2, 4, 5, '#EF4444')} {/* Plume */}
          {/* Eyes */}
          {r(13, 11, 2, 2, '#1E293B')}
          {r(17, 11, 2, 2, '#1E293B')}
          {/* Sword in Hand */}
          {r(23, 8, 2, 17, '#E2E8F0')}
          {r(22, 19, 4, 2, '#F59E0B')}
        </g>
      );
      break;

    case 'musashi':
      // Miyamoto Musashi (SR) - Master Ronin: Topknot, Haori Kimono, Dual Katanas
      pixels = (
        <g>
          <circle cx="16" cy="16" r="15" fill="#18181B" stroke="#DC2626" strokeWidth="1" />
          {/* Ronin Grey Kimono */}
          {r(10, 17, 12, 12, '#3F3F46')}
          {r(12, 18, 4, 8, '#F8FAFC')}
          {r(10, 23, 12, 2, '#18181B')}
          {/* Face */}
          {r(11, 9, 10, 7, '#FED7AA')}
          {/* Topknot Chonmage Hair */}
          {r(9, 6, 14, 4, '#18181B')}
          {r(15, 3, 3, 4, '#18181B')}
          {/* Eyes */}
          {r(13, 11, 2, 1.5, '#18181B')}
          {r(17, 11, 2, 1.5, '#18181B')}
          {/* Dual Katana: Main Long Katana */}
          {r(23, 4, 2, 22, '#F8FAFC')}
          {r(22, 18, 4, 2, '#F59E0B')}
          {/* Offhand Short Wakizashi */}
          {r(6, 14, 2, 12, '#F8FAFC')}
        </g>
      );
      break;

    case 'sun_tzu':
      // Sun Tzu (SR) - Ahli Strategi Perang: Green Scholar Hanfu, Bamboo Scrolls
      pixels = (
        <g>
          <circle cx="16" cy="16" r="15" fill="#064E3B" stroke="#10B981" strokeWidth="1" />
          {/* Emerald Scholar Hanfu */}
          {r(10, 17, 12, 12, '#047857')}
          {r(12, 18, 8, 9, '#10B981')}
          {/* Face */}
          {r(11, 9, 10, 7, '#FED7AA')}
          {/* Traditional Black Scholar Cap */}
          {r(11, 5, 10, 4, '#0F172A')}
          {r(9, 8, 14, 2, '#0F172A')}
          {/* Sage Beard */}
          {r(13, 14, 6, 5, '#F8FAFC')}
          {/* Eyes */}
          {r(13, 11, 2, 1.5, '#0F172A')}
          {r(17, 11, 2, 1.5, '#0F172A')}
          {/* Bamboo War Scroll in Hand */}
          {r(22, 16, 5, 8, '#FBBF24')}
          {r(22, 18, 5, 1, '#78350F')}
          {r(22, 21, 5, 1, '#78350F')}
        </g>
      );
      break;

    case 'leonidas':
      // Leonidas (SR) - Raja Sparta: Spartan Corinthian Helm, Round Hoplon Shield, Spear
      pixels = (
        <g>
          <circle cx="16" cy="16" r="15" fill="#450A0A" stroke="#EF4444" strokeWidth="1" />
          {/* Red Spartan Cape & Bronze Cuirass */}
          {r(8, 17, 16, 12, '#B91C1C')}
          {r(11, 18, 10, 10, '#D97706')}
          {/* Face */}
          {r(12, 9, 8, 7, '#FED7AA')}
          {/* Heavy Black Spartan Beard */}
          {r(11, 13, 10, 6, '#1C1917')}
          {/* Corinthian Bronze Helm with Giant Red Transverse Crest */}
          {r(10, 6, 12, 4, '#D97706')}
          {r(8, 2, 16, 4, '#DC2626')} {/* Crest */}
          {/* Fierce Eyes */}
          {r(13, 11, 2, 2, '#FFFFFF')}
          {r(14, 11, 1, 1, '#000000')}
          {r(17, 11, 2, 2, '#FFFFFF')}
          {r(17, 11, 1, 1, '#000000')}
          {/* Hoplon Round Shield with Lambda (Λ) */}
          {r(4, 15, 7, 9, '#D97706')}
          {r(6, 17, 3, 5, '#DC2626')}
          {/* Spear in Hand */}
          {r(24, 3, 2, 26, '#CBD5E1')}
        </g>
      );
      break;

    default:
      // Primitive Hunters, Shamans, and Other R Heroes
      pixels = (
        <g>
          <circle cx="16" cy="16" r="15" fill="#334155" stroke="#94A3B8" strokeWidth="1" />
          {/* Animal Fur Tunic */}
          {r(10, 17, 12, 12, '#78350F')}
          {r(11, 18, 10, 9, '#92400E')}
          {/* Face */}
          {r(11, 9, 10, 7, '#FED7AA')}
          {/* Wild Dark Hair with Bone or Headband */}
          {r(9, 6, 14, 4, '#1E293B')}
          {r(8, 8, 3, 5, '#1E293B')}
          {r(21, 8, 3, 5, '#1E293B')}
          {r(13, 5, 6, 2, '#F8FAFC')} {/* Bone accessory */}
          {/* Eyes */}
          {r(13, 11, 2, 2, '#0F172A')}
          {r(17, 11, 2, 2, '#0F172A')}
          {/* Primitive Spear / Club */}
          {r(23, 6, 2, 22, '#78350F')}
          {r(22, 5, 4, 3, '#94A3B8')}
        </g>
      );
      break;
  }

  return (
    <svg
      viewBox="0 0 32 32"
      width={size}
      height={size}
      style={{ imageRendering: 'pixelated', display: 'block', ...style }}
    >
      {pixels}
    </svg>
  );
}
