import React, { useRef, useEffect, useState } from 'react';
import confetti from 'canvas-confetti';
import { DUNGEON_MONSTERS, formatNumber } from '../gameState';
import { sound } from '../utils/retroAudio';

export default function DungeonArena({ state, onAttackBoss, onToggleAutoFarm }) {
  const canvasRef = useRef(null);
  const [skillCooldown, setSkillCooldown] = useState(0);

  // Player HP State in Dungeon (PVE Combat)
  const playerMaxHp = (state.garrisonedPasukan || 10) * 150 + 1000;
  const [playerHp, setPlayerHp] = useState(playerMaxHp);

  const currentBoss = DUNGEON_MONSTERS.find((m) => m.level === state.currentDungeonLevel)
    || DUNGEON_MONSTERS[0];

  const primaryHero = (Array.isArray(state.commanders) && state.commanders.find((c) => c.owned))
    || state.commanders[0];

  const simRef = useRef({
    tick: 0,
    // Player Party
    heroX: 130,
    heroAnim: 'idle', // 'idle' | 'slash' | 'skill' | 'hurt'
    heroSlashTimer: 0,
    heroHurtTimer: 0,
    // Boss AI & Attack Cycle
    bossBaseX: 470,
    bossX: 470,
    bossAnim: 'idle', // 'idle' | 'telegraph' | 'lunge' | 'strike'
    bossAttackTimer: 140, // Counts down to attack every ~2.5s
    bossHitTimer: 0,
    // Visual Effects
    screenShake: 0,
    damageTexts: [],
    arrows: [],
    magicBeams: [],
    particles: [],
    claws: []
  });

  // Manual Player Attack Trigger
  const handlePlayerAttack = () => {
    sound.playAttack();
    const sim = simRef.current;
    sim.heroAnim = 'slash';
    sim.heroSlashTimer = 12;
    sim.bossHitTimer = 10;
    sim.screenShake = 6;

    const dmg = (state.damage || 100) * 10;
    // Floating damage text on boss
    sim.damageTexts.push({
      x: sim.bossX + (Math.random() * 40 - 20),
      y: 110,
      text: `-${formatNumber(dmg)}`,
      color: '#F87171',
      size: 16,
      life: 35
    });

    // Blood / Spark particles
    for (let i = 0; i < 8; i++) {
      sim.particles.push({
        x: sim.bossX,
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

  // Hero Ultimate Skill Burst
  const handleHeroUltimate = () => {
    if (skillCooldown > 0) return;
    sound.playUpgrade();
    setSkillCooldown(8);

    const sim = simRef.current;
    sim.heroAnim = 'skill';
    sim.heroSlashTimer = 25;
    sim.bossHitTimer = 22;
    sim.screenShake = 14;

    const ultDmg = (state.damage || 100) * 35;
    sim.damageTexts.push({
      x: sim.bossX,
      y: 90,
      text: `CRIT! -${formatNumber(ultDmg)} 💥`,
      color: '#FACC15',
      size: 20,
      life: 45
    });

    // Lightning beams
    for (let b = 0; b < 6; b++) {
      sim.magicBeams.push({
        x1: 150,
        y1: 130,
        x2: sim.bossX + (Math.random() * 40 - 20),
        y2: 140 + (Math.random() * 40 - 20),
        life: 18,
        color: '#67E8F9'
      });
    }

    confetti({ particleCount: 70, spread: 80 });
    if (onAttackBoss) onAttackBoss();
  };

  // Drink Healing Potion
  const handleUsePotion = () => {
    sound.playUpgrade();
    setPlayerHp((prev) => Math.min(playerMaxHp, prev + Math.floor(playerMaxHp * 0.5)));
    simRef.current.damageTexts.push({
      x: 130,
      y: 110,
      text: `+${formatNumber(Math.floor(playerMaxHp * 0.5))} HP 💚`,
      color: '#4ADE80',
      size: 16,
      life: 40
    });
  };

  // Cooldown countdown
  useEffect(() => {
    if (skillCooldown <= 0) return;
    const timer = setInterval(() => {
      setSkillCooldown((c) => Math.max(0, c - 1));
    }, 1000);
    return () => clearInterval(timer);
  }, [skillCooldown]);

  // Periodic Archer Troop Proyektil (Support Arrows every 1.2s)
  useEffect(() => {
    const arrowTimer = setInterval(() => {
      const sim = simRef.current;
      sim.arrows.push({
        x: 95,
        y: 145,
        vx: 9,
        vy: -2,
        gravity: 0.12,
        life: 50
      });
    }, 1200);
    return () => clearInterval(arrowTimer);
  }, []);

  // 60 FPS Arena Canvas Rendering Loop with Full PVE AI & Enemy Counterattacks!
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

      // ======================================================================
      // 0. ENEMY BOSS AI & COUNTER-ATTACK BEHAVIOR
      // ======================================================================
      sim.bossAttackTimer--;
      if (sim.bossAttackTimer === 30) {
        // Telegraph phase: boss prepares to strike (eyes flash)
        sim.bossAnim = 'telegraph';
      } else if (sim.bossAttackTimer <= 15 && sim.bossAttackTimer > 0) {
        // Lunge attack: boss leaps forward toward player party!
        sim.bossAnim = 'lunge';
        sim.bossX = sim.bossBaseX - (15 - sim.bossAttackTimer) * 14;
      } else if (sim.bossAttackTimer === 0) {
        // Strike moment: Deal damage to player party!
        sim.bossAnim = 'strike';
        sim.heroHurtTimer = 16;
        sim.screenShake = 10;
        sound.playAttack();

        // Claw / Strike visual arc at player position
        sim.claws.push({ x: 130, y: 140, life: 14 });

        // Deal damage to player
        const bossDmg = Math.max(45, Math.floor(currentBoss.hp * 0.04));
        setPlayerHp((prev) => {
          const next = prev - bossDmg;
          if (next <= 0) {
            // Player rally / revival
            return Math.floor(playerMaxHp * 0.4);
          }
          return next;
        });

        // Floating player damage text
        sim.damageTexts.push({
          x: 130 + (Math.random() * 30 - 15),
          y: 100,
          text: `-${bossDmg}`,
          color: '#EF4444',
          size: 16,
          life: 40
        });

        // Reset timer
        sim.bossAttackTimer = 140 + Math.floor(Math.random() * 40); // 2.5s - 3s
      } else if (sim.bossAttackTimer > 30) {
        // Return smoothly to base
        sim.bossAnim = 'idle';
        sim.bossX += (sim.bossBaseX - sim.bossX) * 0.15;
      }

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
      // 1. DUNGEON ENVIRONMENT (Dungeon Brick Wall, Pillars & Animated Torches)
      // ======================================================================
      const caveGrad = ctx.createLinearGradient(0, 0, 0, h);
      caveGrad.addColorStop(0, '#090A14');
      caveGrad.addColorStop(0.65, '#12162A');
      caveGrad.addColorStop(1, '#05070D');
      ctx.fillStyle = caveGrad;
      ctx.fillRect(0, 0, w, h);

      // Stone Brick Pillars
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
      // 2. HERO PARTY (Commander + Supporting Troops)
      // ======================================================================
      const isSlashing = sim.heroSlashTimer > 0;
      if (sim.heroSlashTimer > 0) sim.heroSlashTimer--;

      const isHurt = sim.heroHurtTimer > 0;
      if (sim.heroHurtTimer > 0) sim.heroHurtTimer--;

      const heroBaseX = isSlashing ? sim.heroX + 25 : (isHurt ? sim.heroX - 10 : sim.heroX);
      const heroBob = Math.sin(sim.tick * 0.2) * 2;
      const heroColorFlash = isHurt ? '#EF4444' : null;

      // Accompanying Spearman in back
      const spX = heroBaseX - 55;
      ctx.fillStyle = 'rgba(0,0,0,0.35)';
      ctx.fillRect(spX - 6, groundY - 2, 16, 4);
      ctx.fillStyle = heroColorFlash || '#EF4444'; // Red soldier tunic
      ctx.fillRect(spX - 5, groundY - 26 + heroBob, 12, 14);
      ctx.fillStyle = '#FED7AA'; // Head
      ctx.fillRect(spX - 3, groundY - 34 + heroBob, 8, 8);
      ctx.fillStyle = '#475569'; // Helmet
      ctx.fillRect(spX - 4, groundY - 36 + heroBob, 10, 4);
      ctx.fillStyle = '#CBD5E1'; // Spear
      ctx.fillRect(spX + 8, groundY - 42 + heroBob, 2, 28);

      // Accompanying Archer in back
      const arX = heroBaseX - 32;
      ctx.fillStyle = 'rgba(0,0,0,0.35)';
      ctx.fillRect(arX - 6, groundY - 2, 16, 4);
      ctx.fillStyle = heroColorFlash || '#10B981'; // Green archer tunic
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
      ctx.fillStyle = heroColorFlash || primaryHero.color || '#3B82F6';
      ctx.fillRect(heroBaseX - 8, groundY - 34 + heroBob, 18, 20);

      // Royal Cloak
      ctx.fillStyle = '#DC2626';
      ctx.fillRect(heroBaseX - 12, groundY - 32 + heroBob, 5, 16);

      // Hero Head & Crown
      ctx.fillStyle = '#FED7AA';
      ctx.fillRect(heroBaseX - 5, groundY - 44 + heroBob, 12, 11);
      ctx.fillStyle = '#FACC15'; // Crown
      ctx.fillRect(heroBaseX - 7, groundY - 48 + heroBob, 16, 5);
      ctx.fillRect(heroBaseX + 1, groundY - 51 + heroBob, 4, 4);

      // Eyes
      ctx.fillStyle = '#0F172A';
      ctx.fillRect(heroBaseX + 2, groundY - 40 + heroBob, 3, 3);

      // Hero Sword & Golden Slash Arc
      if (isSlashing) {
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

        ctx.fillStyle = '#F8FAFC';
        ctx.fillRect(heroBaseX + 12, groundY - 36, 26, 4);
      } else {
        ctx.fillStyle = '#CBD5E1';
        ctx.fillRect(heroBaseX + 10, groundY - 38 + heroBob, 4, 22);
        ctx.fillStyle = '#FACC15';
        ctx.fillRect(heroBaseX + 8, groundY - 24 + heroBob, 8, 3);
      }

      // ======================================================================
      // 3. PIXEL ART DUNGEON MONSTER (Rich Detail & Lunge Animations)
      // ======================================================================
      const bX = sim.bossX;
      const isBossHit = sim.bossHitTimer > 0;
      if (sim.bossHitTimer > 0) sim.bossHitTimer--;

      const bossBob = Math.sin(sim.tick * 0.16) * 3;
      const flashColor = isBossHit ? '#FFFFFF' : null;
      const isTelegraph = sim.bossAnim === 'telegraph';

      // Boss Ground Shadow
      ctx.fillStyle = 'rgba(0,0,0,0.5)';
      ctx.beginPath();
      ctx.ellipse(bX, groundY - 2, 34, 8, 0, 0, Math.PI * 2);
      ctx.fill();

      // Render Specific Monster Sprite by Level
      if (currentBoss.level <= 1) {
        // Savage Dire Wolf
        ctx.fillStyle = flashColor || '#475569';
        ctx.fillRect(bX - 22, groundY - 28 + bossBob, 38, 18);
        ctx.fillRect(bX - 30, groundY - 38 + bossBob, 18, 18); // Head
        // Ears & Snout
        ctx.fillRect(bX - 28, groundY - 44 + bossBob, 5, 7);
        ctx.fillRect(bX - 36, groundY - 28 + bossBob, 10, 8);
        // Glowing Red Eyes
        ctx.fillStyle = isTelegraph ? '#FDE047' : '#EF4444';
        ctx.fillRect(bX - 26, groundY - 34 + bossBob, 4, 3);
        // Sharp White Fangs
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
        ctx.fillStyle = isTelegraph ? '#FFFFFF' : '#FDE047';
        ctx.fillRect(bX - 36, groundY - 46 + bossBob, 5, 4);
      } else if (currentBoss.level <= 10) {
        // Goblin King with Crown & Spiked Club
        ctx.fillStyle = flashColor || '#15803D';
        ctx.fillRect(bX - 20, groundY - 44 + bossBob, 36, 32);
        ctx.fillRect(bX - 16, groundY - 58 + bossBob, 28, 20); // Head
        // Jagged Crown
        ctx.fillStyle = '#FACC15';
        ctx.fillRect(bX - 16, groundY - 64 + bossBob, 28, 6);
        // Spiked Club in Hand
        ctx.fillStyle = '#78350F';
        ctx.fillRect(bX - 36, groundY - 52 + bossBob, 10, 36);
        ctx.fillStyle = '#94A3B8';
        ctx.fillRect(bX - 40, groundY - 56 + bossBob, 18, 12);
        // Red Eyes
        ctx.fillStyle = isTelegraph ? '#FFFFFF' : '#EF4444';
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
        // Heads
        ctx.fillRect(bX - 30, groundY - 82 + bossBob, 24, 22);
        ctx.fillRect(bX + 4, groundY - 82 + bossBob, 24, 22);
        ctx.fillStyle = isTelegraph ? '#FFFFFF' : '#FDE047';
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
        // Flaming Wings
        ctx.fillStyle = '#7F1D1D';
        ctx.beginPath();
        ctx.moveTo(bX + 28, groundY - 60 + bossBob);
        ctx.lineTo(bX + 70, groundY - 90 + bossBob);
        ctx.lineTo(bX + 50, groundY - 30 + bossBob);
        ctx.closePath();
        ctx.fill();
        // Glowing Flaming Core
        ctx.fillStyle = isTelegraph ? '#FFFFFF' : '#F59E0B';
        ctx.beginPath();
        ctx.arc(bX - 2, groundY - 48 + bossBob, 12, 0, Math.PI * 2);
        ctx.fill();
      }

      // ======================================================================
      // 4. CLAW SLASH EFFECTS & ARCHER ARROWS
      // ======================================================================
      // Claws on player
      for (let i = sim.claws.length - 1; i >= 0; i--) {
        const c = sim.claws[i];
        ctx.strokeStyle = '#EF4444';
        ctx.lineWidth = 3;
        ctx.beginPath();
        ctx.moveTo(c.x - 14, c.y - 18);
        ctx.lineTo(c.x + 14, c.y + 18);
        ctx.moveTo(c.x - 6, c.y - 22);
        ctx.lineTo(c.x + 22, c.y + 14);
        ctx.stroke();
        c.life--;
        if (c.life <= 0) sim.claws.splice(i, 1);
      }

      // Archer Arrows
      for (let i = sim.arrows.length - 1; i >= 0; i--) {
        const ar = sim.arrows[i];
        ar.x += ar.vx;
        ar.y += ar.vy;
        ar.vy += ar.gravity;
        ctx.fillStyle = '#FDE047';
        ctx.fillRect(ar.x, ar.y, 10, 2);
        ar.life--;
        if (ar.x >= sim.bossX || ar.life <= 0) {
          // Arrow impact
          sim.bossHitTimer = 4;
          sim.arrows.splice(i, 1);
        }
      }

      // Magic Beams
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

      // Particles
      for (let i = sim.particles.length - 1; i >= 0; i--) {
        const p = sim.particles[i];
        p.x += p.vx;
        p.y += p.vy;
        p.life--;
        ctx.fillStyle = p.color;
        ctx.fillRect(p.x, p.y, p.size, p.size);
        if (p.life <= 0) sim.particles.splice(i, 1);
      }

      // Floating Damage Numbers
      for (let i = sim.damageTexts.length - 1; i >= 0; i--) {
        const dt = sim.damageTexts[i];
        dt.y -= 1.2;
        dt.life--;
        ctx.fillStyle = dt.color;
        ctx.font = `bold ${dt.size}px "Press Start 2P", monospace`;
        ctx.fillText(dt.text, dt.x, dt.y);
        if (dt.life <= 0) sim.damageTexts.splice(i, 1);
      }

      ctx.restore();
      animId = requestAnimationFrame(render);
    };

    render();
    return () => cancelAnimationFrame(animId);
  }, [currentBoss, primaryHero, playerMaxHp]);

  // HP Percentages
  const bossHpPercent = Math.max(0, Math.min(100, (state.monsterHp / (currentBoss.hp || 1)) * 100));
  const playerHpPercent = Math.max(0, Math.min(100, (playerHp / playerMaxHp) * 100));

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
      {/* Dual Health Bars HUD (Player Party vs Dungeon Boss) */}
      <div style={{
        backgroundColor: 'rgba(15, 23, 42, 0.9)',
        borderRadius: '12px',
        border: '1.5px solid rgba(255, 255, 255, 0.1)',
        padding: '12px 14px',
        display: 'flex',
        flexDirection: 'column',
        gap: '8px'
      }}>
        {/* Boss HP Bar */}
        <div>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '4px' }}>
            <span style={{ fontSize: '13px', fontWeight: '900', color: '#F87171', display: 'flex', alignItems: 'center', gap: '6px' }}>
              <span>👾</span> Lv.{currentBoss.level} {currentBoss.name}
            </span>
            <span style={{ fontSize: '11px', color: '#CBD5E1', fontFamily: '"Press Start 2P", monospace' }}>
              {formatNumber(state.monsterHp)} / {formatNumber(currentBoss.hp)}
            </span>
          </div>
          <div style={{ width: '100%', height: '10px', backgroundColor: '#1E293B', borderRadius: '5px', overflow: 'hidden', border: '1px solid rgba(255, 255, 255, 0.1)' }}>
            <div style={{ width: `${bossHpPercent}%`, height: '100%', backgroundColor: '#EF4444', transition: 'width 0.15s ease' }} />
          </div>
        </div>

        {/* Player Party HP Bar */}
        <div>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '4px' }}>
            <span style={{ fontSize: '12px', fontWeight: '800', color: '#4ADE80', display: 'flex', alignItems: 'center', gap: '6px' }}>
              <span>🛡️</span> Tim Pahlawan & Garnison ({state.garrisonedPasukan} Prajurit)
            </span>
            <span style={{ fontSize: '10px', color: '#86EFAC', fontFamily: '"Press Start 2P", monospace' }}>
              {formatNumber(playerHp)} / {formatNumber(playerMaxHp)} HP
            </span>
          </div>
          <div style={{ width: '100%', height: '8px', backgroundColor: '#1E293B', borderRadius: '4px', overflow: 'hidden', border: '1px solid rgba(255, 255, 255, 0.1)' }}>
            <div style={{ width: `${playerHpPercent}%`, height: '100%', backgroundColor: '#22C55E', transition: 'width 0.2s ease' }} />
          </div>
        </div>
      </div>

      {/* 60 FPS Canvas Battle Arena */}
      <div style={{
        position: 'relative',
        borderRadius: '14px',
        overflow: 'hidden',
        border: '2px solid rgba(255, 255, 255, 0.15)',
        boxShadow: '0 8px 30px rgba(0, 0, 0, 0.6)'
      }}>
        <canvas
          ref={canvasRef}
          width={640}
          height={260}
          style={{ width: '100%', height: 'auto', display: 'block' }}
        />
      </div>

      {/* Battle Controls: Attack, Ultimate Skill, Potion, and Auto-Farm */}
      <div style={{ display: 'flex', gap: '8px' }}>
        <button
          onClick={handlePlayerAttack}
          className="btn-pixel gold"
          style={{ flex: 1.5, padding: '12px 10px', fontSize: '12px', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '6px' }}
        >
          <span>⚔️</span> Serang Cepat
        </button>

        <button
          onClick={handleHeroUltimate}
          disabled={skillCooldown > 0}
          className={`btn-pixel ${skillCooldown === 0 ? 'cyan' : ''}`}
          style={{ flex: 1.5, padding: '12px 10px', fontSize: '12px', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '6px' }}
        >
          <span>⚡</span> Skill Petir {skillCooldown > 0 ? `(${skillCooldown}s)` : 'SIAP'}
        </button>

        <button
          onClick={handleUsePotion}
          className="btn-pixel"
          style={{ flex: 1, padding: '12px 6px', fontSize: '11px', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '4px', backgroundColor: 'rgba(5, 150, 105, 0.4)', border: '1.5px solid #10B981' }}
          title="Minum Ramuan HP untuk memulihkan darah"
        >
          <span>🧪</span> +HP
        </button>

        <button
          onClick={onToggleAutoFarm}
          className={`btn-pixel ${state.autoFarmDungeon ? 'purple' : ''}`}
          style={{ flex: 1.2, padding: '12px 6px', fontSize: '11px', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '4px' }}
        >
          <span>🤖</span> Auto: {state.autoFarmDungeon ? 'ON' : 'OFF'}
        </button>
      </div>
    </div>
  );
}
