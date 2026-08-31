import React from 'react';
import type { DrsReplayResponse } from '../types';

interface ReplayPlayerProps {
    replay: DrsReplayResponse | null;
}

export const ReplayPlayer: React.FC<ReplayPlayerProps> = ({ replay }) => {
    if (!replay) {
        return (
            <div className="glass-panel animate-pulse" style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '300px' }}>
                <div style={{ color: 'var(--text-muted)' }}>Checking replay status...</div>
            </div>
        );
    }

    if (replay.status === 'FAILED') {
        return (
            <div className="glass-panel" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '300px', border: '1px solid rgba(239, 68, 68, 0.2)' }}>
                <div style={{ color: 'var(--status-failed)', marginBottom: '8px' }}>Replay Generation Failed</div>
                <div style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>{replay.errorMessage || 'An error occurred while generating the replay.'}</div>
            </div>
        );
    }

    if (!replay.available || !replay.outputEndpoint) {
        return (
            <div className="glass-panel" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '300px' }}>
                <div className="spinner" style={{ width: '24px', height: '24px', border: '3px solid var(--border-light)', borderTopColor: 'var(--accent-primary)', borderRadius: '50%', marginBottom: '16px' }}></div>
                <div style={{ color: 'var(--text-main)' }}>Replay is being generated.</div>
                <div style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginTop: '8px' }}>This may take a few moments.</div>
            </div>
        );
    }

    return (
        <div className="glass-panel" style={{ padding: '0', overflow: 'hidden' }}>
            <div className="glass-header" style={{ padding: '16px 24px', marginBottom: '0', borderBottom: '1px solid var(--border-light)' }}>
                DRS Replay
            </div>
            <div style={{ position: 'relative', width: '100%', backgroundColor: '#000' }}>
                <video 
                    controls 
                    autoPlay 
                    muted 
                    loop
                    style={{ width: '100%', display: 'block', maxHeight: '500px' }}
                    src={replay.outputEndpoint}
                >
                    Your browser does not support HTML video.
                </video>
            </div>
        </div>
    );
};
