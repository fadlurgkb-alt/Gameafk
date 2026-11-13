import React from 'react';

export default function Tabs({ activeTab, onTabChange }) {
  const tabs = [
    { id: 'bangunan', label: 'Bangunan', icon: '🏛️' },
    { id: 'riset', label: 'Riset', icon: '🔬' },
    { id: 'filsafat', label: 'Filsafat', icon: '📜' },
    { id: 'komandan', label: 'Komandan', icon: '👥' },
    { id: 'dungeon', label: 'Dungeon & Raid', icon: '🗺️' },
    { id: 'market', label: 'Pasar', icon: '🏪' },
    { id: 'kamus', label: 'Kamus Max', icon: '📖' }
  ];

  return (
    <nav style={{
      display: 'flex',
      backgroundColor: 'rgba(15, 23, 42, 0.98)',
      backdropFilter: 'blur(10px)',
      borderTop: '1.5px solid rgba(255, 255, 255, 0.08)',
      padding: '4px',
      overflowX: 'auto'
    }}>
      {tabs.map((tab) => {
        const isActive = activeTab === tab.id;
        return (
          <button
            key={tab.id}
            onClick={() => onTabChange(tab.id)}
            style={{
              flex: '1 0 auto',
              minWidth: '65px',
              padding: '8px 6px',
              backgroundColor: isActive ? 'rgba(30, 41, 59, 0.8)' : 'transparent',
              border: 'none',
              borderBottom: isActive ? '2px solid #FFB300' : '2px solid transparent',
              borderRadius: '6px',
              color: isActive ? '#FFD54F' : '#94A3B8',
              fontFamily: '"Rajdhani", sans-serif',
              fontWeight: '700',
              fontSize: '11px',
              cursor: 'pointer',
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              gap: '3px',
              transition: 'all 0.15s ease',
              whiteSpace: 'nowrap'
            }}
          >
            <span style={{ fontSize: '16px' }}>{tab.icon}</span>
            <span>{tab.label}</span>
          </button>
        );
      })}
    </nav>
  );
}
