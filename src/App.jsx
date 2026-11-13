import React, { useState, useEffect } from 'react';
import confetti from 'canvas-confetti';
import {
  INITIAL_GAME_STATE,
  ALL_BUILDINGS,
  ALL_RESEARCH,
  ALL_PHILOSOPHIES,
  DUNGEON_MONSTERS,
  formatNumber
} from './gameState';

import Header from './components/Header';
import PixelCanvas from './components/PixelCanvas';
import Tabs from './components/Tabs';
import {
  BuildingsPanel,
  ResearchPanel,
  PhilosophyPanel,
  CommandersPanel,
  DungeonRaidPanel,
  MarketPanel
} from './components/TabPanels';
import {
  GudangModal,
  HospitalModal,
  GerbangModal,
  KamusMaxModal
} from './components/Dialogs';

const STORAGE_KEY = 'GENESIS_PIXEL_SAVE_V2';

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

  // Calculate passive production rates
  const zenithRate = ALL_BUILDINGS.reduce((sum, b) => {
    const lvl = state.buildingLevels[b.id] || (state.unlockedNodes.includes(b.id) ? 1 : 0);
    if (lvl <= 0) return sum;
    return sum + (b.zenithPerSec * lvl);
  }, 5.0); // Base 5.0

  // Main 1-Second Idle Game Loop
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

        // Calculate building yields
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

        // Cap resources at maxStorage
        const maxCap = prev.maxStorage;
        newKayu = Math.min(newKayu, maxCap);
        newBatu = Math.min(newBatu, maxCap);
        newBesi = Math.min(newBesi, maxCap);
        newEmas = Math.min(newEmas, maxCap);

        // Raid Timer Countdown
        let newRaidTimer = prev.raidTimer - 1;
        let newRaidLevel = prev.raidLevel;
        let newGameCoins = prev.gameCoins;

        if (newRaidTimer <= 0) {
          newRaidTimer = 180;
          newRaidLevel += 1;
          newGameCoins += 150;
        }

        // Auto Farm Dungeon
        let newMonsterHp = prev.monsterHp;
        let newDungeonLevel = prev.currentDungeonLevel;
        let newDungeonCoins = prev.dungeonCoins;

        if (prev.autoFarmDungeon) {
          const dmg = prev.damage * 5;
          newMonsterHp -= dmg;
          if (newMonsterHp <= 0) {
            newDungeonCoins += 100;
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

  // Auto-Save to LocalStorage every 5s
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
      // Deduct resources
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
      if (building.id === 'gudang') {
        updated.maxStorage += 500;
      }
      if (building.id === 'tembok') {
        updated.maxGerbangHp += 250;
        updated.gerbangHp += 250;
      }
      if (building.id === 'barak_militer') {
        updated.maxPasukan += 20;
      }
      confetti({ particleCount: 25, spread: 50, origin: { y: 0.8 } });
      return updated;
    });
  };

  // Unlock Research
  const handleUnlockResearch = (research) => {
    if (state.totalZenith < research.costZenith) return;
    setState((prev) => ({
      ...prev,
      totalZenith: prev.totalZenith - research.costZenith,
      unlockedNodes: [...prev.unlockedNodes, research.id]
    }));
    confetti({ particleCount: 40, spread: 70, origin: { y: 0.6 } });
  };

  // Adopt Philosophy
  const handleAdoptPhilosophy = (philosophy) => {
    if (state.totalZenith < philosophy.costZenith) return;
    setState((prev) => ({
      ...prev,
      totalZenith: prev.totalZenith - philosophy.costZenith,
      unlockedNodes: [...prev.unlockedNodes, philosophy.id]
    }));
    confetti({ particleCount: 50, spread: 90, origin: { y: 0.5 } });
  };

  // Gacha Summon Commander
  const handleGachaRecruit = () => {
    setState((prev) => {
      const unowned = prev.commanders.filter((c) => !c.owned);
      if (unowned.length === 0) {
        // Upgrade stars of existing commander
        const upgraded = prev.commanders.map((c, i) => i === 0 ? { ...c, stars: (c.stars || 1) + 1 } : c);
        confetti({ particleCount: 70, spread: 80 });
        return { ...prev, commanders: upgraded };
      }
      const recruited = unowned[0];
      const updated = prev.commanders.map((c) => c.id === recruited.id ? { ...c, owned: true, level: 1 } : c);
      confetti({ particleCount: 100, spread: 100 });
      return {
        ...prev,
        commanders: updated,
        gachaTickets: Math.max(0, prev.gachaTickets - 1)
      };
    });
  };

  // Attack Dungeon Monster
  const handleAttackMonster = () => {
    setState((prev) => {
      const dmg = prev.damage * 10;
      const nextHp = prev.monsterHp - dmg;
      if (nextHp <= 0) {
        const nextLvl = Math.min(100, prev.currentDungeonLevel + 1);
        const nextMon = DUNGEON_MONSTERS.find((m) => m.level === nextLvl) || DUNGEON_MONSTERS[0];
        confetti({ particleCount: 60, spread: 80, origin: { y: 0.5 } });
        return {
          ...prev,
          currentDungeonLevel: nextLvl,
          monsterHp: nextMon.hp,
          monsterMaxHp: nextMon.hp,
          dungeonCoins: prev.dungeonCoins + 150
        };
      }
      return { ...prev, monsterHp: nextHp };
    });
  };

  // Defend Raid
  const handleDefendRaid = () => {
    setState((prev) => {
      confetti({ particleCount: 80, spread: 110, origin: { y: 0.4 } });
      return {
        ...prev,
        raidTimer: 180,
        raidLevel: prev.raidLevel + 1,
        gameCoins: prev.gameCoins + 250,
        totalZenith: prev.totalZenith + 2000
      };
    });
  };

  // Heal All Troops
  const handleHealAll = () => {
    setState((prev) => {
      const cost = prev.woundedPasukan * 2;
      if (prev.berry < cost) return prev;
      return {
        ...prev,
        berry: prev.berry - cost,
        currentPasukan: prev.currentPasukan + prev.woundedPasukan,
        woundedPasukan: 0
      };
    });
  };

  // Trade Market
  const handleTrade = (tr) => {
    setState((prev) => {
      if ((prev[tr.from] || 0) < tr.fromQty) return prev;
      return {
        ...prev,
        [tr.from]: prev[tr.from] - tr.fromQty,
        [tr.to]: (prev[tr.to] || 0) + tr.toQty
      };
    });
  };

  // Canvas Tap Harvest
  const handleCanvasTap = () => {
    setState((prev) => ({
      ...prev,
      totalZenith: prev.totalZenith + Math.max(1, Math.floor(zenithRate * 0.15) + 3)
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
      maxStorage: prev.maxStorage + 20000
    }));
    confetti({ particleCount: 80, spread: 100 });
  };

  // Max All Debug
  const handleMaxAll = () => {
    const allUnlocked = [
      ...ALL_BUILDINGS.map((b) => b.id),
      ...ALL_RESEARCH.map((r) => r.id),
      ...ALL_PHILOSOPHIES.map((p) => p.id)
    ];
    const maxLevels = {};
    ALL_BUILDINGS.forEach((b) => {
      maxLevels[b.id] = 10;
    });
    setState((prev) => ({
      ...prev,
      totalZenith: 50000000,
      kayu: 100000,
      batu: 100000,
      besi: 50000,
      emas: 50000,
      oil: 50000,
      listrik: 50000,
      titanium: 50000,
      uranium: 50000,
      maxStorage: 500000,
      unlockedNodes: allUnlocked,
      buildingLevels: maxLevels,
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
      {/* Top Header */}
      <Header
        state={state}
        zenithRate={zenithRate}
        onOpenGudang={() => setIsGudangOpen(true)}
        onOpenHospital={() => setIsHospitalOpen(true)}
        onOpenGerbang={() => setIsGerbangOpen(true)}
        onOpenStats={() => setIsKamusOpen(true)}
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
        {/* PETA RESPONSIF LEGA DI RUANG UTAMA */}
        <PixelCanvas state={state} onCanvasTap={handleCanvasTap} />

        {/* Dynamic Tab Panel Views */}
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
            onGachaRecruit={handleGachaRecruit}
          />
        )}
        {activeTab === 'dungeon' && (
          <DungeonRaidPanel
            state={state}
            onAttackMonster={handleAttackMonster}
            onDefendRaid={handleDefendRaid}
            onToggleAutoFarm={() => setState((p) => ({ ...p, autoFarmDungeon: !p.autoFarmDungeon }))}
          />
        )}
        {activeTab === 'market' && (
          <MarketPanel
            state={state}
            onTrade={handleTrade}
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
              Selamat datang di Genesis Pixel: Idle Cosmos! Bangun peradaban dari satu percikan api hingga era Cyberpunk kosmis. Kelola 15+ rantai bahan pangan dan material, tugaskan ksatria di gerbang benteng untuk menahan serbuan raid musuh, rekrut pahlawan bintang di kedai, dan telusuri lantai-lantai dungeon mistis.
            </p>
          </div>
        )}
      </main>

      {/* Bottom Tabs Switcher */}
      <Tabs activeTab={activeTab} onTabChange={setActiveTab} />

      {/* Interactive Dialogs */}
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
        onHealAll={handleHealAll}
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
    </div>
  );
}
