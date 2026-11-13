import React from 'react';
import { formatNumber, calculateCombatPower } from '../gameState';
import CommanderVisual from './CommanderVisual';

export default function ProfileModal({ isOpen, onClose, state, onSelectAvatar }) {
  if (!isOpen) return null;

  const totalCP = calculateCombatPower(state);
  const currentAvatarHero = (Array.isArray(state.commanders) && state.commanders.find((c) => c.id === state.avatarId))
    || (Array.isArray(state.commanders) && state.commanders[0])
    || { icon: '👑', color: '#3B82F6', name: 'Pemimpin' };

  const ownedHeroes = Array.isArray(state.commanders) ? state.commanders.filter((c) => c.owned) : [];

  return (
    <div className="modal-backdrop">
      <div className="modal-box" style={{ maxWidth: '460px' }}>
        <div className="modal-header">
          <h3><span>👑</span> Profil Penguasa Peradaban</h3>
          <button onClick={onClose} className="modal-close-btn">✕</button>
        </div>

        <div className="modal-body" style={{ padding: '18px', display: 'flex', flexDirection: 'column', gap: '14px' }}>
          {/* Hero Banner & Active Avatar */}
          <div style={{
            backgroundColor: 'rgba(30, 41, 59, 0.7)',
            borderRadius: '14px',
            padding: '16px',
            border: '1.5px solid rgba(255, 255, 255, 0.1)',
            display: 'flex',
            alignItems: 'center',
            gap: '16px'
          }}>
            <div style={{
              width: '64px',
              height: '64px',
              borderRadius: '12px',
              backgroundColor: 'rgba(15, 23, 42, 0.9)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              border: `3px solid ${currentAvatarHero.color || '#FFD54F'}`,
              boxShadow: `0 0 20px ${currentAvatarHero.color || '#FFD54F'}66`,
              overflow: 'hidden',
              flexShrink: 0
            }}>
              <CommanderVisual id={currentAvatarHero.id} size={58} />
            </div>

            <div>
              <h3 style={{ fontSize: '18px', color: '#F8FAFC', margin: 0, fontWeight: '900' }}>
                {state.playerName}
              </h3>
              <span style={{ fontSize: '11px', color: '#38BDF8', fontWeight: '800' }}>
                {state.selectedNationality || 'Suku Primordial'} • Avatar: {currentAvatarHero.name}
              </span>
              <div style={{
                marginTop: '6px',
                fontSize: '14px',
                fontWeight: '900',
                color: '#FFD54F',
                fontFamily: '"Press Start 2P", monospace'
              }}>
                👑 {formatNumber(totalCP)} CP
              </div>
            </div>
          </div>

          {/* Change Avatar Picker */}
          <div>
            <label style={{ fontSize: '11px', fontWeight: '800', color: '#94A3B8', textTransform: 'uppercase', display: 'block', marginBottom: '8px' }}>
              Pilih Foto Profil (Dari Komandan Milik Anda):
            </label>
            <div style={{
              display: 'flex',
              gap: '8px',
              overflowX: 'auto',
              paddingBottom: '4px'
            }}>
              {ownedHeroes.map((hero) => {
                const isSelected = state.avatarId === hero.id;
                return (
                  <button
                    key={hero.id}
                    onClick={() => onSelectAvatar(hero.id)}
                    style={{
                      width: '46px',
                      height: '46px',
                      borderRadius: '10px',
                      backgroundColor: 'rgba(15, 23, 42, 0.9)',
                      border: isSelected ? '2.5px solid #FFD54F' : '1.5px solid rgba(255, 255, 255, 0.15)',
                      cursor: 'pointer',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      flexShrink: 0,
                      transform: isSelected ? 'scale(1.1)' : 'scale(1)',
                      transition: 'transform 0.15s ease',
                      overflow: 'hidden',
                      padding: 0
                    }}
                    title={hero.name}
                  >
                    <CommanderVisual id={hero.id} size={40} />
                  </button>
                );
              })}
            </div>
          </div>

          {/* Detailed Power Breakdown */}
          <div style={{
            backgroundColor: 'rgba(15, 23, 42, 0.7)',
            borderRadius: '12px',
            padding: '14px',
            border: '1px solid rgba(255, 255, 255, 0.08)',
            display: 'flex',
            flexDirection: 'column',
            gap: '8px',
            fontSize: '12px'
          }}>
            <div style={{ fontWeight: '800', color: '#FCD34D', marginBottom: '2px' }}>
              📊 Rincian Sumber Kekuatan (CP Breakdown):
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: '#94A3B8' }}>⚔️ Pasukan Barak & Gerbang:</span>
              <strong style={{ color: '#F8FAFC' }}>
                {formatNumber((state.currentPasukan + state.garrisonedPasukan) * 150)} CP
              </strong>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: '#94A3B8' }}>👥 Komandan & Bintang:</span>
              <strong style={{ color: '#F8FAFC' }}>
                {formatNumber(state.commanders.filter((c) => c.owned).reduce((sum, c) => sum + (c.baseAtk * (c.stars || 1)), 0))} CP
              </strong>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: '#94A3B8' }}>🛡️ Ketahanan Tembok Gerbang:</span>
              <strong style={{ color: '#F8FAFC' }}>
                {formatNumber(state.maxGerbangHp * 2)} CP
              </strong>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: '#94A3B8' }}>🏛️ Balai Kota Tingkat {state.buildingLevels.town_hall || 1}:</span>
              <strong style={{ color: '#F8FAFC' }}>
                {formatNumber((state.buildingLevels.town_hall || 1) * 5000)} CP
              </strong>
            </div>
          </div>
        </div>

        <div className="modal-footer">
          <button onClick={onClose} className="btn-pixel gold" style={{ fontSize: '12px' }}>
            Simpan & Tutup
          </button>
        </div>
      </div>
    </div>
  );
}
