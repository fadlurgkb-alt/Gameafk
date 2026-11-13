import React from 'react';
import { formatNumber, calculateCombatPower } from '../gameState';

export default function Header({
  state,
  zenithRate,
  onOpenGudang,
  onOpenHospital,
  onOpenGerbang,
  onOpenProfile,
  onGodMode,
  onMaxAll
}) {
  const totalCP = calculateCombatPower(state);
  const activeAvatar = state.commanders.find((c) => c.id === state.avatarId)
    || state.commanders[0];

  return (
    <header style={{
      padding: '10px 14px',
      backgroundColor: 'rgba(11, 15, 25, 0.96)',
      backdropFilter: 'blur(12px)',
      borderBottom: '1.5px solid rgba(255, 255, 255, 0.08)',
      display: 'flex',
      flexDirection: 'column',
      gap: '8px'
    }}>
      {/* Top Bar: Profile, Combat Power, Debug Controls */}
      <div style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between'
      }}>
        {/* Clickable Profile Card */}
        <div
          onClick={onOpenProfile}
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '10px',
            cursor: 'pointer',
            padding: '4px 8px',
            borderRadius: '10px',
            backgroundColor: 'rgba(30, 41, 59, 0.6)',
            border: '1px solid rgba(255, 255, 255, 0.1)',
            transition: 'background-color 0.15s ease'
          }}
          title="Buka Profil Penguasa & Ganti Avatar"
        >
          <div style={{
            width: '36px',
            height: '36px',
            borderRadius: '50%',
            backgroundColor: activeAvatar.color || '#3B82F6',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontSize: '18px',
            border: '2px solid #FFD54F',
            boxShadow: '0 0 10px rgba(255, 213, 79, 0.3)'
          }}>
            {activeAvatar.icon}
          </div>
          <div>
            <div style={{ fontSize: '13px', fontWeight: '900', color: '#F8FAFC', lineHeight: '1.1' }}>
              {state.playerName}
            </div>
            <div style={{
              fontSize: '11px',
              fontWeight: '900',
              color: '#FFD54F',
              fontFamily: '"Press Start 2P", monospace',
              marginTop: '2px'
            }}>
              👑 {formatNumber(totalCP)} CP
            </div>
          </div>
        </div>

        {/* Action Buttons */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
          <button
            onClick={onOpenGerbang}
            title="Penugasan Gerbang & Pertahanan"
            className="btn-pixel"
            style={{ fontSize: '10px', padding: '4px 7px' }}
          >
            🛡️ Gerbang ({state.garrisonedPasukan})
          </button>
          <button
            onClick={onOpenHospital}
            title="Balai Pengobatan Pasukan Terluka"
            className="btn-pixel"
            style={{ fontSize: '10px', padding: '4px 7px' }}
          >
            🏥 {state.woundedPasukan > 0 ? `(${state.woundedPasukan})` : 'Obat'}
          </button>
          <button
            onClick={onOpenGudang}
            title="Gudang Sumber Daya Lengkap"
            className="btn-pixel"
            style={{ fontSize: '10px', padding: '4px 7px' }}
          >
            📦
          </button>
          <button
            onClick={onGodMode}
            title="God Mode: +50k Zenith, +10k Resources"
            className="btn-pixel gold"
            style={{ fontSize: '10px', padding: '4px 6px' }}
          >
            🛠️
          </button>
          <button
            onClick={onMaxAll}
            title="Buka Semua Era & Komandan Mythical"
            className="btn-pixel cyan"
            style={{ fontSize: '10px', padding: '4px 6px' }}
          >
            ⚡
          </button>
        </div>
      </div>

      {/* Middle Row: Zenith & Troops Status Banner */}
      <div style={{
        backgroundColor: 'rgba(30, 41, 59, 0.7)',
        borderRadius: '10px',
        border: '1.5px solid rgba(255, 255, 255, 0.1)',
        padding: '8px 12px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between'
      }}>
        {/* Zenith counter */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <span style={{ fontSize: '20px' }}>✨</span>
          <div>
            <div style={{
              fontSize: '18px',
              fontWeight: '900',
              color: '#FFD54F',
              fontFamily: '"Press Start 2P", monospace',
              letterSpacing: '-0.5px'
            }}>
              {formatNumber(state.totalZenith)}
            </div>
            <div style={{ fontSize: '10px', color: '#4ADE80', fontWeight: '700' }}>
              +{formatNumber(zenithRate)} / detik
            </div>
          </div>
        </div>

        {/* Pasukan status pill */}
        <div style={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'flex-end',
          gap: '2px',
          fontSize: '11px'
        }}>
          <div style={{ color: '#CBD5E1' }}>
            ⚔️ Barak: <strong style={{ color: '#60A5FA' }}>{state.currentPasukan}/{state.maxPasukan}</strong>
          </div>
          <div style={{ color: '#94A3B8', fontSize: '10px' }}>
            🛡️ Gerbang: <strong style={{ color: '#FCD34D' }}>{state.garrisonedPasukan}</strong> • 🏥 Sakit: <strong style={{ color: '#F87171' }}>{state.woundedPasukan}</strong>
          </div>
        </div>
      </div>

      {/* Horizontal Resource Strip */}
      <div style={{
        display: 'flex',
        gap: '6px',
        overflowX: 'auto',
        paddingBottom: '2px'
      }}>
        <ResourceBadge icon="🪨" label="Batu" val={state.batu} color="#94A3B8" />
        <ResourceBadge icon="🪵" label="Kayu" val={state.kayu} color="#A1887F" />
        <ResourceBadge icon="⛓️" label="Besi" val={state.besi} color="#90CAF9" />
        <ResourceBadge icon="🪙" label="Emas" val={state.emas} color="#FBBF24" />
        <ResourceBadge icon="🥩" label="Daging" val={state.daging} color="#F87171" />
        <ResourceBadge icon="🫐" label="Berry" val={state.berry} color="#C084FC" />
        <ResourceBadge icon="🌾" label="Gandum" val={state.gandum} color="#FDE047" />
        <ResourceBadge icon="🍞" label="Roti" val={state.roti} color="#FB923C" />
        <ResourceBadge icon="🛢️" label="Minyak" val={state.oil} color="#64748B" />
        <ResourceBadge icon="⚡" label="Listrik" val={state.listrik} color="#38BDF8" />
        <ResourceBadge icon="🦾" label="Titanium" val={state.titanium} color="#A7F3D0" />
        <ResourceBadge icon="☢️" label="Uranium" val={state.uranium} color="#86EFAC" />
      </div>
    </header>
  );
}

function ResourceBadge({ icon, label, val, color }) {
  return (
    <div style={{
      display: 'inline-flex',
      alignItems: 'center',
      gap: '4px',
      backgroundColor: 'rgba(30, 41, 59, 0.6)',
      border: '1px solid rgba(255, 255, 255, 0.08)',
      padding: '3px 7px',
      borderRadius: '6px',
      fontSize: '11px',
      whiteSpace: 'nowrap'
    }}>
      <span>{icon}</span>
      <span style={{ color: '#94A3B8' }}>{label}:</span>
      <span style={{ fontWeight: '800', color: color || '#F8FAFC' }}>
        {formatNumber(val)}
      </span>
    </div>
  );
}
