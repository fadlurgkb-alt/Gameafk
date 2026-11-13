import React, { useRef, useEffect, useState } from 'react';
import confetti from 'canvas-confetti';
import { DUNGEON_MONSTERS, formatNumber } from '../gameState';

export default function DungeonArena({ state, onAttackBoss, onToggleAutoFarm }) {
  const canvasRef = useRef(null);
  const [skillCooldown, setSkillCooldown] = useState(0);

  const currentBoss = DUNGEON_MONSTERS.find((m) => m.level === state.currentDungeonLevel)
    || DUNGEON_MONSTERS[0];

  const deployedHeroes = state.commanders.filter((c) => c.owned).slice(0, 3);

  const simRef = useRef({
    tick: 0,
    heroX: 140,
    heroAnim: 'idle', // 'idle' | 'attack' | 'skill'
    heroSkillTimer: 0,
    bossX: 460,
    bossHitTimer: 0,
    damageTexts: [],
    skillEffects: []
  });

  // Main 60 FPS Battle Arena Loop
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

      // 1. Dungeon Cave Background
      const caveGrad = ctx.createLinearGradient(0, 0, 0, h);
      caveGrad.addColorStop(0, '#0B0D19');
      caveGrad.addColorStop(0.7, '#16192E');
      caveGrad.addColorStop(1, '#080A12');
      ctx.fillStyle = caveGrad;
      ctx.fillRect(0, 0, w, h);

      // Arena Ground Platform
      const groundY = h * 0.72;
      ctx.fillStyle = '#1E2538';
      ctx.fillRect(0, groundY, w, h - groundY);
      ctx.fillStyle = '#334155';
      ctx.fillRect(0, groundY, w, 4);

      // Torch Lights on Background Wall
      const torchX1 = 120;
      const torchX2 = w - 120;
      [torchX1, torchX2].forEach((tx) => {
        ctx.fillStyle = '#78350F';
        ctx.fillRect(tx - 3, groundY - 50, 6, 20);
        const tF = Math.sin(sim.tick * 0.3 + tx) * 3;
        ctx.fillStyle = '#EA580C';
        ctx.fillRect(tx - 4, groundY - 56 - tF, 8, 8 + tF);
        ctx.fillStyle = '#FEF08A';
        ctx.fillRect(tx - 2, groundY - 54 - tF * 0.7, 4, 4);
      });

      // 2. Draw Deployed Chibi Heroes
      deployedHeroes.forEach((hero, index) => {
        const hX = sim.heroX - (index * 35);
        const stepBob = Math.sin(sim.tick * 0.25 + index) * 2;
        const isAttacking = sim.heroAnim === 'attack';
        const attackOffset = isAttacking ? 15 : 0;

        // Shadow
        ctx.fillStyle = 'rgba(0, 0, 0, 0.4)';
        ctx.beginPath();
        ctx.ellipse(hX + attackOffset, groundY - 2, 12, 4, 0, 0, Math.PI * 2);
        ctx.fill();

        // Chibi Body
        ctx.fillStyle = hero.color || '#3B82F6';
        ctx.fillRect(hX - 8 + attackOffset, groundY - 24 + stepBob, 16, 16);

        // Chibi Head
        ctx.fillStyle = '#FED7AA';
        ctx.fillRect(hX - 6 + attackOffset, groundY - 34 + stepBob, 12, 10);

        // Eyes
        ctx.fillStyle = '#0F172A';
        ctx.fillRect(hX + 1 + attackOffset, groundY - 30 + stepBob, 2, 3);

        // Weapon
        ctx.fillStyle = '#F8FAFC';
        ctx.fillRect(hX + 9 + attackOffset, groundY - 32 + stepBob, 3, 16);
      });

      // 3. Draw Dungeon Boss Monster
      const bX = sim.bossX;
      const bossBob = Math.sin(sim.tick * 0.15) * 3;
      const isBossHit = sim.bossHitTimer > 0;
      if (sim.bossHitTimer > 0) sim.bossHitTimer--;

      // Boss Shadow
      ctx.fillStyle = 'rgba(0, 0, 0, 0.45)';
      ctx.beginPath();
      ctx.ellipse(bX, groundY - 2, currentBoss.isBoss ? 28 : 20, 6, 0, 0, Math.PI * 2);
      ctx.fill();

      // Boss Body (Flash red when hit)
      ctx.fillStyle = isBossHit ? '#FFFFFF' : (currentBoss.color || '#DC2626');
      const bSize = currentBoss.isBoss ? 48 : 34;
      ctx.fillRect(bX - bSize / 2, groundY - bSize + bossBob, bSize, bSize * 0.85);

      // Boss Eyes & Horns
      ctx.fillStyle = '#FDE047';
      ctx.fillRect(bX - 12, groundY - bSize * 0.75 + bossBob, 5, 5);
      ctx.fillRect(bX - 2, groundY - bSize * 0.75 + bossBob, 5, 5);

      if (currentBoss.isBoss) {
        // Horns
        ctx.fillStyle = '#1E293B';
        ctx.fillRect(bX - bSize / 2, groundY - bSize - 8 + bossBob, 6, 10);
        ctx.fillRect(bX + bSize / 2 - 6, groundY - bSize - 8 + bossBob, 6, 10);
      }

      // 4. Draw Active Skill Effects (Lightning / Beam)
      if (sim.heroSkillTimer > 0) {
        sim.heroSkillTimer--;
        // Zeus Lightning Bolt / Holy Beam Strike
        ctx.strokeStyle = '#FDE047';
        ctx.lineWidth = 6;
        ctx.shadowColor = '#FDE047';
        ctx.shadowBlur = 18;

        ctx.beginPath();
        ctx.moveTo(bX + (Math.random() * 20 - 10), 0);
        ctx.lineTo(bX - 5, groundY - bSize / 2);
        ctx.stroke();

        ctx.strokeStyle = '#00E5FF';
        ctx.lineWidth = 4;
        ctx.stroke();
        ctx.shadowBlur = 0;

        // Shockwave impact
        ctx.fillStyle = 'rgba(255, 255, 255, 0.8)';
        ctx.beginPath();
        ctx.arc(bX, groundY - bSize / 2, 28, 0, Math.PI * 2);
        ctx.fill();
      }

      // 5. Update & Draw Floating Damage Popups
      for (let i = sim.damageTexts.length - 1; i >= 0; i--) {
        const dt = sim.damageTexts[i];
        dt.y -= 1.4;
        dt.alpha -= 0.025;

        if (dt.alpha <= 0) {
          sim.damageTexts.splice(i, 1);
          continue;
        }

        ctx.font = 'bold 15px "Press Start 2P", monospace';
        ctx.fillStyle = `rgba(239, 68, 68, ${dt.alpha})`;
        ctx.shadowColor = 'rgba(0, 0, 0, 0.9)';
        ctx.shadowBlur = 4;
        ctx.fillText(dt.text, dt.x, dt.y);
        ctx.shadowBlur = 0;
      }

      animId = requestAnimationFrame(render);
    };

    render();
    return () => cancelAnimationFrame(animId);
  }, [currentBoss, deployedHeroes]);

  // Attack Trigger
  const triggerAttack = () => {
    simRef.current.heroAnim = 'attack';
    simRef.current.bossHitTimer = 12;

    const dmg = state.damage * 10;
    simRef.current.damageTexts.push({
      x: simRef.current.bossX - 10 + (Math.random() * 20 - 10),
      y: 180,
      alpha: 1.0,
      text: `-${formatNumber(dmg)}`
    });

    setTimeout(() => {
      simRef.current.heroAnim = 'idle';
    }, 250);

    if (onAttackBoss) onAttackBoss();
  };

  // Ultimate Skill Trigger
  const triggerSkill = () => {
    if (skillCooldown > 0) return;
    setSkillCooldown(10); // 10s cooldown
    simRef.current.heroSkillTimer = 18;
    simRef.current.bossHitTimer = 25;

    const skillDmg = state.damage * 45;
    simRef.current.damageTexts.push({
      x: simRef.current.bossX,
      y: 160,
      alpha: 1.0,
      text: `💥 -${formatNumber(skillDmg)}`
    });

    confetti({ particleCount: 60, spread: 80, origin: { y: 0.6 } });
    if (onAttackBoss) onAttackBoss();
  };

  // Cooldown countdown
  useEffect(() => {
    if (skillCooldown <= 0) return;
    const t = setInterval(() => {
      setSkillCooldown((c) => Math.max(0, c - 1));
    }, 1000);
    return () => clearInterval(t);
  }, [skillCooldown]);

  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      gap: '12px',
      backgroundColor: '#070A14',
      borderRadius: '16px',
      padding: '14px',
      border: '1.5px solid rgba(255, 255, 255, 0.1)'
    }}>
      {/* Boss Info Header */}
      <div style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <span style={{ fontSize: '24px' }}>{currentBoss.icon}</span>
          <div>
            <h4 style={{ fontSize: '14px', fontWeight: '900', color: '#F8FAFC', margin: 0 }}>
              {currentBoss.name} (Lantai {state.currentDungeonLevel})
            </h4>
            <span style={{ fontSize: '10px', color: currentBoss.isBoss ? '#EF4444' : '#38BDF8', fontWeight: '800' }}>
              {currentBoss.isBoss ? '👑 ELITE DUNGEON BOS' : 'Monster Liar'}
            </span>
          </div>
        </div>

        <div style={{
          backgroundColor: 'rgba(30, 41, 59, 0.8)',
          padding: '4px 10px',
          borderRadius: '8px',
          fontSize: '11px',
          color: '#FCD34D',
          fontWeight: '800'
        }}>
          🪙 {formatNumber(state.dungeonCoins)} Koin
        </div>
      </div>

      {/* Monster Health Bar */}
      <div>
        <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '11px', marginBottom: '4px' }}>
          <span style={{ color: '#94A3B8' }}>HP Monster Dungeon:</span>
          <strong style={{ color: '#F87171' }}>
            {formatNumber(state.monsterHp)} / {formatNumber(state.monsterMaxHp)}
          </strong>
        </div>
        <div style={{
          height: '10px',
          backgroundColor: 'rgba(15, 23, 42, 0.8)',
          borderRadius: '6px',
          overflow: 'hidden'
        }}>
          <div style={{
            height: '100%',
            width: `${Math.max(0, Math.min(100, (state.monsterHp / state.monsterMaxHp) * 100))}%`,
            backgroundColor: '#EF4444',
            transition: 'width 0.2s ease'
          }} />
        </div>
      </div>

      {/* 60 FPS Pixel Arena Canvas */}
      <div style={{
        position: 'relative',
        width: '100%',
        height: '240px',
        borderRadius: '12px',
        overflow: 'hidden',
        border: '1.5px solid rgba(255, 255, 255, 0.08)'
      }}>
        <canvas
          ref={canvasRef}
          width={600}
          height={300}
          style={{ width: '100%', height: '100%', display: 'block', imageRendering: 'pixelated' }}
        />
      </div>

      {/* Battle Combat Controls */}
      <div style={{ display: 'flex', gap: '8px' }}>
        <button
          onClick={triggerAttack}
          className="btn-pixel gold"
          style={{ flex: 1, padding: '10px', fontSize: '13px' }}
        >
          ⚔️ Serbu & Tebas
        </button>

        <button
          onClick={triggerSkill}
          disabled={skillCooldown > 0}
          className="btn-pixel cyan"
          style={{ padding: '10px 14px', fontSize: '12px' }}
        >
          {skillCooldown > 0 ? `⏳ Skill (${skillCooldown}s)` : '⚡ SKILL KOSMIS'}
        </button>

        <button
          onClick={onToggleAutoFarm}
          className={`btn-pixel ${state.autoFarmDungeon ? 'green' : ''}`}
          style={{ padding: '10px 12px', fontSize: '11px' }}
        >
          {state.autoFarmDungeon ? '🤖 Auto: ON' : 'Auto: OFF'}
        </button>
      </div>
    </div>
  );
}
