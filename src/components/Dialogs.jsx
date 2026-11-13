import React from 'react';
import { formatNumber } from '../gameState';

export function GudangModal({ isOpen, onClose, resources }) {
  if (!isOpen) return null;

  const items = [
    { name: 'Batu Gunung', key: 'batu', icon: '🪨', desc: 'Material utama dinding benteng dan pondasi peradaban.' },
    { name: 'Kayu Rimba', key: 'kayu', icon: '🪵', desc: 'Bahan bakar api unggun dan konstruksi rumah suku.' },
    { name: 'Daging Buruan', key: 'daging', icon: '🥩', desc: 'Makanan bergizi menjaga populasi dan pekerja tetap fit.' },
    { name: 'Besi Tempa', key: 'besi', icon: '⛓️', desc: 'Logam berkualitas tinggi untuk senjata ksatria dan mesin.' }
  ];

  return (
    <div style={{
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      backgroundColor: 'rgba(0,0,0,0.75)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      zIndex: 100,
      padding: '20px'
    }}>
      <div style={{
        backgroundColor: '#161B26',
        border: '2px solid #2F3B54',
        borderRadius: '12px',
        width: '100%',
        maxWidth: '420px',
        maxHeight: '80vh',
        display: 'flex',
        flexDirection: 'column',
        boxShadow: '0 10px 40px rgba(0,0,0,0.8)'
      }}>
        {/* Header */}
        <div style={{
          padding: '14px 16px',
          borderBottom: '1px solid #252F44',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between'
        }}>
          <h3 style={{ fontSize: '15px', color: '#ECEFF1', display: 'flex', alignItems: 'center', gap: '8px' }}>
            <span>📦</span> Gudang Sumber Daya
          </h3>
          <button
            onClick={onClose}
            style={{
              background: 'none',
              border: 'none',
              color: '#90A4AE',
              fontSize: '18px',
              cursor: 'pointer'
            }}
          >
            ✕
          </button>
        </div>

        {/* Content list (Scrollable) */}
        <div style={{
          padding: '14px 16px',
          overflowY: 'auto',
          display: 'flex',
          flexDirection: 'column',
          gap: '10px'
        }}>
          {items.map((item) => (
            <div
              key={item.key}
              style={{
                backgroundColor: '#1C2333',
                border: '1px solid #2B374E',
                borderRadius: '8px',
                padding: '10px 12px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between'
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <span style={{ fontSize: '24px' }}>{item.icon}</span>
                <div>
                  <div style={{ fontWeight: '700', fontSize: '13px', color: '#ECEFF1' }}>
                    {item.name}
                  </div>
                  <div style={{ fontSize: '10px', color: '#90A4AE', maxWidth: '200px' }}>
                    {item.desc}
                  </div>
                </div>
              </div>

              <div style={{
                fontWeight: '800',
                fontSize: '14px',
                color: '#FFD54F',
                fontFamily: '"Press Start 2P", monospace'
              }}>
                {formatNumber(resources[item.key])}
              </div>
            </div>
          ))}
        </div>

        {/* Footer */}
        <div style={{
          padding: '12px 16px',
          borderTop: '1px solid #252F44',
          display: 'flex',
          justifyContent: 'flex-end'
        }}>
          <button onClick={onClose} className="btn-pixel gold" style={{ fontSize: '12px' }}>
            Tutup Gudang
          </button>
        </div>
      </div>
    </div>
  );
}

export function StatsModal({ isOpen, onClose, era, nodes, resources }) {
  if (!isOpen) return null;

  const totalLevels = nodes.reduce((acc, n) => acc + n.level, 0);
  const unlockedCount = nodes.filter((n) => n.unlocked).length;

  return (
    <div style={{
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      backgroundColor: 'rgba(0,0,0,0.75)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      zIndex: 100,
      padding: '20px'
    }}>
      <div style={{
        backgroundColor: '#161B26',
        border: '2px solid #2F3B54',
        borderRadius: '12px',
        width: '100%',
        maxWidth: '420px',
        padding: '16px',
        display: 'flex',
        flexDirection: 'column',
        gap: '12px',
        boxShadow: '0 10px 40px rgba(0,0,0,0.8)'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <h3 style={{ fontSize: '15px', color: '#ECEFF1', display: 'flex', alignItems: 'center', gap: '8px' }}>
            <span>📊</span> Statistik Peradaban
          </h3>
          <button
            onClick={onClose}
            style={{ background: 'none', border: 'none', color: '#90A4AE', fontSize: '18px', cursor: 'pointer' }}
          >
            ✕
          </button>
        </div>

        <div style={{
          backgroundColor: '#1C2333',
          border: '1px solid #2B374E',
          borderRadius: '8px',
          padding: '12px',
          display: 'flex',
          flexDirection: 'column',
          gap: '8px',
          fontSize: '12px'
        }}>
          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
            <span style={{ color: '#90A4AE' }}>Era Saat Ini:</span>
            <span style={{ color: era.color, fontWeight: '700' }}>{era.name}</span>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
            <span style={{ color: '#90A4AE' }}>Bangunan Aktif:</span>
            <span style={{ color: '#ECEFF1', fontWeight: '700' }}>{unlockedCount} / {nodes.length}</span>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
            <span style={{ color: '#90A4AE' }}>Total Tingkat Bangunan:</span>
            <span style={{ color: '#FFD54F', fontWeight: '700' }}>Lv. {totalLevels}</span>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
            <span style={{ color: '#90A4AE' }}>Cadangan Zenith:</span>
            <span style={{ color: '#FFD54F', fontWeight: '700' }}>{formatNumber(resources.zenith)}</span>
          </div>
        </div>

        <button onClick={onClose} className="btn-pixel gold" style={{ fontSize: '12px' }}>
          Tutup
        </button>
      </div>
    </div>
  );
}
