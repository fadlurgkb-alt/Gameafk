import React from 'react';
import confetti from 'canvas-confetti';
import {
  ALL_BUILDINGS,
  ALL_RESEARCH,
  ALL_PHILOSOPHIES,
  DUNGEON_MONSTERS,
  formatNumber,
  calculateBuildingCost
} from '../gameState';

// Panel 1: Bangunan (23 Bangunan Asli)
export function BuildingsPanel({ state, onUpgradeBuilding }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
      <div style={{
        fontSize: '12px',
        fontWeight: '800',
        color: '#94A3B8',
        textTransform: 'uppercase',
        letterSpacing: '0.5px'
      }}>
        Daftar Bangunan Peradaban ({ALL_BUILDINGS.length} Bangunan)
      </div>

      {ALL_BUILDINGS.map((b) => {
        const currentLvl = state.buildingLevels[b.id] || (state.unlockedNodes.includes(b.id) ? 1 : 0);
        const cost = calculateBuildingCost(b, currentLvl);
        const isUnlocked = state.unlockedNodes.includes(b.id);

        // Check if player has enough resources
        let canAfford = true;
        for (const [resKey, amount] of Object.entries(cost)) {
          if ((state[resKey] || 0) < amount) {
            canAfford = false;
            break;
          }
        }

        return (
          <div
            key={b.id}
            style={{
              backgroundColor: isUnlocked ? 'rgba(30, 41, 59, 0.7)' : 'rgba(15, 23, 42, 0.6)',
              border: `1.5px solid ${isUnlocked ? 'rgba(255, 255, 255, 0.1)' : 'rgba(255, 255, 255, 0.04)'}`,
              borderRadius: '12px',
              padding: '12px 14px',
              display: 'flex',
              flexDirection: 'column',
              gap: '8px',
              opacity: isUnlocked ? 1 : 0.65
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <span style={{
                  fontSize: '22px',
                  width: '38px',
                  height: '38px',
                  borderRadius: '10px',
                  backgroundColor: 'rgba(15, 23, 42, 0.8)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  border: '1px solid rgba(255, 255, 255, 0.08)'
                }}>
                  {b.icon}
                </span>
                <div>
                  <div style={{ fontWeight: '800', fontSize: '13px', color: '#F8FAFC' }}>
                    {b.name}
                  </div>
                  <div style={{ fontSize: '11px', color: '#4ADE80', fontWeight: '700' }}>
                    +{formatNumber(b.zenithPerSec * Math.max(1, currentLvl))} Zenith/s
                    {b.produces && (
                      <span style={{ color: '#38BDF8', marginLeft: '6px' }}>
                        +{formatNumber(b.ratePerSec * Math.max(1, currentLvl))} {b.produces}/s
                      </span>
                    )}
                  </div>
                </div>
              </div>

              <div style={{
                backgroundColor: 'rgba(15, 23, 42, 0.8)',
                padding: '3px 8px',
                borderRadius: '12px',
                border: '1px solid #FFD54F',
                fontSize: '11px',
                fontWeight: '800',
                color: '#FFD54F'
              }}>
                Lv. {currentLvl}
              </div>
            </div>

            <p style={{ fontSize: '11px', color: '#94A3B8', margin: 0 }}>
              {b.desc}
            </p>

            <div style={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              paddingTop: '6px',
              borderTop: '1px solid rgba(255, 255, 255, 0.06)'
            }}>
              <div style={{ display: 'flex', gap: '8px', fontSize: '11px' }}>
                <span style={{ color: '#94A3B8' }}>Biaya:</span>
                {Object.entries(cost).map(([resKey, amount]) => {
                  const hasEnough = (state[resKey] || 0) >= amount;
                  return (
                    <span key={resKey} style={{ color: hasEnough ? '#FCD34D' : '#F87171', fontWeight: '700' }}>
                      {resKey}: {formatNumber(amount)}
                    </span>
                  );
                })}
              </div>

              <button
                onClick={() => onUpgradeBuilding(b, cost)}
                disabled={!canAfford}
                className={`btn-pixel ${canAfford ? 'gold' : ''}`}
                style={{ fontSize: '11px', padding: '6px 12px' }}
              >
                {currentLvl === 0 ? 'Bangun' : 'Tingkatkan'}
              </button>
            </div>
          </div>
        );
      })}
    </div>
  );
}

