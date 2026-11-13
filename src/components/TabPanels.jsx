import React, { useState } from 'react';
import confetti from 'canvas-confetti';
import {
  ALL_BUILDINGS,
  ALL_PHILOSOPHIES,
  ARMORY_WEAPONS,
  formatNumber,
  calculateBuildingCost
} from '../gameState';

import TalentTreeCanvas from './TalentTreeCanvas';
import DungeonArena from './DungeonArena';

// Panel 1: Bangunan (23 Bangunan)
export function BuildingsPanel({ state, onUpgradeBuilding, onSelectBuilding }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
      <div style={{ fontSize: '12px', fontWeight: '800', color: '#94A3B8', textTransform: 'uppercase' }}>
        Daftar Bangunan Peradaban ({ALL_BUILDINGS.length} Bangunan) • Klik untuk fungsi RPG
      </div>

      {ALL_BUILDINGS.map((b) => {
        const currentLvl = state.buildingLevels[b.id] || (state.unlockedNodes.includes(b.id) ? 1 : 0);
        const cost = calculateBuildingCost(b, currentLvl);
        const isUnlocked = state.unlockedNodes.includes(b.id);

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
            onClick={() => onSelectBuilding(b)}
            style={{
              backgroundColor: isUnlocked ? 'rgba(30, 41, 59, 0.7)' : 'rgba(15, 23, 42, 0.6)',
              border: `1.5px solid ${isUnlocked ? 'rgba(255, 255, 255, 0.1)' : 'rgba(255, 255, 255, 0.04)'}`,
              borderRadius: '12px',
              padding: '12px 14px',
              display: 'flex',
              flexDirection: 'column',
              gap: '8px',
              opacity: isUnlocked ? 1 : 0.65,
              cursor: 'pointer',
              transition: 'transform 0.15s ease'
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
              <span style={{ fontSize: '11px', color: '#38BDF8', fontWeight: '800' }}>
                🔍 Buka Kartu RPG & Fungsi Bangunan ➔
              </span>

              <button
                onClick={(e) => {
                  e.stopPropagation();
                  onUpgradeBuilding(b, cost);
                }}
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

// Panel 2: Talent Tree Riset
export function ResearchPanel({ state, onUnlockResearch }) {
  return (
    <TalentTreeCanvas
      state={state}
      onUnlockResearch={onUnlockResearch}
    />
  );
}

// Panel 3: Komandan & Armory Tempa Senjata (22 Komandan & 12 Senjata)
export function CommandersPanel({ state, onOpenGacha, onForgeWeapon, onCombineShards, onToggleGarrison }) {
  const [subTab, setSubTab] = useState('heroes');

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
      {/* Tavern Gacha Header Card */}
      <div style={{
        backgroundColor: 'rgba(30, 41, 59, 0.8)',
        borderRadius: '14px',
        padding: '14px 18px',
        border: '1.5px solid rgba(255, 179, 0, 0.3)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between'
      }}>
        <div>
          <h4 style={{ fontSize: '15px', color: '#FFD54F', margin: 0, fontWeight: '900' }}>
            🍻 KEDAI PAHLAWAN (TAVERN)
          </h4>
          <span style={{ fontSize: '11px', color: '#94A3B8' }}>
            Tersedia: <strong>{state.gachaTickets}</strong> Tiket Panggilan Kosmis
          </span>
        </div>
        <button
          onClick={onOpenGacha}
          className="btn-pixel gold"
          style={{ fontSize: '12px', padding: '8px 16px' }}
        >
          🎲 Tarik Pahlawan (Hujan Pedang)
        </button>
      </div>

      {/* Sub Tabs */}
      <div style={{ display: 'flex', gap: '6px' }}>
        <button
          onClick={() => setSubTab('heroes')}
          className={`btn-pixel ${subTab === 'heroes' ? 'cyan' : ''}`}
          style={{ flex: 1, fontSize: '11px', padding: '6px' }}
        >
          👥 22 Pahlawan
        </button>
        <button
          onClick={() => setSubTab('armory')}
          className={`btn-pixel ${subTab === 'armory' ? 'cyan' : ''}`}
          style={{ flex: 1, fontSize: '11px', padding: '6px' }}
        >
          ⚔️ Tempa Senjata & Armor
        </button>
        <button
          onClick={() => setSubTab('shards')}
          className={`btn-pixel ${subTab === 'shards' ? 'cyan' : ''}`}
          style={{ flex: 1, fontSize: '11px', padding: '6px' }}
        >
          💎 Peleburan Shard
        </button>
      </div>

      {/* SubTab 1: Heroes Grid */}
      {subTab === 'heroes' && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
          {state.commanders.map((cmd) => {
            const isGarrisoned = state.garrisonedHeroes.includes(cmd.name);

            return (
              <div
                key={cmd.id}
                style={{
                  backgroundColor: cmd.owned ? 'rgba(30, 41, 59, 0.7)' : 'rgba(15, 23, 42, 0.5)',
                  border: `1.5px solid ${cmd.owned ? cmd.color : 'rgba(255, 255, 255, 0.05)'}`,
                  borderRadius: '12px',
                  padding: '12px 14px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between',
                  opacity: cmd.owned ? 1 : 0.55
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                  <div style={{
                    fontSize: '26px',
                    width: '46px',
                    height: '46px',
                    borderRadius: '50%',
                    backgroundColor: cmd.color,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    border: '2px solid rgba(255,255,255,0.2)'
                  }}>
                    {cmd.icon}
                  </div>
                  <div>
                    <div style={{ fontWeight: '800', fontSize: '13px', color: '#F8FAFC' }}>
                      {cmd.name}
                    </div>
                    <div style={{ fontSize: '11px', color: '#4ADE80', fontWeight: '700' }}>
                      {cmd.bonus}
                    </div>
                    <div style={{ fontSize: '10px', color: '#CBD5E1' }}>
                      {'⭐'.repeat(cmd.stars || 1)} • Skill: {cmd.skill}
                    </div>
                  </div>
                </div>

                <div>
                  {cmd.owned ? (
                    <button
                      onClick={() => onToggleGarrison(cmd.name)}
                      className={`btn-pixel ${isGarrisoned ? 'gold' : ''}`}
                      style={{ fontSize: '11px', padding: '6px 10px' }}
                    >
                      {isGarrisoned ? '🛡️ Menjaga Gerbang' : 'Tugaskan Jaga'}
                    </button>
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
            );
          })}
        </div>
      )}

      {/* SubTab 2: Armory Tempa Senjata */}
      {subTab === 'armory' && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
          {ARMORY_WEAPONS.map((w) => {
            const isForged = state.forgedWeapons.includes(w.id);

            let canAfford = true;
            for (const [resKey, amt] of Object.entries(w.cost)) {
              if ((state[resKey] || 0) < amt) {
                canAfford = false;
                break;
              }
            }

            return (
              <div
                key={w.id}
                style={{
                  backgroundColor: isForged ? 'rgba(6, 95, 70, 0.25)' : 'rgba(30, 41, 59, 0.6)',
                  border: `1.5px solid ${isForged ? '#10B981' : 'rgba(255, 255, 255, 0.08)'}`,
                  borderRadius: '12px',
                  padding: '12px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between'
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                  <img
                    src={w.img}
                    alt={w.name}
                    style={{ width: '40px', height: '40px', objectFit: 'contain' }}
                  />
                  <div>
                    <div style={{ fontWeight: '800', fontSize: '13px', color: '#F8FAFC' }}>
                      {w.name}
                    </div>
                    <div style={{ fontSize: '11px', color: '#4ADE80', fontWeight: '700' }}>
                      +{formatNumber(w.atkBonus)} Attack • +{formatNumber(w.hpBonus)} HP
                    </div>
                    <div style={{ fontSize: '10px', color: '#FCD34D' }}>
                      Biaya: {Object.entries(w.cost).map(([k, v]) => `${k}: ${v}`).join(', ')}
                    </div>
                  </div>
                </div>

                <div>
                  {isForged ? (
                    <span style={{ color: '#10B981', fontWeight: '800', fontSize: '11px' }}>✓ Ditempa</span>
                  ) : (
                    <button
                      onClick={() => onForgeWeapon(w)}
                      disabled={!canAfford}
                      className={`btn-pixel ${canAfford ? 'gold' : ''}`}
                      style={{ fontSize: '11px', padding: '6px 12px' }}
                    >
                      Tempa Senjata
                    </button>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* SubTab 3: Shard Combine */}
      {subTab === 'shards' && (
        <div style={{
          backgroundColor: 'rgba(30, 41, 59, 0.7)',
          borderRadius: '12px',
          padding: '16px',
          border: '1.5px solid rgba(255, 255, 255, 0.1)',
          display: 'flex',
          flexDirection: 'column',
          gap: '12px'
        }}>
          <h4 style={{ color: '#FFD54F', margin: 0, fontSize: '14px' }}>💎 Peleburan Token Shard</h4>
          <p style={{ fontSize: '11px', color: '#94A3B8', margin: 0 }}>
            Kumpulkan dan lebur shard untuk memanggil komandan langka (Rare, SR, SSR, UR).
          </p>

          <ShardRow label="Rare Shard" count={state.shard_r} needed={20} onCombine={() => onCombineShards('r', 20)} />
          <ShardRow label="SR Shard" count={state.shard_sr} needed={50} onCombine={() => onCombineShards('sr', 50)} />
          <ShardRow label="SSR Shard" count={state.shard_ssr} needed={100} onCombine={() => onCombineShards('ssr', 100)} />
          <ShardRow label="UR Mythical Shard" count={state.shard_ur} needed={150} onCombine={() => onCombineShards('ur', 150)} />
        </div>
      )}
    </div>
  );
}

function ShardRow({ label, count, needed, onCombine }) {
  const canCombine = count >= needed;
  return (
    <div style={{
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      backgroundColor: 'rgba(15, 23, 42, 0.6)',
      padding: '10px 14px',
      borderRadius: '8px'
    }}>
      <span style={{ fontSize: '12px', color: '#F8FAFC', fontWeight: '700' }}>
        {label}: <strong style={{ color: '#38BDF8' }}>{count}/{needed}</strong>
      </span>
      <button
        onClick={onCombine}
        disabled={!canCombine}
        className={`btn-pixel ${canCombine ? 'gold' : ''}`}
        style={{ fontSize: '11px', padding: '4px 10px' }}
      >
        💥 Combine Pahlawan
      </button>
    </div>
  );
}

// Panel 4: Dungeon & Raid (Arena Tempur Pixel Chibi 60 FPS)
export function DungeonPanel({ state, onAttackBoss, onDefendRaid, onToggleAutoFarm }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
      <div style={{
        backgroundColor: 'rgba(220, 38, 38, 0.15)',
        border: '1.5px solid #EF4444',
        borderRadius: '14px',
        padding: '14px',
        display: 'flex',
        flexDirection: 'column',
        gap: '8px'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <span style={{ fontWeight: '800', fontSize: '14px', color: '#FCA5A5', display: 'flex', alignItems: 'center', gap: '6px' }}>
            <span>⚔️</span> Serangan Raid Musuh (Gelombang #{state.raidLevel})
          </span>
          <span style={{ fontFamily: '"Press Start 2P", monospace', fontSize: '11px', color: '#FCD34D' }}>
            ⏱️ {state.raidTimer}s
          </span>
        </div>
        <p style={{ fontSize: '11px', color: '#CBD5E1', margin: 0 }}>
          Musuh bergerak mendekati gerbang benteng! Prajurit jaga: <strong>{state.garrisonedPasukan} Prajurit</strong>.
        </p>
        <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '4px' }}>
          <button onClick={onDefendRaid} className="btn-pixel gold" style={{ fontSize: '11px', padding: '6px 14px' }}>
            🛡️ Hadang Raid (+250 Koin Pertahanan)
          </button>
        </div>
      </div>

      <DungeonArena
        state={state}
        onAttackBoss={onAttackBoss}
        onToggleAutoFarm={onToggleAutoFarm}
      />
    </div>
  );
}

// Panel 5: Pasar & Toko Dungeon / Raid
export function MarketPanel({ state, onBuyDungeonItem }) {
  const [subMarket, setSubMarket] = useState('dungeon');

  const dungeonItems = [
    { id: 'hp_pot', name: 'Potion HP Lv.1', cost: 200, icon: '🧪', desc: 'Sembuhkan hero saat perang' },
    { id: 'atk_pot', name: 'Potion Atk Lv.1', cost: 200, icon: '⚡', desc: 'Tambah daya serang hero' },
    { id: 'wood_100', name: 'Paket 100 Kayu', cost: 200, icon: '🪵', desc: 'Bahan bangunan instan' },
    { id: 'stone_100', name: 'Paket 100 Batu', cost: 200, icon: '🪨', desc: 'Bahan batu instan' },
    { id: 'shard_r', name: 'Token Shard Rare', cost: 50, icon: '💎', desc: 'Panggil pahlawan unggul' },
    { id: 'ai_core', name: 'Quantum AI Core', cost: 8000, icon: '🧠', desc: 'Komponen teknologi tinggi' }
  ];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
      <div style={{ display: 'flex', gap: '8px' }}>
        <button
          onClick={() => setSubMarket('dungeon')}
          className={`btn-pixel ${subMarket === 'dungeon' ? 'gold' : ''}`}
          style={{ flex: 1, fontSize: '12px' }}
        >
          💰 Toko Dungeon ({formatNumber(state.dungeonCoins)} 🪙)
        </button>
        <button
          onClick={() => setSubMarket('raid')}
          className={`btn-pixel ${subMarket === 'raid' ? 'gold' : ''}`}
          style={{ flex: 1, fontSize: '12px' }}
        >
          🪙 Toko Koin Raid ({formatNumber(state.gameCoins)} 🪙)
        </button>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
        {dungeonItems.map((it) => (
          <div
            key={it.id}
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
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              <span style={{ fontSize: '24px' }}>{it.icon}</span>
              <div>
                <div style={{ fontWeight: '800', fontSize: '13px', color: '#F8FAFC' }}>{it.name}</div>
                <div style={{ fontSize: '10px', color: '#94A3B8' }}>{it.desc}</div>
              </div>
            </div>

            <button
              onClick={() => onBuyDungeonItem(it)}
              disabled={state.dungeonCoins < it.cost}
              className={`btn-pixel ${state.dungeonCoins >= it.cost ? 'gold' : ''}`}
              style={{ fontSize: '11px', padding: '6px 12px' }}
            >
              Beli ({it.cost} Koin)
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}

// Panel 6: Filsafat & Doktrin (10 Mazhab)
export function PhilosophyPanel({ state, onAdoptPhilosophy }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
      <div style={{ fontSize: '12px', fontWeight: '800', color: '#94A3B8', textTransform: 'uppercase' }}>
        10 Mazhab Filsafat & Doktrin Peradaban
      </div>

      {ALL_PHILOSOPHIES.map((p) => {
        const isAdopted = state.unlockedNodes.includes(p.id);
        const canAfford = state.totalZenith >= p.costZenith && !isAdopted;

        return (
          <div
            key={p.id}
            style={{
              backgroundColor: isAdopted ? 'rgba(76, 29, 149, 0.3)' : 'rgba(30, 41, 59, 0.6)',
              border: `1.5px solid ${isAdopted ? '#A855F7' : 'rgba(255, 255, 255, 0.06)'}`,
              borderRadius: '12px',
              padding: '12px 14px',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between'
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
              <img
                src={p.img}
                alt={p.name}
                style={{ width: '38px', height: '38px', objectFit: 'contain' }}
              />
              <div>
                <div style={{ fontWeight: '800', fontSize: '13px', color: '#F8FAFC' }}>{p.name}</div>
                <div style={{ fontSize: '11px', color: '#4ADE80', fontWeight: '700' }}>Efek: {p.effect}</div>
              </div>
            </div>

            <div>
              {isAdopted ? (
                <span style={{ color: '#C084FC', fontWeight: '800', fontSize: '11px' }}>✓ Aktif</span>
              ) : (
                <button
                  onClick={() => onAdoptPhilosophy(p)}
                  disabled={!canAfford}
                  className={`btn-pixel ${canAfford ? 'purple' : ''}`}
                  style={{ fontSize: '11px', padding: '6px 12px' }}
                >
                  Terapkan ({formatNumber(p.costZenith)} Z)
                </button>
              )}
            </div>
          </div>
        );
      })}
    </div>
  );
}
