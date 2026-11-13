import React, { useRef, useEffect, useState } from 'react';
import confetti from 'canvas-confetti';
import { COSMIC_RESEARCH_TREE, formatNumber } from '../gameState';
import { sound } from '../utils/retroAudio';

// Branch Categorization for Clean Elegant View
const RESEARCH_CATEGORIES = [
  { id: 'all', label: '🌟 Semua Cabang', icon: '🌌' },
  { id: 'pangan', label: '🌾 Pangan & Ekonomi', icon: '🌾', nodes: ['memasak_makanan', 'budidaya', 'pengasapan_daging', 'domestikasi_hewan', 'seni_memasak', 'manufaktur_roti', 'pengalengan_makanan', 'kerja_shift'] },
  { id: 'militer', label: '🛡️ Militer & Tempur', icon: '⚔️', nodes: ['perkakas_batu', 'pertahanan_primitif', 'naluri_berburu', 'metalurgi_militer', 'overclock_tubuh'] },
  { id: 'sains', label: '🧠 Sains & Kognisi', icon: '💡', nodes: ['percikan_api', 'panggil_manusia', 'otak_kognitif', 'emosi_jiwa', 'moralitas_komunitas', 'penyelesaian_masalah', 'logistik_komputer', 'brain_link'] },
  { id: 'kosmik', label: '🌌 Kedaulatan & Kosmik', icon: '👑', nodes: ['spiritualitas_suku', 'kedaulatan_bangsa', 'fusi_partikel', 'singularity_ai'] }
];

