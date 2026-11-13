import React, { useRef, useEffect, useState, useCallback } from 'react';
import { ALL_BUILDINGS } from '../gameState';
import { sound } from '../utils/retroAudio';
import { drawBuildingSprite } from '../utils/canvasBuildingRenderer';

// Virtual world map dimensions in 32x32 tiles (Spacious 36 x 26 grid)
const TILE_SIZE = 32;
const MAP_COLS = 36;
const MAP_ROWS = 26;
const WORLD_WIDTH = MAP_COLS * TILE_SIZE;  // 1152px
const WORLD_HEIGHT = MAP_ROWS * TILE_SIZE; // 832px

// Defined Building World Coordinates (col, row, widthInTiles, heightInTiles)
const BUILDING_LOCATIONS = {
  town_hall:     { col: 15, row: 10, w: 4, h: 4, name: 'Balai Kota', color: '#EAB308', icon: '🏛️' },
  gudang:        { col: 10, row: 9,  w: 3, h: 3, name: 'Gudang Hasil', color: '#D97706', icon: '📦' },
  barak_militer: { col: 22, row: 10, w: 3, h: 3, name: 'Barak Militer', color: '#EF4444', icon: '⚔️' },
  tembok:        { col: 26, row: 12, w: 3, h: 4, name: 'Gerbang Kota', color: '#64748B', icon: '🛡️' },
  hospital:      { col: 11, row: 15, w: 3, h: 3, name: 'Balai Medis', color: '#10B981', icon: '🏥' },
  tavern:        { col: 20, row: 16, w: 3, h: 3, name: 'Kedai Komandan', color: '#F59E0B', icon: '🍻' },
  dungeon:       { col: 30, row: 5,  w: 3, h: 3, name: 'Portal Dungeon', color: '#8B5CF6', icon: '🌀' },
  market:        { col: 15, row: 17, w: 3, h: 3, name: 'Pasar Rakyat', color: '#06B6D4', icon: '⚖️' },
  pertanian:     { col: 4,  row: 16, w: 4, h: 3, name: 'Ladang Gandum', color: '#84CC16', icon: '🌾' },
  pertambangan:  { col: 4,  row: 5,  w: 4, h: 3, name: 'Gua Tambang', color: '#78716C', icon: '⛏️' }
};

