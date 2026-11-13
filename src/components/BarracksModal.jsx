import React from 'react';
import confetti from 'canvas-confetti';
import { formatNumber } from '../gameState';

export default function BarracksModal({ isOpen, onClose, state, onTrainTroop, onTrainTroopsBatch }) {
  if (!isOpen) return null;

  const costPerTroop = { kayu: 15, batu: 10, berry: 5 };
  const canAffordSingle = state.kayu >= costPerTroop.kayu && state.batu >= costPerTroop.batu && state.berry >= costPerTroop.berry && state.currentPasukan < state.maxPasukan;

  // Calculate max trainable troops
  const maxTrainableByWood = Math.floor(state.kayu / costPerTroop.kayu);
  const maxTrainableByStone = Math.floor(state.batu / costPerTroop.batu);
  const maxTrainableByBerry = Math.floor(state.berry / costPerTroop.berry);
  const remainingCap = state.maxPasukan - state.currentPasukan;
  const maxPossible = Math.max(0, Math.min(remainingCap, maxTrainableByWood, maxTrainableByStone, maxTrainableByBerry));

  const units = [
    {
      name: 'Prajurit Kapak Batu',
      role: 'Garis Depan Primitif',
      img: '/assets/img_unit_stone_axe.png',
      atk: 180,
      hp: 650,
      desc: 'Pejuang berani bersenjatakan kapak obsidian tajam pembelah perisai musuh.'
    },
    {
      name: 'Pemburu Tombak Primitif',
      role: 'Penembak Jarak Menengah',
      img: '/assets/img_unit_spear.png',
      atk: 240,
      hp: 480,
      desc: 'Pelacak lincah bersenjatakan tombak bambu runcing pelumpuh serbuan raid.'
    },
    {
      name: 'Batalion Pasukan Suku',
      role: 'Formasi Tempur Padu',
      img: '/assets/img_prehistoric_troops.png',
      atk: 550,
      hp: 1400,
      desc: 'Kelompok pejuang terlatih bertempur bahu-membahu menjaga tanah peradaban.'
    }
  ];

  return (
    <div className="modal-backdrop">
      <div className="modal-box" style={{ maxWidth: '520px' }}>
        {/* Header */}
        <div className="modal-header">
          <h3><span>🛡️</span> Barak Militer & Pelatihan Pasukan</h3>
          <button onClick={onClose} className="modal-close-btn">✕</button>
        </div>

        <div className="modal-body" style={{ padding: '16px', display: 'flex', flexDirection: 'column', gap: '14px' }}>
          {/* Status Pasukan Banner */}
          <div style={{
            backgroundColor: 'rgba(30, 41, 59, 0.7)',
            borderRadius: '12px',
            padding: '14px',
            border: '1.5px solid rgba(255, 255, 255, 0.1)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between'
          }}>
            <div>
              <div style={{ fontSize: '11px', color: '#94A3B8' }}>Kapasitas Garnisun Pasukan:</div>
              <div style={{
                fontSize: '18px',
                fontWeight: '900',
                color: '#22C55E',
                fontFamily: '"Press Start 2P", monospace',
                marginTop: '4px'
              }}>
                {state.currentPasukan} / {state.maxPasukan}
              </div>
              <div style={{ fontSize: '11px', color: '#FCD34D', marginTop: '4px' }}>
                Biaya: 15 Kayu, 10 Batu, 5 Berry / Pasukan
              </div>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
              <button
                onClick={() => {
                  onTrainTroop();
                  confetti({ particleCount: 20, spread: 40 });
                }}
                disabled={!canAffordSingle}
                className="btn-pixel green"
                style={{ fontSize: '11px', padding: '6px 12px' }}
              >
                ⚔️ Latih (+1)
              </button>
              <button
                onClick={() => {
                  onTrainTroopsBatch(maxPossible);
                  confetti({ particleCount: 50, spread: 70 });
                }}
                disabled={maxPossible <= 0}
                className="btn-pixel gold"
                style={{ fontSize: '11px', padding: '6px 12px' }}
              >
                ⚡ Latih Maksimal (+{maxPossible})
              </button>
            </div>
          </div>

          {/* Daftar Satuan Unit Pasukan */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
            <div style={{ fontSize: '11px', fontWeight: '800', color: '#94A3B8', textTransform: 'uppercase' }}>
              Satuan Tempur Peradaban
            </div>

            {units.map((u, i) => (
              <div
                key={i}
                style={{
                  backgroundColor: 'rgba(15, 23, 42, 0.7)',
                  borderRadius: '10px',
                  padding: '10px 12px',
                  border: '1px solid rgba(255, 255, 255, 0.08)',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '12px'
                }}
              >
                <img
                  src={u.img}
                  alt={u.name}
                  style={{
                    width: '46px',
                    height: '46px',
                    objectFit: 'contain',
                    backgroundColor: 'rgba(30, 41, 59, 0.8)',
                    borderRadius: '8px',
                    padding: '4px',
                    border: '1px solid rgba(255, 255, 255, 0.1)'
                  }}
                />
                <div style={{ flex: 1 }}>
                  <div style={{ fontWeight: '800', fontSize: '13px', color: '#F8FAFC' }}>{u.name}</div>
                  <div style={{ fontSize: '10px', color: '#38BDF8', fontWeight: '700' }}>{u.role}</div>
                  <div style={{ fontSize: '10px', color: '#CBD5E1', marginTop: '2px' }}>{u.desc}</div>
                  <div style={{ fontSize: '10px', color: '#4ADE80', fontWeight: '700', marginTop: '2px' }}>
                    ⚔️ Atk: {u.atk} • ❤️ HP: {u.hp}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="modal-footer">
          <button onClick={onClose} className="btn-pixel" style={{ fontSize: '12px' }}>Tutup</button>
        </div>
      </div>
    </div>
  );
}
