import React from 'react';
import confetti from 'canvas-confetti';
import { formatNumber, calculateBuildingCost } from '../gameState';

export default function BuildingDetailModal({
  isOpen,
  onClose,
  building,
  state,
  onUpgradeBuilding,
  onOpenGudang,
  onOpenBarracks,
  onOpenGerbang,
  onOpenHospital,
  onOpenTavern,
  onOpenDungeon,
  onOpenMarket,
  onOpenProfile,
  onQuickHarvest
}) {
  if (!isOpen || !building) return null;

  const currentLvl = state.buildingLevels[building.id] || (state.unlockedNodes.includes(building.id) ? 1 : 0);
  const cost = calculateBuildingCost(building, currentLvl);

  let canAfford = true;
  for (const [resKey, amt] of Object.entries(cost)) {
    if ((state[resKey] || 0) < amt) {
      canAfford = false;
      break;
    }
  }

  // Choose the best image illustration for the building
  let buildingImg = '/assets/img_building_chief_hut.png';
  if (building.id === 'town_hall') buildingImg = '/assets/img_prehistoric_settlement.png';
  else if (building.id === 'barak_militer') buildingImg = '/assets/img_prehistoric_troops.png';
  else if (building.id === 'tembok') buildingImg = '/assets/img_era_kerajaan.png';
  else if (building.id === 'dungeon') buildingImg = '/assets/img_era_cyberpunk.png';
  else if (building.id === 'market') buildingImg = '/assets/ic_fil_perdagangan.png';
  else if (building.id === 'hospital') buildingImg = '/assets/img_campfire_stones.png';
  else if (building.id === 'temple') buildingImg = '/assets/ic_fil_teokrasi.png';

  // Specific Functional Action Handler
  const handleSpecialAction = () => {
    onClose();
    if (building.id === 'gudang') onOpenGudang();
    else if (building.id === 'barak_militer') onOpenBarracks();
    else if (building.id === 'tembok') onOpenGerbang();
    else if (building.id === 'hospital') onOpenHospital();
    else if (building.id === 'tavern') onOpenTavern();
    else if (building.id === 'dungeon') onOpenDungeon();
    else if (building.id === 'market') onOpenMarket();
    else if (building.id === 'town_hall') onOpenProfile();
    else {
      // Resource producer quick harvest
      onQuickHarvest(building);
      confetti({ particleCount: 30, spread: 60 });
    }
  };

  // Specific Action Button Label & Icon
  let actionLabel = '⚡ Panen Ekstra Cepat (+50 Bahan)';
  let actionIcon = '🌾';
  if (building.id === 'gudang') { actionLabel = 'Buka Gudang Sumber Daya'; actionIcon = '📦'; }
  else if (building.id === 'barak_militer') { actionLabel = 'Rekrut & Latih Pasukan Purba'; actionIcon = '⚔️'; }
  else if (building.id === 'tembok') { actionLabel = 'Atur Penugasan Prajurit & Pahlawan Gerbang'; actionIcon = '🛡️'; }
  else if (building.id === 'hospital') { actionLabel = 'Masuk Balai Pengobatan (Rawat Pasukan)'; actionIcon = '🏥'; }
  else if (building.id === 'tavern') { actionLabel = 'Masuk Kedai & Tarik Pahlawan (Gacha)'; actionIcon = '🍻'; }
  else if (building.id === 'dungeon') { actionLabel = 'Masuk Arena Pertarungan Dungeon'; actionIcon = '🌀'; }
  else if (building.id === 'market') { actionLabel = 'Buka Kios Pasar & Toko Koin'; actionIcon = '🏪'; }
  else if (building.id === 'temple') { actionLabel = 'Buka Doktrin Agama & Berkah Kuil'; actionIcon = '⛪'; }
  else if (building.id === 'town_hall') { actionLabel = 'Buka Profil Penguasa & Kedaulatan Bangsa'; actionIcon = '👑'; }

  return (
    <div className="modal-backdrop">
      <div className="modal-box" style={{ maxWidth: '620px', padding: '0', overflow: 'hidden' }}>
        {/* Top Bar */}
        <div className="modal-header" style={{ backgroundColor: 'rgba(15, 23, 42, 0.95)' }}>
          <h3 style={{ fontSize: '15px', color: '#F8FAFC', display: 'flex', alignItems: 'center', gap: '8px', margin: 0 }}>
            <span>{building.icon}</span> {building.name}
          </h3>
          <button onClick={onClose} className="modal-close-btn">✕</button>
        </div>

        {/* RPG Split Card Layout */}
        <div style={{
          display: 'grid',
          gridTemplateColumns: '1fr 1.3fr',
          backgroundColor: '#070A14'
        }}>
          {/* Left Column: Portrait & Stats */}
          <div style={{
            padding: '20px',
            backgroundColor: 'rgba(15, 23, 42, 0.7)',
            borderRight: '1.5px solid rgba(255, 255, 255, 0.08)',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            textAlign: 'center',
            gap: '12px'
          }}>
            {/* Building Photo / Illustration Frame */}
            <div style={{
              position: 'relative',
              width: '100%',
              aspectRatio: '1 / 1',
              borderRadius: '14px',
              overflow: 'hidden',
              border: '2px solid #FFD54F',
              boxShadow: '0 0 25px rgba(255, 213, 79, 0.25)',
              backgroundColor: '#1E293B'
            }}>
              <img
                src={buildingImg}
                alt={building.name}
                style={{
                  width: '100%',
                  height: '100%',
                  objectFit: 'cover',
                  display: 'block'
                }}
              />
              <div style={{
                position: 'absolute',
                top: '8px',
                right: '8px',
                backgroundColor: 'rgba(15, 23, 42, 0.85)',
                padding: '2px 8px',
                borderRadius: '8px',
                border: '1px solid #FFD54F',
                color: '#FFD54F',
                fontSize: '11px',
                fontWeight: '900',
                fontFamily: '"Press Start 2P", monospace'
              }}>
                Lv. {currentLvl}
              </div>
            </div>

            {/* Output Stat Badge */}
            <div style={{
              width: '100%',
              backgroundColor: 'rgba(30, 41, 59, 0.8)',
              padding: '8px',
              borderRadius: '8px',
              border: '1px solid rgba(255, 255, 255, 0.08)'
            }}>
              <div style={{ fontSize: '10px', color: '#94A3B8' }}>Produksi Pasif:</div>
              <div style={{ fontSize: '12px', fontWeight: '800', color: '#4ADE80', marginTop: '2px' }}>
                +{formatNumber(building.zenithPerSec * Math.max(1, currentLvl))} Zenith/s
              </div>
              {building.produces && (
                <div style={{ fontSize: '12px', fontWeight: '800', color: '#38BDF8', marginTop: '2px' }}>
                  +{formatNumber(building.ratePerSec * Math.max(1, currentLvl))} {building.produces}/s
                </div>
              )}
            </div>
          </div>

          {/* Right Column: Lore, Function & Upgrade */}
          <div style={{
            padding: '20px',
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'space-between',
            gap: '14px'
          }}>
            <div>
              <div style={{
                fontSize: '11px',
                color: '#FFD54F',
                fontWeight: '800',
                textTransform: 'uppercase',
                letterSpacing: '0.5px'
              }}>
                Struktur Peradaban
              </div>
              <h4 style={{ fontSize: '18px', fontWeight: '900', color: '#F8FAFC', margin: '4px 0 0 0' }}>
                {building.name}
              </h4>
              <p style={{ fontSize: '12px', color: '#CBD5E1', lineHeight: '1.5', marginTop: '6px' }}>
                {building.desc}
              </p>
            </div>

            {/* SPECIAL INTERACTIVE RPG ACTION BUTTON */}
            <div style={{
              backgroundColor: 'rgba(30, 41, 59, 0.7)',
              padding: '12px',
              borderRadius: '10px',
              border: '1.5px solid rgba(56, 189, 248, 0.3)'
            }}>
              <div style={{ fontSize: '10px', color: '#38BDF8', fontWeight: '800', textTransform: 'uppercase', marginBottom: '6px' }}>
                ⚡ FUNGSI KHUSUS BANGUNAN
              </div>
              <button
                onClick={handleSpecialAction}
                className="btn-pixel cyan"
                style={{ width: '100%', padding: '10px', fontSize: '12px', fontWeight: '800' }}
              >
                <span>{actionIcon}</span> {actionLabel}
              </button>
            </div>

            {/* UPGRADE SECTION */}
            <div style={{
              backgroundColor: 'rgba(15, 23, 42, 0.8)',
              padding: '12px',
              borderRadius: '10px',
              border: '1px solid rgba(255, 255, 255, 0.08)'
            }}>
              <div style={{ fontSize: '11px', color: '#94A3B8', marginBottom: '6px' }}>
                Biaya Peningkatan ke Tingkat <strong>Lv. {currentLvl + 1}</strong>:
              </div>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px', marginBottom: '10px' }}>
                {Object.entries(cost).map(([resKey, amt]) => {
                  const hasEnough = (state[resKey] || 0) >= amt;
                  return (
                    <span
                      key={resKey}
                      style={{
                        fontSize: '11px',
                        padding: '2px 8px',
                        borderRadius: '6px',
                        backgroundColor: hasEnough ? 'rgba(34, 197, 94, 0.2)' : 'rgba(239, 68, 68, 0.2)',
                        color: hasEnough ? '#4ADE80' : '#F87171',
                        fontWeight: '700'
                      }}
                    >
                      {resKey}: {formatNumber(amt)} ({formatNumber(state[resKey] || 0)})
                    </span>
                  );
                })}
              </div>

              <button
                onClick={() => {
                  onUpgradeBuilding(building, cost);
                  confetti({ particleCount: 30, spread: 60 });
                }}
                disabled={!canAfford}
                className={`btn-pixel ${canAfford ? 'gold' : ''}`}
                style={{ width: '100%', padding: '10px', fontSize: '12px' }}
              >
                🔨 Tingkatkan Bangunan
              </button>
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="modal-footer" style={{ backgroundColor: 'rgba(15, 23, 42, 0.95)' }}>
          <button onClick={onClose} className="btn-pixel" style={{ fontSize: '12px' }}>
            Kembali ke Peta
          </button>
        </div>
      </div>
    </div>
  );
}
