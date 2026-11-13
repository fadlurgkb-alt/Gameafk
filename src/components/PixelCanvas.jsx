import React, { useRef, useEffect, useState } from 'react';
import { ALL_BUILDINGS } from '../gameState';

export default function PixelCanvas({ state, onCanvasTap, onSelectBuilding }) {
  const canvasRef = useRef(null);
  const containerRef = useRef(null);

  // Pan & Zoom Camera State
  const [camera, setCamera] = useState({ x: 0, y: 0, zoom: 1.0 });
  const isDraggingRef = useRef(false);
  const dragStartRef = useRef({ x: 0, y: 0 });
  const [isExpanded, setIsExpanded] = useState(false);

  // Era detection
  const eraId = Array.isArray(state.unlockedNodes) && state.unlockedNodes.includes('brain_link') ? 'cyberpunk'
    : Array.isArray(state.unlockedNodes) && state.unlockedNodes.includes('pengalengan_makanan') ? 'modern'
    : Array.isArray(state.unlockedNodes) && state.unlockedNodes.includes('domestikasi_hewan') ? 'kerajaan'
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
      { x: 60, y: 40, speed: 0.35, size: 65 },
      { x: 340, y: 80, speed: 0.25, size: 85 },
      { x: 640, y: 50, speed: 0.38, size: 55 }
    ],
    wildTrees: [
      { x: 140, y: 220, type: 0 }, { x: 200, y: 190, type: 1 }, { x: 100, y: 310, type: 2 },
      { x: 700, y: 240, type: 1 }, { x: 750, y: 320, type: 0 }, { x: 670, y: 440, type: 2 },
      { x: 260, y: 480, type: 0 }, { x: 330, y: 520, type: 1 }, { x: 600, y: 510, type: 2 }
    ],
    citizens: [
      { x: 390, y: 290, targetX: 430, speed: 0.5, dir: 1, role: 'petani', talk: 'Panen gandum aman 🌾' },
      { x: 450, y: 310, targetX: 370, speed: 0.45, dir: -1, role: 'pemburu', talk: 'Menjaga perbatasan 🏹' },
      { x: 340, y: 270, targetX: 370, speed: 0.4, dir: 1, role: 'pekerja', talk: 'Membangun peradaban 🔨' },
      { x: 500, y: 230, targetX: 530, speed: 0.55, dir: 1, role: 'ksatria', talk: 'Gerbang kokoh terjaga 🛡️' }
    ],
    animals: [
      { x: 220, y: 340, targetX: 270, speed: 0.22, dir: 1, type: 'sheep' },
      { x: 580, y: 370, targetX: 530, speed: 0.25, dir: -1, type: 'cow' }
    ],
    smokeParticles: [],
    tapParticles: []
  });

  useEffect(() => {
    const img = new Image();
    img.src = eraBgMap[eraId];
    img.onload = () => {
      simRef.current.bgImg = img;
    };
  }, [eraId]);

  // Main 60 FPS Render Loop
  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    ctx.imageSmoothingEnabled = false;

    let animId;

    const render = () => {
      const sim = simRef.current;
      sim.tick++;
      const w = canvas.width;
      const h = canvas.height;

      ctx.clearRect(0, 0, w, h);

      ctx.save();
      ctx.translate(w / 2 + camera.x, h / 2 + camera.y);
      ctx.scale(camera.zoom, camera.zoom);
      ctx.translate(-w / 2, -h / 2);

      // 1. Draw Era Background
      if (sim.bgImg) {
        ctx.drawImage(sim.bgImg, 0, 0, w, h);
      } else {
        ctx.fillStyle = '#0F172A';
        ctx.fillRect(0, 0, w, h * 0.35);
        ctx.fillStyle = '#14532D';
        ctx.fillRect(0, h * 0.35, w, h * 0.65);
      }

      // 2. Animated River Water Shimmer
      const riverY = h * 0.72;
      ctx.fillStyle = 'rgba(56, 189, 248, 0.28)';
      ctx.fillRect(0, riverY - 14, w, 28);
      ctx.fillStyle = 'rgba(255, 255, 255, 0.7)';
      for (let i = 0; i < 10; i++) {
        const rx = ((sim.tick * 1.8 + i * 95) % (w + 40)) - 20;
        const ry = riverY + Math.sin(sim.tick * 0.06 + i) * 8;
        ctx.fillRect(rx, ry, 8, 2);
      }

      // 3. Draw Wild Trees & Stones
      sim.wildTrees.forEach((t) => {
        ctx.fillStyle = '#451A03';
        ctx.fillRect(t.x + 6, t.y + 12, 4, 10);
        ctx.fillStyle = t.type === 0 ? '#15803D' : t.type === 1 ? '#16A34A' : '#14532D';
        ctx.fillRect(t.x, t.y, 16, 14);
        ctx.fillRect(t.x + 3, t.y - 6, 10, 8);
      });

      // 4. Campfire
      const plazaX = 400;
      const plazaY = 280;
      const flameH = 6 + Math.sin(sim.tick * 0.35) * 3;
      ctx.fillStyle = '#EA580C';
      ctx.fillRect(plazaX - 4, plazaY - flameH, 8, flameH);
      ctx.fillStyle = '#FDE047';
      ctx.fillRect(plazaX - 2, plazaY - flameH * 0.7, 4, flameH * 0.7);

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

      // 5. Draw Dynamic Built Structures
      const unlocked = state.unlockedNodes || [];

      // Town Hall (Balai Kota)
      if (unlocked.includes('town_hall')) {
        ctx.fillStyle = '#334155';
        ctx.fillRect(plazaX - 26, plazaY - 42, 52, 32);
        ctx.fillStyle = '#94A3B8';
        ctx.fillRect(plazaX - 22, plazaY - 50, 14, 10);
        ctx.fillRect(plazaX + 8, plazaY - 50, 14, 10);
        ctx.fillStyle = '#F59E0B';
        ctx.fillRect(plazaX - 2, plazaY - 58, 4, 18);
        ctx.font = 'bold 9px sans-serif';
        ctx.fillStyle = '#FFF';
        ctx.fillText('🏛️ Balai Kota', plazaX - 22, plazaY - 60);
      }

      // Rumah Warga
      if (unlocked.includes('rumah_warga')) {
        ctx.fillStyle = '#78350F';
        ctx.fillRect(285, 230, 30, 20);
        ctx.fillStyle = '#FBBF24';
        ctx.fillRect(280, 220, 40, 10);
      }

      // Pertanian
      if (unlocked.includes('pertanian')) {
        ctx.fillStyle = '#CA8A04';
        ctx.fillRect(220, 350, 44, 22);
        ctx.fillStyle = '#FEF08A';
        for (let i = -18; i < 20; i += 6) {
          ctx.fillRect(242 + i, 345 + Math.sin(sim.tick * 0.1 + i) * 2, 2, 6);
        }
        ctx.font = 'bold 9px sans-serif';
        ctx.fillStyle = '#FFF';
        ctx.fillText('🌾 Ladang', 224, 340);
      }

      // Gerbang Benteng
      if (unlocked.includes('tembok')) {
        ctx.fillStyle = '#475569';
        ctx.fillRect(360, 120, 80, 18);
        ctx.fillStyle = '#0F172A';
        ctx.fillRect(390, 125, 20, 13);
        ctx.fillStyle = '#DC2626';
        ctx.fillRect(398, 105, 4, 15);
        ctx.font = 'bold 9px sans-serif';
        ctx.fillStyle = '#FFF';
        ctx.fillText('🛡️ Gerbang', 376, 102);
      }

      // Barak Militer
      if (unlocked.includes('barak_militer')) {
        ctx.fillStyle = '#1E3A8A';
        ctx.fillRect(505, 155, 36, 24);
        ctx.fillStyle = '#3B82F6';
        ctx.fillRect(500, 148, 46, 8);
        ctx.font = 'bold 9px sans-serif';
        ctx.fillStyle = '#FFF';
        ctx.fillText('⚔️ Barak', 506, 142);
      }

      // Balai Pengobatan (Hospital)
      if (unlocked.includes('hospital')) {
        ctx.fillStyle = '#14532D';
        ctx.fillRect(265, 155, 34, 24);
        ctx.fillStyle = '#22C55E';
        ctx.fillRect(260, 148, 44, 8);
        ctx.font = 'bold 9px sans-serif';
        ctx.fillStyle = '#FFF';
        ctx.fillText('🏥 Obat', 268, 142);
      }

      // Gudang
      if (unlocked.includes('gudang')) {
        ctx.fillStyle = '#713F12';
        ctx.fillRect(485, 230, 32, 22);
        ctx.fillStyle = '#F59E0B';
        ctx.fillRect(480, 222, 42, 9);
        ctx.font = 'bold 9px sans-serif';
        ctx.fillStyle = '#FFF';
        ctx.fillText('📦 Gudang', 482, 218);
      }

      // Dungeon Portal
      if (unlocked.includes('dungeon')) {
        ctx.fillStyle = '#581C87';
        ctx.beginPath();
        ctx.arc(680, 420, 16 + Math.sin(sim.tick * 0.1) * 2, 0, Math.PI * 2);
        ctx.fill();
        ctx.fillStyle = '#C084FC';
        ctx.fillRect(676, 416, 8, 8);
        ctx.font = 'bold 9px sans-serif';
        ctx.fillStyle = '#FFF';
        ctx.fillText('🌀 Dungeon', 655, 396);
      }

      // 6. Draw Grazing Animals
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
        ctx.fillStyle = an.type === 'sheep' ? '#F8FAFC' : '#78350F';
        ctx.fillRect(an.x, an.y + bob, 16, 11);
        ctx.fillStyle = '#3E2723';
        const hX = an.dir > 0 ? an.x + 14 : an.x - 4;
        ctx.fillRect(hX, an.y - 2 + bob, 6, 6);
        ctx.fillStyle = '#0F172A';
        ctx.fillRect(an.x + 3, an.y + 11 + bob, 2, 4);
        ctx.fillRect(an.x + 11, an.y + 11 + bob, 2, 4);
      });

      // 7. Draw Walking Citizens with Thoughts
      sim.citizens.forEach((c, idx) => {
        if (Math.abs(c.x - c.targetX) < 4) {
          if (Math.random() < 0.02) {
            c.targetX = 140 + Math.random() * (w - 280);
            c.dir = c.targetX > c.x ? 1 : -1;
          }
        } else {
          c.x += c.dir * c.speed;
        }

        const stepBob = Math.sin(sim.tick * 0.28 + idx) * 2;
        ctx.fillStyle = '#FED7AA';
        ctx.fillRect(c.x + 2, c.y - 14 + stepBob, 6, 6);
        ctx.fillStyle = c.role === 'ksatria' ? '#64748B' : c.role === 'pemburu' ? '#78350F' : '#16A34A';
        ctx.fillRect(c.x, c.y - 8 + stepBob, 10, 10);
        ctx.fillStyle = '#0F172A';
        ctx.fillRect(c.x + 1, c.y + 2 + stepBob, 3, 5);
        ctx.fillRect(c.x + 6, c.y + 2 + stepBob, 3, 5);

        if (c.role === 'ksatria' || c.role === 'pemburu') {
          ctx.fillStyle = '#CBD5E1';
          const toolX = c.dir > 0 ? c.x + 11 : c.x - 3;
          ctx.fillRect(toolX, c.y - 16 + stepBob, 2, 16);
        }

        if ((sim.tick + idx * 120) % 360 < 100) {
          ctx.fillStyle = 'rgba(15, 23, 42, 0.9)';
          ctx.fillRect(c.x - 28, c.y - 32 + stepBob, 76, 14);
          ctx.fillStyle = '#FCD34D';
          ctx.font = '8px "Press Start 2P", monospace';
          ctx.fillText(c.talk, c.x - 24, c.y - 22 + stepBob);
        }
      });

      // 8. Update Smoke Particles
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

      // 9. Floating Clouds
      sim.clouds.forEach((cl) => {
        cl.x += cl.speed;
        if (cl.x > w + 120) cl.x = -120;
        ctx.fillStyle = 'rgba(0, 0, 0, 0.12)';
        ctx.beginPath();
        ctx.ellipse(cl.x, h * 0.72, cl.size * 1.1, cl.size * 0.35, 0, 0, Math.PI * 2);
        ctx.fill();

        ctx.fillStyle = 'rgba(255, 255, 255, 0.9)';
        ctx.fillRect(cl.x, cl.y, cl.size, cl.size * 0.45);
        ctx.fillRect(cl.x + 10, cl.y - 10, cl.size * 0.65, cl.size * 0.55);
        ctx.fillRect(cl.x + 24, cl.y + 6, cl.size * 0.75, cl.size * 0.35);
      });

      // 10. Tap Particles
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

      ctx.restore();

      animId = requestAnimationFrame(render);
    };

    render();
    return () => cancelAnimationFrame(animId);
  }, [state, eraId, camera]);

  const handlePointerDown = (e) => {
    isDraggingRef.current = true;
    dragStartRef.current = { x: e.clientX - camera.x, y: e.clientY - camera.y };
  };

  const handlePointerMove = (e) => {
    if (!isDraggingRef.current) return;
    setCamera((prev) => ({
      ...prev,
      x: e.clientX - dragStartRef.current.x,
      y: e.clientY - dragStartRef.current.y
    }));
  };

  const handlePointerUp = (e) => {
    const dist = Math.hypot(
      e.clientX - (dragStartRef.current.x + camera.x),
      e.clientY - (dragStartRef.current.y + camera.y)
    );

    if (dist < 6) {
      const canvas = canvasRef.current;
      if (canvas) {
        const rect = canvas.getBoundingClientRect();
        const screenX = (e.clientX - rect.left) * (canvas.width / rect.width);
        const screenY = (e.clientY - rect.top) * (canvas.height / rect.height);

        // Reverse camera transform to find world coordinates
        const worldX = (screenX - (canvas.width / 2 + camera.x)) / camera.zoom + canvas.width / 2;
        const worldY = (screenY - (canvas.height / 2 + camera.y)) / camera.zoom + canvas.height / 2;

        // Check if any building was clicked on map!
        const hitBuilding = ALL_BUILDINGS.find((b) => {
          return state.unlockedNodes.includes(b.id) && Math.hypot(b.plotX - worldX, b.plotY - worldY) < 40;
        });

        if (hitBuilding && onSelectBuilding) {
          onSelectBuilding(hitBuilding);
          isDraggingRef.current = false;
          return;
        }

        // Otherwise, add harvest particle
        simRef.current.tapParticles.push({
          x: screenX,
          y: screenY,
          vy: -2.2,
          alpha: 1.0,
          text: '+✨'
        });
      }
      if (onCanvasTap) onCanvasTap();
    }
    isDraggingRef.current = false;
  };

  return (
    <div
      ref={containerRef}
      style={{
        position: 'relative',
        width: '100%',
        height: isExpanded ? '68vh' : '390px',
        transition: 'height 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
        borderRadius: '16px',
        overflow: 'hidden',
        border: '2px solid rgba(255, 255, 255, 0.12)',
        boxShadow: '0 12px 40px rgba(0, 0, 0, 0.7)'
      }}
    >
      <canvas
        ref={canvasRef}
        width={800}
        height={560}
        onPointerDown={handlePointerDown}
        onPointerMove={handlePointerMove}
        onPointerUp={handlePointerUp}
        onPointerLeave={() => { isDraggingRef.current = false; }}
        style={{
          width: '100%',
          height: '100%',
          display: 'block',
          cursor: isDraggingRef.current ? 'grabbing' : 'pointer',
          imageRendering: 'pixelated',
          touchAction: 'none'
        }}
      />

      {/* Era Badge */}
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

      {/* Camera Controls */}
      <div style={{
        position: 'absolute',
        top: '12px',
        right: '12px',
        display: 'flex',
        gap: '6px'
      }}>
        <button onClick={() => setCamera((p) => ({ ...p, zoom: Math.min(2.0, p.zoom + 0.2) }))} className="btn-pixel" title="Zoom In" style={{ padding: '6px 10px', fontSize: '12px' }}>🔍+</button>
        <button onClick={() => setCamera((p) => ({ ...p, zoom: Math.max(0.6, p.zoom - 0.2) }))} className="btn-pixel" title="Zoom Out" style={{ padding: '6px 10px', fontSize: '12px' }}>🔍-</button>
        <button onClick={() => setCamera({ x: 0, y: 0, zoom: 1.0 })} className="btn-pixel" title="Reset Kamera" style={{ padding: '6px 8px', fontSize: '11px' }}>🎯</button>
        <button onClick={() => setIsExpanded(!isExpanded)} className="btn-pixel gold" style={{ padding: '6px 10px', fontSize: '12px' }}>{isExpanded ? '🗗 Kecilkan' : '⛶ Peta Lega'}</button>
      </div>

      {/* Bottom Hint */}
      <div style={{
        position: 'absolute',
        bottom: '10px',
        left: '50%',
        transform: 'translateX(-50%)',
        padding: '4px 14px',
        borderRadius: '20px',
        backgroundColor: 'rgba(15, 23, 42, 0.85)',
        backdropFilter: 'blur(6px)',
        color: 'rgba(255, 255, 255, 0.8)',
        fontSize: '11px',
        pointerEvents: 'none',
        display: 'flex',
        alignItems: 'center',
        gap: '8px'
      }}>
        <span>👆 Klik bangunan di peta untuk membuka fungsi RPG-nya</span>
      </div>
    </div>
  );
}
