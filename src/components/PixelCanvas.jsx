import React, { useRef, useEffect } from 'react';

export default function PixelCanvas({ era, onCanvasClick }) {
  const canvasRef = useRef(null);
  const stateRef = useRef({
    clouds: [
      { x: 30, y: 50, speed: 0.35, size: 45 },
      { x: 260, y: 90, speed: 0.25, size: 60 },
      { x: 500, y: 40, speed: 0.40, size: 40 }
    ],
    citizens: [
      { x: 200, y: 380, targetX: 250, speed: 0.5, dir: 1, type: 'villager', stateTime: 0 },
      { x: 340, y: 420, targetX: 300, speed: 0.4, dir: -1, type: 'hunter', stateTime: 0 },
      { x: 450, y: 390, targetX: 520, speed: 0.6, dir: 1, type: 'worker', stateTime: 0 },
      { x: 150, y: 440, targetX: 180, speed: 0.3, dir: 1, type: 'soldier', stateTime: 0 }
    ],
    animals: [
      { x: 120, y: 340, targetX: 160, speed: 0.2, dir: 1, type: 'sheep' },
      { x: 480, y: 460, targetX: 430, speed: 0.25, dir: -1, type: 'cow' }
    ],
    particles: [], // Tap float numbers & smoke
    bgImage: null,
    tick: 0
  });

  // Preload Era Background Image
  useEffect(() => {
    const img = new Image();
    img.src = era.img;
    img.onload = () => {
      stateRef.current.bgImage = img;
    };
  }, [era.img]);

  // Main Canvas Render Loop
  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    ctx.imageSmoothingEnabled = false; // Keep crisp pixel art

    let animationFrameId;

    const render = () => {
      stateRef.current.tick++;
      const { tick, clouds, citizens, animals, particles, bgImage } = stateRef.current;
      const width = canvas.width;
      const height = canvas.height;

      ctx.clearRect(0, 0, width, height);

      // 1. Draw Era Background
      if (bgImage) {
        ctx.drawImage(bgImage, 0, 0, width, height);
      } else {
        // Fallback procedural ground & sky
        ctx.fillStyle = '#1D2430';
        ctx.fillRect(0, 0, width, height * 0.4);
        ctx.fillStyle = '#223826';
        ctx.fillRect(0, height * 0.4, width, height * 0.6);
      }

      // 2. Animated River Water Shimmer (Procedural sparkles on bottom-mid river area)
      const riverY = height * 0.62;
      ctx.fillStyle = 'rgba(255, 255, 255, 0.4)';
      for (let i = 0; i < 8; i++) {
        const rx = ((tick * 1.5 + i * 85) % (width * 0.7)) + (width * 0.15);
        const ry = riverY + Math.sin(tick * 0.05 + i) * 6;
        ctx.fillRect(rx, ry, 6, 2);
      }

      // 3. Central Campfire / Plaza Light & Rising Smoke Particles
      const fireX = width * 0.5;
      const fireY = height * 0.52;

      // Add smoke particle periodically
      if (tick % 8 === 0) {
        particles.push({
          x: fireX + (Math.random() * 8 - 4),
          y: fireY,
          vx: (Math.random() - 0.5) * 0.4,
          vy: -0.8 - Math.random() * 0.5,
          alpha: 0.6,
          size: 4 + Math.random() * 3,
          isSmoke: true
        });
      }

      // Campfire flame glow
      const flameHeight = 6 + Math.sin(tick * 0.3) * 3;
      ctx.fillStyle = '#FF5722';
      ctx.fillRect(fireX - 4, fireY - flameHeight, 8, flameHeight);
      ctx.fillStyle = '#FFEB3B';
      ctx.fillRect(fireX - 2, fireY - flameHeight * 0.7, 4, flameHeight * 0.7);

      // 4. Update & Draw Floating Clouds with Ground Shadows
      clouds.forEach((cloud) => {
        cloud.x += cloud.speed;
        if (cloud.x > width + 100) cloud.x = -100;

        // Cloud shadow on ground
        ctx.fillStyle = 'rgba(0, 0, 0, 0.12)';
        ctx.beginPath();
        ctx.ellipse(cloud.x, height * 0.75, cloud.size * 1.1, cloud.size * 0.3, 0, 0, Math.PI * 2);
        ctx.fill();

        // Pixel cloud body
        ctx.fillStyle = 'rgba(255, 255, 255, 0.85)';
        ctx.fillRect(cloud.x, cloud.y, cloud.size, cloud.size * 0.5);
        ctx.fillRect(cloud.x + 8, cloud.y - 8, cloud.size * 0.65, cloud.size * 0.6);
        ctx.fillRect(cloud.x + 18, cloud.y + 4, cloud.size * 0.8, cloud.size * 0.3);
      });

      // 5. Update & Draw Roaming Animals
      animals.forEach((animal) => {
        if (Math.abs(animal.x - animal.targetX) < 5) {
          if (Math.random() < 0.02) {
            animal.targetX = 80 + Math.random() * (width - 160);
            animal.dir = animal.targetX > animal.x ? 1 : -1;
          }
        } else {
          animal.x += animal.dir * animal.speed;
        }

        // Animal Body (Pixel art sheep / cow)
        const bob = Math.sin(tick * 0.2) * 2;
        ctx.fillStyle = animal.type === 'sheep' ? '#F5F5F5' : '#8D6E63';
        ctx.fillRect(animal.x, animal.y + bob, 14, 10);
        // Head
        ctx.fillStyle = '#3E2723';
        const headX = animal.dir > 0 ? animal.x + 12 : animal.x - 4;
        ctx.fillRect(headX, animal.y - 2 + bob, 6, 6);
        // Legs
        ctx.fillStyle = '#212121';
        ctx.fillRect(animal.x + 2, animal.y + 10 + bob, 2, 4);
        ctx.fillRect(animal.x + 10, animal.y + 10 + bob, 2, 4);
      });

      // 6. Update & Draw Roaming Citizens/Villagers/Soldiers
      citizens.forEach((c) => {
        if (Math.abs(c.x - c.targetX) < 4) {
          if (Math.random() < 0.03) {
            c.targetX = 60 + Math.random() * (width - 120);
            c.dir = c.targetX > c.x ? 1 : -1;
          }
        } else {
          c.x += c.dir * c.speed;
        }

        const walkBob = Math.sin(tick * 0.25) * 2;
        // Head
        ctx.fillStyle = '#FFCC80';
        ctx.fillRect(c.x + 2, c.y - 12 + walkBob, 6, 6);

        // Body costume depends on Era / Type
        if (era.id === 'primitif') {
          ctx.fillStyle = c.type === 'hunter' ? '#8D6E63' : '#6D4C41';
        } else if (era.id === 'kerajaan') {
          ctx.fillStyle = c.type === 'soldier' ? '#78909C' : '#D32F2F';
        } else if (era.id === 'modern') {
          ctx.fillStyle = '#1976D2';
        } else {
          ctx.fillStyle = '#00E5FF'; // Cyberpunk glow
        }
        ctx.fillRect(c.x, c.y - 6 + walkBob, 10, 10);

        // Legs
        ctx.fillStyle = '#37474F';
        ctx.fillRect(c.x + 1, c.y + 4 + walkBob, 3, 5);
        ctx.fillRect(c.x + 6, c.y + 4 + walkBob, 3, 5);

        // Tool / Weapon
        if (c.type === 'hunter' || c.type === 'soldier') {
          ctx.fillStyle = '#B0BEC5';
          const weaponX = c.dir > 0 ? c.x + 11 : c.x - 3;
          ctx.fillRect(weaponX, c.y - 10 + walkBob, 2, 12);
        }
      });

      // 7. Update & Draw Smoke & Floating Click Particles
      for (let i = particles.length - 1; i >= 0; i--) {
        const p = particles[i];
        p.x += p.vx || 0;
        p.y += p.vy || -1;
        p.alpha -= p.isSmoke ? 0.008 : 0.02;

        if (p.alpha <= 0) {
          particles.splice(i, 1);
          continue;
        }

        if (p.isSmoke) {
          ctx.fillStyle = `rgba(180, 180, 180, ${p.alpha})`;
          ctx.beginPath();
          ctx.arc(p.x, p.y, p.size, 0, Math.PI * 2);
          ctx.fill();
        } else {
          // Tap "+Zenith" float text
          ctx.font = 'bold 16px "Press Start 2P", monospace';
          ctx.fillStyle = `rgba(255, 215, 0, ${p.alpha})`;
          ctx.shadowColor = 'rgba(0,0,0,0.8)';
          ctx.shadowBlur = 4;
          ctx.fillText(p.text, p.x - 20, p.y);
          ctx.shadowBlur = 0;
        }
      }

      animationFrameId = requestAnimationFrame(render);
    };

    render();

    return () => {
      cancelAnimationFrame(animationFrameId);
    };
  }, [era]);

  const handleTap = (e) => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const rect = canvas.getBoundingClientRect();
    const scaleX = canvas.width / rect.width;
    const scaleY = canvas.height / rect.height;
    const clickX = (e.clientX - rect.left) * scaleX;
    const clickY = (e.clientY - rect.top) * scaleY;

    // Add float text particle
    stateRef.current.particles.push({
      x: clickX,
      y: clickY,
      vx: (Math.random() - 0.5) * 1.5,
      vy: -2.5,
      alpha: 1.0,
      text: '+✨',
      isSmoke: false
    });

    if (onCanvasClick) {
      onCanvasClick();
    }
  };

  return (
    <div style={{
      position: 'relative',
      width: '100%',
      aspectRatio: '1 / 1',
      maxHeight: '380px',
      overflow: 'hidden',
      borderRadius: '12px',
      border: '2px solid #2A3245',
      boxShadow: 'inset 0 0 30px rgba(0,0,0,0.6)'
    }}>
      <canvas
        ref={canvasRef}
        width={600}
        height={600}
        onClick={handleTap}
        style={{
          width: '100%',
          height: '100%',
          display: 'block',
          cursor: 'pointer',
          imageRendering: 'pixelated'
        }}
      />
      
      {/* Era Badge Overlay */}
      <div style={{
        position: 'absolute',
        top: '10px',
        left: '12px',
        padding: '4px 10px',
        borderRadius: '6px',
        backgroundColor: 'rgba(11, 14, 20, 0.85)',
        border: `1.5px solid ${era.color}`,
        color: era.color,
        fontSize: '11px',
        fontWeight: '700',
        display: 'flex',
        alignItems: 'center',
        gap: '6px',
        backdropFilter: 'blur(4px)'
      }}>
        <span>🌐</span>
        <span>{era.name}</span>
      </div>

      {/* Tap prompt */}
      <div style={{
        position: 'absolute',
        bottom: '8px',
        right: '12px',
        fontSize: '10px',
        color: 'rgba(255, 255, 255, 0.65)',
        backgroundColor: 'rgba(0,0,0,0.6)',
        padding: '3px 8px',
        borderRadius: '4px',
        pointerEvents: 'none'
      }}>
        👆 Ketuk untuk memanen Zenith
      </div>
    </div>
  );
}
