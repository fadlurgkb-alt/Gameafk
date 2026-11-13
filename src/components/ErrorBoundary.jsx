import React from 'react';

export default class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null, errorInfo: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    console.error('Game Error Boundary caught an error:', error, errorInfo);
    this.setState({ errorInfo });
  }

  handleReset = () => {
    try {
      localStorage.removeItem('GENESIS_PIXEL_SAVE_V3');
      localStorage.clear();
    } catch (e) {
      console.error(e);
    }
    window.location.reload();
  };

  render() {
    if (this.state.hasError) {
      return (
        <div style={{
          minHeight: '100vh',
          width: '100vw',
          backgroundColor: '#070A12',
          color: '#ECEFF1',
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          padding: '24px',
          fontFamily: '"Plus Jakarta Sans", sans-serif',
          textAlign: 'center'
        }}>
          <div style={{
            maxWidth: '480px',
            backgroundColor: 'rgba(30, 41, 59, 0.85)',
            border: '2px solid #EF4444',
            borderRadius: '16px',
            padding: '24px',
            boxShadow: '0 0 30px rgba(239, 68, 68, 0.3)'
          }}>
            <div style={{ fontSize: '48px', marginBottom: '12px' }}>⚠️</div>
            <h2 style={{ fontSize: '18px', fontWeight: '900', color: '#F87171', marginBottom: '8px' }}>
              Terjadi Kesalahan Memuat Game
            </h2>
            <p style={{ fontSize: '13px', color: '#CBD5E1', lineHeight: '1.6', marginBottom: '16px' }}>
              Data save lama di browser mungkin tidak kompatibel dengan pembaruan sistem RPG &amp; 22 Komandan terbaru.
            </p>

            <div style={{
              backgroundColor: 'rgba(0, 0, 0, 0.4)',
              padding: '12px',
              borderRadius: '8px',
              fontSize: '11px',
              fontFamily: 'monospace',
              color: '#FCA5A5',
              textAlign: 'left',
              overflowX: 'auto',
              marginBottom: '16px',
              maxHeight: '120px'
            }}>
              {this.state.error?.toString()}
            </div>

            <div style={{ display: 'flex', gap: '10px', justifyContent: 'center' }}>
              <button
                onClick={this.handleReset}
                style={{
                  padding: '10px 18px',
                  backgroundColor: '#E11D48',
                  color: '#FFFFFF',
                  border: 'none',
                  borderRadius: '10px',
                  fontWeight: '800',
                  fontSize: '13px',
                  cursor: 'pointer',
                  boxShadow: '0 4px 12px rgba(225, 29, 72, 0.4)'
                }}
              >
                🔄 Reset Data &amp; Muat Ulang
              </button>
              <button
                onClick={() => window.location.reload()}
                style={{
                  padding: '10px 18px',
                  backgroundColor: '#3B82F6',
                  color: '#FFFFFF',
                  border: 'none',
                  borderRadius: '10px',
                  fontWeight: '800',
                  fontSize: '13px',
                  cursor: 'pointer'
                }}
              >
                Muat Ulang Saja
              </button>
            </div>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}
