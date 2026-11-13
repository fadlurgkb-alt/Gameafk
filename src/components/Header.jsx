import React from 'react';
import { formatNumber } from '../gameState';

export default function Header({
  resources,
  zenithRate,
  onOpenGudang,
  onOpenStats,
  onOpenCommanders,
  onGodMode,
  onMaxAll
}) {
  return (
    <header style={{
      padding: '12px 14px',
      backgroundColor: '#121622',
      borderBottom: '2px solid #232B3E',
      display: 'flex',
      flexDirection: 'column',
      gap: '10px'
    }}>
      {/* Top Bar: Title & Debug Buttons */}
      <div style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <span style={{ fontSize: '20px' }}>🌌</span>
          <div>
            <h1 style={{
              fontSize: '15px',
              fontWeight: '800',
              letterSpacing: '0.8px',
              color: '#ECEFF1',
              lineHeight: '1.2'
            }}>
              GENESIS PIXEL
            </h1>
            <span style={{ fontSize: '10px', color: '#90A4AE', textTransform: 'uppercase' }}>
              Idle Cosmos
            </span>
          </div>
        </div>

        {/* Action & Debug Controls */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
          <button
            onClick={onGodMode}
            title="Dewa Mode (+50k Zenith, +10k Resources)"
            className="btn-pixel gold"
            style={{ fontSize: '11px', padding: '4px 8px' }}
          >
            🛠️ Dewa
          </button>
          <button
            onClick={onMaxAll}
            title="Buka Semua Era & Hero Mythical"
            className="btn-pixel cyan"
            style={{ fontSize: '11px', padding: '4px 8px' }}
          >
            ⚡ MAX
          </button>
          <button
            onClick={onOpenGudang}
            title="Gudang Sumber Daya"
            className="btn-pixel"
            style={{ fontSize: '11px', padding: '4px 8px' }}
          >
            📦
          </button>
          <button
            onClick={onOpenStats}
            title="Statistik Peradaban"
            className="btn-pixel"
            style={{ fontSize: '11px', padding: '4px 8px' }}
          >
            📊
          </button>
        </div>
      </div>

      {/* Primary Zenith Display */}
      <div style={{
        backgroundColor: '#1A2130',
        borderRadius: '8px',
        border: '1.5px solid #2E3A52',
        padding: '8px 12px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <span style={{ fontSize: '22px' }}>✨</span>
          <div>
            <div style={{
              fontSize: '20px',
              fontWeight: '800',
              color: '#FFD700',
              fontFamily: '"Press Start 2P", monospace',
              letterSpacing: '-0.5px'
            }}>
              {formatNumber(resources.zenith)}
            </div>
            <div style={{ fontSize: '11px', color: '#81C784', fontWeight: '600' }}>
              +{formatNumber(zenithRate)} / detik
            </div>
          </div>
        </div>

        <div style={{
          display: 'flex',
          gap: '12px',
          backgroundColor: '#111622',
          padding: '6px 10px',
          borderRadius: '6px',
          border: '1px solid #242D40',
          fontSize: '11px',
          fontWeight: '700'
        }}>
          <div style={{ color: '#B0BEC5' }}>🪨 {formatNumber(resources.batu)}</div>
          <div style={{ color: '#A1887F' }}>🪵 {formatNumber(resources.kayu)}</div>
          <div style={{ color: '#FF8A65' }}>🥩 {formatNumber(resources.daging)}</div>
          <div style={{ color: '#90CAF9' }}>⛓️ {formatNumber(resources.besi)}</div>
        </div>
      </div>
    </header>
  );
}
