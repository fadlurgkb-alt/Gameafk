import React from 'react';
import { formatNumber, calculateNodeCost } from '../gameState';

export default function NodeCard({ node, zenith, onUpgrade }) {
  const cost = calculateNodeCost(node);
  const canAfford = zenith >= cost && node.level < node.maxLevel;
  const isMax = node.level >= node.maxLevel;

  return (
    <div style={{
      backgroundColor: node.unlocked ? '#161B26' : '#11141C',
      border: `1.5px solid ${node.unlocked ? '#283347' : '#1C2230'}`,
      borderRadius: '10px',
      padding: '12px',
      display: 'flex',
      flexDirection: 'column',
      gap: '8px',
      position: 'relative',
      opacity: node.unlocked ? 1 : 0.65,
      transition: 'all 0.15s ease'
    }}>
      {/* Top Header: Icon, Name, Level */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <span style={{
            fontSize: '20px',
            backgroundColor: '#1E2536',
            width: '36px',
            height: '36px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            borderRadius: '8px',
            border: '1px solid #2F3B54'
          }}>
            {node.icon}
          </span>
          <div>
            <div style={{ fontWeight: '700', fontSize: '13px', color: '#ECEFF1' }}>
              {node.name}
            </div>
            <div style={{ fontSize: '11px', color: '#81C784', fontWeight: '600' }}>
              +{formatNumber(node.zenithPerSec * Math.max(1, node.level))} Zenith/dtk
              {node.resourceType && (
                <span style={{ color: '#90CAF9', marginLeft: '6px' }}>
                  +{formatNumber(node.resourcePerSec * Math.max(1, node.level))} {node.resourceType}/dtk
                </span>
              )}
            </div>
          </div>
        </div>

        {/* Level Badge */}
        <div style={{
          backgroundColor: '#1F2738',
          border: '1px solid #36435E',
          padding: '2px 8px',
          borderRadius: '12px',
          fontSize: '11px',
          fontWeight: '700',
          color: '#FFD54F'
        }}>
          Lv. {node.level}
        </div>
      </div>

      {/* Description */}
      <div style={{ fontSize: '11px', color: '#90A4AE', lineHeight: '1.3' }}>
        {node.desc}
      </div>

      {/* Footer: Cost & Upgrade Button */}
      <div style={{
        marginTop: 'auto',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        paddingTop: '6px',
        borderTop: '1px solid #222A3B'
      }}>
        {/* Cost display */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
          <span style={{ fontSize: '12px', color: '#90A4AE' }}>Biaya:</span>
          {isMax ? (
            <span style={{ color: '#FFD54F', fontWeight: '700', fontSize: '12px' }}>MAXED</span>
          ) : (
            <span style={{
              fontWeight: '700',
              fontSize: '12px',
              color: canAfford ? '#FFD54F' : '#EF5350'
            }}>
              ✨ {formatNumber(cost)}
            </span>
          )}
        </div>

        {/* Action Button */}
        {node.unlocked ? (
          <button
            onClick={() => onUpgrade(node)}
            disabled={!canAfford || isMax}
            className={`btn-pixel ${canAfford ? 'gold' : ''}`}
            style={{
              padding: '6px 14px',
              fontSize: '11px',
              fontWeight: '700'
            }}
          >
            {isMax ? 'Maksimal' : node.level === 0 ? 'Buka Bangunan' : 'Tingkatkan'}
          </button>
        ) : (
          <div style={{
            fontSize: '11px',
            color: '#EF5350',
            fontWeight: '700',
            backgroundColor: 'rgba(239, 83, 80, 0.1)',
            padding: '4px 8px',
            borderRadius: '4px',
            border: '1px solid rgba(239, 83, 80, 0.3)'
          }}>
            🔒 Terkunci Era
          </div>
        )}
      </div>
    </div>
  );
}
