import React from 'react';
import type { VideoPipelineStatusResponse } from '../types';

interface PipelineStatusProps {
    status: VideoPipelineStatusResponse | null;
}

export const PipelineStatus: React.FC<PipelineStatusProps> = ({ status }) => {
    if (!status) {
        return (
            <div className="glass-panel animate-pulse">
                <div className="glass-header">Pipeline Status</div>
                <div style={{ height: '100px' }}>Loading status...</div>
            </div>
        );
    }

    const steps = [
        { label: 'Video Processing', active: true, completed: status.processingStatus === 'COMPLETED' },
        { label: 'Frame Analysis', active: status.processingStatus === 'COMPLETED', completed: status.analysisStatus === 'COMPLETED' },
        { label: 'Ball Tracking', active: status.analysisStatus === 'COMPLETED', completed: status.cricketEventsAvailable },
        { label: 'LBW Analysis', active: status.cricketEventsAvailable, completed: status.lbwAnalysisAvailable },
        { label: 'DRS Decision', active: status.lbwAnalysisAvailable, completed: status.drsDecisionAvailable },
        { label: 'Replay Generation', active: status.drsDecisionAvailable, completed: status.replayAvailable }
    ];

    return (
        <div className="glass-panel">
            <div className="glass-header">Pipeline Status</div>
            
            <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                <div className="data-row">
                    <span className="data-label">Overall Progress</span>
                    <span className="data-value">{status.progressPercent?.toFixed(1) || 0}% ({status.processedFrames || 0} / {status.totalFrames || 0} frames)</span>
                </div>
                
                <div style={{ marginTop: '16px' }}>
                    {steps.map((step, idx) => (
                        <div key={idx} style={{ 
                            display: 'flex', 
                            alignItems: 'center', 
                            gap: '12px',
                            padding: '8px 0',
                            opacity: step.active ? 1 : 0.4
                        }}>
                            <div style={{
                                width: '24px', height: '24px', borderRadius: '50%',
                                background: step.completed ? 'var(--status-not-out)' : (step.active ? 'var(--accent-primary)' : 'transparent'),
                                border: step.completed ? 'none' : `2px solid ${step.active ? 'var(--accent-primary)' : 'var(--border-light)'}`,
                                display: 'flex', alignItems: 'center', justifyContent: 'center',
                                fontSize: '12px', fontWeight: 'bold', color: step.completed ? '#000' : '#fff'
                            }}>
                                {step.completed ? '✓' : idx + 1}
                            </div>
                            <span style={{ 
                                fontWeight: step.active && !step.completed ? 600 : 400,
                                color: step.completed ? 'var(--text-main)' : (step.active ? 'var(--accent-primary)' : 'var(--text-muted)')
                            }}>
                                {step.label}
                            </span>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};
