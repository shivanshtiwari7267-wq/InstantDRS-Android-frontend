import React from 'react';
import type { DrsReviewResponse } from '../types';

interface DecisionCardProps {
    review: DrsReviewResponse | null;
}

export const DecisionCard: React.FC<DecisionCardProps> = ({ review }) => {
    if (!review) {
        return (
            <div className="glass-panel animate-pulse" style={{ gridColumn: '1 / -1', minHeight: '150px' }}>
                <div className="glass-header">Final Decision</div>
                <div>Loading...</div>
            </div>
        );
    }

    const decision = review.decision || 'PENDING';
    const status = review.decisionStatus || 'PROCESSING';
    
    let badgeClass = 'default';
    if (decision === 'OUT') badgeClass = 'out';
    else if (decision === 'NOT_OUT') badgeClass = 'not-out';
    else if (status === 'FAILED') badgeClass = 'failed';
    else if (status === 'PROCESSING' || status === 'QUEUED') badgeClass = 'pending';

    return (
        <div className="glass-panel" style={{ 
            gridColumn: '1 / -1', 
            textAlign: 'center',
            background: decision === 'OUT' ? 'rgba(248, 113, 113, 0.05)' : (decision === 'NOT_OUT' ? 'rgba(52, 211, 153, 0.05)' : undefined),
            border: decision === 'OUT' ? '1px solid rgba(248, 113, 113, 0.2)' : (decision === 'NOT_OUT' ? '1px solid rgba(52, 211, 153, 0.2)' : undefined),
        }}>
            <div className="glass-header" style={{ justifyContent: 'center' }}>Final Decision</div>
            
            <div style={{ fontSize: '3rem', fontWeight: 800, margin: '16px 0', letterSpacing: '0.05em' }}>
                <span className={`badge ${badgeClass}`} style={{ fontSize: '2rem', padding: '12px 32px' }}>
                    {status === 'FAILED' ? 'FAILED' : decision}
                </span>
            </div>
            
            {review.reasonCode && (
                <div style={{ color: 'var(--text-muted)', marginTop: '8px', fontSize: '1.125rem' }}>
                    Reason: <strong style={{ color: 'var(--text-main)' }}>{review.reasonCode.replace(/_/g, ' ')}</strong>
                </div>
            )}
            
            {review.drsConfidence !== null && review.drsConfidence !== undefined && (
                <div style={{ color: 'var(--text-muted)', marginTop: '4px' }}>
                    Confidence: {(review.drsConfidence * 100).toFixed(1)}%
                </div>
            )}
            
            {status === 'FAILED' && review.analysisErrorMessage && (
                <div style={{ color: 'var(--status-failed)', marginTop: '16px', background: 'rgba(239, 68, 68, 0.1)', padding: '12px', borderRadius: '8px' }}>
                    Error: {review.analysisErrorMessage}
                </div>
            )}
        </div>
    );
};
