import React, { useState } from 'react';
import { formatNumber } from '../gameState';

// Modal Gudang Lengkap (15+ Sumber Daya & Upgrade Kapasitas)
export function GudangModal({ isOpen, onClose, state, onUpgradeGudang }) {
  if (!isOpen) return null;

  const items = [
    { name: 'Kayu Rimba', key: 'kayu', icon: '🪵', desc: 'Bahan bangunan, peralatan suku, dan api perapian.' },
    { name: 'Batu Gunung', key: 'batu', icon: '🪨', desc: 'Material pondasi rumah, perkakas batu, dan tembok benteng.' },
    { name: 'Besi Tempa', key: 'besi', icon: '⛓️', desc: 'Logam kokoh untuk senjata ksatria dan mesin peleburan.' },
    { name: 'Emas Murni', key: 'emas', icon: '🪙', desc: 'Mata uang peradaban, upeti kuil, dan perdagangan pasar.' },
    { name: 'Minyak Mentah', key: 'oil', icon: '🛢️', desc: 'Bahan bakar kendaraan dan turbin industri modern.' },
    { name: 'Daya Listrik', key: 'listrik', icon: '⚡', desc: 'Energi utama penerangan kota dan jaringan komputer.' },
    { name: 'Titanium Langka', key: 'titanium', icon: '🦾', desc: 'Logam dirgantara untuk armor robot & exosuit tempur.' },
    { name: 'Uranium Radioaktif', key: 'uranium', icon: '☢️', desc: 'Bahan bakar fisi nuklir & generator kosmis puncak.' },
    { name: 'Daging Buruan', key: 'daging', icon: '🥩', desc: 'Pangan protein tinggi menjaga stamina pemburu & prajurit.' },
    { name: 'Berry Semak', key: 'berry', icon: '🫐', desc: 'Buah manis berkhasiat untuk konsumsi dan ramuan obat.' },
    { name: 'Gandum Ladang', key: 'gandum', icon: '🌾', desc: 'Hasil panen pertanian bahan baku pembuatan roti.' },
    { name: 'Roti Panggang', key: 'roti', icon: '🍞', desc: 'Makanan olahan pokok meningkatkan efisiensi gizi.' },
    { name: 'Makanan Kalengan', key: 'kalengan', icon: '🥫', desc: 'Pangan tahan lama ransum wajib militer ekspedisi.' },
    { name: 'Ikan Laut', key: 'ikan', icon: '🐟', desc: 'Tangkapan perahu dermaga untuk variasi pangan warga.' }
  ];

  return (
    <div className="modal-backdrop">
      <div className="modal-box">
        <div className="modal-header">
          <h3><span>📦</span> Gudang Logistik Peradaban</h3>
          <button onClick={onClose} className="modal-close-btn">✕</button>
        </div>

        <div style={{
          padding: '12px 16px',
          backgroundColor: 'rgba(30, 41, 59, 0.5)',
          borderBottom: '1px solid rgba(255, 255, 255, 0.08)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between'
        }}>
          <div>
            <div style={{ fontSize: '11px', color: '#94A3B8' }}>Kapasitas Gudang (Tingkat {state.gudangLevel})</div>
            <div style={{ fontSize: '15px', fontWeight: '800', color: '#38BDF8' }}>
              Maksimal: {formatNumber(state.maxStorage)} unit
            </div>
          </div>
          <button onClick={onUpgradeGudang} className="btn-pixel gold" style={{ fontSize: '11px' }}>
            Tingkatkan Gudang (+500 Cap)
          </button>
        </div>

        <div className="modal-body">
          {items.map((it) => (
            <div key={it.key} className="resource-row">
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <span style={{ fontSize: '24px' }}>{it.icon}</span>
                <div>
                  <div style={{ fontWeight: '700', fontSize: '13px', color: '#F8FAFC' }}>{it.name}</div>
                  <div style={{ fontSize: '10px', color: '#94A3B8' }}>{it.desc}</div>
                </div>
              </div>
              <div style={{
                fontFamily: '"Press Start 2P", monospace',
                fontSize: '13px',
                color: '#FCD34D'
              }}>
                {formatNumber(state[it.key] || 0)}
              </div>
            </div>
          ))}
        </div>

        <div className="modal-footer">
          <button onClick={onClose} className="btn-pixel" style={{ fontSize: '12px' }}>Tutup</button>
        </div>
      </div>
    </div>
  );
}