export default function TalentTreeCanvas({ state, onUnlockResearch }) {
  const [activeCategory, setActiveCategory] = useState('all');
  const [selectedNode, setSelectedNode] = useState(COSMIC_RESEARCH_TREE[0]);
  const [viewMode, setViewMode] = useState('matrix'); // 'matrix' | 'constellation'
  const canvasRef = useRef(null);

  const unNodes = Array.isArray(state?.unlockedNodes) ? state.unlockedNodes : [];

  // Filter nodes based on category
  const filteredNodes = activeCategory === 'all'
    ? COSMIC_RESEARCH_TREE
    : COSMIC_RESEARCH_TREE.filter((n) => {
        const cat = RESEARCH_CATEGORIES.find((c) => c.id === activeCategory);
        return cat?.nodes?.includes(n.id);
      });

  const handleUnlock = (node) => {
    if (state.totalZenith < node.costZenith) return;
    sound.playUpgrade();
    confetti({ particleCount: 40, spread: 60 });
    if (onUnlockResearch) onUnlockResearch(node);
  };

  // Constellation Canvas Render Loop
  useEffect(() => {
    if (viewMode !== 'constellation') return;
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');

    let animId;
    let tick = 0;

    const render = () => {
      tick++;
      const w = canvas.width;
      const h = canvas.height;

      ctx.clearRect(0, 0, w, h);

      // Deep space gradient
      const bgGrad = ctx.createLinearGradient(0, 0, w, h);
      bgGrad.addColorStop(0, '#050713');
      bgGrad.addColorStop(0.5, '#0B0F24');
      bgGrad.addColorStop(1, '#03050B');
      ctx.fillStyle = bgGrad;
      ctx.fillRect(0, 0, w, h);

      // Star twinkle
      for (let i = 0; i < 35; i++) {
        const sx = ((i * 137 + tick * 0.1) % w);
        const sy = ((i * 91) % h);
        const alpha = 0.2 + Math.sin(tick * 0.05 + i) * 0.25;
        ctx.fillStyle = `rgba(255, 255, 255, ${alpha})`;
        ctx.fillRect(sx, sy, 2, 2);
      }

      // Connecting Constellation Lines
      COSMIC_RESEARCH_TREE.forEach((node) => {
        node.reqs.forEach((reqId) => {
          const parent = COSMIC_RESEARCH_TREE.find((n) => n.id === reqId);
          if (!parent) return;

          const isUnlocked = unNodes.includes(node.id);
          const isParentUnlocked = unNodes.includes(parent.id);

          ctx.beginPath();
          ctx.moveTo(parent.x * 0.75 + 40, parent.y * 0.65);
          ctx.lineTo(node.x * 0.75 + 40, node.y * 0.65);

          if (isUnlocked) {
            ctx.strokeStyle = '#38BDF8';
            ctx.lineWidth = 2.5;
            ctx.stroke();
          } else if (isParentUnlocked) {
            ctx.strokeStyle = '#F59E0B';
            ctx.lineWidth = 1.5;
            ctx.setLineDash([4, 4]);
            ctx.stroke();
            ctx.setLineDash([]);
          } else {
            ctx.strokeStyle = 'rgba(255, 255, 255, 0.08)';
            ctx.lineWidth = 1;
            ctx.stroke();
          }
        });
      });

      // Nodes
      COSMIC_RESEARCH_TREE.forEach((node) => {
        const nx = node.x * 0.75 + 40;
        const ny = node.y * 0.65;
        const isUnlocked = unNodes.includes(node.id);
        const isSelected = selectedNode?.id === node.id;

        // Shadow / Glow
        if (isUnlocked) {
          ctx.fillStyle = 'rgba(56, 189, 248, 0.25)';
          ctx.beginPath();
          ctx.arc(nx, ny, 16, 0, Math.PI * 2);
          ctx.fill();
        }

        ctx.fillStyle = isUnlocked ? '#0284C7' : '#1E293B';
        ctx.strokeStyle = isSelected ? '#FACC15' : isUnlocked ? '#38BDF8' : '#475569';
        ctx.lineWidth = isSelected ? 2.5 : 1.5;

        ctx.beginPath();
        ctx.arc(nx, ny, 11, 0, Math.PI * 2);
        ctx.fill();
        ctx.stroke();
      });

      animId = requestAnimationFrame(render);
    };

    animId = requestAnimationFrame(render);
    return () => cancelAnimationFrame(animId);
  }, [viewMode, selectedNode, unNodes]);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
      {/* Top Header Controls: Mode Switcher & Categories */}
      <div style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        flexWrap: 'wrap',
        gap: '10px'
      }}>
        {/* Category Pills */}
        <div style={{ display: 'flex', gap: '6px', overflowX: 'auto', paddingBottom: '2px' }}>
          {RESEARCH_CATEGORIES.map((cat) => {
            const isCatActive = activeCategory === cat.id;
            return (
              <button
                key={cat.id}
                onClick={() => {
                  setActiveCategory(cat.id);
                  sound.playClick();
                }}
                className={`btn-pixel ${isCatActive ? 'gold' : ''}`}
                style={{
                  fontSize: '11px',
                  padding: '6px 10px',
                  whiteSpace: 'nowrap'
                }}
              >
                {cat.label}
              </button>
            );
          })}
        </div>

        {/* View Toggle */}
        <div style={{ display: 'flex', gap: '6px' }}>
          <button
            onClick={() => { setViewMode('matrix'); sound.playClick(); }}
            className={`btn-pixel ${viewMode === 'matrix' ? 'cyan' : ''}`}
            style={{ fontSize: '11px', padding: '6px 12px' }}
          >
            📋 Matriks Riset
          </button>
          <button
            onClick={() => { setViewMode('constellation'); sound.playClick(); }}
            className={`btn-pixel ${viewMode === 'constellation' ? 'cyan' : ''}`}
            style={{ fontSize: '11px', padding: '6px 12px' }}
          >
            🌌 Peta Konstelasi
          </button>
        </div>
      </div>

      {/* VIEW 1: ELEGANT SCI-FI / RPG TALENT MATRIX */}
      {viewMode === 'matrix' && (
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))',
          gap: '12px'
        }}>
          {filteredNodes.map((node) => {
            const isUnlocked = unNodes.includes(node.id);
            const canAfford = state.totalZenith >= node.costZenith;
            const reqsMet = node.reqs.every((r) => unNodes.includes(r));
            const isAvailable = !isUnlocked && reqsMet;

            // Tier styling
            const tierBadgeColor = node.tier === 1 ? '#D97706'
              : node.tier === 2 ? '#EF4444'
              : node.tier === 3 ? '#0284C7'
              : '#8B5CF6';

            return (
              <div
                key={node.id}
                onClick={() => setSelectedNode(node)}
                style={{
                  backgroundColor: isUnlocked
                    ? 'rgba(15, 23, 42, 0.85)'
                    : isAvailable
                    ? 'rgba(30, 41, 59, 0.75)'
                    : 'rgba(15, 23, 42, 0.5)',
                  border: isUnlocked
                    ? '1.5px solid #38BDF8'
                    : isAvailable
                    ? '1.5px solid #F59E0B'
                    : '1px solid rgba(255, 255, 255, 0.06)',
                  borderRadius: '14px',
                  padding: '14px',
                  display: 'flex',
                  flexDirection: 'column',
                  gap: '10px',
                  boxShadow: isUnlocked
                    ? '0 0 15px rgba(56, 189, 248, 0.15)'
                    : isAvailable
                    ? '0 0 15px rgba(245, 158, 11, 0.12)'
                    : 'none',
                  opacity: isUnlocked ? 1 : isAvailable ? 0.95 : 0.6,
                  transition: 'transform 0.15s ease'
                }}
              >
                {/* Node Header */}
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                    <div style={{
                      width: '38px',
                      height: '38px',
                      borderRadius: '10px',
                      backgroundColor: isUnlocked ? '#0284C7' : '#1E293B',
                      border: `1.5px solid ${isUnlocked ? '#38BDF8' : 'rgba(255,255,255,0.1)'}`,
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      fontSize: '18px'
                    }}>
                      {node.icon}
                    </div>
                    <div>
                      <div style={{ fontWeight: '900', fontSize: '13px', color: '#F8FAFC' }}>
                        {node.name}
                      </div>
                      <div style={{
                        fontSize: '9px',
                        fontWeight: '800',
                        color: tierBadgeColor,
                        textTransform: 'uppercase',
                        fontFamily: '"Press Start 2P", monospace',
                        marginTop: '2px'
                      }}>
                        Tier {node.tier}
                      </div>
                    </div>
                  </div>

                  {/* Status Badge */}
                  <div>
                    {isUnlocked ? (
                      <span style={{
                        backgroundColor: 'rgba(16, 185, 129, 0.2)',
                        border: '1px solid #10B981',
                        color: '#10B981',
                        fontSize: '10px',
                        fontWeight: '800',
                        padding: '3px 8px',
                        borderRadius: '8px'
                      }}>
                        ✓ Terbuka
                      </span>
                    ) : (
                      <span style={{
                        backgroundColor: 'rgba(15, 23, 42, 0.8)',
                        border: '1px solid rgba(255, 255, 255, 0.1)',
                        color: '#94A3B8',
                        fontSize: '10px',
                        fontWeight: '800',
                        padding: '3px 8px',
                        borderRadius: '8px'
                      }}>
                        🔒 Terkunci
                      </span>
                    )}
                  </div>
                </div>

                {/* Lore / Effect Description */}
                <p style={{ fontSize: '11px', color: '#CBD5E1', lineHeight: '1.5', margin: 0 }}>
                  {node.desc}
                </p>

                {/* Prerequisites Warning */}
                {!reqsMet && node.reqs.length > 0 && (
                  <div style={{ fontSize: '10px', color: '#F87171' }}>
                    ⚠️ Butuh riset: {node.reqs.map((r) => {
                      const p = COSMIC_RESEARCH_TREE.find((item) => item.id === r);
                      return p ? p.name : r;
                    }).join(', ')}
                  </div>
                )}

                {/* Cost & Unlock Action */}
                <div style={{
                  paddingTop: '8px',
                  borderTop: '1px solid rgba(255, 255, 255, 0.08)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between'
                }}>
                  <div style={{
                    fontSize: '11px',
                    fontWeight: '900',
                    color: canAfford ? '#FACC15' : '#EF4444',
                    fontFamily: '"Press Start 2P", monospace'
                  }}>
                    ✨ {formatNumber(node.costZenith)}
                  </div>

                  {!isUnlocked && (
                    <button
                      onClick={() => handleUnlock(node)}
                      disabled={!canAfford || !reqsMet}
                      className={`btn-pixel ${canAfford && reqsMet ? 'gold' : ''}`}
                      style={{ fontSize: '10px', padding: '5px 12px' }}
                    >
                      Buka Riset
                    </button>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* VIEW 2: CONSTELLATION CANVAS MAP */}
      {viewMode === 'constellation' && (
        <div style={{
          backgroundColor: '#050713',
          borderRadius: '16px',
          border: '2px solid rgba(255, 255, 255, 0.1)',
          overflow: 'hidden',
          display: 'flex',
          flexDirection: 'column'
        }}>
          <canvas
            ref={canvasRef}
            width={640}
            height={380}
            style={{ width: '100%', height: '380px', display: 'block' }}
          />

          {/* Selected Node Details Bar */}
          {selectedNode && (
            <div style={{
              padding: '12px 16px',
              backgroundColor: 'rgba(15, 23, 42, 0.95)',
              borderTop: '1px solid rgba(255, 255, 255, 0.1)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between'
            }}>
              <div>
                <div style={{ fontWeight: '900', fontSize: '13px', color: '#F8FAFC' }}>
                  {selectedNode.icon} {selectedNode.name}
                </div>
                <div style={{ fontSize: '11px', color: '#94A3B8' }}>
                  {selectedNode.desc}
                </div>
              </div>

              {!unNodes.includes(selectedNode.id) && (
                <button
                  onClick={() => handleUnlock(selectedNode)}
                  disabled={state.totalZenith < selectedNode.costZenith}
                  className="btn-pixel gold"
                  style={{ fontSize: '11px', padding: '6px 14px' }}
                >
                  Buka: {formatNumber(selectedNode.costZenith)} ✨
                </button>
              )}
            </div>
          )}
        </div>
      )}
    </div>
  );
}
