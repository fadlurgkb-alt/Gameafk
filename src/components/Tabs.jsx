import React from 'react';

export default function Tabs({ activeTab, onTabChange }) {
  const tabs = [
    { id: 'bangunan', label: 'Bangunan', icon: '🏛️' },
    { id: 'riset', label: 'Riset', icon: '🔬' },
    { id: 'komandan', label: 'Komandan', icon: '⚔️' },
    { id: 'ekspedisi', label: 'Ekspedisi', icon: '🗺️' }
  ];

  return (
    <nav style={{
      display: 'flex',
      backgroundColor: '#121622',
      borderTop: '2px solid #232B3E',
      padding: '4px'
    }}>
      {tabs.map((tab) => {
        const isActive = activeTab === tab.id;
        return (
          <button
            key={tab.id}
            onClick={() => onTabChange(tab.id)}
            style={{
              flex: 1,
              padding: '8px 4px',
              backgroundColor: isActive ? '#1F2738' : 'transparent',
              border: 'none',
              borderBottom: isActive ? '2px solid #FFB300' : '2px solid transparent',
              color: isActive ? '#FFD54F' : '#90A4AE',
              fontFamily: '"Rajdhani", sans-serif',
              fontWeight: '700',
              fontSize: '12px',
              cursor: 'pointer',
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              gap: '2px',
              transition: 'all 0.15s ease'
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