// Modal Balai Pengobatan (Hospital)
export function HospitalModal({ isOpen, onClose, state, onHealAll }) {
  if (!isOpen) return null;

  const costPerTroop = 2; // 2 berry per prajurit terluka
  const neededBerry = state.woundedPasukan * costPerTroop;
  const canHeal = state.berry >= costPerTroop && state.woundedPasukan > 0;

  return (
    <div className="modal-backdrop">
      <div className="modal-box">
        <div className="modal-header">
          <h3><span>🏥</span> Balai Pengobatan (Hospital)</h3>
          <button onClick={onClose} className="modal-close-btn">✕</button>
        </div>

        <div className="modal-body" style={{ textAlign: 'center', padding: '20px' }}>
          <span style={{ fontSize: '42px' }}>🌿</span>
          <h4 style={{ fontSize: '16px', color: '#F8FAFC', marginTop: '10px' }}>Rawat Pasukan yang Gugur & Terluka</h4>
          <p style={{ fontSize: '12px', color: '#94A3B8', marginTop: '6px' }}>
            Prajurit yang tumbang saat menahan raid musuh dibawa ke mari untuk diobati menggunakan ramuan herbal Berry.
          </p>

          <div style={{
            backgroundColor: 'rgba(30, 41, 59, 0.6)',
            borderRadius: '10px',
            padding: '14px',
            margin: '16px 0',
            border: '1px solid rgba(255, 255, 255, 0.08)',
            display: 'flex',
            flexDirection: 'column',
            gap: '8px',
            fontSize: '13px'
          }}>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: '#94A3B8' }}>Prajurit Terluka:</span>
              <strong style={{ color: '#F87171' }}>{state.woundedPasukan} Prajurit</strong>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: '#94A3B8' }}>Stok Berry Tersedia:</span>
              <strong style={{ color: '#C084FC' }}>{formatNumber(state.berry)} 🧺 Berry</strong>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: '#94A3B8' }}>Biaya Pengobatan Penuh:</span>
              <strong style={{ color: '#FCD34D' }}>{neededBerry} Berry</strong>
            </div>
          </div>

          <button
            onClick={onHealAll}
            disabled={!canHeal}
            className="btn-pixel green"
            style={{ width: '100%', padding: '12px', fontSize: '13px' }}
          >
            {state.woundedPasukan === 0
              ? '✨ Semua Pasukan Dalam Kondisi Prima'
              : `Sembuhkan Semua (${neededBerry} Berry)`}
          </button>
        </div>

        <div className="modal-footer">
          <button onClick={onClose} className="btn-pixel" style={{ fontSize: '12px' }}>Tutup</button>
        </div>
      </div>
    </div>
  );
}

