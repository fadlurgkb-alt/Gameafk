import { ERA_COLORS } from '../components/BuildingVisual';

/**
 * Draws High-End Pixel-Art Buildings directly onto HTML5 Canvas
 * Visually 100% matched with the Building Cards in Menu & Modals!
 */
export function drawBuildingSprite(ctx, bId, eraId, level, bx, by, bw, bh, isHovered, tick) {
  const p = ERA_COLORS[eraId] || ERA_COLORS.primitif;

  // 1. Drop Shadow
  ctx.fillStyle = 'rgba(0, 0, 0, 0.4)';
  ctx.beginPath();
  ctx.ellipse(bx + bw / 2, by + bh - 2, bw * 0.52, bh * 0.22, 0, 0, Math.PI * 2);
  ctx.fill();

  // 2. Hover Outline
  if (isHovered) {
    ctx.strokeStyle = '#FACC15';
    ctx.lineWidth = 2.5;
    ctx.strokeRect(bx - 3, by - 3, bw + 6, bh + 6);
  }

  // 3. Building-Specific Detailed Architecture
  if (bId === 'town_hall') {
    // Grand Headquarters (Balai Kota)
    // Foundation
    ctx.fillStyle = p.walls;
    ctx.fillRect(bx + 6, by + 24, bw - 12, bh - 26);
    ctx.strokeStyle = p.wallTrim;
    ctx.lineWidth = 1.5;
    ctx.strokeRect(bx + 6, by + 24, bw - 12, bh - 26);

    // Columns
    ctx.fillStyle = p.wallTrim;
    ctx.fillRect(bx + 12, by + 26, 6, bh - 28);
    ctx.fillRect(bx + bw - 18, by + 26, 6, bh - 28);

    // Grand Tiered Roof
    ctx.fillStyle = p.roof;
    ctx.beginPath();
    ctx.moveTo(bx + bw / 2, by + 4);
    ctx.lineTo(bx + bw - 2, by + 25);
    ctx.lineTo(bx + 2, by + 25);
    ctx.closePath();
    ctx.fill();
    ctx.strokeStyle = p.wallTrim;
    ctx.stroke();

    // Roof Highlight
    ctx.fillStyle = p.roofHighlight;
    ctx.fillRect(bx + bw / 2 - 12, by + 12, 24, 4);

    // Archway Grand Door
    ctx.fillStyle = p.door;
    ctx.beginPath();
    ctx.arc(bx + bw / 2, by + bh - 16, 7, Math.PI, 0);
    ctx.rect(bx + bw / 2 - 7, by + bh - 16, 14, 14);
    ctx.fill();
    ctx.strokeStyle = p.wallTrim;
    ctx.stroke();

    // Windows with warm light
    ctx.fillStyle = p.accent;
    ctx.fillRect(bx + 16, by + 32, 6, 6);
    ctx.fillRect(bx + bw - 22, by + 32, 6, 6);

    // Banner / Flag
    ctx.fillStyle = p.accent;
    ctx.fillRect(bx + bw / 2 - 1, by - 8, 2, 12);
    ctx.fillStyle = p.banner;
    ctx.beginPath();
    ctx.moveTo(bx + bw / 2 + 1, by - 8);
    ctx.lineTo(bx + bw / 2 + 12, by - 4);
    ctx.lineTo(bx + bw / 2 + 1, by);
    ctx.closePath();
    ctx.fill();
  } else if (bId === 'barak_militer') {
    // Military Barracks
    ctx.fillStyle = p.walls;
    ctx.fillRect(bx + 6, by + 18, bw - 12, bh - 20);
    ctx.strokeStyle = p.wallTrim;
    ctx.strokeRect(bx + 6, by + 18, bw - 12, bh - 20);

    // Sloped Battle Roof
    ctx.fillStyle = p.roof;
    ctx.beginPath();
    ctx.moveTo(bx + bw / 2, by + 6);
    ctx.lineTo(bx + bw - 2, by + 19);
    ctx.lineTo(bx + 2, by + 19);
    ctx.closePath();
    ctx.fill();
    ctx.strokeStyle = p.wallTrim;
    ctx.stroke();

    // Archery Target Prop
    ctx.fillStyle = '#FFFFFF';
    ctx.beginPath();
    ctx.arc(bx + bw - 10, by + bh - 10, 6, 0, Math.PI * 2);
    ctx.fill();
    ctx.fillStyle = '#EF4444';
    ctx.beginPath();
    ctx.arc(bx + bw - 10, by + bh - 10, 3, 0, Math.PI * 2);
    ctx.fill();

    // Door
    ctx.fillStyle = p.door;
    ctx.fillRect(bx + 12, by + bh - 16, 10, 14);

    // Red Command Flag
    ctx.fillStyle = '#EF4444';
    ctx.fillRect(bx + 8, by - 2, 8, 6);
  } else if (bId === 'gudang') {
    // Vaulted Storage Barn
    ctx.fillStyle = p.walls;
    ctx.fillRect(bx + 6, by + 18, bw - 12, bh - 20);
    ctx.strokeStyle = p.wallTrim;
    ctx.strokeRect(bx + 6, by + 18, bw - 12, bh - 20);

    // Barn Gambrel Roof
    ctx.fillStyle = p.roof;
    ctx.beginPath();
    ctx.moveTo(bx + bw / 2, by + 4);
    ctx.lineTo(bx + bw - 4, by + 19);
    ctx.lineTo(bx + 4, by + 19);
    ctx.closePath();
    ctx.fill();
    ctx.strokeStyle = p.wallTrim;
    ctx.stroke();

    // Double Wooden Doors with Cross Iron
    ctx.fillStyle = p.door;
    ctx.fillRect(bx + bw / 2 - 8, by + bh - 18, 16, 16);
    ctx.strokeStyle = p.accent;
    ctx.lineWidth = 1;
    ctx.beginPath();
    ctx.moveTo(bx + bw / 2 - 8, by + bh - 18);
    ctx.lineTo(bx + bw / 2 + 8, by + bh - 2);
    ctx.moveTo(bx + bw / 2 + 8, by + bh - 18);
    ctx.lineTo(bx + bw / 2 - 8, by + bh - 2);
    ctx.stroke();

    // Grain Crate & Barrel Props outside
    ctx.fillStyle = '#B45309';
    ctx.fillRect(bx + bw - 14, by + bh - 12, 8, 10);
    ctx.fillStyle = '#D97706';
    ctx.fillRect(bx + 6, by + bh - 10, 7, 8);
  } else if (bId === 'tembok') {
    // Fortress Gatehouse
    ctx.fillStyle = p.walls;
    ctx.fillRect(bx + 2, by + 14, bw - 4, bh - 16);
    ctx.strokeStyle = p.wallTrim;
    ctx.strokeRect(bx + 2, by + 14, bw - 4, bh - 16);

    // Crenellations (Battlement top)
    ctx.fillStyle = p.wallTrim;
    for (let c = 2; c < bw - 4; c += 10) {
      ctx.fillRect(bx + c, by + 6, 6, 9);
    }

    // High Arch Portcullis
    ctx.fillStyle = '#0F172A';
    ctx.beginPath();
    ctx.arc(bx + bw / 2, by + bh - 20, 9, Math.PI, 0);
    ctx.rect(bx + bw / 2 - 9, by + bh - 20, 18, 18);
    ctx.fill();

    // Portcullis Grate
    ctx.strokeStyle = '#94A3B8';
    ctx.lineWidth = 1.5;
    ctx.beginPath();
    ctx.moveTo(bx + bw / 2 - 4, by + bh - 26);
    ctx.lineTo(bx + bw / 2 - 4, by + bh - 2);
    ctx.moveTo(bx + bw / 2 + 4, by + bh - 26);
    ctx.lineTo(bx + bw / 2 + 4, by + bh - 2);
    ctx.moveTo(bx + bw / 2 - 9, by + bh - 12);
    ctx.lineTo(bx + bw / 2 + 9, by + bh - 12);
    ctx.stroke();
  } else if (bId === 'hospital') {
    // Medical Center
    ctx.fillStyle = '#F8FAFC';
    ctx.fillRect(bx + 6, by + 18, bw - 12, bh - 20);
    ctx.strokeStyle = p.wallTrim;
    ctx.strokeRect(bx + 6, by + 18, bw - 12, bh - 20);

    // Green/Clay Roof
    ctx.fillStyle = eraId === 'kerajaan' ? '#059669' : p.roof;
    ctx.beginPath();
    ctx.moveTo(bx + bw / 2, by + 6);
    ctx.lineTo(bx + bw - 2, by + 19);
    ctx.lineTo(bx + 2, by + 19);
    ctx.closePath();
    ctx.fill();

    // Red Cross Sign
    ctx.fillStyle = '#EF4444';
    ctx.fillRect(bx + bw / 2 - 2, by + 24, 4, 12);
    ctx.fillRect(bx + bw / 2 - 6, by + 28, 12, 4);

    // Door
    ctx.fillStyle = p.door;
    ctx.fillRect(bx + bw / 2 - 5, by + bh - 12, 10, 10);
  } else if (bId === 'tavern') {
    // Tavern
    ctx.fillStyle = p.walls;
    ctx.fillRect(bx + 6, by + 18, bw - 12, bh - 20);
    ctx.strokeStyle = p.wallTrim;
    ctx.strokeRect(bx + 6, by + 18, bw - 12, bh - 20);

    // Peaked Roof
    ctx.fillStyle = p.roof;
    ctx.beginPath();
    ctx.moveTo(bx + bw / 2, by + 6);
    ctx.lineTo(bx + bw - 2, by + 19);
    ctx.lineTo(bx + 2, by + 19);
    ctx.closePath();
    ctx.fill();

    // Hanging Lantern
    ctx.fillStyle = '#FACC15';
    ctx.beginPath();
    ctx.arc(bx + 12, by + 28, 3.5, 0, Math.PI * 2);
    ctx.fill();

    // Door
    ctx.fillStyle = p.door;
    ctx.fillRect(bx + bw / 2 - 6, by + bh - 14, 12, 12);
  } else if (bId === 'dungeon') {
    // Obsidian Spire Portal
    ctx.fillStyle = '#1E1B4B';
    ctx.beginPath();
    ctx.moveTo(bx + 6, by + 4);
    ctx.lineTo(bx + 16, by + bh - 4);
    ctx.lineTo(bx + 2, by + bh - 4);
    ctx.closePath();
    ctx.fill();
    ctx.beginPath();
    ctx.moveTo(bx + bw - 6, by + 4);
    ctx.lineTo(bx + bw - 2, by + bh - 4);
    ctx.lineTo(bx + bw - 16, by + bh - 4);
    ctx.closePath();
    ctx.fill();

    // Swirling Void Vortex
    const vortexRadius = (Math.sin(tick * 0.1) * 3) + 12;
    ctx.fillStyle = '#8B5CF6';
    ctx.beginPath();
    ctx.arc(bx + bw / 2, by + bh / 2 + 2, vortexRadius, 0, Math.PI * 2);
    ctx.fill();
    ctx.fillStyle = '#C084FC';
    ctx.beginPath();
    ctx.arc(bx + bw / 2, by + bh / 2 + 2, vortexRadius * 0.5, 0, Math.PI * 2);
    ctx.fill();
  } else if (bId === 'market') {
    // Striped Merchant Stalls
    ctx.fillStyle = p.walls;
    ctx.fillRect(bx + 8, by + 26, bw - 16, bh - 28);
    // Striped Awning Canopy
    ctx.fillStyle = '#DC2626';
    ctx.beginPath();
    ctx.moveTo(bx + 4, by + 12);
    ctx.lineTo(bx + bw - 4, by + 12);
    ctx.lineTo(bx + bw - 8, by + 26);
    ctx.lineTo(bx + 8, by + 26);
    ctx.closePath();
    ctx.fill();
    ctx.fillStyle = '#F8FAFC';
    ctx.fillRect(bx + 14, by + 12, 8, 14);
    ctx.fillRect(bx + bw - 22, by + 12, 8, 14);

    // Market Crates
    ctx.fillStyle = '#D97706';
    ctx.fillRect(bx + 12, by + bh - 10, 6, 6);
    ctx.fillRect(bx + bw - 18, by + bh - 10, 6, 6);
  } else if (bId === 'pertanian') {
    // Farm Soil Plots & Wheat
    ctx.fillStyle = '#451A03';
    ctx.fillRect(bx + 4, by + 20, bw - 8, bh - 22);
    ctx.fillStyle = '#FBBF24';
    ctx.fillRect(bx + 8, by + 24, bw - 16, 3);
    ctx.fillRect(bx + 8, by + 30, bw - 16, 3);
    ctx.fillRect(bx + 8, by + 36, bw - 16, 3);
    // Small Silo
    ctx.fillStyle = p.walls;
    ctx.fillRect(bx + bw - 18, by + 6, 12, 16);
    ctx.fillStyle = p.roof;
    ctx.beginPath();
    ctx.moveTo(bx + bw - 12, by + 1);
    ctx.lineTo(bx + bw - 6, by + 7);
    ctx.lineTo(bx + bw - 18, by + 7);
    ctx.closePath();
    ctx.fill();
  } else if (bId === 'pertambangan') {
    // Quarry Mountain Cave
    ctx.fillStyle = '#57534E';
    ctx.beginPath();
    ctx.moveTo(bx + bw / 2, by + 4);
    ctx.lineTo(bx + bw - 2, by + bh - 2);
    ctx.lineTo(bx + 2, by + bh - 2);
    ctx.closePath();
    ctx.fill();
    ctx.fillStyle = '#0C0A09';
    ctx.beginPath();
    ctx.arc(bx + bw / 2, by + bh - 10, 9, Math.PI, 0);
    ctx.fill();
    // Ore glints
    ctx.fillStyle = '#FACC15';
    ctx.fillRect(bx + bw / 2 - 4, by + bh - 12, 2, 2);
    ctx.fillStyle = '#38BDF8';
    ctx.fillRect(bx + bw / 2 + 3, by + bh - 11, 2, 2);
  } else {
    // Default Clean Pixel Structure
    ctx.fillStyle = p.walls;
    ctx.fillRect(bx + 6, by + 18, bw - 12, bh - 20);
    ctx.fillStyle = p.roof;
    ctx.beginPath();
    ctx.moveTo(bx + bw / 2, by + 6);
    ctx.lineTo(bx + bw - 2, by + 19);
    ctx.lineTo(bx + 2, by + 19);
    ctx.closePath();
    ctx.fill();
    ctx.fillStyle = p.door;
    ctx.fillRect(bx + bw / 2 - 5, by + bh - 12, 10, 10);
  }
}
