import { ERA_COLORS } from '../components/BuildingVisual';
import { getCachedImage, GAME_ASSETS } from './assetLoader';

/**
 * Draws Authentic 2000s Farming & Village Pixel-Art Buildings onto HTML5 Canvas.
 * Directly renders real extracted PNG sprites when loaded, with seamless procedural fallback!
 */
export function drawBuildingSprite(ctx, bId, eraId, level, bx, by, bw, bh, isHovered, tick) {
  const p = ERA_COLORS[eraId] || ERA_COLORS.primitif;

  // 1. Soft Isometric Drop Shadow on Grass
  ctx.fillStyle = 'rgba(0, 0, 0, 0.42)';
  ctx.beginPath();
  ctx.ellipse(bx + bw / 2, by + bh - 2, bw * 0.52, bh * 0.20, 0, 0, Math.PI * 2);
  ctx.fill();

  // 2. Check if Real Extracted Asset PNG is available
  const assetSrc = GAME_ASSETS[bId] || (bId.includes('pertanian') ? GAME_ASSETS.pertanian : null);
  const realImg = assetSrc ? getCachedImage(assetSrc) : null;

  if (realImg) {
    // Draw Authentic Real Sliced PNG Sprite!
    ctx.imageSmoothingEnabled = false;
    ctx.drawImage(realImg, bx - 2, by - 6, bw + 4, bh + 8);

    // Hover Highlight
    if (isHovered) {
      ctx.strokeStyle = '#FACC15';
      ctx.lineWidth = 2.5;
      ctx.strokeRect(bx - 3, by - 3, bw + 6, bh + 6);
    }
    return;
  }

  // 3. High-Quality Fallback Pixel Art Renderer (if image still loading or custom building)
  if (isHovered) {
    ctx.strokeStyle = '#FACC15';
    ctx.lineWidth = 2.5;
    ctx.strokeRect(bx - 3, by - 3, bw + 6, bh + 6);
  }

  const smokePuff = (tick * 0.05) % 1;
  const smokeY1 = (by + 4) - smokePuff * 14;
  const smokeY2 = (by - 3) - smokePuff * 12;

  // TOWN HALL / BALAI DESA
  if (bId === 'town_hall') {
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

    ctx.fillStyle = p.cobble;
    ctx.fillRect(bx + 6, by + bh - 14, bw - 12, 14);
    ctx.fillStyle = p.cobbleDark;
    ctx.fillRect(bx + 6, by + bh - 14, bw - 12, 2);

    ctx.fillStyle = p.walls;
    ctx.fillRect(bx + 8, by + 26, bw - 16, bh - 38);
    ctx.fillStyle = p.wallsDark;
    for (let py = by + 32; py < by + bh - 14; py += 7) {
      ctx.fillRect(bx + 8, py, bw - 16, 1.5);
    }

    ctx.fillStyle = p.roof;
    ctx.beginPath();
    ctx.moveTo(bx + bw / 2, by + 4);
    ctx.lineTo(bx + bw - 2, by + 28);
    ctx.lineTo(bx + 2, by + 28);
    ctx.closePath();
    ctx.fill();

    ctx.fillStyle = p.door;
    ctx.fillRect(bx + bw / 2 - 6, by + bh - 18, 12, 18);
    ctx.fillStyle = p.windowGlow;
    ctx.fillRect(bx + 16, by + 34, 8, 8);
    ctx.fillRect(bx + bw - 24, by + 34, 8, 8);
    return;
  }

  // DEFAULT PIXEL ART BUILDING
  ctx.fillStyle = p.cobble;
  ctx.fillRect(bx + 8, by + bh - 10, bw - 16, 10);
  ctx.fillStyle = p.walls;
  ctx.fillRect(bx + 10, by + 26, bw - 20, bh - 36);
  ctx.fillStyle = p.roof;
  ctx.beginPath();
  ctx.moveTo(bx + bw / 2, by + 8);
  ctx.lineTo(bx + bw - 2, by + 27);
  ctx.lineTo(bx + 2, by + 27);
  ctx.closePath();
  ctx.fill();
  ctx.fillStyle = p.door;
  ctx.fillRect(bx + bw / 2 - 4, by + bh - 16, 8, 16);
  ctx.fillStyle = p.windowGlow;
  ctx.fillRect(bx + 16, by + 32, 7, 7);
}