// Panel 2: Riset (26 Cabang Asli)
export function ResearchPanel({ state, onUnlockResearch }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
      <div style={{
        fontSize: '12px',
        fontWeight: '800',
        color: '#94A3B8',
        textTransform: 'uppercase',
        letterSpacing: '0.5px'
      }}>
        Pohon Riset Teknologi & Kognitif ({ALL_RESEARCH.length} Cabang)
      </div>

      {ALL_RESEARCH.map((r) => {
        const isUnlocked = state.unlockedNodes.includes(r.id);
        const reqsMet = r.reqs.every((req) => state.unlockedNodes.includes(req));
        const canAfford = state.totalZenith >= r.costZenith && reqsMet && !isUnlocked;

        return (
          <div
            key={r.id}
            style={{
              backgroundColor: isUnlocked ? 'rgba(30, 41, 59, 0.7)' : 'rgba(15, 23, 42, 0.6)',
              border: `1.5px solid ${isUnlocked ? 'rgba(74, 222, 128, 0.4)' : 'rgba(255, 255, 255, 0.06)'}`,
              borderRadius: '12px',
              padding: '12px 14px',
              display: 'flex',
              flexDirection: 'column',
              gap: '6px',
              opacity: isUnlocked || reqsMet ? 1 : 0.5
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <span style={{ fontSize: '20px' }}>{r.icon}</span>
                <span style={{ fontWeight: '800', fontSize: '13px', color: '#F8FAFC' }}>
                  {r.name}
                </span>
                <span style={{
                  fontSize: '10px',
                  backgroundColor: 'rgba(56, 189, 248, 0.2)',
                  color: '#38BDF8',
                  padding: '1px 6px',
                  borderRadius: '4px'
                }}>
                  Tier {r.tier}
                </span>
              </div>

              {isUnlocked && (
                <span style={{ color: '#4ADE80', fontWeight: '800', fontSize: '11px' }}>
                  ✓ Diteliti
                </span>
              )}
            </div>

            <p style={{ fontSize: '11px', color: '#94A3B8', margin: 0 }}>
              {r.desc}
            </p>

            {!isUnlocked && (
              <div style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                paddingTop: '6px',
                borderTop: '1px solid rgba(255, 255, 255, 0.06)'
              }}>
                <div style={{ fontSize: '11px', color: state.totalZenith >= r.costZenith ? '#FCD34D' : '#F87171' }}>
                  Biaya: ✨ {formatNumber(r.costZenith)} Zenith
                  {!reqsMet && <span style={{ color: '#F87171', marginLeft: '6px' }}>(Prasyarat belum)</span>}
                </div>

                <button
                  onClick={() => onUnlockResearch(r)}
                  disabled={!canAfford}
                  className={`btn-pixel ${canAfford ? 'cyan' : ''}`}
                  style={{ fontSize: '11px', padding: '5px 12px' }}
                >
                  Teliti Riset
                </button>
              </div>
            )}
          </div>
        );
      })}
    </div>
  );
}

// Panel 3: Filsafat & Ideologi (10 Mazhab)
export function PhilosophyPanel({ state, onAdoptPhilosophy }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
      <div style={{
        fontSize: '12px',
        fontWeight: '800',
        color: '#94A3B8',
        textTransform: 'uppercase',
        letterSpacing: '0.5px'
      }}>
        10 Mazhab Filsafat & Doktrin Negara
      </div>

      {ALL_PHILOSOPHIES.map((p) => {
        const isAdopted = state.unlockedNodes.includes(p.id);
        const canAfford = state.totalZenith >= p.costZenith && !isAdopted;

        return (
          <div
            key={p.id}
            style={{
              backgroundColor: isAdopted ? 'rgba(76, 29, 149, 0.3)' : 'rgba(15, 23, 42, 0.6)',
              border: `1.5px solid ${isAdopted ? '#A855F7' : 'rgba(255, 255, 255, 0.06)'}`,
              borderRadius: '12px',
              padding: '12px 14px',
              display: 'flex',
              flexDirection: 'column',
              gap: '6px'
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <span style={{ fontSize: '20px' }}>{p.icon}</span>
                <span style={{ fontWeight: '800', fontSize: '13px', color: '#F8FAFC' }}>
                  {p.name}
                </span>
              </div>
              {isAdopted && (
                <span style={{ color: '#C084FC', fontWeight: '800', fontSize: '11px' }}>
                  ✓ Doktrin Aktif
                </span>
              )}
            </div>

            <div style={{ fontSize: '11px', color: '#4ADE80', fontWeight: '700' }}>
              Efek: {p.effect}
            </div>

            {!isAdopted && (
              <div style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                paddingTop: '6px',
                borderTop: '1px solid rgba(255, 255, 255, 0.06)'
              }}>
                <span style={{ fontSize: '11px', color: state.totalZenith >= p.costZenith ? '#FCD34D' : '#F87171' }}>
                  Biaya: ✨ {formatNumber(p.costZenith)} Zenith
                </span>
                <button
                  onClick={() => onAdoptPhilosophy(p)}
                  disabled={!canAfford}
                  className={`btn-pixel ${canAfford ? 'purple' : ''}`}
                  style={{ fontSize: '11px', padding: '5px 12px' }}
                >
                  Terapkan Doktrin
                </button>
              </div>
            )}
          </div>
        );
      })}
    </div>
  );
}

