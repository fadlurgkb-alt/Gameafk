import React, { useRef, useEffect, useState, useCallback } from 'react';
import { ALL_BUILDINGS } from '../gameState';
import { sound } from '../utils/retroAudio';

// Virtual world map dimensions in 32x32 tiles
const TILE_SIZE = 32;
const MAP_COLS = 28;
const MAP_ROWS = 20;
const WORLD_WIDTH = MAP_COLS * TILE_SIZE;  // 896px
const WORLD_HEIGHT = MAP_ROWS * TILE_SIZE; // 640px

// Defined Building Map Coordinates (col, row, widthInTiles, heightInTiles)
const BUILDING_LOCATIONS = {
  town_hall:     { col: 11, row: 7,  w: 3, h: 3, name: 'Balai Kota', color: '#EAB308', icon: '🏛️' },
  gudang:        { col: 7,  row: 6,  w: 2, h: 2, name: 'Gudang Hasil', color: '#D97706', icon: '📦' },
  barak_militer: { col: 16, row: 7,  w: 2, h: 2, name: 'Barak Militer', color: '#EF4444', icon: '⚔️' },
  tembok:        { col: 19, row: 9,  w: 2, h: 3, name: 'Gerbang Kota', color: '#64748B', icon: '🛡️' },
  hospital:      { col: 8,  row: 10, w: 2, h: 2, name: 'Balai Medis', color: '#10B981', icon: '🏥' },
  tavern:        { col: 15, row: 11, w: 2, h: 2, name: 'Kedai Komandan', color: '#F59E0B', icon: '🍻' },
  dungeon:       { col: 22, row: 4,  w: 2, h: 2, name: 'Portal Dungeon', color: '#8B5CF6', icon: '🌀' },
  market:        { col: 11, row: 12, w: 2, h: 2, name: 'Pasar Rakyat', color: '#06B6D4', icon: '⚖️' },
  pertanian:     { col: 4,  row: 12, w: 3, h: 2, name: 'Ladang Gandum', color: '#84CC16', icon: '🌾' },
  pertambangan:  { col: 3,  row: 4,  w: 3, h: 2, name: 'Gua Tambang', color: '#78716C', icon: '⛏️' }
};

