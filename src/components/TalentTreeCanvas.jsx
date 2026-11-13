import React, { useRef, useEffect, useState } from 'react';
import confetti from 'canvas-confetti';
import { COSMIC_RESEARCH_TREE, formatNumber } from '../gameState';

export default function TalentTreeCanvas({ state, onUnlockResearch }) {
  const canvasRef = useRef(null);
  const [selectedNode, setSelectedNode] = useState(COSMIC_RESEARCH_TREE[0]);
  const [viewMode, setViewMode] = useState('tree'); // 'tree' | 'list'
  const [camera, setCamera] = useState({ x: 0, y: 0, zoom: 0.9 });
  const isDraggingRef = useRef(false);
  const dragStartRef = useRef({ x: 0, y: 0 });

  // Canvas Render Loop
  useEffect(() => {
    if (viewMode !== 'tree') return;
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

      // Deep space background gradient
      const bgGrad = ctx.createLinearGradient(0, 0, w, h);
      bgGrad.addColorStop(0, '#050713');
      bgGrad.addColorStop(0.5, '#0B0F24');
      bgGrad.addColorStop(1, '#03050B');
      ctx.fillStyle = bgGrad;
      ctx.fillRect(0, 0, w, h);

      // Distant stars twinkle
      for (let i = 0; i < 40; i++) {
        const sx = ((i * 123 + tick * 0.1) % w);
        const sy = ((i * 87) % h);
        const alpha = 0.2 + Math.sin(tick * 0.05 + i) * 0.2;
        ctx.fillStyle = `rgba(255, 255, 255, ${alpha})`;
        ctx.fillRect(sx, sy, 2, 2);
      }

      ctx.save();
      // Apply Camera Transform
      ctx.translate(w / 2 + camera.x, h / 2 + camera.y);
      ctx.scale(camera.zoom, camera.zoom);
      ctx.translate(-w / 2, -h / 2);

      // 1. Draw Connecting Constellation Lines
      COSMIC_RESEARCH_TREE.forEach((node) => {
        node.reqs.forEach((reqId) => {
          const parent = COSMIC_RESEARCH_TREE.find((n) => n.id === reqId);
          if (!parent) return;

          const isParentUnlocked = state.unlockedNodes.includes(parent.id);
          const isCurrentUnlocked = state.unlockedNodes.includes(node.id);

          ctx.beginPath();
          ctx.moveTo(parent.x, parent.y);
          ctx.lineTo(node.x, node.y);

          if (isCurrentUnlocked) {
            // Glowing cyan/gold laser
            ctx.strokeStyle = '#00E5FF';
            ctx.lineWidth = 3;
            ctx.shadowColor = '#00E5FF';
            ctx.shadowBlur = 8;
            ctx.stroke();
            ctx.shadowBlur = 0;
          } else if (isParentUnlocked) {
            // Pulsing energetic connection
            ctx.strokeStyle = 'rgba(251, 191, 36, 0.7)';
            ctx.lineWidth = 2;
            ctx.setLineDash([6, 6]);
            ctx.lineDashOffset = -tick * 0.8;
            ctx.stroke();
            ctx.setLineDash([]);
          } else {
            // Dark inactive line
            ctx.strokeStyle = 'rgba(255, 255, 255, 0.12)';
            ctx.lineWidth = 1.5;
            ctx.stroke();
          }
        });
      });

      // 2. Draw Interactive Constellation Nodes
      COSMIC_RESEARCH_TREE.forEach((node) => {
        const isUnlocked = state.unlockedNodes.includes(node.id);
        const reqsMet = node.reqs.every((req) => state.unlockedNodes.includes(req));
        const isSelected = selectedNode && selectedNode.id === node.id;
        const radius = 22;

        // Selection ring
        if (isSelected) {
          ctx.beginPath();
          ctx.arc(node.x, node.y, radius + 8, 0, Math.PI * 2);
          ctx.strokeStyle = '#FFD54F';
          ctx.lineWidth = 2;
          ctx.stroke();
        }

        // Base node circle
        ctx.beginPath();
        ctx.arc(node.x, node.y, radius, 0, Math.PI * 2);

        if (isUnlocked) {
          ctx.fillStyle = '#065F46';
          ctx.fill();
          ctx.strokeStyle = '#10B981';
          ctx.lineWidth = 3;
          ctx.stroke();
        } else if (reqsMet) {
          // Ready to research (pulsing ring)
          const pulse = Math.sin(tick * 0.08) * 3;
          ctx.fillStyle = '#1E293B';
          ctx.fill();
          ctx.strokeStyle = '#00E5FF';
          ctx.lineWidth = 2.5 + pulse * 0.5;
          ctx.shadowColor = '#00E5FF';
          ctx.shadowBlur = 10;
          ctx.stroke();
          ctx.shadowBlur = 0;
        } else {
          // Locked
          ctx.fillStyle = '#0F172A';
          ctx.fill();
          ctx.strokeStyle = 'rgba(255, 255, 255, 0.15)';
          ctx.lineWidth = 1.5;
          ctx.stroke();
        }

        // Icon inside node
        ctx.font = '16px "Segoe UI Emoji", sans-serif';
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';
        ctx.fillText(node.icon, node.x, node.y);

        // Status badge indicator
        if (isUnlocked) {
          ctx.fillStyle = '#10B981';
          ctx.beginPath();
          ctx.arc(node.x + 15, node.y - 15, 7, 0, Math.PI * 2);
          ctx.fill();
          ctx.fillStyle = '#fff';
          ctx.font = 'bold 9px sans-serif';
          ctx.fillText('✓', node.x + 15, node.y - 15);
        } else if (!reqsMet) {
          ctx.fillStyle = 'rgba(0,0,0,0.7)';
          ctx.beginPath();
          ctx.arc(node.x + 15, node.y - 15, 7, 0, Math.PI * 2);
          ctx.fill();
          ctx.fillStyle = '#EF4444';
          ctx.font = '8px sans-serif';
          ctx.fillText('🔒', node.x + 15, node.y - 15);
        }

        // Node label below
        ctx.font = 'bold 10px "Rajdhani", sans-serif';
        ctx.fillStyle = isUnlocked ? '#A7F3D0' : reqsMet ? '#E0F2FE' : '#64748B';
        ctx.fillText(node.name, node.x, node.y + radius + 14);
      });

      ctx.restore();

      animId = requestAnimationFrame(render);
    };

    render();
    return () => cancelAnimationFrame(animId);
  }, [state, selectedNode, camera, viewMode]);

  // Pointer Drag & Click
  const handlePointerDown = (e) => {
    isDraggingRef.current = true;
    dragStartRef.current = { x: e.clientX - camera.x, y: e.clientY - camera.y };
  };

  const handlePointerMove = (e) => {
    if (!isDraggingRef.current) return;
    setCamera((p) => ({
      ...p,
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
      // Find clicked node in world coordinates
      const canvas = canvasRef.current;
      if (canvas) {
        const rect = canvas.getBoundingClientRect();
        const screenX = (e.clientX - rect.left) * (canvas.width / rect.width);
        const screenY = (e.clientY - rect.top) * (canvas.height / rect.height);

        // Reverse camera transform
        const worldX = (screenX - (canvas.width / 2 + camera.x)) / camera.zoom + canvas.width / 2;
        const worldY = (screenY - (canvas.height / 2 + camera.y)) / camera.zoom + canvas.height / 2;

        const hit = COSMIC_RESEARCH_TREE.find((n) => Math.hypot(n.x - worldX, n.y - worldY) < 30);
        if (hit) setSelectedNode(hit);
      }
    }
    isDraggingRef.current = false;
  };

  const canAffordSelected = selectedNode &&
    state.totalZenith >= selectedNode.costZenith &&
    selectedNode.reqs.every((r) => state.unlockedNodes.includes(r)) &&
    !state.unlockedNodes.includes(selectedNode.id);

  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      gap: '10px',
      backgroundColor: '#070A14',
      borderRadius: '16px',
      padding: '12px',
      border: '1.5px solid rgba(255, 255, 255, 0.1)'
    }}>
      {/* Top Header & Switcher */}
      <div style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <span style={{ fontSize: '20px' }}>🌌</span>
          <div>
            <h3 style={{ fontSize: '14px', fontWeight: '800', color: '#F8FAFC', margin: 0 }}>
              JEJARING KOSMIS (TALENT TREE)
            </h3>
            <span style={{ fontSize: '10px', color: '#94A3B8' }}>
              26 Cabang Evolusi & Kognisi Peradaban
            </span>
          </div>
        </div>

        <div style={{ display: 'flex', gap: '6px' }}>
          <button
            onClick={() => setViewMode(viewMode === 'tree' ? 'list' : 'tree')}
            className="btn-pixel cyan"
            style={{ fontSize: '11px', padding: '4px 10px' }}
          >
            {viewMode === 'tree' ? '📋 Mode Daftar' : '🌌 Mode Konstelasi'}
          </button>
        </div>
      </div>

      {viewMode === 'tree' ? (
        <div style={{
          position: 'relative',
          width: '100%',
          height: '420px',
          borderRadius: '12px',
          overflow: 'hidden',
          border: '1.5px solid rgba(255, 255, 255, 0.08)'
        }}>
          <canvas
            ref={canvasRef}
            width={800}
            height={600}
            onPointerDown={handlePointerDown}
            onPointerMove={handlePointerMove}
            onPointerUp={handlePointerUp}
            style={{
              width: '100%',
              height: '100%',
              display: 'block',
              cursor: isDraggingRef.current ? 'grabbing' : 'grab',
              touchAction: 'none'
            }}
          />

          {/* Quick Zoom Buttons */}
          <div style={{
            position: 'absolute',
            top: '10px',
            right: '10px',
            display: 'flex',
            gap: '4px'
          }}>
            <button onClick={() => setCamera((p) => ({ ...p, zoom: Math.min(1.8, p.zoom + 0.2) }))} className="btn-pixel" style={{ padding: '4px 8px', fontSize: '11px' }}>+</button>
            <button onClick={() => setCamera((p) => ({ ...p, zoom: Math.max(0.5, p.zoom - 0.2) }))} className="btn-pixel" style={{ padding: '4px 8px', fontSize: '11px' }}>-</button>
            <button onClick={() => setCamera({ x: 0, y: 0, zoom: 0.9 })} className="btn-pixel" style={{ padding: '4px 8px', fontSize: '11px' }}>🎯</button>
          </div>
        </div>
      ) : (
        /* List Mode View */
        <div style={{
          display: 'flex',
          flexDirection: 'column',
          gap: '8px',
          maxHeight: '420px',
          overflowY: 'auto'
        }}>
          {COSMIC_RESEARCH_TREE.map((node) => {
            const isUnlocked = state.unlockedNodes.includes(node.id);
            const reqsMet = node.reqs.every((r) => state.unlockedNodes.includes(r));
            const canAfford = state.totalZenith >= node.costZenith && reqsMet && !isUnlocked;

            return (
              <div
                key={node.id}
                onClick={() => setSelectedNode(node)}
                style={{
                  backgroundColor: isUnlocked ? 'rgba(6, 95, 70, 0.3)' : 'rgba(30, 41, 59, 0.6)',
                  border: `1.5px solid ${isUnlocked ? '#10B981' : reqsMet ? '#00E5FF' : 'rgba(255, 255, 255, 0.08)'}`,
                  borderRadius: '10px',
                  padding: '10px 12px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between',
                  cursor: 'pointer'
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <span style={{ fontSize: '20px' }}>{node.icon}</span>
                  <div>
                    <div style={{ fontWeight: '800', fontSize: '13px', color: '#F8FAFC' }}>
                      {node.name}
                    </div>
                    <div style={{ fontSize: '10px', color: '#94A3B8' }}>
                      Tier {node.tier} • {node.desc}
                    </div>
                  </div>
                </div>

                <div>
                  {isUnlocked ? (
                    <span style={{ color: '#10B981', fontWeight: '800', fontSize: '11px' }}>✓ Dikuasai</span>
                  ) : (
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        onUnlockResearch(node);
                      }}
                      disabled={!canAfford}
                      className={`btn-pixel ${canAfford ? 'cyan' : ''}`}
                      style={{ fontSize: '11px', padding: '4px 10px' }}
                    >
                      Riset ({formatNumber(node.costZenith)} Z)
                    </button>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Selected Node Inspector Card */}
      {selectedNode && (
        <div style={{
          backgroundColor: 'rgba(30, 41, 59, 0.9)',
          borderRadius: '12px',
          border: '1.5px solid rgba(255, 255, 255, 0.15)',
          padding: '12px 16px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between'
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <span style={{ fontSize: '28px' }}>{selectedNode.icon}</span>
            <div>
              <div style={{ fontWeight: '900', fontSize: '14px', color: '#FFD54F' }}>
                {selectedNode.name} <span style={{ fontSize: '11px', color: '#38BDF8' }}>(Tier {selectedNode.tier})</span>
              </div>
              <div style={{ fontSize: '11px', color: '#CBD5E1', maxWidth: '340px', marginTop: '2px' }}>
                {selectedNode.desc}
              </div>
            </div>
          </div>

          <div>
            {state.unlockedNodes.includes(selectedNode.id) ? (
              <span style={{ color: '#10B981', fontWeight: '900', fontSize: '12px' }}>
                ✓ Telah Dikuasai
              </span>
            ) : (
              <button
                onClick={() => onUnlockResearch(selectedNode)}
                disabled={!canAffordSelected}
                className={`btn-pixel ${canAffordSelected ? 'gold' : ''}`}
                style={{ fontSize: '12px', padding: '8px 16px' }}
              >
                🔬 Riset ({formatNumber(selectedNode.costZenith)} Z)
              </button>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
