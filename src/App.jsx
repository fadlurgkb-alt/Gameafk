import React, { useState, useEffect } from 'react';
import confetti from 'canvas-confetti';
import {
  INITIAL_GAME_STATE,
  ALL_BUILDINGS,
  DUNGEON_MONSTERS,
  calculateCombatPower
} from './gameState';

import Header from './components/Header';
import PixelCanvas from './components/PixelCanvas';
import Tabs from './components/Tabs';
import {
  BuildingsPanel,
  ResearchPanel,
  PhilosophyPanel,
  CommandersPanel,
  EquipmentPanel,
  DungeonPanel,
  MarketPanel
} from './components/TabPanels';
import {
  GudangModal,
  HospitalModal,
  GerbangModal,
  KamusMaxModal
} from './components/Dialogs';
import GachaModal from './components/GachaModal';
import ProfileModal from './components/ProfileModal';
import BuildingDetailModal from './components/BuildingDetailModal';
import BarracksModal from './components/BarracksModal';
import GodModeModal from './components/GodModeModal';
import { sound } from './utils/retroAudio';

const STORAGE_KEY = 'GENESIS_PIXEL_SAVE_V3';

export default function App() {
  const [state, setState] = useState(() => {
    try {
      const saved = localStorage.getItem(STORAGE_KEY);
      if (saved) {
        const parsed = JSON.parse(saved);
        return {
          ...INITIAL_GAME_STATE,
          ...parsed,
          commanders: (Array.isArray(parsed.commanders) && parsed.commanders.length >= INITIAL_GAME_STATE.commanders.length)
            ? parsed.commanders
            : INITIAL_GAME_STATE.commanders,
          buildingLevels: { ...INITIAL_GAME_STATE.buildingLevels, ...(parsed.buildingLevels || {}) },
          unlockedNodes: Array.isArray(parsed.unlockedNodes) ? parsed.unlockedNodes : INITIAL_GAME_STATE.unlockedNodes,
          forgedWeapons: Array.isArray(parsed.forgedWeapons) ? parsed.forgedWeapons : INITIAL_GAME_STATE.forgedWeapons,
          garrisonedHeroes: Array.isArray(parsed.garrisonedHeroes) ? parsed.garrisonedHeroes : INITIAL_GAME_STATE.garrisonedHeroes
        };
      }
    } catch (e) {
      console.warn('Gagal memuat save, memuat state default:', e);
    }
    return INITIAL_GAME_STATE;
  });

  const [activeTab, setActiveTab] = useState('peta');
  const [selectedBuilding, setSelectedBuilding] = useState(null);
  const [isBarracksOpen, setIsBarracksOpen] = useState(false);
  const [isGudangOpen, setIsGudangOpen] = useState(false);
  const [isHospitalOpen, setIsHospitalOpen] = useState(false);
  const [isGerbangOpen, setIsGerbangOpen] = useState(false);
  const [isKamusOpen, setIsKamusOpen] = useState(false);
  const [isGachaOpen, setIsGachaOpen] = useState(false);
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const [isGodModeOpen, setIsGodModeOpen] = useState(false);

  // Passive Zenith Rate calculation
  const zenithRate = ALL_BUILDINGS.reduce((sum, b) => {
    const lvl = (state.buildingLevels && state.buildingLevels[b.id])
      || (Array.isArray(state.unlockedNodes) && state.unlockedNodes.includes(b.id) ? 1 : 0);
    if (lvl <= 0) return sum;
    return sum + (b.zenithPerSec * lvl);
  }, 5.0);

  // 1-Second Idle Game Loop
  useEffect(() => {
    const loop = setInterval(() => {
      setState((prev) => {
        let newZenith = prev.totalZenith + zenithRate;
        let newBatu = prev.batu;
        let newKayu = prev.kayu;
        let newBesi = prev.besi;
        let newEmas = prev.emas;
        let newOil = prev.oil;
        let newListrik = prev.listrik;
        let newTitanium = prev.titanium;
        let newUranium = prev.uranium;
        let newDaging = prev.daging;
        let newBerry = prev.berry;
        let newGandum = prev.gandum;
        let newIkan = prev.ikan;

        // Building yields
        ALL_BUILDINGS.forEach((b) => {
          const lvl = (prev.buildingLevels && prev.buildingLevels[b.id])
            || (Array.isArray(prev.unlockedNodes) && prev.unlockedNodes.includes(b.id) ? 1 : 0);
          if (lvl > 0 && b.produces) {
            const yieldAmt = b.ratePerSec * lvl;
            if (b.produces === 'kayu') newKayu += yieldAmt;
            if (b.produces === 'batu') newBatu += yieldAmt;
            if (b.produces === 'besi') newBesi += yieldAmt;
            if (b.produces === 'emas') newEmas += yieldAmt;
            if (b.produces === 'oil') newOil += yieldAmt;
            if (b.produces === 'listrik') newListrik += yieldAmt;
            if (b.produces === 'titanium') newTitanium += yieldAmt;
            if (b.produces === 'uranium') newUranium += yieldAmt;
            if (b.produces === 'daging') newDaging += yieldAmt;
            if (b.produces === 'berry') newBerry += yieldAmt;
            if (b.produces === 'gandum') newGandum += yieldAmt;
            if (b.produces === 'ikan') newIkan += yieldAmt;
          }
        });

        // Cap resources
        const maxCap = prev.maxStorage;
        newKayu = Math.min(newKayu, maxCap);
        newBatu = Math.min(newBatu, maxCap);
        newBesi = Math.min(newBesi, maxCap);
        newEmas = Math.min(newEmas, maxCap);

        // Raid Timer
        let newRaidTimer = prev.raidTimer - 1;
        let newRaidLevel = prev.raidLevel;
        let newGameCoins = prev.gameCoins;

        if (newRaidTimer <= 0) {
          newRaidTimer = 180;
          newRaidLevel += 1;
          newGameCoins += 250;
        }

        // Auto Farm Dungeon
        let newMonsterHp = prev.monsterHp;
        let newDungeonLevel = prev.currentDungeonLevel;
        let newDungeonCoins = prev.dungeonCoins;

        if (prev.autoFarmDungeon) {
          const dmg = prev.damage * 6;
          newMonsterHp -= dmg;
          if (newMonsterHp <= 0) {
            newDungeonCoins += 120;
            newDungeonLevel = Math.min(100, newDungeonLevel + 1);
            const nextMon = DUNGEON_MONSTERS.find((m) => m.level === newDungeonLevel) || DUNGEON_MONSTERS[0];
            newMonsterHp = nextMon.hp;
          }
        }

        return {
          ...prev,
          totalZenith: newZenith,
          kayu: newKayu,
          batu: newBatu,
          besi: newBesi,
          emas: newEmas,
          oil: newOil,
          listrik: newListrik,
          titanium: newTitanium,
          uranium: newUranium,
          daging: newDaging,
          berry: newBerry,
          gandum: newGandum,
          ikan: newIkan,
          raidTimer: newRaidTimer,
          raidLevel: newRaidLevel,
          gameCoins: newGameCoins,
          monsterHp: newMonsterHp,
          currentDungeonLevel: newDungeonLevel,
          dungeonCoins: newDungeonCoins
        };
      });
    }, 1000);

    return () => clearInterval(loop);
  }, [zenithRate]);

  // Auto-Save
  useEffect(() => {
    const saver = setInterval(() => {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
    }, 5000);
    return () => clearInterval(saver);
  }, [state]);

  // Upgrade Building
  const handleUpgradeBuilding = (building, cost) => {
    setState((prev) => {
      const updated = { ...prev };
      for (const [resKey, amount] of Object.entries(cost)) {
        updated[resKey] = Math.max(0, (updated[resKey] || 0) - amount);
      }
      const curLvl = (prev.buildingLevels && prev.buildingLevels[building.id])
        || (Array.isArray(prev.unlockedNodes) && prev.unlockedNodes.includes(building.id) ? 1 : 0);
      updated.buildingLevels = {
        ...(prev.buildingLevels || {}),
        [building.id]: curLvl + 1
      };
      const unNodes = Array.isArray(prev.unlockedNodes) ? prev.unlockedNodes : [];
      if (!unNodes.includes(building.id)) {
        updated.unlockedNodes = [...unNodes, building.id];
      }
      if (building.id === 'gudang') updated.maxStorage += 500;
      if (building.id === 'tembok') {
        updated.maxGerbangHp += 250;
        updated.gerbangHp += 250;
      }
      if (building.id === 'barak_militer') updated.maxPasukan += 20;
      sound.playUpgrade();
      confetti({ particleCount: 25, spread: 50 });
      return updated;
    });
  };

  // Train Single Troop
  const handleTrainTroop = () => {
    setState((prev) => {
      if (prev.currentPasukan >= prev.maxPasukan) return prev;
      if (prev.kayu < 15 || prev.batu < 10 || prev.berry < 5) return prev;
      sound.playCoin();
      return {
        ...prev,
        kayu: prev.kayu - 15,
        batu: prev.batu - 10,
        berry: prev.berry - 5,
        currentPasukan: prev.currentPasukan + 1
      };
    });
  };

  // Train Troops Batch
  const handleTrainTroopsBatch = (count) => {
    setState((prev) => {
      const actualCount = Math.min(count, prev.maxPasukan - prev.currentPasukan);
      const woodCost = actualCount * 15;
      const stoneCost = actualCount * 10;
      const berryCost = actualCount * 5;
      if (prev.kayu < woodCost || prev.batu < stoneCost || prev.berry < berryCost) return prev;
      sound.playCoin();
      return {
        ...prev,
        kayu: prev.kayu - woodCost,
        batu: prev.batu - stoneCost,
        berry: prev.berry - berryCost,
        currentPasukan: prev.currentPasukan + actualCount
      };
    });
  };

  // Quick Harvest from specific building
  const handleQuickHarvest = (building) => {
    sound.playHarvest();
    setState((prev) => {
      const updated = { ...prev };
      if (building.produces) {
        updated[building.produces] = Math.min(prev.maxStorage, (prev[building.produces] || 0) + 50);
      }
      updated.totalZenith += 250;
      return updated;
    });
  };

  // Unlock Research
  const handleUnlockResearch = (node) => {
    if (state.totalZenith < node.costZenith) return;
    sound.playUpgrade();
    setState((prev) => ({
      ...prev,
      totalZenith: prev.totalZenith - node.costZenith,
      unlockedNodes: [...prev.unlockedNodes, node.id]
    }));
    confetti({ particleCount: 50, spread: 80 });
  };

  // Adopt Philosophy
  const handleAdoptPhilosophy = (phil) => {
    if (state.totalZenith < phil.costZenith) return;
    sound.playUpgrade();
    setState((prev) => ({
      ...prev,
      totalZenith: prev.totalZenith - phil.costZenith,
      unlockedNodes: [...prev.unlockedNodes, phil.id]
    }));
    confetti({ particleCount: 60, spread: 90 });
  };

  // Gacha Result
  const handlePerformGacha = (pulledHero) => {
    sound.playGacha();
    setState((prev) => {
      const updatedCommanders = prev.commanders.map((c) => {
        if (c.id === pulledHero.id) {
          return {
            ...c,
            owned: true,
            stars: c.owned ? (c.stars || 1) + 1 : 1
          };
        }
        return c;
      });
      return {
        ...prev,
        commanders: updatedCommanders,
        gachaTickets: Math.max(0, prev.gachaTickets - 1)
      };
    });
  };

  // Forge Weapon
  const handleForgeWeapon = (weapon) => {
    setState((prev) => {
      const updated = { ...prev };
      for (const [resKey, amt] of Object.entries(weapon.cost)) {
        updated[resKey] = Math.max(0, (updated[resKey] || 0) - amt);
      }
      updated.forgedWeapons = [...prev.forgedWeapons, weapon.id];
      updated.damage += weapon.atkBonus;
      confetti({ particleCount: 40, spread: 60 });
      return updated;
    });
  };

  // Shard Combine
  const handleCombineShards = (type, needed) => {
    const key = `shard_${type}`;
    if ((state[key] || 0) < needed) return;
    setState((prev) => {
      const pool = prev.commanders.filter((c) => {
        if (type === 'r') return c.rarity === 'R';
        if (type === 'sr') return c.rarity === 'SR';
        if (type === 'ssr') return c.rarity === 'SSR';
        return c.rarity === 'Mythical';
      });
      const unowned = pool.filter((c) => !c.owned);
      const target = unowned.length > 0 ? unowned[0] : pool[0];

      const updated = prev.commanders.map((c) => c.id === target.id ? { ...c, owned: true, stars: (c.stars || 1) + 1 } : c);
      confetti({ particleCount: 80, spread: 100 });
      return {
        ...prev,
        [key]: prev[key] - needed,
        commanders: updated
      };
    });
  };

  // Garrison Hero Toggle
  const handleToggleGarrison = (heroName) => {
    setState((prev) => {
      const gHeroes = Array.isArray(prev.garrisonedHeroes) ? prev.garrisonedHeroes : [];
      const isAlready = gHeroes.includes(heroName);
      const nextHeroes = isAlready
        ? gHeroes.filter((h) => h !== heroName)
        : [...gHeroes, heroName];
      return { ...prev, garrisonedHeroes: nextHeroes };
    });
  };

  // Attack Dungeon Boss
  const handleAttackBoss = () => {
    sound.playAttack();
    setState((prev) => {
      const dmg = prev.damage * 10;
      const nextHp = prev.monsterHp - dmg;
      if (nextHp <= 0) {
        sound.playUpgrade();
        const nextLvl = Math.min(100, prev.currentDungeonLevel + 1);
        const nextMon = DUNGEON_MONSTERS.find((m) => m.level === nextLvl) || DUNGEON_MONSTERS[0];
        confetti({ particleCount: 70, spread: 90 });
        return {
          ...prev,
          currentDungeonLevel: nextLvl,
          monsterHp: nextMon.hp,
          monsterMaxHp: nextMon.hp,
          dungeonCoins: prev.dungeonCoins + 180,
          shard_r: prev.shard_r + 5
        };
      }
      return { ...prev, monsterHp: nextHp };
    });
  };

  // Defend Raid
  const handleDefendRaid = () => {
    sound.playUpgrade();
    setState((prev) => {
      confetti({ particleCount: 80, spread: 100 });
      return {
        ...prev,
        raidTimer: 180,
        raidLevel: prev.raidLevel + 1,
        gameCoins: prev.gameCoins + 250,
        totalZenith: prev.totalZenith + 2500
      };
    });
  };

  // Buy Dungeon / Market Item
  const handleBuyDungeonItem = (item) => {
    const isGameCoin = item.currency === 'gameCoins';
    const balance = isGameCoin ? (state.gameCoins || 0) : (state.dungeonCoins || 0);
    if (balance < item.cost) return;

    sound.playUpgrade();
    setState((prev) => {
      const updated = {
        ...prev,
        dungeonCoins: isGameCoin ? prev.dungeonCoins : (prev.dungeonCoins || 0) - item.cost,
        gameCoins: isGameCoin ? (prev.gameCoins || 0) - item.cost : prev.gameCoins
      };

      if (item.id === 'hp_pot') updated.hpPotions = (prev.hpPotions || 0) + 1;
      if (item.id === 'mana_pot') updated.manaPotions = (prev.manaPotions || 0) + 1;
      if (item.id === 'atk_pot') updated.atkPotions = (prev.atkPotions || 0) + 1;
      if (item.id === 'regen_pot') updated.regenPotions = (prev.regenPotions || 0) + 1;
      if (item.id === 'wood_100') updated.kayu = Math.min(prev.maxStorage, (prev.kayu || 0) + 500);
      if (item.id === 'stone_100') updated.batu = Math.min(prev.maxStorage, (prev.batu || 0) + 500);
      if (item.id === 'iron_100') updated.besi = Math.min(prev.maxStorage, (prev.besi || 0) + 250);
      if (item.id === 'wheat_100') updated.gandum = Math.min(prev.maxStorage, (prev.gandum || 0) + 500);
      if (item.id === 'meat_100') updated.daging = Math.min(prev.maxStorage, (prev.daging || 0) + 500);
      if (item.id === 'shard_ruby' || item.id === 'shard_r') updated.shard_r = (prev.shard_r || 0) + 5;
      if (item.id === 'shard_sapphire') updated.shard_sr = (prev.shard_sr || 0) + 3;
      if (item.id === 'shard_emerald') updated.shard_ssr = (prev.shard_ssr || 0) + 1;
      if (item.id === 'ai_core') updated.totalZenith = (prev.totalZenith || 0) + 50000;
      if (item.id === 'exp_scroll') updated.gachaTickets = (prev.gachaTickets || 0) + 3;
      if (item.id === 'gold_pouch') updated.emas = Math.min(prev.maxStorage, (prev.emas || 0) + 10000);
      return updated;
    });
  };

  // Canvas Tap Harvest
  const handleCanvasTap = () => {
    setState((prev) => ({
      ...prev,
      totalZenith: prev.totalZenith + Math.max(2, Math.floor(zenithRate * 0.15) + 3)
    }));
  };

  // God Mode Debug
  const handleGodMode = () => {
    setState((prev) => ({
      ...prev,
      totalZenith: prev.totalZenith + 50000,
      kayu: prev.kayu + 10000,
      batu: prev.batu + 10000,
      besi: prev.besi + 5000,
      emas: prev.emas + 2000,
      oil: prev.oil + 1000,
      listrik: prev.listrik + 1000,
      daging: prev.daging + 5000,
      berry: prev.berry + 5000,
      gandum: prev.gandum + 5000,
      gachaTickets: prev.gachaTickets + 20,
      dungeonCoins: prev.dungeonCoins + 2000,
      maxStorage: prev.maxStorage + 25000
    }));
    confetti({ particleCount: 80, spread: 100 });
  };

  // Max All Debug
  const handleMaxAll = () => {
    setState((prev) => ({
      ...prev,
      totalZenith: 100000000,
      kayu: 200000,
      batu: 200000,
      besi: 100000,
      emas: 100000,
      oil: 100000,
      listrik: 100000,
      titanium: 100000,
      uranium: 100000,
      maxStorage: 1000000,
      gachaTickets: 100,
      commanders: prev.commanders.map((c) => ({ ...c, owned: true, stars: 3, level: 10 }))
    }));
    confetti({ particleCount: 150, spread: 130 });
  };

  return (
    <div style={{
      position: 'relative',
      width: '100vw',
      height: '100vh',
      backgroundColor: '#070A12',
      color: '#ECEFF1',
      overflow: 'hidden',
      display: 'flex',
      flexDirection: 'column'
    }}>
      {/* 1. Top Header HUD with Profile, CP, and God Mode Console */}
      <Header
        state={state}
        zenithRate={zenithRate}
        onOpenGudang={() => setIsGudangOpen(true)}
        onOpenHospital={() => setIsHospitalOpen(true)}
        onOpenGerbang={() => setIsGerbangOpen(true)}
        onOpenProfile={() => setIsProfileOpen(true)}
        onGodMode={() => setIsGodModeOpen(true)}
        onMaxAll={handleMaxAll}
      />

      {/* 2. Main Interactive Map & Drawer Body */}
      <div style={{
        flex: 1,
        position: 'relative',
        width: '100%',
        overflow: 'hidden'
      }}>
        {/* Fullscreen Map Canvas Layer */}
        <PixelCanvas
          state={state}
          isFullscreen={false}
          onCanvasTap={handleCanvasTap}
          onSelectBuilding={(b) => setSelectedBuilding(b)}
        />

        {/* Floating Glassmorphism Drawer when a tab is selected */}
        {activeTab !== 'peta' && (
          <div style={{
            position: 'absolute',
            inset: '10px 14px 10px 14px',
            maxWidth: '720px',
            margin: '0 auto',
            backgroundColor: 'rgba(11, 15, 25, 0.94)',
            backdropFilter: 'blur(22px)',
            borderRadius: '16px',
            border: '1.5px solid rgba(255, 255, 255, 0.12)',
            boxShadow: '0 16px 45px rgba(0, 0, 0, 0.85)',
            zIndex: 15,
            display: 'flex',
            flexDirection: 'column',
            overflow: 'hidden'
          }}>
            {/* Drawer Header with Title & Back to Map button */}
            <div style={{
              padding: '12px 16px',
              borderBottom: '1.5px solid rgba(255, 255, 255, 0.08)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              backgroundColor: 'rgba(30, 41, 59, 0.65)'
            }}>
              <h3 style={{ fontSize: '14px', fontWeight: '900', color: '#F8FAFC', margin: 0, display: 'flex', alignItems: 'center', gap: '8px' }}>
                {activeTab === 'bangunan' && '🏛️ Manajemen Bangunan Peradaban'}
                {activeTab === 'riset' && '🔬 Pohon Riset & Bakat Kosmik'}
                {activeTab === 'filsafat' && '📜 Mazhab Filosofi Peradaban'}
                {activeTab === 'komandan' && '👥 Pasukan & 22 Komandan Perang'}
                {activeTab === 'armory' && '⚔️ Persenjataan & Peralatan Tempur'}
                {activeTab === 'dungeon' && '🌀 Arena Dungeon & Pertahanan Raid'}
                {activeTab === 'market' && '🏪 Toko & Pasar Peradaban'}
                {activeTab === 'kamus' && '📖 Kamus Panduan Kerajaan'}
              </h3>
              <button
                onClick={() => setActiveTab('peta')}
                className="btn-pixel gold"
                style={{ fontSize: '11px', padding: '5px 12px' }}
                title="Tutup Panel & Kembali ke Peta Penuh"
              >
                🗺️ Tutup &amp; Lihat Peta
              </button>
            </div>

            {/* Scrollable Content inside Drawer */}
            <div style={{ flex: 1, overflowY: 'auto', padding: '16px' }}>
              {activeTab === 'bangunan' && (
                <BuildingsPanel
                  state={state}
                  onUpgradeBuilding={handleUpgradeBuilding}
                  onSelectBuilding={(b) => setSelectedBuilding(b)}
                />
              )}
              {activeTab === 'riset' && (
                <ResearchPanel
                  state={state}
                  onUnlockResearch={handleUnlockResearch}
                />
              )}
              {activeTab === 'filsafat' && (
                <PhilosophyPanel
                  state={state}
                  onAdoptPhilosophy={handleAdoptPhilosophy}
                />
              )}
              {activeTab === 'komandan' && (
                <CommandersPanel
                  state={state}
                  onOpenGacha={() => setIsGachaOpen(true)}
                  onForgeWeapon={handleForgeWeapon}
                  onCombineShards={handleCombineShards}
                  onToggleGarrison={handleToggleGarrison}
                />
              )}
              {activeTab === 'armory' && (
                <EquipmentPanel
                  state={state}
                  onForgeWeapon={handleForgeWeapon}
                />
              )}
              {activeTab === 'dungeon' && (
                <DungeonPanel
                  state={state}
                  onAttackBoss={handleAttackBoss}
                  onDefendRaid={handleDefendRaid}
                  onToggleAutoFarm={() => setState((p) => ({ ...p, autoFarmDungeon: !p.autoFarmDungeon }))}
                />
              )}
              {activeTab === 'market' && (
                <MarketPanel
                  state={state}
                  onBuyDungeonItem={handleBuyDungeonItem}
                />
              )}
              {activeTab === 'kamus' && (
                <div style={{
                  backgroundColor: 'rgba(30, 41, 59, 0.7)',
                  padding: '16px',
                  borderRadius: '12px',
                  border: '1px solid rgba(255, 255, 255, 0.08)'
                }}>
                  <h4 style={{ color: '#FFD54F', marginBottom: '8px' }}>📖 Panduan Peradaban &amp; Deff</h4>
                  <p style={{ fontSize: '12px', color: '#CBD5E1', lineHeight: '1.6' }}>
                    Klik bangunan manapun di peta atau di tab Bangunan untuk membuka kartu RPG bangunan tersebut dengan fungsi khususnya (Gudang, Barak, Gerbang, Balai Pengobatan, Kuil, dan lainnya).
                  </p>
                </div>
              )}
            </div>
          </div>
        )}
      </div>

      {/* Bottom Tabs */}
      <Tabs activeTab={activeTab} onTabChange={setActiveTab} />

      {/* RPG Building Detail Modal (Card RPG dengan Foto di Sisi Kiri & Fungsi di Sisi Kanan) */}
      <BuildingDetailModal
        isOpen={Boolean(selectedBuilding)}
        onClose={() => setSelectedBuilding(null)}
        building={selectedBuilding}
        state={state}
        onUpgradeBuilding={handleUpgradeBuilding}
        onOpenGudang={() => setIsGudangOpen(true)}
        onOpenBarracks={() => setIsBarracksOpen(true)}
        onOpenGerbang={() => setIsGerbangOpen(true)}
        onOpenHospital={() => setIsHospitalOpen(true)}
        onOpenTavern={() => setIsGachaOpen(true)}
        onOpenDungeon={() => setActiveTab('dungeon')}
        onOpenMarket={() => setActiveTab('market')}
        onOpenProfile={() => setIsProfileOpen(true)}
        onQuickHarvest={handleQuickHarvest}
      />

      {/* Barracks (Barak) Troops Training Modal */}
      <BarracksModal
        isOpen={isBarracksOpen}
        onClose={() => setIsBarracksOpen(false)}
        state={state}
        onTrainTroop={handleTrainTroop}
        onTrainTroopsBatch={handleTrainTroopsBatch}
      />

      {/* Other Interactive Dialogs */}
      <GudangModal
        isOpen={isGudangOpen}
        onClose={() => setIsGudangOpen(false)}
        state={state}
        onUpgradeGudang={() => setState((p) => ({ ...p, maxStorage: p.maxStorage + 500, gudangLevel: p.gudangLevel + 1 }))}
      />
      <HospitalModal
        isOpen={isHospitalOpen}
        onClose={() => setIsHospitalOpen(false)}
        state={state}
        onHealAll={() => setState((p) => ({ ...p, currentPasukan: p.currentPasukan + p.woundedPasukan, woundedPasukan: 0 }))}
      />
      <GerbangModal
        isOpen={isGerbangOpen}
        onClose={() => setIsGerbangOpen(false)}
        state={state}
        onUpdateGarrison={(val) => setState((p) => ({ ...p, garrisonedPasukan: val }))}
      />
      <KamusMaxModal
        isOpen={isKamusOpen}
        onClose={() => setIsKamusOpen(false)}
        state={state}
      />
      <GachaModal
        isOpen={isGachaOpen}
        onClose={() => setIsGachaOpen(false)}
        gachaTickets={state.gachaTickets}
        onPerformGacha={handlePerformGacha}
      />
      <ProfileModal
        isOpen={isProfileOpen}
        onClose={() => setIsProfileOpen(false)}
        state={state}
        onSelectAvatar={(avatarId) => setState((p) => ({ ...p, avatarId }))}
      />
      <GodModeModal
        isOpen={isGodModeOpen}
        onClose={() => setIsGodModeOpen(false)}
        state={state}
        setState={setState}
      />
    </div>
  );
}
