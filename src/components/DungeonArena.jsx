import React, { useRef, useEffect, useState } from 'react';
import confetti from 'canvas-confetti';
import { DUNGEON_MONSTERS, formatNumber } from '../gameState';
import { sound } from '../utils/retroAudio';

export default function DungeonArena({ state, onAttackBoss, onToggleAutoFarm }) {
  const canvasRef = useRef(null);
  const [skillCooldown, setSkillCooldown] = useState(0);

  const currentBoss = DUNGEON_MONSTERS.find((m) => m.level === state.currentDungeonLevel)
    || DUNGEON_MONSTERS[0];

  const primaryHero = (Array.isArray(state.commanders) && state.commanders.find((c) => c.owned))
    || state.commanders[0];

  const simRef = useRef({
    tick: 0,
    heroX: 130,
    heroAnim: 'idle', // 'idle' | 'slash' | 'skill'
    heroSlashTimer: 0,
    bossHitTimer: 0,
    screenShake: 0,
    damageTexts: [],
    magicBeams: [],
    particles: []
  });

  // Manual Attack Trigger with Animation & Sound
  const handlePlayerAttack = () => {
    sound.playAttack();
    simRef.current.heroAnim = 'slash';
    simRef.current.heroSlashTimer = 12;
    simRef.current.bossHitTimer = 10;
    simRef.current.screenShake = 6;

    const dmg = (state.damage || 100) * 10;
    // Spawn floating damage text
    simRef.current.damageTexts.push({
      x: 480 + (Math.random() * 40 - 20),
      y: 110,
      text: `-${formatNumber(dmg)}`,
      color: '#F87171',
      size: 16,
      life: 35
    });

    // Blood / Spark particles
    for (let i = 0; i < 8; i++) {
      simRef.current.particles.push({
        x: 480,
        y: 160,
        vx: (Math.random() - 0.5) * 6,
        vy: -Math.random() * 4 - 1,
        color: ['#F87171', '#FBBF24', '#EF4444'][Math.floor(Math.random() * 3)],
        life: 20,
        size: 3
      });
    }

    if (onAttackBoss) onAttackBoss();
  };

  // Ultimate Skill Burst
  const handleHeroUltimate = () => {
    if (skillCooldown > 0) return;
    sound.playUpgrade();
    setSkillCooldown(8);

    simRef.current.heroAnim = 'skill';
    simRef.current.heroSlashTimer = 25;
    simRef.current.bossHitTimer = 22;
    simRef.current.screenShake = 14;

    const ultDmg = (state.damage || 100) * 35;
    simRef.current.damageTexts.push({
      x: 480,
      y: 90,
      text: `CRIT! -${formatNumber(ultDmg)} 💥`,
      color: '#FACC15',
      size: 20,
      life: 45
    });

    // Lightning beams
    for (let b = 0; b < 6; b++) {
      simRef.current.magicBeams.push({
        x1: 150,
        y1: 130,
        x2: 480 + (Math.random() * 40 - 20),
        y2: 140 + (Math.random() * 40 - 20),
        life: 18,
        color: '#67E8F9'
      });
    }

    confetti({ particleCount: 70, spread: 80 });
    if (onAttackBoss) onAttackBoss();
  };

  // Cooldown countdown
  useEffect(() => {
    if (skillCooldown <= 0) return;
    const timer = setInterval(() => {
      setSkillCooldown((c) => Math.max(0, c - 1));
    }, 1000);
    return () => clearInterval(timer);
  }, [skillCooldown]);

  // 60 FPS Arena Canvas Rendering Loop
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

      ctx.save();

      // Screen shake translation
      if (sim.screenShake > 0) {
        const sx = (Math.random() - 0.5) * sim.screenShake;
        const sy = (Math.random() - 0.5) * sim.screenShake;
        ctx.translate(sx, sy);
        sim.screenShake *= 0.85;
        if (sim.screenShake < 0.5) sim.screenShake = 0;
      }

      ctx.clearRect(0, 0, w, h);

      // ======================================================================
      // 1. DUNGEON ARENA ENVIRONMENT (Deep Cave & Torches)
      // ======================================================================
      const caveGrad = ctx.createLinearGradient(0, 0, 0, h);
      caveGrad.addColorStop(0, '#090A14');
      caveGrad.addColorStop(0.65, '#12162A');
      caveGrad.addColorStop(1, '#05070D');
      ctx.fillStyle = caveGrad;
      ctx.fillRect(0, 0, w, h);

      // Stone Brick Pillars in background
      for (let pX = 60; pX < w; pX += 160) {
        ctx.fillStyle = '#1E2438';
        ctx.fillRect(pX - 16, 20, 32, h);
        ctx.fillStyle = '#2B324D';
        for (let bY = 25; bY < h - 70; bY += 18) {
          ctx.fillRect(pX - 14, bY, 28, 2);
        }
      }

      // Stone Arena Ground Platform
      const groundY = h * 0.76;
      ctx.fillStyle = '#1A1E2E';
      ctx.fillRect(0, groundY, w, h - groundY);
      ctx.fillStyle = '#3B82F6';
      ctx.fillRect(0, groundY, w, 3);
      ctx.fillStyle = '#0F172A';
      ctx.fillRect(0, groundY + 3, w, 4);

      // Wall Torches with Animated Fire
      [80, 280, 520].forEach((tx) => {
        ctx.fillStyle = '#78350F';
        ctx.fillRect(tx - 3, groundY - 65, 6, 22);
        const fH = Math.sin(sim.tick * 0.28 + tx) * 3;
        ctx.fillStyle = '#EF4444';
        ctx.fillRect(tx - 4, groundY - 72 - fH, 8, 8 + fH);
        ctx.fillStyle = '#FBBF24';
        ctx.fillRect(tx - 2, groundY - 70 - fH * 0.7, 4, 5);
      });

      // ======================================================================
      // 2. HERO PARTY (Commander + Accompanying Troops)
      // ======================================================================
      const isSlashing = sim.heroSlashTimer > 0;
      if (sim.heroSlashTimer > 0) sim.heroSlashTimer--;

      const heroBaseX = isSlashing ? sim.heroX + 25 : sim.heroX;
      const heroBob = Math.sin(sim.tick * 0.2) * 2;

      // Accompanying Soldier 1 (Spearman in back)
      const spX = heroBaseX - 55;
      ctx.fillStyle = 'rgba(0,0,0,0.35)';
      ctx.fillRect(spX - 6, groundY - 2, 16, 4);
      ctx.fillStyle = '#EF4444'; // Red soldier tunic
      ctx.fillRect(spX - 5, groundY - 26 + heroBob, 12, 14);
      ctx.fillStyle = '#FED7AA'; // Head
      ctx.fillRect(spX - 3, groundY - 34 + heroBob, 8, 8);
      ctx.fillStyle = '#475569'; // Helmet
      ctx.fillRect(spX - 4, groundY - 36 + heroBob, 10, 4);
      ctx.fillStyle = '#CBD5E1'; // Spear
      ctx.fillRect(spX + 8, groundY - 42 + heroBob, 2, 28);

      // Accompanying Soldier 2 (Archer in back)
      const arX = heroBaseX - 32;
      ctx.fillStyle = 'rgba(0,0,0,0.35)';
      ctx.fillRect(arX - 6, groundY - 2, 16, 4);
      ctx.fillStyle = '#10B981'; // Green archer tunic
      ctx.fillRect(arX - 5, groundY - 24 + heroBob, 12, 12);
      ctx.fillStyle = '#FED7AA';
      ctx.fillRect(arX - 3, groundY - 32 + heroBob, 8, 8);
      ctx.fillStyle = '#F59E0B'; // Bow
      ctx.beginPath();
      ctx.arc(arX + 10, groundY - 26 + heroBob, 8, -Math.PI / 2, Math.PI / 2);
      ctx.strokeStyle = '#F59E0B';
      ctx.lineWidth = 2;
      ctx.stroke();

      // Main Commander Hero (Frontline)
      ctx.fillStyle = 'rgba(0,0,0,0.45)';
      ctx.beginPath();
      ctx.ellipse(heroBaseX + 4, groundY - 2, 16, 5, 0, 0, Math.PI * 2);
      ctx.fill();

      // Hero Body Armor
      ctx.fillStyle = primaryHero.color || '#3B82F6';
      ctx.fillRect(heroBaseX - 8, groundY - 34 + heroBob, 18, 20);

      // Royal Red Cloak
      ctx.fillStyle = '#DC2626';
      ctx.fillRect(heroBaseX - 12, groundY - 32 + heroBob, 5, 16);

      // Hero Head & Golden Helmet/Crown
      ctx.fillStyle = '#FED7AA';
      ctx.fillRect(heroBaseX - 5, groundY - 44 + heroBob, 12, 11);
      ctx.fillStyle = '#FACC15'; // Crown
      ctx.fillRect(heroBaseX - 7, groundY - 48 + heroBob, 16, 5);
      ctx.fillRect(heroBaseX + 1, groundY - 51 + heroBob, 4, 4);

      // Eyes
      ctx.fillStyle = '#0F172A';
      ctx.fillRect(heroBaseX + 2, groundY - 40 + heroBob, 3, 3);

      // Hero Sword & Slash Arc
      if (isSlashing) {
        // Glowing Golden Slash Arc
        ctx.strokeStyle = '#FACC15';
        ctx.lineWidth = 4;
        ctx.beginPath();
        ctx.arc(heroBaseX + 28, groundY - 34, 26, -Math.PI * 0.4, Math.PI * 0.4);
        ctx.stroke();

        ctx.strokeStyle = '#FFFFFF';
        ctx.lineWidth = 2;
        ctx.beginPath();
        ctx.arc(heroBaseX + 28, groundY - 34, 25, -Math.PI * 0.35, Math.PI * 0.35);
        ctx.stroke();

        // Extended Blade
        ctx.fillStyle = '#F8FAFC';
        ctx.fillRect(heroBaseX + 12, groundY - 36, 26, 4);
      } else {
        // Resting Sword
        ctx.fillStyle = '#CBD5E1';
        ctx.fillRect(heroBaseX + 10, groundY - 38 + heroBob, 4, 22);
        ctx.fillStyle = '#FACC15';
        ctx.fillRect(heroBaseX + 8, groundY - 24 + heroBob, 8, 3);
      }

      // ======================================================================
      // 3. PIXEL ART DUNGEON MONSTER (Rich Detail based on Boss Level)
      // ======================================================================
      const bX = 480;
      const isBossHit = sim.bossHitTimer > 0;
      if (sim.bossHitTimer > 0) sim.bossHitTimer--;

      const bossBob = Math.sin(sim.tick * 0.16) * 3;
      const flashColor = isBossHit ? '#FFFFFF' : null;

      // Boss Ground Shadow
      ctx.fillStyle = 'rgba(0,0,0,0.5)';
      ctx.beginPath();
      ctx.ellipse(bX, groundY - 2, 34, 8, 0, 0, Math.PI * 2);
      ctx.fill();

      // Render Specific Monster Sprite by Level
      if (currentBoss.level <= 1) {
        // Savage Grey Wolf
        ctx.fillStyle = flashColor || '#475569';
        ctx.fillRect(bX - 22, groundY - 28 + bossBob, 38, 18);
        ctx.fillRect(bX - 30, groundY - 38 + bossBob, 18, 18); // Head
        // Wolf Ears & Snout
        ctx.fillRect(bX - 28, groundY - 44 + bossBob, 5, 7);
        ctx.fillRect(bX - 36, groundY - 28 + bossBob, 10, 8); // Snout
        // Glowing Red Eyes
        ctx.fillStyle = '#EF4444';
        ctx.fillRect(bX - 26, groundY - 34 + bossBob, 4, 3);
        // Fangs
        ctx.fillStyle = '#FFFFFF';
        ctx.fillRect(bX - 34, groundY - 21 + bossBob, 3, 4);
      } else if (currentBoss.level <= 5) {
        // Alpha Direwolf (Giant Crimson Wolf)
        ctx.fillStyle = flashColor || '#DC2626';
        ctx.fillRect(bX - 30, groundY - 40 + bossBob, 54, 26);
        ctx.fillRect(bX - 42, groundY - 52 + bossBob, 26, 26);
        // Spiked Back Fur
        ctx.fillStyle = '#991B1B';
        ctx.fillRect(bX - 20, groundY - 48 + bossBob, 8, 10);
        ctx.fillRect(bX - 5, groundY - 48 + bossBob, 8, 10);
        // Glowing Yellow Eyes
        ctx.fillStyle = '#FDE047';
        ctx.fillRect(bX - 36, groundY - 46 + bossBob, 5, 4);
      } else if (currentBoss.level <= 10) {
        // Goblin King with Spiked Club & Crown
        ctx.fillStyle = flashColor || '#15803D'; // Green goblin body
        ctx.fillRect(bX - 20, groundY - 44 + bossBob, 36, 32);
        ctx.fillRect(bX - 16, groundY - 58 + bossBob, 28, 20); // Head
        // Jagged Crown
        ctx.fillStyle = '#FACC15';
        ctx.polygon = [[bX - 16, groundY - 58 + bossBob], [bX - 10, groundY - 68 + bossBob], [bX - 4, groundY - 58 + bossBob], [bX + 4, groundY - 68 + bossBob], [bX + 10, groundY - 58 + bossBob]];
        ctx.fillRect(bX - 16, groundY - 64 + bossBob, 28, 6);
        // Spiked Club in Hand
        ctx.fillStyle = '#78350F';
        ctx.fillRect(bX - 36, groundY - 52 + bossBob, 10, 36);
        ctx.fillStyle = '#94A3B8';
        ctx.fillRect(bX - 40, groundY - 56 + bossBob, 18, 12);
        // Red Eyes & Teeth
        ctx.fillStyle = '#EF4444';
        ctx.fillRect(bX - 10, groundY - 50 + bossBob, 4, 4);
      } else if (currentBoss.level <= 15) {
        // Orc Warlord with Battleaxe
        ctx.fillStyle = flashColor || '#EA580C';
        ctx.fillRect(bX - 26, groundY - 54 + bossBob, 48, 40);
        ctx.fillRect(bX - 20, groundY - 72 + bossBob, 36, 24);
        // Iron Horns Helm
        ctx.fillStyle = '#334155';
        ctx.fillRect(bX - 24, groundY - 76 + bossBob, 44, 8);
        ctx.fillRect(bX - 30, groundY - 84 + bossBob, 8, 14);
        ctx.fillRect(bX + 18, groundY - 84 + bossBob, 8, 14);
        // Massive Battleaxe
        ctx.fillStyle = '#78350F';
        ctx.fillRect(bX - 42, groundY - 70 + bossBob, 6, 52);
        ctx.fillStyle = '#CBD5E1';
        ctx.fillRect(bX - 54, groundY - 74 + bossBob, 24, 20);
      } else if (currentBoss.level <= 30) {
        // Two-Headed Ogre
        ctx.fillStyle = flashColor || '#7C3AED';
        ctx.fillRect(bX - 32, groundY - 64 + bossBob, 60, 48);
        // Head 1 & Head 2
        ctx.fillRect(bX - 30, groundY - 82 + bossBob, 24, 22);
        ctx.fillRect(bX + 4, groundY - 82 + bossBob, 24, 22);
        ctx.fillStyle = '#FDE047';
        ctx.fillRect(bX - 22, groundY - 74 + bossBob, 4, 4);
        ctx.fillRect(bX + 12, groundY - 74 + bossBob, 4, 4);
      } else {
        // Archdemon / Cosmic Titan
        ctx.fillStyle = flashColor || '#991B1B';
        ctx.fillRect(bX - 36, groundY - 78 + bossBob, 68, 62);
        // Demonic Horns
        ctx.fillStyle = '#1E1B4B';
        ctx.fillRect(bX - 34, groundY - 96 + bossBob, 10, 22);
        ctx.fillRect(bX + 22, groundY - 96 + bossBob, 10, 22);
        // Giant Bat Wings
        ctx.fillStyle = '#7F1D1D';
        ctx.beginPath();
        ctx.moveTo(bX + 28, groundY - 60 + bossBob);
        ctx.lineTo(bX + 70, groundY - 90 + bossBob);
        ctx.lineTo(bX + 50, groundY - 30 + bossBob);
        ctx.closePath();
        ctx.fill();
        // Flaming Core
        ctx.fillStyle = '#F59E0B';
        ctx.beginPath();
        ctx.arc(bX - 2, groundY - 48 + bossBob, 12, 0, Math.PI * 2);
        ctx.fill();
      }

      // ======================================================================
      // 4. MAGIC BEAMS & PARTICLES
      // ======================================================================
      for (let i = sim.magicBeams.length - 1; i >= 0; i--) {
        const mb = sim.magicBeams[i];
        ctx.strokeStyle = mb.color;
        ctx.lineWidth = 3;
        ctx.beginPath();
        ctx.moveTo(mb.x1, mb.y1);
        ctx.lineTo(mb.x2, mb.y2);
        ctx.stroke();
        mb.life--;
        if (mb.life <= 0) sim.magicBeams.splice(i, 1);
      }

      for (let i = sim.particles.length - 1; i >= 0; i--) {
        const p = sim.particles[i];
        p.x += p.vx;
        p.y += p.vy;
        p.life--;
        ctx.fillStyle = p.color;
        ctx.fillRect(p.x, p.y, p.size, p.size);
        if (p.life <= 0) sim.particles.splice(i, 1);
      }

      // ======================================================================
      // 5. FLOATING DAMAGE NUMBERS
      // ======================================================================
      for (let i = sim.damageTexts.length - 1; i >= 0; i--) {
        const dt = sim.damageTexts[i];
        dt.y -= 1.2;
        dt.life--;
        ctx.fillStyle = dt.color;
        ctx.font = `${dt.size}px "Press Start 2P", monospace`;
        ctx.fillText(dt.text, dt.x, dt.y);
        if (dt.life <= 0) sim.damageTexts.splice(i, 1);
      }

      ctx.restore();
      animId = requestAnimationFrame(render);
    };

    animId = requestAnimationFrame(render);
    return () => cancelAnimationFrame(animId);
  }, [currentBoss, primaryHero, state]);

  // HP Bar Percentage
  const maxHp = currentBoss.hp || 1000;
  const curHp = Math.max(0, state.monsterHp);
  const hpPercent = Math.min(100, Math.max(0, (curHp / maxHp) * 100));

  return (
    <div style={{
      backgroundColor: '#070A14',
      borderRadius: '16px',
      border: '2px solid rgba(255, 255, 255, 0.1)',
      overflow: 'hidden',
      boxShadow: '0 8px 30px rgba(0,0,0,0.8)'
    }}>
      {/* Boss Health Bar Header */}
      <div style={{
        padding: '12px 16px',
        backgroundColor: 'rgba(15, 23, 42, 0.9)',
        borderBottom: '1px solid rgba(255, 255, 255, 0.08)',
        display: 'flex',
        flexDirection: 'column',
        gap: '6px'
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <span style={{ fontSize: '20px' }}>{currentBoss.icon}</span>
            <div>
              <div style={{ fontWeight: '900', fontSize: '13px', color: '#F8FAFC' }}>
                {currentBoss.name} <span style={{ color: '#F59E0B' }}>(Level {state.currentDungeonLevel})</span>
              </div>
              <div style={{ fontSize: '10px', color: '#EF4444', fontWeight: '700' }}>
                {currentBoss.isBoss ? '👑 DUNGEON BOSS' : '⚔️ ELITE BEAST'}
              </div>
            </div>
          </div>

          <div style={{
            fontSize: '11px',
            fontFamily: '"Press Start 2P", monospace',
            color: '#F87171'
          }}>
            {formatNumber(curHp)} / {formatNumber(maxHp)} HP
          </div>
        </div>

        {/* Dynamic HP Bar */}
        <div style={{
          width: '100%',
          height: '10px',
          backgroundColor: '#1E293B',
          borderRadius: '5px',
          overflow: 'hidden',
          border: '1px solid rgba(255, 255, 255, 0.1)'
        }}>
          <div style={{
            width: `${hpPercent}%`,
            height: '100%',
            backgroundColor: hpPercent > 50 ? '#EF4444' : hpPercent > 20 ? '#F59E0B' : '#DC2626',
            boxShadow: '0 0 10px rgba(239, 68, 68, 0.5)',
            transition: 'width 0.15s ease'
          }} />
        </div>
      </div>

      {/* Battle Canvas */}
      <canvas
        ref={canvasRef}
        width={600}
        height={220}
        style={{
          width: '100%',
          height: '220px',
          display: 'block'
        }}
      />

      {/* Battle Action Controls */}
      <div style={{
        padding: '12px 16px',
        backgroundColor: 'rgba(15, 23, 42, 0.95)',
        borderTop: '1px solid rgba(255, 255, 255, 0.08)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        gap: '10px'
      }}>
        {/* Auto Farm Toggle */}
        <button
          onClick={onToggleAutoFarm}
          className={`btn-pixel ${state.autoFarmDungeon ? 'gold' : ''}`}
          style={{ fontSize: '11px', padding: '8px 14px' }}
        >
          {state.autoFarmDungeon ? '⚡ Auto-Farm AKTIF' : '⏸️ Auto-Farm MATI'}
        </button>

        {/* Combat Buttons */}
        <div style={{ display: 'flex', gap: '8px' }}>
          <button
            onClick={handleHeroUltimate}
            disabled={skillCooldown > 0}
            className="btn-pixel cyan"
            style={{ fontSize: '11px', padding: '8px 14px' }}
          >
            {skillCooldown > 0 ? `⏳ Skill (${skillCooldown}s)` : '🔥 Skill Hero'}
          </button>
          <button
            onClick={handlePlayerAttack}
            className="btn-pixel gold"
            style={{ fontSize: '12px', padding: '8px 20px', fontWeight: '900' }}
          >
            ⚔️ Serang Bos!
          </button>
        </div>
      </div>
    </div>
  );
}
