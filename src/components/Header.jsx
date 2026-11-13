import React from 'react';
import { formatNumber } from '../gameState';

export default function Header({
  state,
  zenithRate,
  onOpenGudang,
  onOpenHospital,
  onOpenGerbang,
  onOpenStats,
  onGodMode,
  onMaxAll
}) {
  return (
    <header style={{
      padding: '12px 14px',
      backgroundColor: 'rgba(15, 23, 42, 0.95)',
      backdropFilter: 'blur(12px)',
      borderBottom: '1.5px solid rgba(255, 255, 255, 0.08)',
      display: 'flex',
      flexDirection: 'column',
      gap: '10px'
    }}>
      {/* Top Row: Brand & Quick Action Controls */}
      <div style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <span style={{ fontSize: '22px' }}>🌌</span>
          <div>
            <h1 style={{
              fontSize: '15px',
              fontWeight: '900',
              letterSpacing: '0.8px',
              color: '#F8FAFC',
              lineHeight: '1.1'
            }}>
              GENESIS PIXEL
            </h1>
            <span style={{
              fontSize: '10px',
              color: '#94A3B8',
              letterSpacing: '0.5px',
              textTransform: 'uppercase'
            }}>
              {state.selectedNationality || 'Suku Primordial'} • Idle Cosmos
            </span>
          </div>
        </div>

        {/* Quick Modal Triggers & Debug Tools */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
          <button
            onClick={onOpenGerbang}
            title="Penugasan Gerbang & Pertahanan"
            className="btn-pixel"
            style={{ fontSize: '11px', padding: '4px 8px' }}
          >
            🛡️ Gerbang
          </button>
          <button
            onClick={onOpenHospital}
            title="Balai Pengobatan Pasukan Terluka"
            className="btn-pixel"
            style={{ fontSize: '11px', padding: '4px 8px' }}
          >
            🏥 {state.woundedPasukan > 0 ? `(${state.woundedPasukan})` : 'Obat'}
          </button>
          <button
            onClick={onOpenGudang}
            title="Gudang Sumber Daya Lengkap"
            className="btn-pixel"
            style={{ fontSize: '11px', padding: '4px 8px' }}
          >
            📦
          </button>
          <button
            onClick={onGodMode}
            title="God Mode: +50k Zenith, +10k Resources"
            className="btn-pixel gold"
            style={{ fontSize: '11px', padding: '4px 8px' }}
          >
            🛠️
          </button>
          <button
            onClick={onMaxAll}
            title="Buka Semua Era & Komandan Mythical"
            className="btn-pixel cyan"
            style={{ fontSize: '11px', padding: '4px 8px' }}
          >
            ⚡
          </button>
        </div>
      </div>

      {/* Hero Zenith Display Card */}
      <div style={{
        backgroundColor: 'rgba(30, 41, 59, 0.7)',
        borderRadius: '12px',
        border: '1.5px solid rgba(255, 255, 255, 0.1)',
        padding: '10px 14px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <div style={{
            width: '40px',
            height: '40px',
            borderRadius: '10px',
            backgroundColor: 'rgba(255, 179, 0, 0.15)',
            border: '1.5px solid rgba(255, 179, 0, 0.4)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontSize: '20px'
          }}>
            ✨
          </div>
          <div>
            <div style={{
              fontSize: '20px',
              fontWeight: '900',
              color: '#FFD54F',
              fontFamily: '"Press Start 2P", monospace',
              letterSpacing: '-0.5px'
            }}>
              {formatNumber(state.totalZenith)}
            </div>
            <div style={{ fontSize: '11px', color: '#4ADE80', fontWeight: '700' }}>
              +{formatNumber(zenithRate)} / detik (Pasif)
            </div>
          </div>
        </div>

        {/* Civilization Vital Pills */}
        <div style={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'flex-end',
          gap: '4px'
        }}>
          <div style={{
            fontSize: '11px',
            color: '#CBD5E1',
            backgroundColor: 'rgba(15, 23, 42, 0.6)',
            padding: '2px 8px',
            borderRadius: '12px',
            border: '1px solid rgba(255, 255, 255, 0.08)'
          }}>
            😊 Kebahagiaan: <strong style={{ color: '#FCD34D' }}>{state.happiness}%</strong>
          </div>
          <div style={{
            fontSize: '11px',
            color: '#CBD5E1',
            backgroundColor: 'rgba(15, 23, 42, 0.6)',
            padding: '2px 8px',
            borderRadius: '12px',
            border: '1px solid rgba(255, 255, 255, 0.08)'
          }}>
            ⚔️ Pasukan: <strong style={{ color: '#60A5FA' }}>{state.currentPasukan}/{state.maxPasukan}</strong>
          </div>
        </div>
      </div>

      {/* Horizontal Scrollable Resource Strip */}
      <div style={{
        display: 'flex',
        gap: '8px',
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
      gap: '5px',
      backgroundColor: 'rgba(30, 41, 59, 0.6)',
      border: '1px solid rgba(255, 255, 255, 0.08)',
      padding: '3px 8px',
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
