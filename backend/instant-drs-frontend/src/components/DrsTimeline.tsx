import React from 'react';
import type { DrsTimelineEventDto } from '../types';

interface DrsTimelineProps {
    timeline: DrsTimelineEventDto[] | null;
}

export const DrsTimeline: React.FC<DrsTimelineProps> = ({ timeline }) => {
    if (!timeline) {
        return (
            <div className="glass-panel animate-pulse">
                <div className="glass-header">Event Timeline</div>
                <div>Loading timeline...</div>
            </div>
        );
    }

    if (timeline.length === 0) {
        return (
            <div className="glass-panel">
                <div className="glass-header">Event Timeline</div>
                <div style={{ color: 'var(--text-muted)' }}>No events detected yet.</div>
            </div>
        );
    }

    return (
        <div className="glass-panel">
            <div className="glass-header">Event Timeline</div>
            <div className="timeline-container">
                {timeline.map((event, i) => (
                    <div key={i} className="timeline-item animate-fade-in" style={{ animationDelay: `${i * 0.1}s` }}>
                        <div className="timeline-dot"></div>
                        <div className="timeline-content">
                            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '4px' }}>
                                <strong style={{ color: 'var(--text-main)' }}>{event.label || event.eventType}</strong>
                                <span style={{ color: 'var(--text-muted)', fontSize: '0.75rem' }}>
                                    {event.timestampSeconds.toFixed(3)}s (Frame {event.frameNumber})
                                </span>
                            </div>
                            <div style={{ fontSize: '0.875rem', color: 'var(--text-muted)' }}>
                                Type: {event.eventType} 
                                {event.confidence !== null && ` • Confidence: ${(event.confidence * 100).toFixed(1)}%`}
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};
