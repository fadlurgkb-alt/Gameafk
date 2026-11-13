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
import BuildingVisual from './BuildingVisual';
import CommanderVisual from './CommanderVisual';
import ArmoryVisual from './ArmoryVisual';
import ItemVisual from './ItemVisual';

// Panel 1: Bangunan (23 Bangunan)
export function BuildingsPanel({ state, onUpgradeBuilding, onSelectBuilding }) {
  const unNodes = Array.isArray(state?.unlockedNodes) ? state.unlockedNodes : [];
  const eraId = unNodes.includes('brain_link') ? 'cyberpunk'
    : unNodes.includes('pengalengan_makanan') ? 'modern'
    : unNodes.includes('domestikasi_hewan') ? 'kerajaan'
    : 'primitif';
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
              <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                <div style={{
                  width: '46px',
                  height: '46px',
                  borderRadius: '10px',
                  backgroundColor: 'rgba(15, 23, 42, 0.85)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  border: '1.5px solid rgba(255, 255, 255, 0.1)',
                  boxShadow: '0 2px 8px rgba(0,0,0,0.3)',
                  overflow: 'hidden'
                }}>
                  <BuildingVisual buildingId={b.id} eraId={eraId} size={42} />
                </div>
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
                    width: '48px',
                    height: '48px',
                    borderRadius: '10px',
                    backgroundColor: 'rgba(15, 23, 42, 0.9)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    border: `2px solid ${cmd.color || '#FFD54F'}`,
                    boxShadow: `0 0 10px ${cmd.color}44`,
                    overflow: 'hidden',
                    flexShrink: 0
                  }}>
                    <CommanderVisual id={cmd.id} size={44} />
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
                  <div style={{
                    width: '46px',
                    height: '46px',
                    borderRadius: '10px',
                    backgroundColor: 'rgba(15, 23, 42, 0.85)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    border: '1.5px solid rgba(255, 255, 255, 0.1)',
                    overflow: 'hidden',
                    flexShrink: 0
                  }}>
                    <ArmoryVisual id={w.id} size={42} />
                  </div>
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

