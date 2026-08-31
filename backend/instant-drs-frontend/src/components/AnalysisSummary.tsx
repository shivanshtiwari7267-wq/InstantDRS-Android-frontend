import React from 'react';
import type { VideoAnalysisResultResponse } from '../types';

interface AnalysisSummaryProps {
    result: VideoAnalysisResultResponse | null;
}

export const AnalysisSummary: React.FC<AnalysisSummaryProps> = ({ result }) => {
    if (!result) {
        return (
            <div className="glass-panel animate-pulse">
                <div className="glass-header">Analysis Details</div>
                <div style={{ height: '200px' }}>Loading...</div>
            </div>
        );
    }

    return (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
            
            {/* LBW Analysis */}
            {result.lbwAnalysis && (
                <div className="glass-panel">
                    <div className="glass-header">LBW Analysis</div>
                    <div className="data-row">
                        <span className="data-label">Total Analyses</span>
                        <span className="data-value">{result.lbwAnalysis.analysisCount}</span>
                    </div>
                </div>
            )}

            {/* Trajectory */}
            {result.trajectory && (
                <div className="glass-panel">
                    <div className="glass-header">Trajectory</div>
                    <div className="data-row">
                        <span className="data-label">Total Points</span>
                        <span className="data-value">{result.trajectory.pointCount}</span>
                    </div>
                    <div className="data-row">
                        <span className="data-label">Events</span>
                        <span className="data-value">{result.trajectory.eventCount}</span>
                    </div>
                </div>
            )}

            {/* Cricket Events */}
            {result.cricketEvents && (
                <div className="glass-panel">
                    <div className="glass-header">Cricket Events</div>
                    <div className="data-row">
                        <span className="data-label">Total Events</span>
                        <span className="data-value">{result.cricketEvents.eventCount}</span>
                    </div>
                    {result.cricketEvents.events.length > 0 && (
                        <div style={{ marginTop: '12px', overflowX: 'auto' }}>
                            <table className="data-table">
                                <thead>
                                    <tr>
                                        <th>Event</th>
                                        <th>Frame</th>
                                        <th>Time (s)</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {result.cricketEvents.events.map((evt, i) => (
                                        <tr key={i}>
                                            <td>{evt.eventType}</td>
                                            <td>{evt.startFrameNumber}</td>
                                            <td>{evt.startTimestampSeconds.toFixed(2)}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            )}

        </div>
    );
};