// Modal Penugasan Gerbang Pertahanan
export function GerbangModal({ isOpen, onClose, state, onUpdateGarrison }) {
  if (!isOpen) return null;

  const [sliderVal, setSliderVal] = useState(state.garrisonedPasukan || 0);
  const maxCap = (state.currentPasukan + state.garrisonedPasukan);

  return (
    <div className="modal-backdrop">
      <div className="modal-box">
        <div className="modal-header">
          <h3><span>🛡️</span> Penugasan Gerbang & Pertahanan</h3>
          <button onClick={onClose} className="modal-close-btn">✕</button>
        </div>

        <div className="modal-body" style={{ padding: '20px' }}>
          <div style={{
            backgroundColor: 'rgba(30, 41, 59, 0.6)',
            borderRadius: '10px',
            padding: '14px',
            border: '1px solid rgba(255, 255, 255, 0.08)',
            display: 'flex',
            flexDirection: 'column',
            gap: '8px',
            fontSize: '13px'
          }}>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: '#94A3B8' }}>Ketahanan Gerbang (HP):</span>
              <strong style={{ color: '#4ADE80' }}>{formatNumber(state.gerbangHp)} / {formatNumber(state.maxGerbangHp)}</strong>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: '#94A3B8' }}>Prajurit Aktif di Barak:</span>
              <strong style={{ color: '#60A5FA' }}>{state.currentPasukan} Prajurit</strong>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: '#94A3B8' }}>Prajurit Jaga Gerbang:</span>
              <strong style={{ color: '#FCD34D' }}>{sliderVal} Prajurit</strong>
            </div>
          </div>

          <div style={{ marginTop: '20px' }}>
            <label style={{ fontSize: '12px', color: '#94A3B8', display: 'block', marginBottom: '8px' }}>
              Geser untuk mengatur jumlah prajurit yang berjaga di gerbang:
            </label>
            <input
              type="range"
              min={0}
              max={maxCap || 1}
              value={sliderVal}
              onChange={(e) => setSliderVal(parseInt(e.target.value, 10))}
              style={{ width: '100%', cursor: 'pointer' }}
            />
            <div style={{ textAlign: 'center', marginTop: '8px', fontWeight: '800', color: '#FCD34D' }}>
              {sliderVal} Prajurit Ditugaskan
            </div>
          </div>

          <button
            onClick={() => {
              onUpdateGarrison(sliderVal);
              onClose();
            }}
            className="btn-pixel gold"
            style={{ width: '100%', marginTop: '20px', padding: '12px', fontSize: '13px' }}
          >
            Simpan Formasi Penugasan
          </button>
        </div>

        <div className="modal-footer">
          <button onClick={onClose} className="btn-pixel" style={{ fontSize: '12px' }}>Tutup</button>
        </div>
      </div>
    </div>
  );
}

// Modal Kamus Max (Deff)
export function KamusMaxModal({ isOpen, onClose, state }) {
  if (!isOpen) return null;

  return (
    <div className="modal-backdrop">
      <div className="modal-box">
        <div className="modal-header">
          <h3><span>📖</span> Kamus Max (Deff & Formula)</h3>
          <button onClick={onClose} className="modal-close-btn">✕</button>
        </div>

        <div className="modal-body" style={{ padding: '16px', display: 'flex', flexDirection: 'column', gap: '12px' }}>
          <div style={{
            backgroundColor: 'rgba(30, 41, 59, 0.6)',
            padding: '12px',
            borderRadius: '8px',
            fontSize: '12px',
            lineHeight: '1.5'
          }}>
            <h5 style={{ color: '#FCD34D', marginBottom: '4px', fontSize: '13px' }}>🛡️ Pertahanan Gerbang (Deff)</h5>
            <p style={{ color: '#94A3B8' }}>
              HP Gerbang meningkat drastis setiap peningkatan level <strong>Tembok Benteng</strong> (+250 HP/level). Prajurit yang ditugaskan di gerbang memberikan perlindungan ganda saat musuh melancarkan serangan Raid.
            </p>
          </div>

          <div style={{
            backgroundColor: 'rgba(30, 41, 59, 0.6)',
            padding: '12px',
            borderRadius: '8px',
            fontSize: '12px',
            lineHeight: '1.5'
          }}>
            <h5 style={{ color: '#38BDF8', marginBottom: '4px', fontSize: '13px' }}>⚔️ Serangan Pasukan & Komandan</h5>
            <p style={{ color: '#94A3B8' }}>
              Pahlawan dengan status bintang tinggi (⭐⭐⭐) memberikan pengali serangan pasif hingga +300% ke seluruh pasukan aktif.
            </p>
          </div>

          <div style={{
            backgroundColor: 'rgba(30, 41, 59, 0.6)',
            padding: '12px',
            borderRadius: '8px',
            fontSize: '12px',
            lineHeight: '1.5'
          }}>
            <h5 style={{ color: '#4ADE80', marginBottom: '4px', fontSize: '13px' }}>🌾 Pengelolaan Gizi & Kebahagiaan</h5>
            <p style={{ color: '#94A3B8' }}>
              Memasak Gandum menjadi Roti dan mengasapi Daging memangkas pemborosan konsumsi makanan hingga 40%, menjaga Kebahagiaan rakyat di atas 100%.
            </p>
          </div>
        </div>

        <div className="modal-footer">
          <button onClick={onClose} className="btn-pixel" style={{ fontSize: '12px' }}>Tutup</button>
        </div>
      </div>
    </div>
  );
}