// Panel 5: Toko & Pasar Peradaban (Raven Fantasy 32x32 Pixel Art)
export function MarketPanel({ state, onBuyDungeonItem }) {
  const [activeCategory, setActiveCategory] = useState('potions');

  const categories = [
    { id: 'potions', label: '🧪 Ramuan', icon: '🧪' },
    { id: 'materials', label: '📦 Material', icon: '🪵' },
    { id: 'gems', label: '💎 Permata', icon: '💎' },
    { id: 'relics', label: '📜 Relik', icon: '📜' }
  ];

  const marketCatalog = {
    potions: [
      { id: 'hp_pot', name: 'Potion HP Darurat', cost: 150, currency: 'dungeonCoins', icon: 'hp_pot', desc: 'Sembuhkan 500 HP pahlawan & pasukan perang saat dungeon' },
      { id: 'mana_pot', name: 'Elixir Energi Kosmis', cost: 200, currency: 'dungeonCoins', icon: 'mana_pot', desc: 'Percepat pemulihan tenaga dan kurangi cooldown skill' },
      { id: 'atk_pot', name: 'Potion Serangan Rage', cost: 250, currency: 'dungeonCoins', icon: 'atk_pot', desc: 'Tingkatkan damage serang hero sebesar +30% selama 60 detik' },
      { id: 'regen_pot', name: 'Ramuan Regenerasi Alami', cost: 300, currency: 'dungeonCoins', icon: 'regen_pot', desc: 'Regenerasi 50 HP per detik selama pertempuran dungeon' }
    ],
    materials: [
      { id: 'wood_100', name: 'Paket 500 Kayu Glondongan', cost: 100, currency: 'gameCoins', icon: 'wood_100', desc: 'Bahan kayu murni dari hutan lebat untuk konstruksi desa' },
      { id: 'stone_100', name: 'Paket 500 Batu Granit', cost: 100, currency: 'gameCoins', icon: 'stone_100', desc: 'Batu tambang kokoh untuk memperkuat bangunan dan benteng' },
      { id: 'iron_100', name: 'Paket 250 Besi Tempa', cost: 150, currency: 'gameCoins', icon: 'iron_100', desc: 'Besi murni berkualitas tinggi untuk persenjataan perang' },
      { id: 'wheat_100', name: 'Karung Gandum Panen', cost: 80, currency: 'gameCoins', icon: 'wheat_100', desc: 'Pasokan makanan melimpah untuk melatih prajurit tempur' },
      { id: 'meat_100', name: 'Daging Panggang Kalori Tinggi', cost: 120, currency: 'gameCoins', icon: 'meat_100', desc: 'Santapan bergizi penambah stamina kerja seluruh warga' }
    ],
    gems: [
      { id: 'shard_ruby', name: 'Permata Rubik Darah', cost: 300, currency: 'dungeonCoins', icon: 'shard_ruby', desc: 'Batu kristal sakti untuk meningkatkan status komandan' },
      { id: 'shard_sapphire', name: 'Kristal Safir Samudera', cost: 450, currency: 'dungeonCoins', icon: 'shard_sapphire', desc: 'Kristal berharga untuk mempercepat kemajuan riset kosmik' },
      { id: 'shard_emerald', name: 'Zamrud Hijau Kemakmuran', cost: 600, currency: 'dungeonCoins', icon: 'shard_emerald', desc: 'Permata langka pemicu berkah produksi harian peradaban' },
      { id: 'ai_core', name: 'Quantum AI Core Singularity', cost: 2500, currency: 'dungeonCoins', icon: 'ai_core', desc: 'Komponen komputasi kuantum tercanggih era masa depan' }
    ],
    relics: [
      { id: 'exp_scroll', name: 'Gulungan Taktik Perang', cost: 200, currency: 'gameCoins', icon: 'exp_scroll', desc: 'Tingkatkan level pahlawan komandan instan sebesar 1 tingkat' },
      { id: 'golden_key', name: 'Kunci Peti Harta Karun', cost: 500, currency: 'dungeonCoins', icon: 'golden_key', desc: 'Buka peti dungeon kuno dengan jaminan drop persenjataan' },
      { id: 'gold_pouch', name: 'Kantong 10.000 Emas Murni', cost: 400, currency: 'dungeonCoins', icon: 'gold_pouch', desc: 'Tukar koin dungeon menjadi pundi-pundi emas peradaban' }
    ]
  };

  const currentItems = marketCatalog[activeCategory] || marketCatalog.potions;

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
      {/* Wallet Balance HUD */}
      <div style={{
        display: 'flex',
        gap: '8px',
        backgroundColor: 'rgba(30, 41, 59, 0.85)',
        padding: '12px 14px',
        borderRadius: '12px',
        border: '1.5px solid rgba(255, 215, 0, 0.25)',
        alignItems: 'center',
        justifyContent: 'space-around'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
          <span style={{ fontSize: '18px' }}>🪙</span>
          <div>
            <div style={{ fontSize: '10px', color: '#94A3B8' }}>Koin Dungeon</div>
            <div style={{ fontSize: '14px', fontWeight: '900', color: '#FACC15' }}>
              {formatNumber(state.dungeonCoins || 0)}
            </div>
          </div>
        </div>
        <div style={{ width: '1px', height: '24px', backgroundColor: 'rgba(255, 255, 255, 0.1)' }} />
        <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
          <span style={{ fontSize: '18px' }}>💰</span>
          <div>
            <div style={{ fontSize: '10px', color: '#94A3B8' }}>Koin Emas / Raid</div>
            <div style={{ fontSize: '14px', fontWeight: '900', color: '#38BDF8' }}>
              {formatNumber(state.gameCoins || 0)}
            </div>
          </div>
        </div>
      </div>

      {/* Category Tabs */}
      <div style={{ display: 'flex', gap: '6px' }}>
        {categories.map((c) => (
          <button
            key={c.id}
            onClick={() => setActiveCategory(c.id)}
            className={`btn-pixel ${activeCategory === c.id ? 'gold' : ''}`}
            style={{ flex: 1, fontSize: '11px', padding: '7px 4px' }}
          >
            {c.icon} {c.label}
          </button>
        ))}
      </div>

      {/* Items List */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
        {currentItems.map((it) => {
          const userBal = it.currency === 'dungeonCoins' ? (state.dungeonCoins || 0) : (state.gameCoins || 0);
          const canAfford = userBal >= it.cost;
          const currSymbol = it.currency === 'dungeonCoins' ? '🪙' : '💰';

          return (
            <div
              key={it.id}
              style={{
                backgroundColor: 'rgba(30, 41, 59, 0.7)',
                border: '1.5px solid rgba(255, 255, 255, 0.08)',
                borderRadius: '12px',
                padding: '12px 14px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                gap: '12px'
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                <ItemVisual id={it.icon} size={44} />
                <div>
                  <div style={{ fontWeight: '800', fontSize: '13px', color: '#F8FAFC' }}>
                    {it.name}
                  </div>
                  <div style={{ fontSize: '11px', color: '#94A3B8' }}>
                    {it.desc}
                  </div>
                </div>
              </div>

              <button
                onClick={() => onBuyDungeonItem(it)}
                disabled={!canAfford}
                className={`btn-pixel ${canAfford ? 'gold' : ''}`}
                style={{ fontSize: '11px', padding: '6px 14px', flexShrink: 0 }}
              >
                {currSymbol} {it.cost}
              </button>
            </div>
          );
        })}
      </div>
    </div>
  );
}

// Panel Dedicated: Persenjataan & Armory Peradaban (12 Senjata & Zirah)
export function EquipmentPanel({ state, onForgeWeapon }) {
  const [filter, setFilter] = useState('all');

  const filteredWeapons = ARMORY_WEAPONS.filter(w => {
    if (filter === 'all') return true;
    return w.type === filter;
  });

  const forgedCount = (state.forgedWeapons || []).length;
  const totalBonusAtk = ARMORY_WEAPONS
    .filter(w => (state.forgedWeapons || []).includes(w.id))
    .reduce((sum, w) => sum + (w.atkBonus || 0), 0);
  const totalBonusHp = ARMORY_WEAPONS
    .filter(w => (state.forgedWeapons || []).includes(w.id))
    .reduce((sum, w) => sum + (w.hpBonus || 0), 0);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
      {/* Equipment HUD Summary Card */}
      <div style={{
        backgroundColor: 'rgba(30, 41, 59, 0.85)',
        borderRadius: '14px',
        padding: '16px',
        border: '1.5px solid rgba(255, 215, 0, 0.35)',
        boxShadow: '0 4px 20px rgba(0,0,0,0.5)',
        display: 'flex',
        flexDirection: 'column',
        gap: '12px'
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <h4 style={{ fontSize: '15px', color: '#FFD54F', margin: 0, fontWeight: '900' }}>
              ⚔️ ARMORY &amp; PERSENJATAAN PERADABAN
            </h4>
            <span style={{ fontSize: '11px', color: '#94A3B8' }}>
              Persenjataan: <strong>{forgedCount} / {ARMORY_WEAPONS.length} Ditempa</strong>
            </span>
          </div>
          <div style={{
            backgroundColor: 'rgba(15, 23, 42, 0.8)',
            padding: '6px 12px',
            borderRadius: '10px',
            border: '1px solid #10B981',
            color: '#10B981',
            fontWeight: '800',
            fontSize: '11px'
          }}>
            +{formatNumber(totalBonusAtk)} ATK • +{formatNumber(totalBonusHp)} HP
          </div>
        </div>

        {/* 4 Equipment Visual Slots */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '8px' }}>
          {[
            { label: 'Senjata Utama', icon: '/assets/ic_wp_pedang.png' },
            { label: 'Baju Zirah', icon: '/assets/ic_wp_baju_besi.png' },
            { label: 'Jarak Jauh', icon: '/assets/ic_wp_musket.png' },
            { label: 'Pamungkas', icon: '/assets/ic_wp_nano_kirana.png' }
          ].map((slot, sIdx) => (
            <div key={sIdx} style={{
              backgroundColor: 'rgba(15, 23, 42, 0.85)',
              borderRadius: '10px',
              padding: '8px',
              border: '1.5px solid rgba(255, 255, 255, 0.1)',
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              gap: '4px'
            }}>
              <img src={slot.icon} alt={slot.label} style={{ width: '32px', height: '32px', objectFit: 'contain', imageRendering: 'pixelated' }} />
              <span style={{ fontSize: '9px', color: '#CBD5E1', textAlign: 'center', fontWeight: '700' }}>{slot.label}</span>
            </div>
          ))}
        </div>
      </div>

      {/* Filter Tabs */}
      <div style={{ display: 'flex', gap: '8px' }}>
        <button onClick={() => setFilter('all')} className={`btn-pixel ${filter === 'all' ? 'gold' : ''}`} style={{ flex: 1, fontSize: '11px', padding: '6px' }}>
          🌟 Semua ({ARMORY_WEAPONS.length})
        </button>
        <button onClick={() => setFilter('senjata')} className={`btn-pixel ${filter === 'senjata' ? 'gold' : ''}`} style={{ flex: 1, fontSize: '11px', padding: '6px' }}>
          ⚔️ Senjata ({ARMORY_WEAPONS.filter(w => w.type === 'senjata').length})
        </button>
        <button onClick={() => setFilter('armor')} className={`btn-pixel ${filter === 'armor' ? 'gold' : ''}`} style={{ flex: 1, fontSize: '11px', padding: '6px' }}>
          🛡️ Zirah ({ARMORY_WEAPONS.filter(w => w.type === 'armor').length})
        </button>
      </div>

      {/* Weapons & Armors List */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
        {filteredWeapons.map((w) => {
          const isForged = (state.forgedWeapons || []).includes(w.id);
          let canAfford = true;
          for (const [resKey, amt] of Object.entries(w.cost)) {
            if ((state[resKey] || 0) < amt) { canAfford = false; break; }
          }

          return (
            <div key={w.id} style={{
              backgroundColor: isForged ? 'rgba(6, 95, 70, 0.25)' : 'rgba(30, 41, 59, 0.65)',
              border: `1.5px solid ${isForged ? '#10B981' : 'rgba(255, 255, 255, 0.08)'}`,
              borderRadius: '12px',
              padding: '12px 14px',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              gap: '12px'
            }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                <ArmoryVisual id={w.id} size={46} />
                <div>
                  <div style={{ fontWeight: '800', fontSize: '13px', color: '#F8FAFC' }}>
                    {w.name}
                  </div>
                  <div style={{ fontSize: '11px', color: '#4ADE80', fontWeight: '700' }}>
                    +{formatNumber(w.atkBonus)} ATK • +{formatNumber(w.hpBonus)} HP
                  </div>
                  <div style={{ fontSize: '10px', color: '#FCD34D' }}>
                    Biaya: {Object.entries(w.cost).map(([k, v]) => `${k}: ${v}`).join(', ')}
                  </div>
                </div>
              </div>

              <div>
                {isForged ? (
                  <span style={{
                    backgroundColor: 'rgba(16, 185, 129, 0.2)',
                    color: '#10B981',
                    border: '1px solid #10B981',
                    padding: '4px 10px',
                    borderRadius: '8px',
                    fontSize: '11px',
                    fontWeight: '800'
                  }}>
                    ✓ Ditempa &amp; Aktif
                  </span>
                ) : (
                  <button
                    onClick={() => onForgeWeapon(w)}
                    disabled={!canAfford}
                    className={`btn-pixel ${canAfford ? 'gold' : ''}`}
                    style={{ fontSize: '11px', padding: '6px 14px' }}
                  >
                    🔨 Tempa
                  </button>
                )}
              </div>
            </div>
          );
        })}
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