export default function PixelCanvas({ state, onCanvasTap, onSelectBuilding }) {
  const canvasRef = useRef(null);
  const containerRef = useRef(null);

  // Camera State
  const [camera, setCamera] = useState({ x: -140, y: -70, zoom: 1.1 });
  const isDraggingRef = useRef(false);
  const dragStartRef = useRef({ x: 0, y: 0 });
  const [hoveredBuilding, setHoveredBuilding] = useState(null);
  const [isExpanded, setIsExpanded] = useState(false);

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
      { x: 40,  y: 35, speed: 0.35, size: 70 },
      { x: 320, y: 80, speed: 0.22, size: 90 },
      { x: 650, y: 45, speed: 0.30, size: 60 }
    ],
    citizens: [
      { id: 1, x: 380, y: 310, targetX: 430, speed: 0.6, dir: 1,  role: 'farmer',  name: 'Petani Suku',  talk: 'Panen melimpah! 🌾' },
      { id: 2, x: 530, y: 260, targetX: 470, speed: 0.5, dir: -1, role: 'guard',   name: 'Prajurit Jaga', talk: 'Perbatasan aman! 🛡️' },
      { id: 3, x: 260, y: 410, targetX: 300, speed: 0.4, dir: 1,  role: 'worker',  name: 'Pekerja Kayu', talk: 'Membangun peradaban 🔨' },
      { id: 4, x: 500, y: 380, targetX: 550, speed: 0.7, dir: 1,  role: 'warrior', name: 'Pemburu',     talk: 'Bersiap raid! ⚔️' }
    ],
    sheep: [
      { x: 190, y: 250, targetX: 230, speed: 0.2, dir: 1 },
      { x: 230, y: 290, targetX: 180, speed: 0.18, dir: -1 }
    ],
    floatingTexts: [],
    particles: []
  });

  // Handle canvas tap harvest
  const handleCanvasClick = (e) => {
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

    // Spawn floating text & tap particles
    simRef.current.floatingTexts.push({
      x: worldX,
      y: worldY,
      text: `+${Math.floor(Math.random() * 5 + 3)} Zenith`,
      color: '#FACC15',
      life: 45
    });

    for (let i = 0; i < 6; i++) {
      simRef.current.particles.push({
        x: worldX,
        y: worldY,
        vx: (Math.random() - 0.5) * 3,
        vy: -Math.random() * 2.5 - 1,
        color: ['#FACC15', '#4ADE80', '#60A5FA'][Math.floor(Math.random() * 3)],
        life: 25,
        size: Math.random() * 3 + 2
      });
    }
  };

  // Mouse / Touch Dragging
  const handleMouseDown = (e) => {
    isDraggingRef.current = true;
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
      const newZoom = Math.min(2.0, Math.max(0.65, prev.zoom * zoomFactor));
      return { ...prev, zoom: newZoom };
    });
  }, []);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    canvas.addEventListener('wheel', handleWheel, { passive: false });
    return () => canvas.removeEventListener('wheel', handleWheel);
  }, [handleWheel]);

  // Main 60 FPS Canvas Rendering Engine
  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    ctx.imageSmoothingEnabled = false;

    let animId;

    const render = () => {
      simRef.current.tick++;
      const { tick, clouds, citizens, sheep, floatingTexts, particles } = simRef.current;

      // Handle Canvas size
      const cw = canvas.width;
      const ch = canvas.height;

      // Clear Screen with Sky/Cosmos Color
      ctx.fillStyle = eraId === 'cyberpunk' ? '#070913' : eraId === 'modern' ? '#1E293B' : '#0F172A';
      ctx.fillRect(0, 0, cw, ch);

      ctx.save();
      // Apply Camera Transform
      ctx.translate(camera.x, camera.y);
      ctx.scale(camera.zoom, camera.zoom);

      // ======================================================================
      // 1. TILE TERRAIN GRID (Grass, River, Paths)
      // ======================================================================
      // Era Color Palette
      const terrainGrass = eraId === 'cyberpunk' ? '#0F172A' : eraId === 'kerajaan' ? '#166534' : '#14532D';
      const terrainGrassAlt = eraId === 'cyberpunk' ? '#1E1B4B' : eraId === 'kerajaan' ? '#15803D' : '#166534';
      const riverWater = eraId === 'cyberpunk' ? '#06B6D4' : '#0284C7';
      const pathStone = eraId === 'cyberpunk' ? '#3B0764' : eraId === 'kerajaan' ? '#475569' : '#78350F';

      for (let r = 0; r < MAP_ROWS; r++) {
        for (let c = 0; c < MAP_COLS; c++) {
          const tx = c * TILE_SIZE;
          const ty = r * TILE_SIZE;

          // River Channel running vertically on column 21-22
          if (c === 21 || (c === 20 && r > 10)) {
            ctx.fillStyle = riverWater;
            ctx.fillRect(tx, ty, TILE_SIZE, TILE_SIZE);

            // Water Ripple Animation
            const waveOffset = Math.sin((tick * 0.05) + (r * 0.8)) * 4;
            ctx.fillStyle = 'rgba(255, 255, 255, 0.28)';
            ctx.fillRect(tx + 4 + waveOffset, ty + 12, 12, 2);
            ctx.fillRect(tx + 14 - waveOffset, ty + 24, 8, 2);
            continue;
          }

          // Main Plaza & Cross Paths
          const isMainRoad = (r === 9 && c >= 6 && c <= 20) || (c === 13 && r >= 5 && r <= 15);
          if (isMainRoad) {
            ctx.fillStyle = pathStone;
            ctx.fillRect(tx, ty, TILE_SIZE, TILE_SIZE);
            // Cobblestone pixel specks
            ctx.fillStyle = eraId === 'cyberpunk' ? '#A855F7' : 'rgba(255, 255, 255, 0.12)';
            ctx.fillRect(tx + 4, ty + 4, 4, 3);
            ctx.fillRect(tx + 18, ty + 16, 5, 3);
            continue;
          }

          // Farm Soil Plots (near pertanian)
          if (c >= 3 && c <= 6 && r >= 12 && r <= 14) {
            ctx.fillStyle = '#451A03';
            ctx.fillRect(tx, ty, TILE_SIZE, TILE_SIZE);
            // Crops Rows
            ctx.fillStyle = '#FBBF24';
            ctx.fillRect(tx + 2, ty + 6, TILE_SIZE - 4, 3);
            ctx.fillRect(tx + 2, ty + 18, TILE_SIZE - 4, 3);
            continue;
          }

          // Standard Grass Tile with subtle checker pattern
          ctx.fillStyle = (r + c) % 2 === 0 ? terrainGrass : terrainGrassAlt;
          ctx.fillRect(tx, ty, TILE_SIZE, TILE_SIZE);

          // Grass pixel tufts
          if ((r * 7 + c * 13) % 5 === 0) {
            ctx.fillStyle = 'rgba(255, 255, 255, 0.08)';
            ctx.fillRect(tx + 8, ty + 10, 2, 4);
            ctx.fillRect(tx + 10, ty + 8, 2, 6);
          }
        }
      }

      // Wooden Bridge across the river
      const bridgeX = 21 * TILE_SIZE;
      const bridgeY = 9 * TILE_SIZE;
      ctx.fillStyle = '#78350F';
      ctx.fillRect(bridgeX - 4, bridgeY, TILE_SIZE + 8, TILE_SIZE);
      ctx.fillStyle = '#92400E';
      for (let b = 0; b < TILE_SIZE; b += 6) {
        ctx.fillRect(bridgeX - 2, bridgeY + b, TILE_SIZE + 4, 2);
      }

      // ======================================================================
      // 2. ENVIRONMENT OBJECTS (Trees, Campfire, Sheep)
      // ======================================================================
      // Pine / Oak Trees (Wind oscillation)
      const treePositions = [
        { col: 1, row: 1 }, { col: 3, row: 1 }, { col: 2, row: 2 },
        { col: 6, row: 2 }, { col: 8, row: 1 }, { col: 17, row: 2 },
        { col: 25, row: 6 }, { col: 26, row: 10 }, { col: 25, row: 15 },
        { col: 2, row: 17 }, { col: 5, row: 18 }, { col: 18, row: 16 }
      ];

      const windSway = Math.sin(tick * 0.04) * 2;
      treePositions.forEach((pos) => {
        const tx = pos.col * TILE_SIZE;
        const ty = pos.row * TILE_SIZE;

        // Tree Shadow
        ctx.fillStyle = 'rgba(0, 0, 0, 0.3)';
        ctx.beginPath();
        ctx.ellipse(tx + 16, ty + 28, 12, 6, 0, 0, Math.PI * 2);
        ctx.fill();

        // Trunk
        ctx.fillStyle = '#5C2D0C';
        ctx.fillRect(tx + 13, ty + 18, 6, 12);

        // Foliage Layers
        ctx.fillStyle = eraId === 'cyberpunk' ? '#0284C7' : '#15803D';
        ctx.fillRect(tx + 4 + windSway, ty + 10, 24, 12);
        ctx.fillStyle = eraId === 'cyberpunk' ? '#38BDF8' : '#22C55E';
        ctx.fillRect(tx + 8 + (windSway * 1.3), ty + 2, 16, 10);
      });

      // Campfire with animated flickering pixel flame
      const fireX = 10 * TILE_SIZE + 8;
      const fireY = 9 * TILE_SIZE + 8;
      // Stones ring
      ctx.fillStyle = '#64748B';
      ctx.fillRect(fireX - 4, fireY + 4, 16, 4);
      // Flame animation
      const flameH = (Math.sin(tick * 0.25) * 4) + 10;
      ctx.fillStyle = '#EF4444';
      ctx.fillRect(fireX + 1, fireY - flameH + 6, 6, flameH);
      ctx.fillStyle = '#F59E0B';
      ctx.fillRect(fireX + 2, fireY - (flameH * 0.7) + 6, 4, flameH * 0.7);

      // Pasture Sheep grazing
      sheep.forEach((s) => {
        s.x += s.speed * s.dir;
        if (Math.abs(s.x - s.targetX) < 2) {
          s.dir *= -1;
          s.targetX = s.dir > 0 ? s.x + 35 : s.x - 35;
        }
        // Shadow
        ctx.fillStyle = 'rgba(0,0,0,0.2)';
        ctx.fillRect(s.x, s.y + 10, 14, 4);
        // Body
        ctx.fillStyle = '#F8FAFC';
        ctx.fillRect(s.x, s.y, 14, 10);
        // Head
        ctx.fillStyle = '#1E293B';
        ctx.fillRect(s.dir > 0 ? s.x + 11 : s.x - 2, s.y + 2, 4, 5);
      });

      // ======================================================================
      // 3. CITIZENS & PATROLLING SOLDIERS (Animated walk cycles)
      // ======================================================================
      citizens.forEach((c) => {
        c.x += c.speed * c.dir;
        if (Math.abs(c.x - c.targetX) < 2) {
          c.dir *= -1;
          c.targetX = c.dir > 0 ? c.x + 50 : c.x - 50;
        }

        // Walk bounce calculation
        const walkBounce = Math.abs(Math.sin(tick * 0.15)) * 2;
        const legOffset = Math.sin(tick * 0.2) * 3;

        // Shadow
        ctx.fillStyle = 'rgba(0, 0, 0, 0.25)';
        ctx.fillRect(c.x, c.y + 16, 12, 4);

        // Legs
        ctx.fillStyle = '#334155';
        ctx.fillRect(c.x + 2 + legOffset, c.y + 12 - walkBounce, 3, 5);
        ctx.fillRect(c.x + 7 - legOffset, c.y + 12 - walkBounce, 3, 5);

        // Body Outfit
        ctx.fillStyle = c.role === 'guard' ? '#EF4444' : c.role === 'warrior' ? '#F59E0B' : '#3B82F6';
        ctx.fillRect(c.x + 1, c.y + 4 - walkBounce, 10, 9);

        // Head
        ctx.fillStyle = '#FBCFE8';
        ctx.fillRect(c.x + 3, c.y - 2 - walkBounce, 6, 6);

        // Weapon / Tool
        if (c.role === 'guard') {
          // Spear
          ctx.fillStyle = '#CBD5E1';
          ctx.fillRect(c.x + (c.dir > 0 ? 12 : -2), c.y - 6 - walkBounce, 2, 18);
        } else if (c.role === 'warrior') {
          // Stone Axe
          ctx.fillStyle = '#78350F';
          ctx.fillRect(c.x + (c.dir > 0 ? 11 : -1), c.y + 1 - walkBounce, 2, 10);
          ctx.fillStyle = '#94A3B8';
          ctx.fillRect(c.x + (c.dir > 0 ? 10 : -3), c.y - 1 - walkBounce, 4, 3);
        }

        // Occasional Speech Bubble (Citizen 1 & 2)
        if ((tick + c.id * 100) % 400 < 120 && c.talk) {
          ctx.fillStyle = 'rgba(15, 23, 42, 0.9)';
          ctx.fillRect(c.x - 20, c.y - 22, 54, 15);
          ctx.strokeStyle = '#FFD54F';
          ctx.lineWidth = 1;
          ctx.strokeRect(c.x - 20, c.y - 22, 54, 15);

          ctx.fillStyle = '#F8FAFC';
          ctx.font = '8px "Press Start 2P", monospace';
          ctx.fillText(c.talk, c.x - 17, c.y - 12);
        }
      });

      // ======================================================================
      // 4. STRUCTURES & BUILDINGS (Isometric Elevation & Shadows)
      // ======================================================================
      for (const [bId, loc] of Object.entries(BUILDING_LOCATIONS)) {
        const bx = loc.col * TILE_SIZE;
        const by = loc.row * TILE_SIZE;
        const bw = loc.w * TILE_SIZE;
        const bh = loc.h * TILE_SIZE;

        const isHovered = hoveredBuilding === bId;
        const bLevel = (state?.buildingLevels && state.buildingLevels[bId])
          || (unNodes.includes(bId) ? 1 : 0);

        // A. Building Drop Shadow
        ctx.fillStyle = 'rgba(0, 0, 0, 0.35)';
        ctx.beginPath();
        ctx.ellipse(bx + bw / 2, by + bh - 2, bw * 0.55, bh * 0.25, 0, 0, Math.PI * 2);
        ctx.fill();

        // B. Hover Glow
        if (isHovered) {
          ctx.strokeStyle = '#FACC15';
          ctx.lineWidth = 2;
          ctx.strokeRect(bx - 3, by - 3, bw + 6, bh + 6);
        }

        // C. Distinct Building Visuals
        if (bId === 'town_hall') {
          // Grand Headquarters (Balai Kota)
          ctx.fillStyle = '#78350F'; // Base walls
          ctx.fillRect(bx + 6, by + 18, bw - 12, bh - 20);
          ctx.fillStyle = '#B45309'; // Front facade
          ctx.fillRect(bx + 12, by + 24, bw - 24, bh - 26);
          // Two-Tier Peaked Roof
          ctx.fillStyle = '#DC2626';
          ctx.beginPath();
          ctx.moveTo(bx + bw / 2, by + 2);
          ctx.lineTo(bx + bw - 2, by + 22);
          ctx.lineTo(bx + 2, by + 22);
          ctx.closePath();
          ctx.fill();
          // Royal Banner
          ctx.fillStyle = '#FACC15';
          ctx.fillRect(bx + bw / 2 - 2, by - 6, 4, 10);
          ctx.fillRect(bx + bw / 2 + 2, by - 6, 8, 6);
          // Entrance Door
          ctx.fillStyle = '#1E293B';
          ctx.fillRect(bx + bw / 2 - 7, by + bh - 16, 14, 14);
        } else if (bId === 'gudang') {
          // Vaulted Storage (Gudang)
          ctx.fillStyle = '#451A03';
          ctx.fillRect(bx + 4, by + 14, bw - 8, bh - 16);
          ctx.fillStyle = '#D97706';
          ctx.fillRect(bx + 8, by + 6, bw - 16, 12);
          // Barrel & Crate props outside
          ctx.fillStyle = '#92400E';
          ctx.fillRect(bx + bw - 14, by + bh - 12, 10, 10);
          ctx.fillStyle = '#B45309';
          ctx.fillRect(bx + 6, by + bh - 10, 8, 8);
        } else if (bId === 'barak_militer') {
          // Military Barracks (Barak)
          ctx.fillStyle = '#991B1B';
          ctx.fillRect(bx + 4, by + 12, bw - 8, bh - 14);
          ctx.fillStyle = '#DC2626';
          ctx.fillRect(bx + 8, by + 4, bw - 16, 12);
          // Archery Target & Weapon Rack props
          ctx.fillStyle = '#FFFFFF';
          ctx.beginPath();
          ctx.arc(bx + bw - 10, by + bh - 8, 5, 0, Math.PI * 2);
          ctx.fill();
          ctx.fillStyle = '#EF4444';
          ctx.beginPath();
          ctx.arc(bx + bw - 10, by + bh - 8, 2.5, 0, Math.PI * 2);
          ctx.fill();
        } else if (bId === 'tembok') {
          // Gate & Fortification Walls (Gerbang)
          ctx.fillStyle = '#475569';
          ctx.fillRect(bx, by + 10, bw, bh - 10);
          // Stone crenellations (battlement top)
          ctx.fillStyle = '#64748B';
          for (let s = 0; s < bw; s += 12) {
            ctx.fillRect(bx + s, by + 4, 8, 8);
          }
          // Arch Portcullis Gate
          ctx.fillStyle = '#0F172A';
          ctx.fillRect(bx + 8, by + bh - 24, bw - 16, 24);
        } else if (bId === 'hospital') {
          // Medical Clinic (Rumah Sakit)
          ctx.fillStyle = '#F8FAFC';
          ctx.fillRect(bx + 4, by + 12, bw - 8, bh - 14);
          ctx.fillStyle = '#059669';
          ctx.fillRect(bx + 8, by + 4, bw - 16, 12);
          // Red Cross Emblem
          ctx.fillStyle = '#EF4444';
          ctx.fillRect(bx + bw / 2 - 2, by + 16, 4, 12);
          ctx.fillRect(bx + bw / 2 - 6, by + 20, 12, 4);
        } else if (bId === 'tavern') {
          // Cozy Tavern (Kedai)
          ctx.fillStyle = '#78350F';
          ctx.fillRect(bx + 4, by + 14, bw - 8, bh - 16);
          ctx.fillStyle = '#F59E0B';
          ctx.fillRect(bx + 6, by + 6, bw - 12, 10);
          // Glowing Lantern
          ctx.fillStyle = '#FEF08A';
          ctx.beginPath();
          ctx.arc(bx + 10, by + bh - 12, 3, 0, Math.PI * 2);
          ctx.fill();
        } else if (bId === 'dungeon') {
          // Mystical Portal (Dungeon)
          ctx.fillStyle = '#1E1B4B';
          ctx.fillRect(bx + 4, by + 10, bw - 8, bh - 12);
          // Swirling Vortex Core
          const vortexRadius = (Math.sin(tick * 0.1) * 3) + 12;
          ctx.fillStyle = '#8B5CF6';
          ctx.beginPath();
          ctx.arc(bx + bw / 2, by + bh / 2, vortexRadius, 0, Math.PI * 2);
          ctx.fill();
          ctx.fillStyle = '#C084FC';
          ctx.beginPath();
          ctx.arc(bx + bw / 2, by + bh / 2, vortexRadius * 0.5, 0, Math.PI * 2);
          ctx.fill();
        } else {
          // Default Building Layout
          ctx.fillStyle = loc.color;
          ctx.fillRect(bx + 4, by + 12, bw - 8, bh - 14);
          ctx.fillStyle = '#334155';
          ctx.fillRect(bx + 8, by + 4, bw - 16, 10);
        }

        // D. Interactive Building Badge & Header
        // Badge Box
        ctx.fillStyle = 'rgba(15, 23, 42, 0.92)';
        ctx.fillRect(bx - 2, by - 16, bw + 4, 15);
        ctx.strokeStyle = loc.color;
        ctx.lineWidth = 1;
        ctx.strokeRect(bx - 2, by - 16, bw + 4, 15);

        // Name & Icon Text
        ctx.fillStyle = '#F8FAFC';
        ctx.font = '8px "Press Start 2P", monospace';
        ctx.fillText(`${loc.icon} ${loc.name}`, bx + 2, by - 6);

        // Level Pill at bottom
        ctx.fillStyle = bLevel > 0 ? '#10B981' : '#64748B';
        ctx.fillRect(bx + bw / 2 - 14, by + bh - 6, 28, 12);
        ctx.fillStyle = '#FFFFFF';
        ctx.font = '7px "Press Start 2P", monospace';
        ctx.fillText(bLevel > 0 ? `Lv.${bLevel}` : 'Kunci', bx + bw / 2 - 11, by + bh + 3);
      }

      // ======================================================================
      // 5. ATMOSPHERIC PARALLAX CLOUDS
      // ======================================================================
      clouds.forEach((cl) => {
        cl.x += cl.speed;
        if (cl.x > WORLD_WIDTH + 50) cl.x = -cl.size - 50;

        ctx.fillStyle = 'rgba(255, 255, 255, 0.22)';
        ctx.beginPath();
        ctx.ellipse(cl.x, cl.y, cl.size, cl.size * 0.45, 0, 0, Math.PI * 2);
        ctx.ellipse(cl.x + cl.size * 0.3, cl.y - 6, cl.size * 0.6, cl.size * 0.35, 0, 0, Math.PI * 2);
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
        ft.y -= 0.8;
        ft.life--;
        ctx.fillStyle = ft.color;
        ctx.font = '9px "Press Start 2P", monospace';
        ctx.fillText(ft.text, ft.x - 15, ft.y);
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
        position: 'relative',
        width: '100%',
        height: isExpanded ? '460px' : '290px',
        backgroundColor: '#070A12',
        borderRadius: '16px',
        overflow: 'hidden',
        border: '2px solid rgba(255, 255, 255, 0.1)',
        boxShadow: '0 8px 30px rgba(0, 0, 0, 0.6)',
        transition: 'height 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
        userSelect: 'none'
      }}
    >
      <canvas
        ref={canvasRef}
        width={896}
        height={isExpanded ? 460 : 290}
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

      {/* Top Map HUD Controls */}
      <div style={{
        position: 'absolute',
        top: '10px',
        left: '12px',
        right: '12px',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        pointerEvents: 'none'
      }}>
        {/* Era Tag */}
        <div style={{
          backgroundColor: 'rgba(15, 23, 42, 0.88)',
          border: '1px solid rgba(255, 255, 255, 0.15)',
          borderRadius: '20px',
          padding: '4px 12px',
          fontSize: '10px',
          fontWeight: '900',
          color: '#FFD54F',
          fontFamily: '"Press Start 2P", monospace',
          boxShadow: '0 4px 12px rgba(0,0,0,0.4)',
          pointerEvents: 'auto'
        }}>
          🗺️ Zaman {eraId.toUpperCase()}
        </div>

        {/* Camera & Zoom Action Buttons */}
        <div style={{ display: 'flex', gap: '6px', pointerEvents: 'auto' }}>
          <button
            onClick={() => setCamera((p) => ({ ...p, zoom: Math.min(2.0, p.zoom + 0.2) }))}
            className="btn-pixel"
            style={{ width: '28px', height: '28px', padding: 0, fontSize: '14px' }}
            title="Perbesar Peta"
          >
            +
          </button>
          <button
            onClick={() => setCamera((p) => ({ ...p, zoom: Math.max(0.65, p.zoom - 0.2) }))}
            className="btn-pixel"
            style={{ width: '28px', height: '28px', padding: 0, fontSize: '14px' }}
            title="Perkecil Peta"
          >
            -
          </button>
          <button
            onClick={() => setCamera({ x: -140, y: -70, zoom: 1.1 })}
            className="btn-pixel"
            style={{ width: '28px', height: '28px', padding: 0, fontSize: '12px' }}
            title="Pusatkan Kamera"
          >
            🎯
          </button>
          <button
            onClick={() => setIsExpanded(!isExpanded)}
            className="btn-pixel"
            style={{ width: '28px', height: '28px', padding: 0, fontSize: '12px' }}
            title={isExpanded ? 'Kecilkan Peta' : 'Perluas Peta'}
          >
            {isExpanded ? '📉' : '📈'}
          </button>
        </div>
      </div>

      {/* Bottom Hint */}
      <div style={{
        position: 'absolute',
        bottom: '8px',
        left: '12px',
        backgroundColor: 'rgba(0, 0, 0, 0.65)',
        padding: '3px 8px',
        borderRadius: '6px',
        fontSize: '9px',
        color: '#94A3B8',
        pointerEvents: 'none'
      }}>
        💡 Geser peta untuk keliling • Klik bangunan untuk buka kartu RPG &amp; fungsinya
      </div>
    </div>
  );
}
