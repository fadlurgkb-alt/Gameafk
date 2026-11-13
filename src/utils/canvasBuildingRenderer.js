import { ERA_COLORS } from '../components/BuildingVisual';

/**
 * Draws Authentic 2000s Farming & Village Pixel-Art Buildings onto HTML5 Canvas.
 * Identically matched with the Building Cards in Menu & RPG Modals!
 */
export function drawBuildingSprite(ctx, bId, eraId, level, bx, by, bw, bh, isHovered, tick) {
  const p = ERA_COLORS[eraId] || ERA_COLORS.primitif;

  // 1. Soft Isometric Drop Shadow on Grass
  ctx.fillStyle = 'rgba(0, 0, 0, 0.42)';
  ctx.beginPath();
  ctx.ellipse(bx + bw / 2, by + bh - 2, bw * 0.52, bh * 0.20, 0, 0, Math.PI * 2);
  ctx.fill();

  // 2. Hover / Selected Golden Pixel Frame
  if (isHovered) {
    ctx.strokeStyle = '#FACC15';
    ctx.lineWidth = 2.5;
    ctx.strokeRect(bx - 3, by - 3, bw + 6, bh + 6);
  }

  // Animated chimney smoke puffs
  const smokePuff = (tick * 0.05) % 1;
  const smokeY1 = (by + 4) - smokePuff * 14;
  const smokeY2 = (by - 3) - smokePuff * 12;

  // =========================================================================
  // (A) TOWN HALL / BALAI DESA (Grand Farmhouse / Manor)
  // =========================================================================
  if (bId === 'town_hall') {
    // Chimney with rising animated smoke
    ctx.fillStyle = p.chimney;
    ctx.fillRect(bx + bw - 16, by + 8, 8, 16);
    ctx.fillStyle = p.roofDark;
    ctx.fillRect(bx + bw - 18, by + 7, 12, 3);

    ctx.fillStyle = 'rgba(226, 232, 240, 0.7)';
    ctx.beginPath();
    ctx.arc(bx + bw - 12 + Math.sin(tick * 0.1) * 2, smokeY1, 4, 0, Math.PI * 2);
    ctx.fill();
    ctx.beginPath();
    ctx.arc(bx + bw - 10 + Math.sin(tick * 0.08) * 3, smokeY2, 5.5, 0, Math.PI * 2);
    ctx.fill();

    // Cobblestone Foundation
    ctx.fillStyle = p.cobble;
    ctx.fillRect(bx + 6, by + bh - 14, bw - 12, 14);
    ctx.fillStyle = p.cobbleDark;
    ctx.fillRect(bx + 6, by + bh - 14, bw - 12, 2);
    for (let cX = bx + 12; cX < bx + bw - 12; cX += 10) {
      ctx.fillRect(cX, by + bh - 8, 2, 8);
    }

    // Wooden Plank Wall
    ctx.fillStyle = p.walls;
    ctx.fillRect(bx + 8, by + 26, bw - 16, bh - 38);
    // Plank horizontal grooves
    ctx.fillStyle = p.wallsDark;
    for (let py = by + 32; py < by + bh - 14; py += 7) {
      ctx.fillRect(bx + 8, py, bw - 16, 1.5);
    }
    // Corner timber beams
    ctx.fillRect(bx + 8, by + 26, 5, bh - 38);
    ctx.fillRect(bx + bw - 13, by + 26, 5, bh - 38);

    // Gabled Roof with Layered Shingles
    ctx.fillStyle = p.roof;
    ctx.beginPath();
    ctx.moveTo(bx + bw / 2, by + 4);
    ctx.lineTo(bx + bw - 2, by + 28);
    ctx.lineTo(bx + 2, by + 28);
    ctx.closePath();
    ctx.fill();

    // Shingle rows
    ctx.fillStyle = p.roofDark;
    ctx.fillRect(bx + 12, by + 23, bw - 24, 2);
    ctx.fillRect(bx + 20, by + 16, bw - 40, 2);
    ctx.fillRect(bx + 26, by + 10, bw - 52, 2);

    // Roof Highlight Ridge
    ctx.fillStyle = p.roofLight;
    ctx.fillRect(bx + bw / 2 - 1, by + 4, 2, 24);

    // Attic Dormer Window
    ctx.fillStyle = p.walls;
    ctx.fillRect(bx + bw / 2 - 6, by + 14, 12, 10);
    ctx.fillStyle = p.roofDark;
    ctx.fillRect(bx + bw / 2 - 8, by + 13, 16, 2);
    ctx.fillStyle = p.windowGlow;
    ctx.fillRect(bx + bw / 2 - 4, by + 16, 8, 7);

    // Wooden Arched Entrance Door
    ctx.fillStyle = p.door;
    ctx.fillRect(bx + bw / 2 - 6, by + bh - 18, 12, 18);
    ctx.fillStyle = '#1E293B';
    ctx.fillRect(bx + bw / 2 - 6, by + bh - 13, 12, 1.5); // Iron strap
    ctx.fillStyle = '#FACC15';
    ctx.fillRect(bx + bw / 2 + 3, by + bh - 9, 2, 2); // Brass handle

    // Glowing Windows with Flower Boxes
    // Left Window
    ctx.fillStyle = p.windowGlow;
    ctx.fillRect(bx + 16, by + 34, 8, 8);
    ctx.fillStyle = p.windowFrame;
    ctx.strokeRect(bx + 16, by + 34, 8, 8);
    ctx.fillRect(bx + 15, by + 42, 10, 3); // Flower box
    ctx.fillStyle = '#EF4444';
    ctx.fillRect(bx + 16, by + 41, 3, 2);
    ctx.fillStyle = '#FACC15';
    ctx.fillRect(bx + 21, by + 41, 3, 2);

    // Right Window
    ctx.fillStyle = p.windowGlow;
    ctx.fillRect(bx + bw - 24, by + 34, 8, 8);
    ctx.fillStyle = p.windowFrame;
    ctx.strokeRect(bx + bw - 24, by + 34, 8, 8);
    ctx.fillRect(bx + bw - 25, by + 42, 10, 3);
    ctx.fillStyle = '#EF4444';
    ctx.fillRect(bx + bw - 24, by + 41, 3, 2);
    ctx.fillStyle = '#38BDF8';
    ctx.fillRect(bx + bw - 19, by + 41, 3, 2);

    // Porch Steps
    ctx.fillStyle = p.wallsDark;
    ctx.fillRect(bx + bw / 2 - 9, by + bh - 2, 18, 2);
    return;
  }

  // =========================================================================
  // (B) LUMBUNG PANGAN / GUDANG (Classic Red Rustic Barn & Silo)
  // =========================================================================
  if (bId === 'lumbung_pangan' || bId === 'gudang') {
    // Round Silo on the left
    ctx.fillStyle = '#94A3B8';
    ctx.fillRect(bx + 6, by + 16, 14, bh - 18);
    ctx.fillStyle = '#64748B';
    ctx.fillRect(bx + 6, by + 26, 14, 2);
    ctx.fillRect(bx + 6, by + 38, 14, 2);
    // Silo Dome
    ctx.fillStyle = '#DC2626';
    ctx.beginPath();
    ctx.arc(bx + 13, by + 16, 7, Math.PI, 0);
    ctx.fill();

    // Red Barn Wooden Body
    ctx.fillStyle = '#B91C1C';
    ctx.fillRect(bx + 20, by + 24, bw - 24, bh - 26);
    // Vertical Board lines
    ctx.fillStyle = '#991B1B';
    for (let vx = bx + 26; vx < bx + bw - 4; vx += 7) {
      ctx.fillRect(vx, by + 24, 1.5, bh - 26);
    }

    // Gambrel Barn Roof
    ctx.fillStyle = '#78350F';
    ctx.beginPath();
    ctx.moveTo(bx + bw / 2 + 8, by + 6);
    ctx.lineTo(bx + bw - 1, by + 24);
    ctx.lineTo(bx + 18, by + 24);
    ctx.closePath();
    ctx.fill();

    // Hay Loft Door with Pulley
    ctx.fillStyle = '#451A03';
    ctx.fillRect(bx + bw / 2 + 4, by + 26, 8, 8);
    ctx.fillStyle = '#FBBF24';
    ctx.fillRect(bx + bw / 2 + 7, by + 18, 2, 8); // rope

    // Large Sliding Barn Doors with 'X' Cross Braces
    ctx.fillStyle = '#F8FAFC';
    ctx.fillRect(bx + bw / 2, by + bh - 18, 20, 16);
    ctx.fillStyle = '#B91C1C';
    ctx.fillRect(bx + bw / 2 + 2, by + bh - 16, 7, 12);
    ctx.fillRect(bx + bw / 2 + 11, by + bh - 16, 7, 12);
    ctx.fillStyle = '#F8FAFC';
    // 'X' marks
    ctx.fillRect(bx + bw / 2 + 4, by + bh - 14, 3, 8);
    ctx.fillRect(bx + bw / 2 + 13, by + bh - 14, 3, 8);

    // Hay bale on right
    ctx.fillStyle = '#FACC15';
    ctx.fillRect(bx + bw - 9, by + bh - 8, 8, 6);
    ctx.fillStyle = '#CA8A04';
    ctx.fillRect(bx + bw - 9, by + bh - 6, 8, 1);
    return;
  }

  // =========================================================================
  // (C) BARAK MILITER (Blacksmith & Guard Garrison)
  // =========================================================================
  if (bId === 'barak_militer') {
    // Stone Forge Chimney with Heavy Smoke
    ctx.fillStyle = '#475569';
    ctx.fillRect(bx + bw - 18, by + 6, 9, 20);
    ctx.fillStyle = '#1E293B';
    ctx.fillRect(bx + bw - 20, by + 5, 13, 3);
    // Smoke
    ctx.fillStyle = 'rgba(100, 116, 139, 0.7)';
    ctx.beginPath();
    ctx.arc(bx + bw - 14, smokeY1, 4, 0, Math.PI * 2);
    ctx.fill();
    ctx.beginPath();
    ctx.arc(bx + bw - 11, smokeY2, 5.5, 0, Math.PI * 2);
    ctx.fill();

    // Cobblestone Main Walls
    ctx.fillStyle = p.cobble;
    ctx.fillRect(bx + 8, by + 24, bw - 16, bh - 26);
    ctx.fillStyle = p.cobbleDark;
    for (let my = by + 30; my < by + bh - 4; my += 7) {
      ctx.fillRect(bx + 8, my, bw - 16, 1.5);
    }

    // Heavy Fortified Roof
    ctx.fillStyle = p.roof;
    ctx.beginPath();
    ctx.moveTo(bx + bw / 2, by + 8);
    ctx.lineTo(bx + bw - 2, by + 25);
    ctx.lineTo(bx + 2, by + 25);
    ctx.closePath();
    ctx.fill();
    ctx.fillStyle = p.roofDark;
    ctx.fillRect(bx + 12, by + 21, bw - 24, 2);

    // Iron Anvil Prop on Left
    ctx.fillStyle = '#1E293B';
    ctx.fillRect(bx + 12, by + bh - 10, 8, 6);
    ctx.fillRect(bx + 10, by + bh - 12, 12, 3);
    ctx.fillStyle = '#FACC15';
    ctx.fillRect(bx + 14, by + bh - 14, 2, 2); // Spark

    // Archery Target on Right
    ctx.fillStyle = '#F8FAFC';
    ctx.beginPath();
    ctx.arc(bx + bw - 10, by + bh - 9, 6, 0, Math.PI * 2);
    ctx.fill();
    ctx.fillStyle = '#DC2626';
    ctx.beginPath();
    ctx.arc(bx + bw - 10, by + bh - 9, 3.5, 0, Math.PI * 2);
    ctx.fill();

    // Heavy Iron Guard Door
    ctx.fillStyle = '#334155';
    ctx.fillRect(bx + bw / 2 - 5, by + bh - 18, 10, 18);
    ctx.fillStyle = '#0F172A';
    ctx.fillRect(bx + bw / 2 - 5, by + bh - 12, 10, 1.5);
    return;
  }

  // =========================================================================
  // (D) GERBANG BENTENG (Palisade & Watchtower)
  // =========================================================================
  if (bId === 'gate') {
    // Log Palisade Fence on Sides
    ctx.fillStyle = '#78350F';
    for (let lx = bx + 4; lx < bx + 18; lx += 4) {
      ctx.fillRect(lx, by + 34, 3, bh - 36);
    }
    for (let rx = bx + bw - 18; rx < bx + bw - 4; rx += 4) {
      ctx.fillRect(rx, by + 34, 3, bh - 36);
    }

    // Watchtower Central Wood Columns
    ctx.fillStyle = '#854D0E';
    ctx.fillRect(bx + 20, by + 16, bw - 40, bh - 18);
    ctx.fillStyle = '#451A03';
    ctx.fillRect(bx + 20, by + 28, bw - 40, 2);
    ctx.fillRect(bx + 20, by + 40, bw - 40, 2);

    // Watchtower Roof
    ctx.fillStyle = p.roof;
    ctx.beginPath();
    ctx.moveTo(bx + bw / 2, by + 4);
    ctx.lineTo(bx + bw - 14, by + 17);
    ctx.lineTo(bx + 14, by + 17);
    ctx.closePath();
    ctx.fill();

    // Alarm Bell
    ctx.fillStyle = '#FACC15';
    ctx.beginPath();
    ctx.arc(bx + bw / 2, by + 22, 3, 0, Math.PI * 2);
    ctx.fill();

    // Iron Portcullis Grate
    ctx.fillStyle = '#1E293B';
    ctx.fillRect(bx + 24, by + bh - 18, bw - 48, 18);
    ctx.fillStyle = '#94A3B8';
    for (let gx = bx + 26; gx < bx + bw - 26; gx += 3) {
      ctx.fillRect(gx, by + bh - 18, 1, 18);
    }
    return;
  }

  // =========================================================================
  // (E) RUMAH WARGA / COZY COTTAGE & OTHER VILLAGE BUILDINGS
  // =========================================================================
  // Cottage Chimney
  ctx.fillStyle = '#B91C1C';
  ctx.fillRect(bx + bw - 14, by + 10, 5, 12);
  ctx.fillStyle = 'rgba(226, 232, 240, 0.6)';
  ctx.beginPath();
  ctx.arc(bx + bw - 11, smokeY1, 3, 0, Math.PI * 2);
  ctx.fill();

  // Stone Foundation
  ctx.fillStyle = p.cobble;
  ctx.fillRect(bx + 8, by + bh - 10, bw - 16, 10);
  ctx.fillStyle = p.cobbleDark;
  ctx.fillRect(bx + 8, by + bh - 10, bw - 16, 1.5);

  // Wooden Walls
  ctx.fillStyle = p.walls;
  ctx.fillRect(bx + 10, by + 26, bw - 20, bh - 36);
  ctx.fillStyle = p.wallsDark;
  for (let wy = by + 32; wy < by + bh - 10; wy += 6) {
    ctx.fillRect(bx + 10, wy, bw - 20, 1);
  }

  // Gabled Shingle Roof
  ctx.fillStyle = p.roof;
  ctx.beginPath();
  ctx.moveTo(bx + bw / 2, by + 8);
  ctx.lineTo(bx + bw - 2, by + 27);
  ctx.lineTo(bx + 2, by + 27);
  ctx.closePath();
  ctx.fill();
  ctx.fillStyle = p.roofDark;
  ctx.fillRect(bx + 12, by + 22, bw - 24, 1.5);

  // Door
  ctx.fillStyle = p.door;
  ctx.fillRect(bx + bw / 2 - 4, by + bh - 16, 8, 16);
  ctx.fillStyle = '#FACC15';
  ctx.fillRect(bx + bw / 2 + 1, by + bh - 8, 1.5, 1.5);

  // Glowing Window with Flower Box
  ctx.fillStyle = p.windowGlow;
  ctx.fillRect(bx + 16, by + 32, 7, 7);
  ctx.fillStyle = p.windowFrame;
  ctx.strokeRect(bx + 16, by + 32, 7, 7);
  ctx.fillRect(bx + 15, by + 39, 9, 2);
  ctx.fillStyle = '#EF4444';
  ctx.fillRect(bx + 16, by + 38.5, 2, 1.5);
  ctx.fillStyle = '#FACC15';
  ctx.fillRect(bx + 20, by + 38.5, 2, 1.5);

  // Barrel Prop on right
  ctx.fillStyle = '#78350F';
  ctx.fillRect(bx + bw - 18, by + bh - 10, 6, 8);
  ctx.fillStyle = '#1E293B';
  ctx.fillRect(bx + bw - 18, by + bh - 8, 6, 1);
  ctx.fillRect(bx + bw - 18, by + bh - 4, 6, 1);
}