export default function PixelCanvas({ state, onCanvasTap, onSelectBuilding, isFullscreen = true }) {
  const canvasRef = useRef(null);
  const containerRef = useRef(null);

  // Camera State (default centered on Town Hall)
  const [camera, setCamera] = useState({ x: -280, y: -160, zoom: 1.05 });
  const isDraggingRef = useRef(false);
  const dragStartRef = useRef({ x: 0, y: 0 });
  const hasMovedRef = useRef(false);
  const [hoveredBuilding, setHoveredBuilding] = useState(null);

  // Era Detection
  const unNodes = Array.isArray(state?.unlockedNodes) ? state.unlockedNodes : [];
  const eraId = unNodes.includes('brain_link') ? 'cyberpunk'
    : unNodes.includes('pengalengan_makanan') ? 'modern'
    : unNodes.includes('domestikasi_hewan') ? 'kerajaan'
    : 'primitif';

  // Live Simulation state in ref for 60fps draw loop
  const simRef = useRef({
    tick: 0,
    clouds: [
      { x: 50,  y: 60,  speed: 0.35, size: 90 },
      { x: 420, y: 120, speed: 0.22, size: 120 },
      { x: 850, y: 70,  speed: 0.28, size: 80 }
    ],
    citizens: [
      { id: 1, x: 490, y: 440, targetX: 560, speed: 0.65, dir: 1,  role: 'guard',   name: 'Prajurit Jaga', talk: 'Gerbang peradaban aman! 🛡️' },
      { id: 2, x: 720, y: 390, targetX: 660, speed: 0.55, dir: -1, role: 'warrior', name: 'Pemburu',     talk: 'Siaga serangan raid! ⚔️' },
      { id: 3, x: 360, y: 550, targetX: 420, speed: 0.45, dir: 1,  role: 'farmer',  name: 'Petani Suku',  talk: 'Hasil gandum melimpah 🌾' },
      { id: 4, x: 520, y: 560, targetX: 470, speed: 0.50, dir: -1, role: 'worker',  name: 'Pekerja Kayu', talk: 'Terus membangun kota 🔨' },
      { id: 5, x: 500, y: 360, targetX: 530, speed: 0.35, dir: 1,  role: 'leader',  name: 'Sang Penguasa',talk: 'Kerajaan semakin makmur! 👑' }
    ],
    animals: [
      { x: 260, y: 340, targetX: 300, speed: 0.22, dir: 1, type: 'sheep' },
      { x: 310, y: 400, targetX: 250, speed: 0.18, dir: -1, type: 'sheep' },
      { x: 180, y: 580, targetX: 230, speed: 0.35, dir: 1, type: 'rabbit' }
    ],
    floatingTexts: [],
    particles: []
  });

  // Handle canvas tap / click
  const handleCanvasClick = (e) => {
    if (hasMovedRef.current) return; // Prevent click when user was dragging
    const canvas = canvasRef.current;
    if (!canvas) return;
    const rect = canvas.getBoundingClientRect();
    const mouseX = e.clientX - rect.left;
    const mouseY = e.clientY - rect.top;

    // Convert screen coord to world coord
    const worldX = (mouseX - camera.x) / camera.zoom;
    const worldY = (mouseY - camera.y) / camera.zoom;

    // Check if clicked any building
    let clickedBuildingId = null;
    for (const [bId, loc] of Object.entries(BUILDING_LOCATIONS)) {
      const bx = loc.col * TILE_SIZE;
      const by = loc.row * TILE_SIZE;
      const bw = loc.w * TILE_SIZE;
      const bh = loc.h * TILE_SIZE;

      if (worldX >= bx && worldX <= bx + bw && worldY >= by && worldY <= by + bh) {
        clickedBuildingId = bId;
        break;
      }
    }

    if (clickedBuildingId) {
      sound.playClick();
      const bObj = ALL_BUILDINGS.find((item) => item.id === clickedBuildingId);
      if (bObj && onSelectBuilding) {
        onSelectBuilding(bObj);
      }
      return;
    }

    // Ground harvest tap
    sound.playHarvest();
    if (onCanvasTap) onCanvasTap();

    // Spawn floating text & tap sparkle particles
    simRef.current.floatingTexts.push({
      x: worldX,
      y: worldY,
      text: `+${Math.floor(Math.random() * 8 + 5)} Zenith`,
      color: '#FACC15',
      life: 50
    });

    for (let i = 0; i < 7; i++) {
      simRef.current.particles.push({
        x: worldX,
        y: worldY,
        vx: (Math.random() - 0.5) * 3.5,
        vy: -Math.random() * 3 - 1.2,
        color: ['#FACC15', '#4ADE80', '#60A5FA', '#F472B6'][Math.floor(Math.random() * 4)],
        life: 28,
        size: Math.random() * 3.5 + 2
      });
    }
  };

  // Mouse / Touch Dragging
  const handleMouseDown = (e) => {
    isDraggingRef.current = true;
    hasMovedRef.current = false;
    dragStartRef.current = { x: e.clientX - camera.x, y: e.clientY - camera.y };
  };

  const handleMouseMove = (e) => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const rect = canvas.getBoundingClientRect();
    const mouseX = e.clientX - rect.left;
    const mouseY = e.clientY - rect.top;
    const worldX = (mouseX - camera.x) / camera.zoom;
    const worldY = (mouseY - camera.y) / camera.zoom;

    if (isDraggingRef.current) {
      const dx = Math.abs(e.clientX - (dragStartRef.current.x + camera.x));
      const dy = Math.abs(e.clientY - (dragStartRef.current.y + camera.y));
      if (dx > 4 || dy > 4) {
        hasMovedRef.current = true;
      }
      setCamera((prev) => ({
        ...prev,
        x: e.clientX - dragStartRef.current.x,
        y: e.clientY - dragStartRef.current.y
      }));
      return;
    }

    // Hover detection for buildings
    let hovered = null;
    for (const [bId, loc] of Object.entries(BUILDING_LOCATIONS)) {
      const bx = loc.col * TILE_SIZE;
      const by = loc.row * TILE_SIZE;
      const bw = loc.w * TILE_SIZE;
      const bh = loc.h * TILE_SIZE;

      if (worldX >= bx && worldX <= bx + bw && worldY >= by && worldY <= by + bh) {
        hovered = bId;
        break;
      }
    }
    setHoveredBuilding(hovered);
  };

  const handleMouseUp = () => {
    isDraggingRef.current = false;
  };

  // Zoom Handler
  const handleWheel = useCallback((e) => {
    e.preventDefault();
    const zoomFactor = e.deltaY < 0 ? 1.1 : 0.9;
    setCamera((prev) => {
      const newZoom = Math.min(2.2, Math.max(0.55, prev.zoom * zoomFactor));
      return { ...prev, zoom: newZoom };
    });
  }, []);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    canvas.addEventListener('wheel', handleWheel, { passive: false });
    return () => canvas.removeEventListener('wheel', handleWheel);
  }, [handleWheel]);

  // Handle window resize so canvas stays sharp
  useEffect(() => {
    const handleResize = () => {
      const canvas = canvasRef.current;
      const container = containerRef.current;
      if (!canvas || !container) return;
      canvas.width = container.clientWidth;
      canvas.height = container.clientHeight;
    };
    handleResize();
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  // Center Camera to Town Hall
  const handleCenterCamera = () => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const th = BUILDING_LOCATIONS.town_hall;
    const thCenterX = (th.col + th.w / 2) * TILE_SIZE;
    const thCenterY = (th.row + th.h / 2) * TILE_SIZE;

    setCamera({
      x: (canvas.width / 2) - (thCenterX * 1.05),
      y: (canvas.height / 2) - (thCenterY * 1.05),
      zoom: 1.05
    });
    sound.playClick();
  };

  // Main 60 FPS Canvas Rendering Engine
  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    ctx.imageSmoothingEnabled = false;

    let animId;

    const render = () => {
      simRef.current.tick++;
      const { tick, clouds, citizens, animals, floatingTexts, particles } = simRef.current;

      const cw = canvas.width;
      const ch = canvas.height;

      // Dark Cosmic Atmosphere
      ctx.fillStyle = eraId === 'cyberpunk' ? '#070913' : eraId === 'modern' ? '#0F172A' : '#090D16';
      ctx.fillRect(0, 0, cw, ch);

      ctx.save();
      // Apply Camera Transform
      ctx.translate(camera.x, camera.y);
      ctx.scale(camera.zoom, camera.zoom);

      // ======================================================================
      // 1. ADVANCED PROCEDURAL TILE TERRAIN
      // ======================================================================
      const grassMain = eraId === 'cyberpunk' ? '#0B132B' : eraId === 'kerajaan' ? '#15803D' : '#14532D';
      const grassAlt  = eraId === 'cyberpunk' ? '#1C2541' : eraId === 'kerajaan' ? '#166534' : '#166534';
      const riverWater = eraId === 'cyberpunk' ? '#06B6D4' : '#0284C7';
      const pathStone  = eraId === 'cyberpunk' ? '#3B0764' : eraId === 'kerajaan' ? '#475569' : '#78350F';
      const soilFarm   = eraId === 'cyberpunk' ? '#1E1B4B' : '#451A03';

      for (let r = 0; r < MAP_ROWS; r++) {
        for (let c = 0; c < MAP_COLS; c++) {
          const tx = c * TILE_SIZE;
          const ty = r * TILE_SIZE;

          // River Path (Columns 27-28 with natural bend)
          const isRiver = (c === 28) || (c === 27 && r > 11) || (c === 29 && r <= 6);
          if (isRiver) {
            ctx.fillStyle = riverWater;
            ctx.fillRect(tx, ty, TILE_SIZE, TILE_SIZE);

            // Water Ripple Animation
            const waveOffset = Math.sin((tick * 0.05) + (r * 0.9)) * 5;
            ctx.fillStyle = 'rgba(255, 255, 255, 0.35)';
            ctx.fillRect(tx + 4 + waveOffset, ty + 10, 14, 2);
            ctx.fillRect(tx + 16 - waveOffset, ty + 22, 10, 2);

            // Lily pads
            if ((r * 11 + c * 7) % 8 === 0) {
              ctx.fillStyle = '#22C55E';
              ctx.beginPath();
              ctx.arc(tx + 16, ty + 16, 5, 0, Math.PI * 1.8);
              ctx.fill();
            }
            continue;
          }

          // Main Crossroads & Paved Paths
          const isMainRoad = (r === 13 && c >= 9 && c <= 27) || (c === 17 && r >= 8 && r <= 20);
          if (isMainRoad) {
            ctx.fillStyle = pathStone;
            ctx.fillRect(tx, ty, TILE_SIZE, TILE_SIZE);
            // Cobblestone mortar specks
            ctx.fillStyle = eraId === 'cyberpunk' ? '#A855F7' : 'rgba(255, 255, 255, 0.15)';
            ctx.fillRect(tx + 3, ty + 3, 5, 4);
            ctx.fillRect(tx + 18, ty + 17, 6, 4);
            continue;
          }

          // Farm Soil Plots (near pertanian)
          if (c >= 3 && c <= 8 && r >= 15 && r <= 19) {
            ctx.fillStyle = soilFarm;
            ctx.fillRect(tx, ty, TILE_SIZE, TILE_SIZE);
            // Golden Wheat Rows
            ctx.fillStyle = '#FBBF24';
            ctx.fillRect(tx + 2, ty + 6, TILE_SIZE - 4, 3);
            ctx.fillRect(tx + 2, ty + 16, TILE_SIZE - 4, 3);
            ctx.fillRect(tx + 2, ty + 26, TILE_SIZE - 4, 3);
            continue;
          }

          // Mountain Quarry Stone Floor (near pertambangan)
          if (c >= 2 && c <= 8 && r >= 4 && r <= 8) {
            ctx.fillStyle = '#57534E';
            ctx.fillRect(tx, ty, TILE_SIZE, TILE_SIZE);
            ctx.fillStyle = '#78716C';
            ctx.fillRect(tx + 4, ty + 4, 8, 8);
            ctx.fillRect(tx + 18, ty + 18, 10, 6);
            continue;
          }

          // Standard Grass Tile
          ctx.fillStyle = (r + c) % 2 === 0 ? grassMain : grassAlt;
          ctx.fillRect(tx, ty, TILE_SIZE, TILE_SIZE);

          // Wild Flowers / Grass Tufts
          const seed = (r * 13 + c * 19);
          if (seed % 7 === 0) {
            ctx.fillStyle = 'rgba(255, 255, 255, 0.12)';
            ctx.fillRect(tx + 8, ty + 10, 2, 5);
            ctx.fillRect(tx + 10, ty + 8, 2, 7);
          } else if (seed % 17 === 0) {
            // Cute colorful flower
            ctx.fillStyle = seed % 2 === 0 ? '#F43F5E' : '#FBBF24';
            ctx.fillRect(tx + 14, ty + 14, 4, 4);
          }
        }
      }

      // Wooden Arch Bridge across River (Column 28, Row 13)
      const bridgeX = 27 * TILE_SIZE;
      const bridgeY = 13 * TILE_SIZE;
      ctx.fillStyle = '#78350F';
      ctx.fillRect(bridgeX - 4, bridgeY, (TILE_SIZE * 2) + 8, TILE_SIZE);
      ctx.fillStyle = '#92400E';
      for (let b = 0; b < TILE_SIZE; b += 6) {
        ctx.fillRect(bridgeX - 2, bridgeY + b, (TILE_SIZE * 2) + 4, 2);
      }

      // ======================================================================
      // 2. ENVIRONMENT OBJECTS (Pines, Campfire, Pasture Animals)
      // ======================================================================
      const treePositions = [
        { col: 1, row: 1 }, { col: 3, row: 2 }, { col: 10, row: 2 },
        { col: 13, row: 3 }, { col: 24, row: 2 }, { col: 32, row: 2 },
        { col: 33, row: 12 }, { col: 34, row: 18 }, { col: 31, row: 22 },
        { col: 12, row: 22 }, { col: 2, row: 23 }, { col: 6, row: 23 }
      ];

      const windSway = Math.sin(tick * 0.04) * 2.5;
      treePositions.forEach((pos) => {
        const tx = pos.col * TILE_SIZE;
        const ty = pos.row * TILE_SIZE;

        // Tree Shadow
        ctx.fillStyle = 'rgba(0, 0, 0, 0.35)';
        ctx.beginPath();
        ctx.ellipse(tx + 16, ty + 28, 14, 6, 0, 0, Math.PI * 2);
        ctx.fill();

        // Trunk
        ctx.fillStyle = '#5C2D0C';
        ctx.fillRect(tx + 13, ty + 18, 6, 12);

        // Multi-tier Pine Leaves
        ctx.fillStyle = eraId === 'cyberpunk' ? '#0284C7' : '#15803D';
        ctx.fillRect(tx + 2 + windSway, ty + 12, 28, 12);
        ctx.fillStyle = eraId === 'cyberpunk' ? '#38BDF8' : '#22C55E';
        ctx.fillRect(tx + 6 + (windSway * 1.3), ty + 2, 20, 10);
      });

      // Central Campfire near Town Hall
      const fireX = 14 * TILE_SIZE + 10;
      const fireY = 13 * TILE_SIZE + 10;
      ctx.fillStyle = '#64748B';
      ctx.fillRect(fireX - 6, fireY + 4, 20, 5);
      const flameH = (Math.sin(tick * 0.25) * 5) + 12;
      ctx.fillStyle = '#EF4444';
      ctx.fillRect(fireX + 2, fireY - flameH + 6, 8, flameH);
      ctx.fillStyle = '#F59E0B';
      ctx.fillRect(fireX + 3, fireY - (flameH * 0.7) + 6, 6, flameH * 0.7);

      // Animals Grazing
      animals.forEach((a) => {
        a.x += a.speed * a.dir;
        if (Math.abs(a.x - a.targetX) < 2) {
          a.dir *= -1;
          a.targetX = a.dir > 0 ? a.x + 40 : a.x - 40;
        }
        ctx.fillStyle = 'rgba(0,0,0,0.2)';
        ctx.fillRect(a.x, a.y + 10, 14, 4);

        if (a.type === 'sheep') {
          ctx.fillStyle = '#F8FAFC';
          ctx.fillRect(a.x, a.y, 14, 10);
          ctx.fillStyle = '#1E293B';
          ctx.fillRect(a.dir > 0 ? a.x + 11 : a.x - 2, a.y + 2, 4, 5);
        } else {
          // Rabbit
          ctx.fillStyle = '#CBD5E1';
          ctx.fillRect(a.x, a.y + 3, 8, 7);
          ctx.fillRect(a.x + 2, a.y, 3, 5);
        }
      });

      // ======================================================================
      // 3. CITIZENS & PATROLLING SOLDIERS (Animated walk cycles)
      // ======================================================================
      citizens.forEach((c) => {
        c.x += c.speed * c.dir;
        if (Math.abs(c.x - c.targetX) < 2) {
          c.dir *= -1;
          c.targetX = c.dir > 0 ? c.x + 60 : c.x - 60;
        }

        const walkBounce = Math.abs(Math.sin(tick * 0.16)) * 2;
        const legOffset = Math.sin(tick * 0.22) * 3;

        // Shadow
        ctx.fillStyle = 'rgba(0, 0, 0, 0.3)';
        ctx.fillRect(c.x, c.y + 16, 12, 4);

        // Legs
        ctx.fillStyle = '#1E293B';
        ctx.fillRect(c.x + 2 + legOffset, c.y + 12 - walkBounce, 3, 5);
        ctx.fillRect(c.x + 7 - legOffset, c.y + 12 - walkBounce, 3, 5);

        // Body Costume
        ctx.fillStyle = c.role === 'leader' ? '#FACC15'
          : c.role === 'guard' ? '#EF4444'
          : c.role === 'warrior' ? '#F97316'
          : '#3B82F6';
        ctx.fillRect(c.x + 1, c.y + 4 - walkBounce, 10, 9);

        // Head
        ctx.fillStyle = '#FBCFE8';
        ctx.fillRect(c.x + 3, c.y - 2 - walkBounce, 6, 6);

        // Leader Royal Cloak / Weapons
        if (c.role === 'leader') {
          ctx.fillStyle = '#DC2626'; // Red cape
          ctx.fillRect(c.dir > 0 ? c.x - 2 : c.x + 9, c.y + 4 - walkBounce, 4, 8);
          ctx.fillStyle = '#FACC15'; // Crown
          ctx.fillRect(c.x + 2, c.y - 4 - walkBounce, 8, 3);
        } else if (c.role === 'guard') {
          // Spear
          ctx.fillStyle = '#E2E8F0';
          ctx.fillRect(c.x + (c.dir > 0 ? 12 : -2), c.y - 8 - walkBounce, 2, 20);
        }

        // Friendly Speech Bubble
        if ((tick + c.id * 120) % 450 < 140 && c.talk) {
          ctx.fillStyle = 'rgba(15, 23, 42, 0.95)';
          ctx.fillRect(c.x - 24, c.y - 24, 68, 16);
          ctx.strokeStyle = '#FFD54F';
          ctx.lineWidth = 1;
          ctx.strokeRect(c.x - 24, c.y - 24, 68, 16);

          ctx.fillStyle = '#F8FAFC';
          ctx.font = '7px "Press Start 2P", monospace';
          ctx.fillText(c.talk, c.x - 21, c.y - 13);
        }
      });

      // ======================================================================
      // 4. HIGH-END DETAILED BUILDINGS (Matched 100% with Building Cards!)
      // ======================================================================
      for (const [bId, loc] of Object.entries(BUILDING_LOCATIONS)) {
        const bx = loc.col * TILE_SIZE;
        const by = loc.row * TILE_SIZE;
        const bw = loc.w * TILE_SIZE;
        const bh = loc.h * TILE_SIZE;

        const isHovered = hoveredBuilding === bId;
        const bLevel = (state?.buildingLevels && state.buildingLevels[bId])
          || (unNodes.includes(bId) ? 1 : 0);

        // Draw the exact authentic architectural pixel sprite
        drawBuildingSprite(ctx, bId, eraId, bLevel, bx, by, bw, bh, isHovered, tick);

        // Interactive Header Badge
        ctx.fillStyle = 'rgba(15, 23, 42, 0.94)';
        ctx.fillRect(bx, by - 16, bw, 15);
        ctx.strokeStyle = loc.color;
        ctx.lineWidth = 1.2;
        ctx.strokeRect(bx, by - 16, bw, 15);

        ctx.fillStyle = '#F8FAFC';
        ctx.font = '8px "Press Start 2P", monospace';
        ctx.fillText(`${loc.icon} ${loc.name}`, bx + 4, by - 6);

        // Level Tag
        ctx.fillStyle = bLevel > 0 ? '#10B981' : '#64748B';
        ctx.fillRect(bx + bw / 2 - 16, by + bh - 6, 32, 12);
        ctx.fillStyle = '#FFFFFF';
        ctx.font = '7px "Press Start 2P", monospace';
        ctx.fillText(bLevel > 0 ? `Lv.${bLevel}` : 'Kunci', bx + bw / 2 - 12, by + bh + 3);
      }

      // ======================================================================
      // 5. PARALLAX CLOUDS CASTING REAL SHADOWS
      // ======================================================================
      clouds.forEach((cl) => {
        cl.x += cl.speed;
        if (cl.x > WORLD_WIDTH + 80) cl.x = -cl.size - 80;

        // Ground shadow
        ctx.fillStyle = 'rgba(0, 0, 0, 0.14)';
        ctx.beginPath();
        ctx.ellipse(cl.x + 20, cl.y + 90, cl.size, cl.size * 0.4, 0, 0, Math.PI * 2);
        ctx.fill();

        // Fluffy Pixel Cloud
        ctx.fillStyle = 'rgba(255, 255, 255, 0.28)';
        ctx.beginPath();
        ctx.ellipse(cl.x, cl.y, cl.size, cl.size * 0.45, 0, 0, Math.PI * 2);
        ctx.ellipse(cl.x + cl.size * 0.35, cl.y - 8, cl.size * 0.6, cl.size * 0.35, 0, 0, Math.PI * 2);
        ctx.fill();
      });

      // ======================================================================
      // 6. PARTICLES & FLOATING TAP TEXTS
      // ======================================================================
      for (let i = particles.length - 1; i >= 0; i--) {
        const p = particles[i];
        p.x += p.vx;
        p.y += p.vy;
        p.life--;
        ctx.fillStyle = p.color;
        ctx.fillRect(p.x, p.y, p.size, p.size);
        if (p.life <= 0) particles.splice(i, 1);
      }

      for (let i = floatingTexts.length - 1; i >= 0; i--) {
        const ft = floatingTexts[i];
        ft.y -= 0.85;
        ft.life--;
        ctx.fillStyle = ft.color;
        ctx.font = '9px "Press Start 2P", monospace';
        ctx.fillText(ft.text, ft.x - 20, ft.y);
        if (ft.life <= 0) floatingTexts.splice(i, 1);
      }

      ctx.restore();
      animId = requestAnimationFrame(render);
    };

    animId = requestAnimationFrame(render);
    return () => cancelAnimationFrame(animId);
  }, [camera, hoveredBuilding, eraId, state]);

  return (
    <div
      ref={containerRef}
      style={{
        position: isFullscreen ? 'absolute' : 'relative',
        inset: isFullscreen ? 0 : 'auto',
        width: '100%',
        height: '100%',
        backgroundColor: '#070A12',
        overflow: 'hidden',
        userSelect: 'none',
        zIndex: 1
      }}
    >
      <canvas
        ref={canvasRef}
        onClick={handleCanvasClick}
        onMouseDown={handleMouseDown}
        onMouseMove={handleMouseMove}
        onMouseUp={handleMouseUp}
        onMouseLeave={handleMouseUp}
        style={{
          width: '100%',
          height: '100%',
          display: 'block',
          cursor: isDraggingRef.current ? 'grabbing' : 'grab'
        }}
      />

      {/* Floating Map HUD - Top Left: Era Tag */}
      <div style={{
        position: 'absolute',
        top: '80px',
        left: '14px',
        backgroundColor: 'rgba(15, 23, 42, 0.88)',
        backdropFilter: 'blur(10px)',
        border: '1.5px solid rgba(255, 255, 255, 0.15)',
        borderRadius: '20px',
        padding: '6px 14px',
        fontSize: '11px',
        fontWeight: '900',
        color: '#FFD54F',
        fontFamily: '"Press Start 2P", monospace',
        boxShadow: '0 4px 15px rgba(0,0,0,0.5)',
        pointerEvents: 'none',
        zIndex: 5
      }}>
        🗺️ Zaman {eraId.toUpperCase()}
      </div>

      {/* Floating Map HUD - Top Right: Zoom & Center Controls */}
      <div style={{
        position: 'absolute',
        top: '80px',
        right: '14px',
        display: 'flex',
        flexDirection: 'column',
        gap: '6px',
        zIndex: 5
      }}>
        <button
          onClick={() => setCamera((p) => ({ ...p, zoom: Math.min(2.2, p.zoom + 0.2) }))}
          className="btn-pixel"
          style={{ width: '32px', height: '32px', padding: 0, fontSize: '16px' }}
          title="Perbesar Peta"
        >
          +
        </button>
        <button
          onClick={() => setCamera((p) => ({ ...p, zoom: Math.max(0.55, p.zoom - 0.2) }))}
          className="btn-pixel"
          style={{ width: '32px', height: '32px', padding: 0, fontSize: '16px' }}
          title="Perkecil Peta"
        >
          -
        </button>
        <button
          onClick={handleCenterCamera}
          className="btn-pixel gold"
          style={{ width: '32px', height: '32px', padding: 0, fontSize: '14px' }}
          title="Pusatkan Kamera ke Balai Kota"
        >
          🎯
        </button>
      </div>

      {/* Bottom Hint */}
      <div style={{
        position: 'absolute',
        bottom: '68px',
        left: '50%',
        transform: 'translateX(-50%)',
        backgroundColor: 'rgba(0, 0, 0, 0.75)',
        backdropFilter: 'blur(8px)',
        padding: '5px 14px',
        borderRadius: '20px',
        fontSize: '10px',
        color: '#E2E8F0',
        pointerEvents: 'none',
        whiteSpace: 'nowrap',
        zIndex: 5,
        border: '1px solid rgba(255, 255, 255, 0.1)'
      }}>
        💡 Geser peta bebas • Tap tanah untuk panen • Klik bangunan untuk buka kartu RPG
      </div>
    </div>
  );
}
