import React, { useState, useEffect, useRef } from 'react';
import confetti from 'canvas-confetti';
import { COMMANDERS_DATABASE } from '../gameState';
import CommanderVisual from './CommanderVisual';

export default function GachaModal({ isOpen, onClose, gachaTickets, onPerformGacha }) {
  if (!isOpen) return null;

  const [animPhase, setAnimPhase] = useState('idle'); // 'idle' | 'sword_rain' | 'reveal'
  const [pulledHero, setPulledHero] = useState(null);
  const canvasRef = useRef(null);

  const startSummon = () => {
    if (gachaTickets <= 0) return;

    // Determine hero pull based on rarity weights:
    // Mythical: 3%, SSR: 12%, SR: 35%, R: 50%
    const rand = Math.random() * 100;
    let pool;
    if (rand < 3) {
      pool = COMMANDERS_DATABASE.filter((c) => c.rarity === 'Mythical');
    } else if (rand < 15) {
      pool = COMMANDERS_DATABASE.filter((c) => c.rarity === 'SSR');
    } else if (rand < 50) {
      pool = COMMANDERS_DATABASE.filter((c) => c.rarity === 'SR');
    } else {
      pool = COMMANDERS_DATABASE.filter((c) => c.rarity === 'R');
    }

    const hero = pool[Math.floor(Math.random() * pool.length)] || COMMANDERS_DATABASE[0];
    setPulledHero(hero);
    setAnimPhase('sword_rain');
  };

  // Sword Rain Animation Canvas Loop
  useEffect(() => {
    if (animPhase !== 'sword_rain' || !pulledHero) return;
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');

    const swords = [];
    const swordColor = pulledHero.color || '#F59E0B';

    // Generate 35 falling glowing swords
    for (let i = 0; i < 35; i++) {
      swords.push({
        x: Math.random() * canvas.width,
        y: -100 - Math.random() * 400,
        speed: 8 + Math.random() * 10,
        length: 25 + Math.random() * 20,
        alpha: 0.8 + Math.random() * 0.2
      });
    }

    let animId;
    let timer = 0;

    const render = () => {
      timer++;
      ctx.clearRect(0, 0, canvas.width, canvas.height);

      // Dark thunderous storm
      ctx.fillStyle = 'rgba(7, 10, 18, 0.4)';
      ctx.fillRect(0, 0, canvas.width, canvas.height);

      // Draw Sword Rain
      swords.forEach((s) => {
        s.y += s.speed;

        // Glowing sword trail
        ctx.beginPath();
        ctx.moveTo(s.x, s.y);
        ctx.lineTo(s.x, s.y - s.length);
        ctx.strokeStyle = swordColor;
        ctx.lineWidth = 3;
        ctx.shadowColor = swordColor;
        ctx.shadowBlur = 12;
        ctx.stroke();
        ctx.shadowBlur = 0;

        // Sword Crossguard & Hilt
        ctx.fillStyle = '#FFFFFF';
        ctx.fillRect(s.x - 4, s.y - s.length, 8, 3);
      });

      if (timer < 80) {
        animId = requestAnimationFrame(render);
      } else {
        // Transition to Hero Card Reveal
        confetti({
          particleCount: 100,
          spread: 120,
          origin: { y: 0.5 },
          colors: [swordColor, '#FFD54F', '#FFFFFF']
        });
        if (onPerformGacha) onPerformGacha(pulledHero);
        setAnimPhase('reveal');
      }
    };

    render();
    return () => cancelAnimationFrame(animId);
  }, [animPhase, pulledHero]);

  return (
    <div className="modal-backdrop">
      <div className="modal-box" style={{ maxWidth: '480px', overflow: 'hidden' }}>
        {/* Header */}
        <div className="modal-header">
          <h3><span>🍻</span> Kedai Pahlawan (Tavern Gacha)</h3>
          <button onClick={onClose} className="modal-close-btn">✕</button>
        </div>

        {/* Content Area */}
        <div style={{
          position: 'relative',
          minHeight: '360px',
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          padding: '20px',
          backgroundColor: '#070A14'
        }}>
          {animPhase === 'idle' && (
            <div style={{ textAlign: 'center', display: 'flex', flexDirection: 'column', gap: '14px', alignItems: 'center' }}>
              <div style={{
                width: '80px',
                height: '80px',
                borderRadius: '50%',
                backgroundColor: 'rgba(255, 179, 0, 0.15)',
                border: '2px solid #FFB300',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontSize: '40px'
              }}>
                ⚔️
              </div>
              <div>
                <h4 style={{ fontSize: '18px', color: '#F8FAFC', margin: 0 }}>
                  Panggil Jiwa Pahlawan Legendaris
                </h4>
                <p style={{ fontSize: '12px', color: '#94A3B8', marginTop: '6px', maxWidth: '300px' }}>
                  Hujan pedang kosmis akan turun dari langit, memanggil komandan berkekuatan dahsyat untuk menjaga peradaban Anda!
                </p>
              </div>

              <div style={{
                backgroundColor: 'rgba(30, 41, 59, 0.8)',
                padding: '8px 16px',
                borderRadius: '20px',
                border: '1px solid rgba(255, 255, 255, 0.1)',
                fontSize: '13px',
                color: '#38BDF8',
                fontWeight: '800'
              }}>
                🔮 Sisa Tiket: {gachaTickets} Tiket
              </div>

              <button
                onClick={startSummon}
                disabled={gachaTickets <= 0}
                className="btn-pixel gold"
                style={{ padding: '12px 28px', fontSize: '14px', marginTop: '10px' }}
              >
                {gachaTickets > 0 ? '⚡ Tarik Pahlawan (Hujan Pedang)' : '❌ Tiket Habis'}
              </button>
            </div>
          )}

          {animPhase === 'sword_rain' && (
            <div style={{ position: 'relative', width: '100%', height: '360px' }}>
              <canvas
                ref={canvasRef}
                width={440}
                height={360}
                style={{ width: '100%', height: '100%', display: 'block' }}
              />
              <div style={{
                position: 'absolute',
                top: '50%',
                left: '50%',
                transform: 'translate(-50%, -50%)',
                color: pulledHero?.color || '#FFD54F',
                fontFamily: '"Press Start 2P", monospace',
                fontSize: '16px',
                textShadow: '0 0 20px rgba(255,255,255,0.8)',
                textAlign: 'center'
              }}>
                ⚔️ HUJAN PEDANG KOSMIS ⚔️
              </div>
            </div>
          )}

          {animPhase === 'reveal' && pulledHero && (
            <div style={{
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              gap: '12px',
              animation: 'fadeIn 0.5s ease-out'
            }}>
              {/* Card Reveal */}
              <div style={{
                width: '260px',
                backgroundColor: 'rgba(30, 41, 59, 0.95)',
                border: `3px solid ${pulledHero.color}`,
                borderRadius: '16px',
                padding: '20px',
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                boxShadow: `0 0 35px ${pulledHero.color}66`,
                textAlign: 'center'
              }}>
                <div style={{
                  backgroundColor: 'rgba(15, 23, 42, 0.9)',
                  width: '76px',
                  height: '76px',
                  borderRadius: '16px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  border: `2.5px solid ${pulledHero.color}`,
                  marginBottom: '10px',
                  overflow: 'hidden',
                  boxShadow: `0 0 15px ${pulledHero.color}88`
                }}>
                  <CommanderVisual id={pulledHero.id} size={68} />
                </div>

                <div style={{
                  backgroundColor: pulledHero.color,
                  color: '#000',
                  padding: '2px 10px',
                  borderRadius: '4px',
                  fontSize: '11px',
                  fontWeight: '900',
                  textTransform: 'uppercase',
                  marginBottom: '6px'
                }}>
                  {pulledHero.rarity}
                </div>

                <h4 style={{ fontSize: '16px', fontWeight: '900', color: '#F8FAFC', margin: 0 }}>
                  {pulledHero.name}
                </h4>
                <span style={{ fontSize: '11px', color: '#94A3B8' }}>{pulledHero.role}</span>

                <div style={{
                  marginTop: '12px',
                  padding: '8px',
                  backgroundColor: 'rgba(15, 23, 42, 0.6)',
                  borderRadius: '8px',
                  width: '100%',
                  fontSize: '11px',
                  color: '#4ADE80',
                  fontWeight: '700'
                }}>
                  ✨ Skill: {pulledHero.skill}
                </div>
                <div style={{ fontSize: '10px', color: '#CBD5E1', marginTop: '4px' }}>
                  {pulledHero.bonus}
                </div>
              </div>

              {/* Action */}
              <div style={{ display: 'flex', gap: '8px', marginTop: '10px' }}>
                <button
                  onClick={() => setAnimPhase('idle')}
                  className="btn-pixel gold"
                  style={{ padding: '8px 16px', fontSize: '12px' }}
                >
                  Tarik Lagi
                </button>
                <button
                  onClick={onClose}
                  className="btn-pixel"
                  style={{ padding: '8px 16px', fontSize: '12px' }}
                >
                  Selesai
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
