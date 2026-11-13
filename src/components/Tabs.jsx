import React from 'react';

export default function Tabs({ activeTab, onTabChange }) {
  const tabs = [
    { id: 'peta', label: 'Peta Dunia', icon: '🗺️' },
    { id: 'bangunan', label: 'Bangunan', icon: '🏛️' },
    { id: 'riset', label: 'Riset', icon: '🔬' },
    { id: 'filsafat', label: 'Filsafat', icon: '📜' },
    { id: 'komandan', label: 'Komandan', icon: '👥' },
    { id: 'armory', label: 'Peralatan', icon: '⚔️' },
    { id: 'dungeon', label: 'Dungeon & Raid', icon: '🌀' },
    { id: 'market', label: 'Toko Pasar', icon: '🏪' },
    { id: 'kamus', label: 'Kamus Max', icon: '📖' }
  ];

  return (
    <nav style={{
      position: 'relative',
      zIndex: 100,
      flexShrink: 0,
      display: 'flex',
      backgroundColor: 'rgba(11, 15, 25, 0.95)',
      backdropFilter: 'blur(16px)',
      borderTop: '1.5px solid rgba(255, 255, 255, 0.1)',
      padding: '4px 8px',
      overflowX: 'auto',
      boxShadow: '0 -4px 25px rgba(0,0,0,0.5)'
    }}>
      {tabs.map((tab) => {
        const isActive = activeTab === tab.id;
        return (
          <button
            key={tab.id}
            onClick={() => onTabChange(tab.id)}
            style={{
              flex: '1 0 auto',
              minWidth: '60px',
              padding: '6px 4px',
              backgroundColor: isActive ? 'rgba(30, 41, 59, 0.85)' : 'transparent',
              border: 'none',
              borderBottom: isActive ? '2px solid #FFD54F' : '2px solid transparent',
              borderRadius: '8px',
              color: isActive ? '#FFD54F' : '#94A3B8',
              fontFamily: '"Plus Jakarta Sans", sans-serif',
              fontWeight: '800',
              fontSize: '11px',
              cursor: 'pointer',
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              gap: '2px',
              transition: 'all 0.15s ease',
              whiteSpace: 'nowrap'
            }}
          >
            <span style={{ fontSize: '18px' }}>{tab.icon}</span>
            <span>{tab.label}</span>
          </button>
        );
      })}
    </nav>
  );
}
