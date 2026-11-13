import React from 'react';
import confetti from 'canvas-confetti';
import { ALL_BUILDINGS, COSMIC_RESEARCH_TREE, COMMANDERS_DATABASE } from '../gameState';
import { sound } from '../utils/retroAudio';

export default function GodModeModal({ isOpen, onClose, state, setState }) {
  if (!isOpen) return null;

  // 1. Berkah Sumber Daya (+100k Zenith, +50k all resources, +100 tiket, +50k koin)
  const handleBlessing = () => {
    sound.playUpgrade();
    confetti({ particleCount: 80, spread: 100, origin: { y: 0.6 } });
    setState((prev) => ({
      ...prev,
      totalZenith: (prev.totalZenith || 0) + 100000,
      kayu: (prev.kayu || 0) + 50000,
      batu: (prev.batu || 0) + 50000,
      besi: (prev.besi || 0) + 50000,
      emas: (prev.emas || 0) + 50000,
      daging: (prev.daging || 0) + 50000,
      berry: (prev.berry || 0) + 50000,
      gandum: (prev.gandum || 0) + 50000,
      roti: (prev.roti || 0) + 50000,
      kalengan: (prev.kalengan || 0) + 50000,
      ikan: (prev.ikan || 0) + 50000,
      oil: (prev.oil || 0) + 50000,
      listrik: (prev.listrik || 0) + 50000,
      titanium: (prev.titanium || 0) + 50000,
      uranium: (prev.uranium || 0) + 50000,
      gachaTickets: (prev.gachaTickets || 0) + 100,
      dungeonCoins: (prev.dungeonCoins || 0) + 50000,
      gameCoins: (prev.gameCoins || 0) + 50000,
      maxStorage: Math.max(prev.maxStorage || 20000, 500000)
    }));
  };

  // 2. Max All Progres (Semua Riset & Bangunan Level Maksimum)
  const handleMaxAll = () => {
    sound.playUpgrade();
    confetti({ particleCount: 150, spread: 120, colors: ['#A855F7', '#EC4899', '#38BDF8'] });

    const allNodeIds = [
      ...ALL_BUILDINGS.map((b) => b.id),
      ...COSMIC_RESEARCH_TREE.map((r) => r.id)
    ];

    const maxLevels = {};
    ALL_BUILDINGS.forEach((b) => {
      maxLevels[b.id] = 10;
    });

    setState((prev) => ({
      ...prev,
      unlockedNodes: Array.from(new Set([...(prev.unlockedNodes || []), ...allNodeIds])),
      buildingLevels: { ...(prev.buildingLevels || {}), ...maxLevels },
      totalZenith: Math.max(prev.totalZenith || 0, 999999),
      maxStorage: 9999999
    }));
  };

  // 3. Rekrut Semua 22 Komandan Sekaligus
  const handleUnlockAllCommanders = () => {
    sound.playUpgrade();
    confetti({ particleCount: 100, spread: 90, colors: ['#F59E0B', '#FDE047', '#FFFFFF'] });

    setState((prev) => ({
      ...prev,
      commanders: COMMANDERS_DATABASE.map((cmd) => ({
        ...cmd,
        owned: true,
        level: Math.max(cmd.level || 1, 10),
        stars: 5
      }))
    }));
  };

  // 4. Tambah 5,000 Pasukan Tempur Instan & Obati Semua
  const handleInstantTroops = () => {
    sound.playClick();
    setState((prev) => ({
      ...prev,
      maxPasukan: Math.max(prev.maxPasukan || 100, (prev.currentPasukan || 0) + 5000),
      currentPasukan: (prev.currentPasukan || 0) + 5000,
      garrisonedPasukan: (prev.garrisonedPasukan || 0) + 1000,
      woundedPasukan: 0
    }));
  };

  // 5. Picu Serangan Raid Sekarang (Set Timer = 0s)
  const handleTriggerRaidNow = () => {
    sound.playAttack();
    setState((prev) => ({
      ...prev,
      raidTimer: 0
    }));
    onClose();
  };

  // 6. Reset Progres Kembali ke Awal (Zaman Primitif)
  const handleResetProgress = () => {
    if (window.confirm('Apakah Anda yakin ingin mereset seluruh progres game kembali ke awal suku primitif?')) {
      try {
        localStorage.removeItem('gameafk_save_v3');
        localStorage.removeItem('gameafk_save_v2');
        localStorage.removeItem('idle_cosmos_save');
      } catch (e) {
        console.error(e);
      }
      window.location.reload();
    }
  };

  return (
    <div className="modal-backdrop" style={{ zIndex: 9999 }}>
      <div className="modal-box" style={{ maxWidth: '480px', border: '2px solid #A855F7', boxShadow: '0 0 30px rgba(168, 85, 247, 0.4)' }}>
        {/* Header Modal */}
        <div className="modal-header" style={{ backgroundColor: 'rgba(30, 27, 75, 0.95)', borderBottom: '1.5px solid #A855F7' }}>
          <h3 style={{ color: '#E9D5FF', display: 'flex', alignItems: 'center', gap: '8px', fontSize: '15px' }}>
            <span>🛠️</span> KONSOL MODE DEWA (DEV TOOLS)
          </h3>
          <button onClick={onClose} className="modal-close-btn">✕</button>
        </div>

        {/* Body Modal */}
        <div className="modal-body" style={{ padding: '16px', display: 'flex', flexDirection: 'column', gap: '12px' }}>
          <p style={{ fontSize: '11px', color: '#CBD5E1', margin: 0, lineHeight: '1.4' }}>
            Konsol pengembang ini disediakan khusus untuk keperluan testing, verifikasi grafik, dan balancing cepat seluruh fitur peradaban.
          </p>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr', gap: '8px' }}>
            {/* Opsi 1 */}
            <button
              onClick={handleBlessing}
              className="btn-pixel gold"
              style={{ padding: '10px 14px', textAlign: 'left', display: 'flex', alignItems: 'center', justifyContent: 'space-between', fontSize: '12px' }}
            >
              <div>
                <strong>⚡ Berkah Sumber Daya Dewa</strong>
                <div style={{ fontSize: '10px', color: '#FEF08A', fontWeight: 'normal' }}>
                  +100K Zenith, +50K semua material, +100 Tiket Gacha, +50K Koin
                </div>
              </div>
              <span style={{ fontSize: '18px' }}>💰</span>
            </button>

            {/* Opsi 2 */}
            <button
              onClick={handleMaxAll}
              className="btn-pixel cyan"
              style={{ padding: '10px 14px', textAlign: 'left', display: 'flex', alignItems: 'center', justifyContent: 'space-between', fontSize: '12px' }}
            >
              <div>
                <strong>👑 Max All Progres Peradaban</strong>
                <div style={{ fontSize: '10px', color: '#BAE6FD', fontWeight: 'normal' }}>
                  Buka semua 26 riset & maksimalkan seluruh 23 bangunan ke Era Cyberpunk
                </div>
              </div>
              <span style={{ fontSize: '18px' }}>🚀</span>
            </button>

            {/* Opsi 3 */}
            <button
              onClick={handleUnlockAllCommanders}
              className="btn-pixel purple"
              style={{ padding: '10px 14px', textAlign: 'left', display: 'flex', alignItems: 'center', justifyContent: 'space-between', fontSize: '12px' }}
            >
              <div>
                <strong>👥 Rekrut Semua 22 Komandan</strong>
                <div style={{ fontSize: '10px', color: '#E9D5FF', fontWeight: 'normal' }}>
                  Buka Zeus, Athena, Arthur, Genghis, Gajah Mada, dan seluruh pahlawan
                </div>
              </div>
              <span style={{ fontSize: '18px' }}>⚔️</span>
            </button>

            {/* Opsi 4 */}
            <button
              onClick={handleInstantTroops}
              className="btn-pixel"
              style={{ padding: '10px 14px', textAlign: 'left', display: 'flex', alignItems: 'center', justifyContent: 'space-between', fontSize: '12px', backgroundColor: 'rgba(30, 41, 59, 0.9)' }}
            >
              <div>
                <strong>🛡️ Tambah 5,000 Pasukan Tempur</strong>
                <div style={{ fontSize: '10px', color: '#94A3B8', fontWeight: 'normal' }}>
                  Rekrut 5,000 prajurit instan & sembuhkan semua prajurit di RS
                </div>
              </div>
              <span style={{ fontSize: '18px' }}>🪖</span>
            </button>

            {/* Opsi 5 */}
            <button
              onClick={handleTriggerRaidNow}
              className="btn-pixel"
              style={{ padding: '10px 14px', textAlign: 'left', display: 'flex', alignItems: 'center', justifyContent: 'space-between', fontSize: '12px', backgroundColor: 'rgba(153, 27, 27, 0.4)', border: '1.5px solid #EF4444' }}
            >
              <div>
                <strong>⏱️ Picu Serangan Raid Sekarang (0 Detik)</strong>
                <div style={{ fontSize: '10px', color: '#FCA5A5', fontWeight: 'normal' }}>
                  Musuh langsung menyerang gerbang peradaban untuk uji coba pertahanan
                </div>
              </div>
              <span style={{ fontSize: '18px' }}>🔥</span>
            </button>

            {/* Opsi 6 */}
            <button
              onClick={handleResetProgress}
              className="btn-pixel"
              style={{ padding: '8px 14px', textAlign: 'left', display: 'flex', alignItems: 'center', justifyContent: 'space-between', fontSize: '11px', backgroundColor: 'rgba(15, 23, 42, 0.8)', border: '1px solid #64748B', marginTop: '6px' }}
            >
              <div>
                <strong style={{ color: '#94A3B8' }}>🔄 Reset Seluruh Progres</strong>
                <div style={{ fontSize: '9px', color: '#64748B', fontWeight: 'normal' }}>
                  Hapus save dan kembali ke zaman batu awal
                </div>
              </div>
              <span style={{ fontSize: '14px' }}>⚠️</span>
            </button>
          </div>
        </div>

        {/* Footer */}
        <div className="modal-footer" style={{ padding: '10px 16px', display: 'flex', justifyContent: 'flex-end', borderTop: '1px solid rgba(255, 255, 255, 0.08)' }}>
          <button onClick={onClose} className="btn-pixel" style={{ padding: '6px 16px', fontSize: '11px' }}>
            Tutup Konsol
          </button>
        </div>
      </div>
    </div>
  );
}
