import React, { useState, useEffect, useRef } from 'react';
import confetti from 'canvas-confetti';
import {
  ERAS,
  INITIAL_RESOURCES,
  INITIAL_NODES,
  INITIAL_COMMANDERS,
  formatNumber,
  calculateNodeCost
} from './gameState';

import Header from './components/Header';
import PixelCanvas from './components/PixelCanvas';
import NodeCard from './components/NodeCard';
import Tabs from './components/Tabs';
import { GudangModal, StatsModal } from './components/Dialogs';

const STORAGE_KEY = 'GENESIS_PIXEL_SAVE_V1';

export default function App() {
  const [resources, setResources] = useState(() => {
    const saved = localStorage.getItem(STORAGE_KEY);
    if (saved) {
      try {
        const parsed = JSON.parse(saved);
        return parsed.resources || INITIAL_RESOURCES;
      } catch (e) {
        console.error(e);
      }
    }
    return INITIAL_RESOURCES;
  });

  const [nodes, setNodes] = useState(() => {
    const saved = localStorage.getItem(STORAGE_KEY);
    if (saved) {
      try {
        const parsed = JSON.parse(saved);
        return parsed.nodes || INITIAL_NODES;
      } catch (e) {
        console.error(e);
      }
    }
    return INITIAL_NODES;
  });

  const [commanders, setCommanders] = useState(() => {
    const saved = localStorage.getItem(STORAGE_KEY);
    if (saved) {
      try {
        const parsed = JSON.parse(saved);
        return parsed.commanders || INITIAL_COMMANDERS;
      } catch (e) {
        console.error(e);
      }
    }
    return INITIAL_COMMANDERS;
  });

  const [activeTab, setActiveTab] = useState('bangunan');
  const [isGudangOpen, setIsGudangOpen] = useState(false);
  const [isStatsOpen, setIsStatsOpen] = useState(false);

  // Determine current active Era based on zenith or node unlocks
  const currentEra = ERAS.slice().reverse().find((e) => resources.zenith >= e.minZenith) || ERAS[0];

  // Calculate total Zenith Per Second rate
  const zenithRate = nodes.reduce((sum, node) => {
    if (!node.unlocked || node.level <= 0) return sum;
    return sum + (node.zenithPerSec * node.level);
  }, 0);

  // Main Game Tick Loop (1000ms)
  useEffect(() => {
    const interval = setInterval(() => {
      setResources((prev) => {
        let newZenith = prev.zenith;
        let newBatu = prev.batu;
        let newKayu = prev.kayu;
        let newDaging = prev.daging;
        let newBesi = prev.besi;

        nodes.forEach((n) => {
          if (n.unlocked && n.level > 0) {
            newZenith += n.zenithPerSec * n.level;
            if (n.resourceType === 'batu') newBatu += n.resourcePerSec * n.level;
            if (n.resourceType === 'kayu') newKayu += n.resourcePerSec * n.level;
            if (n.resourceType === 'daging') newDaging += n.resourcePerSec * n.level;
            if (n.resourceType === 'besi') newBesi += n.resourcePerSec * n.level;
          }
        });

        return {
          zenith: newZenith,
          batu: newBatu,
          kayu: newKayu,
          daging: newDaging,
          besi: newBesi
        };
      });
    }, 1000);

    return () => clearInterval(interval);
  }, [nodes]);

  // Unlock nodes when reaching Era thresholds
  useEffect(() => {
    setNodes((prevNodes) =>
      prevNodes.map((n) => {
        const reqEra = ERAS.find((e) => e.id === n.eraId);
        if (reqEra && resources.zenith >= reqEra.minZenith && !n.unlocked) {
          return { ...n, unlocked: true };
        }
        return n;
      })
    );
  }, [resources.zenith]);

  // Auto-Save to LocalStorage every 5 seconds
  useEffect(() => {
    const saveTimer = setInterval(() => {
      localStorage.setItem(
        STORAGE_KEY,
        JSON.stringify({
          resources,
          nodes,
          commanders,
          lastSave: Date.now()
        })
      );
    }, 5000);

    return () => clearInterval(saveTimer);
  }, [resources, nodes, commanders]);

  // Upgrade Node Handler
  const handleUpgradeNode = (targetNode) => {
    const cost = calculateNodeCost(targetNode);
    if (resources.zenith < cost) return;

    setResources((prev) => ({
      ...prev,
      zenith: prev.zenith - cost
    }));

    setNodes((prevNodes) =>
      prevNodes.map((n) => {
        if (n.id === targetNode.id) {
          const newLevel = n.level + 1;
          if (newLevel % 10 === 0) {
            confetti({ particleCount: 30, spread: 60, origin: { y: 0.7 } });
          }
          return { ...n, level: newLevel };
        }
        return n;
      })
    );
  };

  // Canvas Tap Harvest
  const handleCanvasTap = () => {
    const tapGain = Math.max(1, Math.floor(zenithRate * 0.1) + 2);
    setResources((prev) => ({
      ...prev,
      zenith: prev.zenith + tapGain
    }));
  };

  // God Mode Debug
  const handleGodMode = () => {
    setResources((prev) => ({
      ...prev,
      zenith: prev.zenith + 50000,
      batu: prev.batu + 10000,
      kayu: prev.kayu + 10000,
      daging: prev.daging + 10000,
      besi: prev.besi + 10000
    }));
    confetti({ particleCount: 80, spread: 100, origin: { y: 0.5 } });
  };

  // Max All Debug
  const handleMaxAll = () => {
    setResources({
      zenith: 10000000,
      batu: 500000,
      kayu: 500000,
      daging: 500000,
      besi: 500000
    });
    setNodes((prev) =>
      prev.map((n) => ({
        ...n,
        unlocked: true,
        level: Math.max(10, n.level)
      }))
    );
    setCommanders((prev) =>
      prev.map((c) => ({
        ...c,
        owned: true,
        level: Math.max(5, c.level)
      }))
    );
    confetti({ particleCount: 150, spread: 120, origin: { y: 0.4 } });
  };

  // Filter nodes for active tab
  const displayedNodes = nodes.filter((n) => n.type === activeTab);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%', overflow: 'hidden' }}>
      {/* Top Header */}
      <Header
        resources={resources}
        zenithRate={zenithRate}
        onOpenGudang={() => setIsGudangOpen(true)}
        onOpenStats={() => setIsStatsOpen(true)}
        onGodMode={handleGodMode}
        onMaxAll={handleMaxAll}
      />

      {/* Main Content Area (Scrollable) */}
      <main style={{
        flex: 1,
        overflowY: 'auto',
        padding: '12px',
        display: 'flex',
        flexDirection: 'column',
        gap: '12px'
      }}>
        {/* Pixel World Canvas */}
        <PixelCanvas era={currentEra} onCanvasClick={handleCanvasTap} />

        {/* Tab Content */}
        {activeTab === 'bangunan' || activeTab === 'riset' ? (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
            <div style={{
              fontSize: '12px',
              fontWeight: '700',
              color: '#90A4AE',
              textTransform: 'uppercase',
              letterSpacing: '0.5px'
            }}>
              Daftar {activeTab === 'bangunan' ? 'Bangunan Peradaban' : 'Penelitian Teknologi'}
            </div>
            {displayedNodes.map((node) => (
              <NodeCard
                key={node.id}
                node={node}
                zenith={resources.zenith}
                onUpgrade={handleUpgradeNode}
              />
            ))}
          </div>
        ) : activeTab === 'komandan' ? (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
            <div style={{
              fontSize: '12px',
              fontWeight: '700',
              color: '#90A4AE',
              textTransform: 'uppercase'
            }}>
              Barak Pahlawan & Tokoh Bangsa
            </div>
            {commanders.map((cmd) => (
              <div
                key={cmd.id}
                style={{
                  backgroundColor: '#161B26',
                  border: `1.5px solid ${cmd.owned ? '#3D4D6B' : '#1C2230'}`,
                  borderRadius: '10px',
                  padding: '12px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between',
                  opacity: cmd.owned ? 1 : 0.6
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                  <span style={{ fontSize: '28px' }}>{cmd.icon}</span>
                  <div>
                    <div style={{ fontWeight: '700', fontSize: '13px', color: '#ECEFF1' }}>
                      {cmd.name}
                    </div>
                    <div style={{ fontSize: '11px', color: '#81C784', fontWeight: '600' }}>
                      {cmd.bonus}
                    </div>
                    <div style={{ fontSize: '10px', color: '#FFD54F', fontWeight: '700' }}>
                      Rarity: {cmd.rarity}
                    </div>
                  </div>
                </div>

                <div>
                  {cmd.owned ? (
                    <span style={{
                      backgroundColor: '#1B5E20',
                      color: '#A5D6A7',
                      padding: '4px 8px',
                      borderRadius: '6px',
                      fontSize: '11px',
                      fontWeight: '700'
                    }}>
                      Aktif (Lv. {cmd.level})
                    </span>
                  ) : (
                    <span style={{
                      backgroundColor: '#37474F',
                      color: '#B0BEC5',
                      padding: '4px 8px',
                      borderRadius: '6px',
                      fontSize: '11px'
                    }}>
                      Belum Direkrut
                    </span>
                  )}
                </div>
              </div>
            ))}
          </div>
        ) : (
          /* Ekspedisi Tab */
          <div style={{
            backgroundColor: '#161B26',
            border: '1.5px solid #283347',
            borderRadius: '10px',
            padding: '16px',
            textAlign: 'center',
            display: 'flex',
            flexDirection: 'column',
            gap: '12px'
          }}>
            <span style={{ fontSize: '40px' }}>🐉</span>
            <h3 style={{ fontSize: '16px', color: '#ECEFF1' }}>Ekspedisi Lembah Mammoth</h3>
            <p style={{ fontSize: '12px', color: '#90A4AE' }}>
              Kirim ksatria dan komandan untuk menaklukkan monster liar dan merebut pundi Zenith kuno.
            </p>
            <div style={{
              backgroundColor: '#1C2333',
              borderRadius: '8px',
              padding: '10px',
              border: '1px solid #2B374E',
              display: 'flex',
              flexDirection: 'column',
              gap: '6px'
            }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '12px' }}>
                <span style={{ color: '#90A4AE' }}>HP Mammoth Raksasa:</span>
                <span style={{ color: '#EF5350', fontWeight: '700' }}>8,500 / 10,000</span>
              </div>
              <div style={{
                height: '8px',
                backgroundColor: '#263238',
                borderRadius: '4px',
                overflow: 'hidden'
              }}>
                <div style={{
                  height: '100%',
                  width: '85%',
                  backgroundColor: '#EF5350'
                }} />
              </div>
            </div>
            <button
              onClick={() => {
                setResources((prev) => ({ ...prev, zenith: prev.zenith + 1200 }));
                confetti({ particleCount: 40, spread: 70, origin: { y: 0.6 } });
              }}
              className="btn-pixel gold"
              style={{ padding: '10px 16px', fontSize: '13px' }}
            >
              ⚔️ Serang Bos (+1.2K Zenith)
            </button>
          </div>
        )}
      </main>

      {/* Bottom Tabs Navigation */}
      <Tabs activeTab={activeTab} onTabChange={setActiveTab} />

      {/* Modals */}
      <GudangModal
        isOpen={isGudangOpen}
        onClose={() => setIsGudangOpen(false)}
        resources={resources}
      />
      <StatsModal
        isOpen={isStatsOpen}
        onClose={() => setIsStatsOpen(false)}
        era={currentEra}
        nodes={nodes}
        resources={resources}
      />
    </div>
  );
}
