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

const STORAGE_KEY = 'GENESIS_PIXEL_SAVE_V3';

export default function App() {
  const [state, setState] = useState(() => {
    const saved = localStorage.getItem(STORAGE_KEY);
    if (saved) {
      try {
        const parsed = JSON.parse(saved);
        return { ...INITIAL_GAME_STATE, ...parsed };
      } catch (e) {
        console.error(e);
      }
    }
    return INITIAL_GAME_STATE;
  });

  const [activeTab, setActiveTab] = useState('bangunan');
  const [isGudangOpen, setIsGudangOpen] = useState(false);
  const [isHospitalOpen, setIsHospitalOpen] = useState(false);
  const [isGerbangOpen, setIsGerbangOpen] = useState(false);
  const [isKamusOpen, setIsKamusOpen] = useState(false);
  const [isGachaOpen, setIsGachaOpen] = useState(false);
  const [isProfileOpen, setIsProfileOpen] = useState(false);

  // Passive Zenith Rate calculation
  const zenithRate = ALL_BUILDINGS.reduce((sum, b) => {
    const lvl = state.buildingLevels[b.id] || (state.unlockedNodes.includes(b.id) ? 1 : 0);
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
          const lvl = prev.buildingLevels[b.id] || (prev.unlockedNodes.includes(b.id) ? 1 : 0);
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
      const curLvl = prev.buildingLevels[building.id] || (prev.unlockedNodes.includes(building.id) ? 1 : 0);
      updated.buildingLevels = {
        ...prev.buildingLevels,
        [building.id]: curLvl + 1
      };
      if (!updated.unlockedNodes.includes(building.id)) {
        updated.unlockedNodes = [...prev.unlockedNodes, building.id];
      }
      if (building.id === 'gudang') updated.maxStorage += 500;
      if (building.id === 'tembok') {
        updated.maxGerbangHp += 250;
        updated.gerbangHp += 250;
      }
      if (building.id === 'barak_militer') updated.maxPasukan += 20;
      confetti({ particleCount: 25, spread: 50 });
      return updated;
    });
  };

  // Unlock Research
  const handleUnlockResearch = (node) => {
    if (state.totalZenith < node.costZenith) return;
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
    setState((prev) => ({
      ...prev,
      totalZenith: prev.totalZenith - phil.costZenith,
      unlockedNodes: [...prev.unlockedNodes, phil.id]
    }));
    confetti({ particleCount: 60, spread: 90 });
  };

  // Gacha Result Callback
  const handlePerformGacha = (pulledHero) => {
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
      // Unlock one commander matching rarity or upgrade star
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
      const isAlready = prev.garrisonedHeroes.includes(heroName);
      const nextHeroes = isAlready
        ? prev.garrisonedHeroes.filter((h) => h !== heroName)
        : [...prev.garrisonedHeroes, heroName];
      return { ...prev, garrisonedHeroes: nextHeroes };
    });
  };

  // Attack Dungeon Boss
  const handleAttackBoss = () => {
    setState((prev) => {
      const dmg = prev.damage * 10;
      const nextHp = prev.monsterHp - dmg;
      if (nextHp <= 0) {
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

  // Buy Dungeon Item
  const handleBuyDungeonItem = (item) => {
    if (state.dungeonCoins < item.cost) return;
    setState((prev) => {
      const updated = { ...prev, dungeonCoins: prev.dungeonCoins - item.cost };
      if (item.id === 'hp_pot') updated.hpPotions = (prev.hpPotions || 0) + 1;
      if (item.id === 'wood_100') updated.kayu = Math.min(prev.maxStorage, prev.kayu + 100);
      if (item.id === 'stone_100') updated.batu = Math.min(prev.maxStorage, prev.batu + 100);
      if (item.id === 'shard_r') updated.shard_r = prev.shard_r + 5;
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
      display: 'flex',
      flexDirection: 'column',
      height: '100%',
      backgroundColor: '#070A12',
      color: '#ECEFF1'
    }}>
      {/* Header with Avatar & Combat Power */}
      <Header
        state={state}
        zenithRate={zenithRate}
        onOpenGudang={() => setIsGudangOpen(true)}
        onOpenHospital={() => setIsHospitalOpen(true)}
        onOpenGerbang={() => setIsGerbangOpen(true)}
        onOpenProfile={() => setIsProfileOpen(true)}
        onGodMode={handleGodMode}
        onMaxAll={handleMaxAll}
      />

      {/* Main Spacious Content */}
      <main style={{
        flex: 1,
        overflowY: 'auto',
        padding: '12px',
        display: 'flex',
        flexDirection: 'column',
        gap: '12px'
      }}>
        {/* PETA RESPONSIF LEGA DENGAN ZOOM & PAN */}
        <PixelCanvas state={state} onCanvasTap={handleCanvasTap} />

        {/* Tab Panel Views */}
        {activeTab === 'bangunan' && (
          <BuildingsPanel
            state={state}
            onUpgradeBuilding={handleUpgradeBuilding}
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
            <h4 style={{ color: '#FFD54F', marginBottom: '8px' }}>📖 Panduan Peradaban & Deff</h4>
            <p style={{ fontSize: '12px', color: '#CBD5E1', lineHeight: '1.6' }}>
              Kelola 22 komandan suku legendaris, tempa 12 persenjataan, kuasai 26 jejaring kosmis, dan pertahankan gerbang kota Anda dari gelombang raid musuh!
            </p>
          </div>
        )}
      </main>

      {/* Bottom Tabs */}
      <Tabs activeTab={activeTab} onTabChange={setActiveTab} />

      {/* Modals & Dialogs */}
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
    </div>
  );
}
