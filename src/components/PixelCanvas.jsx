import React, { useRef, useEffect, useState } from 'react';

export default function PixelCanvas({ state, onCanvasTap }) {
  const canvasRef = useRef(null);
  const [isExpanded, setIsExpanded] = useState(false);

  // Era background selection
  const eraId = state.unlockedNodes.includes('brain_link') ? 'cyberpunk'
    : state.unlockedNodes.includes('pengalengan_makanan') ? 'modern'
    : state.unlockedNodes.includes('domestikasi_hewan') ? 'kerajaan'
    : 'primitif';

  const eraBgMap = {
    primitif: '/assets/img_era_primitif.png',
    kerajaan: '/assets/img_era_kerajaan.png',
    modern: '/assets/img_era_modern.png',
    cyberpunk: '/assets/img_era_cyberpunk.png'
  };

  const simRef = useRef({
    tick: 0,
    bgImg: null,
    clouds: [
      { x: 40, y: 35, speed: 0.28, size: 55 },
      { x: 280, y: 70, speed: 0.20, size: 75 },
      { x: 520, y: 30, speed: 0.32, size: 48 },
      { x: 740, y: 60, speed: 0.24, size: 65 }
    ],
    // Natural wilderness trees & stones
    wildTrees: [
      { x: 120, y: 220, type: 0 }, { x: 180, y: 190, type: 1 }, { x: 90, y: 310, type: 2 },
      { x: 700, y: 240, type: 1 }, { x: 760, y: 310, type: 0 }, { x: 680, y: 440, type: 2 },
      { x: 240, y: 520, type: 0 }, { x: 310, y: 560, type: 1 }, { x: 620, y: 540, type: 2 }
    ],
    wildStones: [
      { x: 60, y: 260 }, { x: 740, y: 210 }, { x: 790, y: 380 }, { x: 160, y: 550 }
    ],
    // Peaceful roaming villagers & guards
    citizens: [
      { x: 380, y: 390, targetX: 430, speed: 0.45, dir: 1, role: 'petani', talk: 'Panen melimpah! 🌾' },
      { x: 440, y: 420, targetX: 360, speed: 0.40, dir: -1, role: 'pemburu', talk: 'Menjaga perbatasan 🏹' },
      { x: 320, y: 380, targetX: 350, speed: 0.35, dir: 1, role: 'pekerja', talk: 'Membangun peradaban 🔨' },
      { x: 490, y: 340, targetX: 520, speed: 0.50, dir: 1, role: 'ksatria', talk: 'Gerbang aman terjaga 🛡️' }
    ],
    // Peaceful grazing animals
    animals: [
      { x: 260, y: 450, targetX: 300, speed: 0.20, dir: 1, type: 'sheep' },
      { x: 580, y: 460, targetX: 540, speed: 0.25, dir: -1, type: 'cow' }
    ],
    smokeParticles: [],
    tapParticles: []
  });

  // Preload Era Background Image
  useEffect(() => {
    const img = new Image();
    img.src = eraBgMap[eraId];
    img.onload = () => {
      simRef.current.bgImg = img;
    };
  }, [eraId]);

  // Main Canvas Render Loop (60 FPS)
  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    ctx.imageSmoothingEnabled = false; // Crisp pixel art

    let animId;

    const render = () => {
      const sim = simRef.current;
      sim.tick++;
      const w = canvas.width;
      const h = canvas.height;

      ctx.clearRect(0, 0, w, h);

      // 1. Draw Era Background
      if (sim.bgImg) {
        ctx.drawImage(sim.bgImg, 0, 0, w, h);
      } else {
        // Fallback procedural nature palette
        ctx.fillStyle = '#111827';
        ctx.fillRect(0, 0, w, h * 0.35); // Sky
        ctx.fillStyle = '#16331C';
        ctx.fillRect(0, h * 0.35, w, h * 0.65); // Grassland
      }

      // 2. Animated River Winding Across Mid-Map
      const riverY = h * 0.68;
      ctx.fillStyle = 'rgba(56, 189, 248, 0.25)';
      ctx.fillRect(0, riverY - 14, w, 28);
      // River shimmering ripples & sparkles
      ctx.fillStyle = 'rgba(255, 255, 255, 0.65)';
      for (let i = 0; i < 10; i++) {
        const rx = ((sim.tick * 1.8 + i * 90) % (w + 40)) - 20;
        const ry = riverY + Math.sin(sim.tick * 0.06 + i) * 8;
        ctx.fillRect(rx, ry, 8, 2);
      }

      // 3. Draw Wild Trees & Stones
      sim.wildTrees.forEach((t) => {
        // Trunk
        ctx.fillStyle = '#5D4037';
        ctx.fillRect(t.x + 6, t.y + 12, 4, 10);
        // Foliage (layers of pixel greens)
        ctx.fillStyle = t.type === 0 ? '#2E7D32' : t.type === 1 ? '#388E3C' : '#1B5E20';
        ctx.fillRect(t.x, t.y, 16, 14);
        ctx.fillRect(t.x + 3, t.y - 6, 10, 8);
      });

      sim.wildStones.forEach((s) => {
        ctx.fillStyle = '#616161';
        ctx.fillRect(s.x, s.y, 12, 8);
        ctx.fillStyle = '#9E9E9E';
        ctx.fillRect(s.x + 2, s.y - 2, 8, 4);
      });

      // 4. Draw Constructed Landmark Buildings on Map
      const unlocked = state.unlockedNodes || [];
      const levels = state.buildingLevels || {};

      // Central Town Hall / Campfire Plaza
      const plazaX = w * 0.50;
      const plazaY = h * 0.48;

      // Draw Campfire in plaza
      const flameH = 6 + Math.sin(sim.tick * 0.35) * 3;
      ctx.fillStyle = '#FF5722';
      ctx.fillRect(plazaX - 4, plazaY - flameH, 8, flameH);
      ctx.fillStyle = '#FFEB3B';
      ctx.fillRect(plazaX - 2, plazaY - flameH * 0.7, 4, flameH * 0.7);

      // Periodic campfire smoke
      if (sim.tick % 7 === 0) {
        sim.smokeParticles.push({
          x: plazaX + (Math.random() * 6 - 3),
          y: plazaY - flameH,
          vx: (Math.random() - 0.5) * 0.3,
          vy: -0.9 - Math.random() * 0.4,
          size: 4 + Math.random() * 3,
          alpha: 0.65
        });
      }

      // Render Unlocked Buildings directly onto their plots
      if (unlocked.includes('town_hall')) {
        // Town Hall Castle / Hub
        ctx.fillStyle = '#37474F';
        ctx.fillRect(plazaX - 24, plazaY - 38, 48, 28);
        ctx.fillStyle = '#B0BEC5';
        ctx.fillRect(plazaX - 20, plazaY - 46, 12, 10);
        ctx.fillRect(plazaX + 8, plazaY - 46, 12, 10);
        // Roof banner
        ctx.fillStyle = '#FFB300';
        ctx.fillRect(plazaX - 2, plazaY - 52, 4, 16);
      }

      if (unlocked.includes('rumah_warga')) {
        const hx = w * 0.36;
        const hy = h * 0.42;
        ctx.fillStyle = '#795548';
        ctx.fillRect(hx - 14, hy - 14, 28, 20);
        ctx.fillStyle = '#FFB74D';
        ctx.fillRect(hx - 18, hy - 24, 36, 10); // Thatch roof
      }

      if (unlocked.includes('pertanian')) {
        const fx = w * 0.28;
        const fy = h * 0.62;
        ctx.fillStyle = '#FBC02D';
        ctx.fillRect(fx - 20, fy - 10, 40, 20);
        // Wheat stalks
        ctx.fillStyle = '#FFF59D';
        for (let i = -16; i < 18; i += 6) {
          ctx.fillRect(fx + i, fy - 14 + Math.sin(sim.tick * 0.1 + i) * 2, 2, 6);
        }
      }

      if (unlocked.includes('tembok')) {
        const gx = w * 0.50;
        const gy = h * 0.22;
        ctx.fillStyle = '#546E7A';
        ctx.fillRect(gx - 40, gy - 12, 80, 16);
        // Archway gate
        ctx.fillStyle = '#263238';
        ctx.fillRect(gx - 10, gy - 8, 20, 12);
        // Flag
        ctx.fillStyle = '#E53935';
        ctx.fillRect(gx - 2, gy - 24, 4, 12);
      }

      if (unlocked.includes('dungeon')) {
        const dx = w * 0.85;
        const dy = h * 0.72;
        // Swirling dungeon vortex
        ctx.fillStyle = '#4A148C';
        ctx.beginPath();
        ctx.arc(dx, dy, 14 + Math.sin(sim.tick * 0.1) * 2, 0, Math.PI * 2);
        ctx.fill();
        ctx.fillStyle = '#B388FF';
        ctx.fillRect(dx - 4, dy - 4, 8, 8);
      }

      // 5. Draw Grazing Animals
      sim.animals.forEach((an) => {
        if (Math.abs(an.x - an.targetX) < 4) {
          if (Math.random() < 0.015) {
            an.targetX = 140 + Math.random() * (w - 280);
            an.dir = an.targetX > an.x ? 1 : -1;
          }
        } else {
          an.x += an.dir * an.speed;
        }

        const bob = Math.sin(sim.tick * 0.2) * 2;
        ctx.fillStyle = an.type === 'sheep' ? '#F5F5F5' : '#8D6E63';
        ctx.fillRect(an.x, an.y + bob, 16, 11);
        ctx.fillStyle = '#3E2723';
        const hX = an.dir > 0 ? an.x + 14 : an.x - 4;
        ctx.fillRect(hX, an.y - 2 + bob, 6, 6);
        // Legs
        ctx.fillStyle = '#212121';
        ctx.fillRect(an.x + 3, an.y + 11 + bob, 2, 4);
        ctx.fillRect(an.x + 11, an.y + 11 + bob, 2, 4);
      });

      // 6. Draw Citizens & Walking Guards with Idle Thoughts
      sim.citizens.forEach((c, idx) => {
        if (Math.abs(c.x - c.targetX) < 4) {
          if (Math.random() < 0.02) {
            c.targetX = 120 + Math.random() * (w - 240);
            c.dir = c.targetX > c.x ? 1 : -1;
          }
        } else {
          c.x += c.dir * c.speed;
        }

        const stepBob = Math.sin(sim.tick * 0.28 + idx) * 2;

        // Head
        ctx.fillStyle = '#FFCC80';
        ctx.fillRect(c.x + 2, c.y - 14 + stepBob, 6, 6);

        // Body costume
        ctx.fillStyle = c.role === 'ksatria' ? '#78909C' : c.role === 'pemburu' ? '#6D4C41' : '#388E3C';
        ctx.fillRect(c.x, c.y - 8 + stepBob, 10, 10);

        // Legs
        ctx.fillStyle = '#263238';
        ctx.fillRect(c.x + 1, c.y + 2 + stepBob, 3, 5);
        ctx.fillRect(c.x + 6, c.y + 2 + stepBob, 3, 5);

        // Spear / Tool
        if (c.role === 'ksatria' || c.role === 'pemburu') {
          ctx.fillStyle = '#ECEFF1';
          const toolX = c.dir > 0 ? c.x + 11 : c.x - 3;
          ctx.fillRect(toolX, c.y - 16 + stepBob, 2, 16);
        }

        // Periodic speech bubble
        if ((sim.tick + idx * 120) % 360 < 100) {
          ctx.fillStyle = 'rgba(15, 23, 42, 0.85)';
          ctx.fillRect(c.x - 28, c.y - 32 + stepBob, 66, 14);
          ctx.fillStyle = '#FFD54F';
          ctx.font = '8px "Press Start 2P", monospace';
          ctx.fillText(c.talk, c.x - 24, c.y - 22 + stepBob);
        }
      });

      // 7. Update & Draw Rising Campfire Smoke
      for (let i = sim.smokeParticles.length - 1; i >= 0; i--) {
        const p = sim.smokeParticles[i];
        p.x += p.vx;
        p.y += p.vy;
        p.alpha -= 0.007;

        if (p.alpha <= 0) {
          sim.smokeParticles.splice(i, 1);
          continue;
        }

        ctx.fillStyle = `rgba(200, 200, 200, ${p.alpha})`;
        ctx.beginPath();
        ctx.arc(p.x, p.y, p.size, 0, Math.PI * 2);
        ctx.fill();
      }

      // 8. Update & Draw Floating Clouds with Ground Shadows
      sim.clouds.forEach((cl) => {
        cl.x += cl.speed;
        if (cl.x > w + 120) cl.x = -120;

        // Ground shadow
        ctx.fillStyle = 'rgba(0, 0, 0, 0.12)';
        ctx.beginPath();
        ctx.ellipse(cl.x, h * 0.72, cl.size * 1.1, cl.size * 0.35, 0, 0, Math.PI * 2);
        ctx.fill();

        // Cloud body
        ctx.fillStyle = 'rgba(255, 255, 255, 0.90)';
        ctx.fillRect(cl.x, cl.y, cl.size, cl.size * 0.45);
        ctx.fillRect(cl.x + 10, cl.y - 10, cl.size * 0.65, cl.size * 0.55);
        ctx.fillRect(cl.x + 24, cl.y + 6, cl.size * 0.75, cl.size * 0.35);
      });

      // 9. Interactive Tap Floating Numbers
      for (let i = sim.tapParticles.length - 1; i >= 0; i--) {
        const tp = sim.tapParticles[i];
        tp.y += tp.vy;
        tp.alpha -= 0.02;

        if (tp.alpha <= 0) {
          sim.tapParticles.splice(i, 1);
          continue;
        }

        ctx.font = 'bold 15px "Press Start 2P", monospace';
        ctx.fillStyle = `rgba(255, 215, 0, ${tp.alpha})`;
        ctx.shadowColor = 'rgba(0,0,0,0.9)';
        ctx.shadowBlur = 4;
        ctx.fillText(tp.text, tp.x - 20, tp.y);
        ctx.shadowBlur = 0;
      }

      animId = requestAnimationFrame(render);
    };

    render();

    return () => cancelAnimationFrame(animId);
  }, [state, eraId]);

  const handlePointerDown = (e) => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const rect = canvas.getBoundingClientRect();
    const scaleX = canvas.width / rect.width;
    const scaleY = canvas.height / rect.height;
    const x = (e.clientX - rect.left) * scaleX;
    const y = (e.clientY - rect.top) * scaleY;

    simRef.current.tapParticles.push({
      x,
      y,
      vy: -2.2,
      alpha: 1.0,
      text: '+✨'
    });

    if (onCanvasTap) onCanvasTap();
  };

  return (
    <div style={{
      position: 'relative',
      width: '100%',
      height: isExpanded ? '65vh' : '380px',
      transition: 'height 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
      borderRadius: '16px',
      overflow: 'hidden',
      border: '2px solid rgba(255, 255, 255, 0.12)',
      boxShadow: '0 8px 32px rgba(0, 0, 0, 0.6)'
    }}>
      <canvas
        ref={canvasRef}
        width={800}
        height={600}
        onPointerDown={handlePointerDown}
        style={{
          width: '100%',
          height: '100%',
          display: 'block',
          cursor: 'pointer',
          imageRendering: 'pixelated'
        }}
      />

      {/* Top Left Era Badge */}
      <div style={{
        position: 'absolute',
        top: '12px',
        left: '12px',
        padding: '6px 12px',
        borderRadius: '8px',
        backgroundColor: 'rgba(15, 23, 42, 0.88)',
        backdropFilter: 'blur(8px)',
        border: '1.5px solid #FFB300',
        display: 'flex',
        alignItems: 'center',
        gap: '8px',
        color: '#FFD54F',
        fontSize: '12px',
        fontWeight: '800'
      }}>
        <span>🌐</span>
        <span style={{ textTransform: 'uppercase' }}>Era {eraId}</span>
      </div>

      {/* Top Right Expand / Minimize Button */}
      <button
        onClick={() => setIsExpanded(!isExpanded)}
        title={isExpanded ? 'Kecilkan Peta' : 'Perluas Peta Ruang Utama (Lega)'}
        style={{
          position: 'absolute',
          top: '12px',
          right: '12px',
          padding: '6px 10px',
          borderRadius: '8px',
          backgroundColor: 'rgba(15, 23, 42, 0.88)',
          backdropFilter: 'blur(8px)',
          border: '1.5px solid rgba(255, 255, 255, 0.2)',
          color: '#ECEFF1',
          fontSize: '12px',
          fontWeight: '700',
          cursor: 'pointer',
          display: 'flex',
          alignItems: 'center',
          gap: '6px'
        }}
      >
        <span>{isExpanded ? '🗗 Perkecil' : '⛶ Peta Lega'}</span>
      </button>

      {/* Bottom Floating Tap Hint */}
      <div style={{
        position: 'absolute',
        bottom: '10px',
        left: '50%',
        transform: 'translateX(-50%)',
        padding: '4px 12px',
        borderRadius: '20px',
        backgroundColor: 'rgba(0, 0, 0, 0.70)',
        backdropFilter: 'blur(4px)',
        color: 'rgba(255, 255, 255, 0.75)',
        fontSize: '11px',
        pointerEvents: 'none',
        display: 'flex',
        alignItems: 'center',
        gap: '6px'
      }}>
        <span>👆</span> Ketuk wilayah untuk memanen Zenith Kosmis
      </div>
    </div>
  );
}