// Panel 4: Komandan & Pahlawan
export function CommandersPanel({ state, onGachaRecruit }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
      <div style={{
        backgroundColor: 'rgba(30, 41, 59, 0.8)',
        borderRadius: '12px',
        padding: '14px',
        border: '1.5px solid rgba(255, 179, 0, 0.3)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between'
      }}>
        <div>
          <h4 style={{ fontSize: '14px', color: '#FFD54F', margin: 0 }}>Kedai Pahlawan (Tavern)</h4>
          <p style={{ fontSize: '11px', color: '#94A3B8', margin: '4px 0 0 0' }}>
            Tiket Gacha: <strong>{state.gachaTickets}</strong> Tiket
          </p>
        </div>
        <button
          onClick={onGachaRecruit}
          className="btn-pixel gold"
          style={{ fontSize: '12px', padding: '8px 14px' }}
        >
          🎲 Rekrut Pahlawan
        </button>
      </div>

      <div style={{
        fontSize: '12px',
        fontWeight: '800',
        color: '#94A3B8',
        textTransform: 'uppercase'
      }}>
        Daftar Pahlawan & Tokoh Bangsa
      </div>

      {state.commanders.map((cmd) => (
        <div
          key={cmd.id}
          style={{
            backgroundColor: cmd.owned ? 'rgba(30, 41, 59, 0.7)' : 'rgba(15, 23, 42, 0.5)',
            border: `1.5px solid ${cmd.owned ? 'rgba(255, 255, 255, 0.12)' : 'rgba(255, 255, 255, 0.04)'}`,
            borderRadius: '12px',
            padding: '12px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            opacity: cmd.owned ? 1 : 0.55
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <span style={{ fontSize: '28px' }}>{cmd.icon}</span>
            <div>
              <div style={{ fontWeight: '800', fontSize: '13px', color: '#F8FAFC' }}>
                {cmd.name}
              </div>
              <div style={{ fontSize: '11px', color: '#4ADE80', fontWeight: '700' }}>
                {cmd.bonus}
              </div>
              <div style={{ fontSize: '10px', color: '#FCD34D' }}>
                {'⭐'.repeat(cmd.stars || 1)} • Rarity: {cmd.rarity}
              </div>
            </div>
          </div>

          <div>
            {cmd.owned ? (
              <span style={{
                backgroundColor: 'rgba(34, 197, 94, 0.2)',
                color: '#4ADE80',
                padding: '4px 10px',
                borderRadius: '6px',
                fontSize: '11px',
                fontWeight: '800'
              }}>
                Aktif (Lv. {cmd.level})
              </span>
            ) : (
              <span style={{
                backgroundColor: 'rgba(100, 116, 139, 0.2)',
                color: '#94A3B8',
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
  );
}

// Panel 5: Dungeon & Raid (Pertempuran & Bos)
export function DungeonRaidPanel({ state, onAttackMonster, onDefendRaid, onToggleAutoFarm }) {
  const currentMonster = DUNGEON_MONSTERS.find((m) => m.level === state.currentDungeonLevel)
    || DUNGEON_MONSTERS[0];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
      {/* Raid Banner */}
      <div style={{
        backgroundColor: 'rgba(220, 38, 38, 0.15)',
        border: '1.5px solid #EF4444',
        borderRadius: '12px',
        padding: '14px',
        display: 'flex',
        flexDirection: 'column',
        gap: '8px'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <span style={{ fontWeight: '800', fontSize: '14px', color: '#FCA5A5', display: 'flex', alignItems: 'center', gap: '6px' }}>
            <span>⚔️</span> Serangan Raid Musuh (Gelombang #{state.raidLevel})
          </span>
          <span style={{
            fontFamily: '"Press Start 2P", monospace',
            fontSize: '11px',
            color: '#FCD34D'
          }}>
            ⏱️ {state.raidTimer}s
          </span>
        </div>
        <p style={{ fontSize: '11px', color: '#CBD5E1', margin: 0 }}>
          Musuh sedang mendekati gerbang kota! Siapkan pasukan jaga dan pahlawan untuk menangkis serbuan.
        </p>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '4px' }}>
          <span style={{ fontSize: '11px', color: '#94A3B8' }}>
            Prajurit Gerbang: <strong>{state.garrisonedPasukan} Prajurit</strong>
          </span>
          <button onClick={onDefendRaid} className="btn-pixel gold" style={{ fontSize: '11px', padding: '6px 12px' }}>
            🛡️ Hadang Raid (+{formatNumber(state.gameCoins + 50)} Koin)
          </button>
        </div>
      </div>

      {/* Dungeon Section */}
      <div style={{
        backgroundColor: 'rgba(30, 41, 59, 0.7)',
        border: '1.5px solid rgba(255, 255, 255, 0.1)',
        borderRadius: '12px',
        padding: '16px',
        display: 'flex',
        flexDirection: 'column',
        gap: '12px',
        textAlign: 'center'
      }}>
        <span style={{ fontSize: '36px' }}>{currentMonster.icon}</span>
        <div>
          <h4 style={{ fontSize: '15px', color: '#F8FAFC', margin: 0 }}>
            {currentMonster.name} (Lantai {state.currentDungeonLevel})
          </h4>
          <span style={{ fontSize: '11px', color: currentMonster.isBoss ? '#EF4444' : '#38BDF8', fontWeight: '800' }}>
            {currentMonster.isBoss ? '👑 ELITE BOS DUNGEON' : 'Monster Liar'}
          </span>
        </div>

        {/* Monster HP Bar */}
        <div>
          <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '11px', marginBottom: '4px' }}>
            <span style={{ color: '#94A3B8' }}>HP Monster:</span>
            <strong style={{ color: '#F87171' }}>
              {formatNumber(state.monsterHp)} / {formatNumber(state.monsterMaxHp)}
            </strong>
          </div>
          <div style={{
            height: '10px',
            backgroundColor: 'rgba(15, 23, 42, 0.8)',
            borderRadius: '5px',
            overflow: 'hidden'
          }}>
            <div style={{
              height: '100%',
              width: `${Math.max(0, Math.min(100, (state.monsterHp / state.monsterMaxHp) * 100))}%`,
              backgroundColor: '#EF4444',
              transition: 'width 0.2s ease'
            }} />
          </div>
        </div>

        <div style={{ display: 'flex', gap: '8px' }}>
          <button
            onClick={onAttackMonster}
            className="btn-pixel gold"
            style={{ flex: 1, padding: '10px', fontSize: '13px' }}
          >
            ⚔️ Serang Bos (-{formatNumber(state.damage * 10)} HP)
          </button>
          <button
            onClick={onToggleAutoFarm}
            className={`btn-pixel ${state.autoFarmDungeon ? 'green' : ''}`}
            style={{ fontSize: '12px', padding: '10px 14px' }}
          >
            {state.autoFarmDungeon ? '🤖 Auto-Farm (ON)' : 'Auto-Farm (OFF)'}
          </button>
        </div>

        <div style={{ fontSize: '11px', color: '#94A3B8' }}>
          Koin Dungeon Terkumpul: <strong style={{ color: '#FCD34D' }}>{formatNumber(state.dungeonCoins)} 🪙</strong>
        </div>
      </div>
    </div>
  );
}

// Panel 6: Pasar (Market) Barter Komoditas
export function MarketPanel({ state, onTrade }) {
  const trades = [
    { from: 'kayu', fromQty: 100, to: 'besi', toQty: 25, label: 'Tukar 100 Kayu ➔ 25 Besi' },
    { from: 'batu', fromQty: 100, to: 'emas', toQty: 10, label: 'Tukar 100 Batu ➔ 10 Emas' },
    { from: 'daging', fromQty: 50, to: 'totalZenith', toQty: 500, label: 'Jual 50 Daging ➔ 500 Zenith' },
    { from: 'gandum', fromQty: 80, to: 'roti', toQty: 40, label: 'Giling 80 Gandum ➔ 40 Roti' }
  ];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
      <div style={{
        fontSize: '12px',
        fontWeight: '800',
        color: '#94A3B8',
        textTransform: 'uppercase'
      }}>
        Pasar Komoditas & Barter Peradaban
      </div>

      {trades.map((tr, idx) => {
        const canTrade = (state[tr.from] || 0) >= tr.fromQty;

        return (
          <div
            key={idx}
            style={{
              backgroundColor: 'rgba(30, 41, 59, 0.7)',
              border: '1.5px solid rgba(255, 255, 255, 0.08)',
              borderRadius: '12px',
              padding: '12px',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between'
            }}
          >
            <div>
              <div style={{ fontWeight: '800', fontSize: '13px', color: '#F8FAFC' }}>
                {tr.label}
              </div>
              <div style={{ fontSize: '11px', color: canTrade ? '#4ADE80' : '#F87171' }}>
                Tersedia: {formatNumber(state[tr.from] || 0)} / {tr.fromQty}
              </div>
            </div>

            <button
              onClick={() => onTrade(tr)}
              disabled={!canTrade}
              className={`btn-pixel ${canTrade ? 'gold' : ''}`}
              style={{ fontSize: '11px', padding: '6px 12px' }}
            >
              Tukar
            </button>
          </div>
        );
      })}
    </div>
  );
}
