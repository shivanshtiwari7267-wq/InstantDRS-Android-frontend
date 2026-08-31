import React from 'react';
import type { EvidenceQualityResponse, EvidenceReasonCode, EvidenceReadiness } from '../types';
import { ShieldAlert, ShieldCheck, Shield, HelpCircle, Activity, Crosshair, MapPin, Target } from 'lucide-react';

interface Props {
    evidenceQuality: EvidenceQualityResponse | null;
    loading: boolean;
    error: string | null;
    notFound: boolean;
}

export const EvidenceQualityPanel: React.FC<Props> = ({ evidenceQuality, loading, error, notFound }) => {

    if (notFound) {
        return null;
    }

    if (error) {
        return (
            <div className="glass-panel" style={{ border: '1px solid rgba(239, 68, 68, 0.3)' }}>
                <h3 style={{ fontSize: '1.25rem', marginBottom: '8px', color: 'var(--status-failed)' }}>Evidence Quality</h3>
                <p>Evidence Quality unavailable. Unable to retrieve evidence-quality data.</p>
            </div>
        );
    }

    if (loading && !evidenceQuality) {
        return (
            <div className="glass-panel">
                <h3 style={{ fontSize: '1.25rem', marginBottom: '8px' }}>Evidence Quality</h3>
                <p style={{ color: 'var(--text-muted)' }}>Waiting for analysis...</p>
            </div>
        );
    }

    if (!evidenceQuality) {
        return null;
    }

    const { readiness, reasonCodes, tracking, trajectory, bounce, impact, projection } = evidenceQuality;

    const getReadinessColor = (status: EvidenceReadiness) => {
        switch (status) {
            case 'READY': return 'var(--status-completed)';
            case 'PARTIAL': return 'var(--status-processing)';
            case 'INSUFFICIENT_DATA': return 'var(--status-failed)';
            default: return 'var(--text-muted)';
        }
    };

    const getReadinessIcon = (status: EvidenceReadiness) => {
        switch (status) {
            case 'READY': return <ShieldCheck size={24} color={getReadinessColor(status)} />;
            case 'PARTIAL': return <ShieldAlert size={24} color={getReadinessColor(status)} />;
            case 'INSUFFICIENT_DATA': return <Shield size={24} color={getReadinessColor(status)} />;
            default: return <HelpCircle size={24} color={getReadinessColor(status)} />;
        }
    };

    const formatReasonCode = (code: EvidenceReasonCode): string => {
        const mappings: Record<string, string> = {
            'TRACKING_INSUFFICIENT': 'Tracking is insufficient',
            'TRAJECTORY_INSUFFICIENT': 'Trajectory is insufficient',
            'BOUNCE_EVIDENCE_MISSING': 'Bounce evidence is missing',
            'IMPACT_EVIDENCE_MISSING': 'Impact evidence is missing',
            'PROJECTION_INSUFFICIENT': 'Projection is insufficient',
            'WICKET_INTERSECTION_UNAVAILABLE': 'Wicket intersection is unavailable',
            'LOW_EVIDENCE_CONFIDENCE': 'Evidence has low confidence',
            'MULTIPLE_CONFLICTING_CANDIDATES': 'Multiple conflicting candidates',
            'EVIDENCE_COMPLETE': 'Evidence is complete'
        };
        
        return mappings[code as string] || `Reason: ${code}`;
    };

    return (
        <div className="glass-panel" style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '24px' }}>
                <div>
                    <h2 style={{ fontSize: '1.25rem', marginBottom: '4px' }}>Evidence Quality</h2>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                        {getReadinessIcon(readiness)}
                        <span style={{ fontSize: '1.5rem', fontWeight: 600, color: getReadinessColor(readiness) }}>
                            {readiness}
                        </span>
                    </div>
                </div>
            </div>

            {reasonCodes && reasonCodes.length > 0 && !reasonCodes.includes('EVIDENCE_COMPLETE') && (
                <div style={{ marginBottom: '24px', padding: '12px', background: 'rgba(255,255,255,0.05)', borderRadius: '6px', borderLeft: `4px solid ${getReadinessColor(readiness)}` }}>
                    <h4 style={{ margin: '0 0 8px 0', fontSize: '0.9rem', color: 'var(--text-muted)' }}>Why?</h4>
                    <ul style={{ margin: 0, paddingLeft: '20px', display: 'flex', flexDirection: 'column', gap: '4px' }}>
                        {reasonCodes.map((code, idx) => (
                            <li key={idx}>{formatReasonCode(code)}</li>
                        ))}
                    </ul>
                </div>
            )}

            <div style={{ display: 'grid', gridTemplateColumns: '1fr', gap: '12px' }}>
                {/* Tracking Quality */}
                {tracking && (
                    <div style={{ padding: '12px', background: 'var(--bg-dark)', borderRadius: '8px', border: '1px solid var(--border-light)' }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '8px' }}>
                            <Activity size={18} color="var(--accent-primary)" />
                            <span style={{ fontWeight: 500 }}>Tracking</span>
                            <span style={{ marginLeft: 'auto', fontSize: '0.8rem', padding: '2px 8px', borderRadius: '12px', background: tracking.missingPoints > 5 ? 'rgba(239, 68, 68, 0.2)' : 'rgba(16, 185, 129, 0.2)', color: tracking.missingPoints > 5 ? 'var(--status-failed)' : 'var(--status-completed)' }}>
                                {tracking.validPoints > 10 ? 'GOOD' : tracking.validPoints > 0 ? 'PARTIAL' : 'INSUFFICIENT_DATA'}
                            </span>
                        </div>
                        <div style={{ display: 'flex', gap: '16px', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                            <span>Total: {tracking.totalPoints}</span>
                            <span>Valid: {tracking.validPoints}</span>
                            <span>Missing: {tracking.missingPoints}</span>
                        </div>
                    </div>
                )}

                {/* Trajectory Quality */}
                {trajectory && (
                    <div style={{ padding: '12px', background: 'var(--bg-dark)', borderRadius: '8px', border: '1px solid var(--border-light)' }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '8px' }}>
                            <Crosshair size={18} color="var(--accent-secondary)" />
                            <span style={{ fontWeight: 500 }}>Trajectory</span>
                            <span style={{ marginLeft: 'auto', fontSize: '0.8rem', padding: '2px 8px', borderRadius: '12px', background: trajectory.validDerivatives < 3 ? 'rgba(239, 68, 68, 0.2)' : 'rgba(16, 185, 129, 0.2)', color: trajectory.validDerivatives < 3 ? 'var(--status-failed)' : 'var(--status-completed)' }}>
                                {trajectory.validDerivatives > 5 ? 'GOOD' : trajectory.validDerivatives > 0 ? 'PARTIAL' : 'INSUFFICIENT_DATA'}
                            </span>
                        </div>
                        <div style={{ display: 'flex', gap: '16px', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                            <span>Valid Derivs: {trajectory.validDerivatives}</span>
                            <span>Segments: {trajectory.trajectorySegmentCount}</span>
                        </div>
                    </div>
                )}

                {/* Bounce Quality */}
                {bounce && (
                    <div style={{ padding: '12px', background: 'var(--bg-dark)', borderRadius: '8px', border: '1px solid var(--border-light)' }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '8px' }}>
                            <MapPin size={18} color="var(--status-processing)" />
                            <span style={{ fontWeight: 500 }}>Bounce</span>
                            <span style={{ marginLeft: 'auto', fontSize: '0.8rem', padding: '2px 8px', borderRadius: '12px', background: !bounce.available ? 'rgba(239, 68, 68, 0.2)' : bounce.candidateCount > 1 ? 'rgba(251, 191, 36, 0.2)' : 'rgba(16, 185, 129, 0.2)', color: !bounce.available ? 'var(--status-failed)' : bounce.candidateCount > 1 ? 'var(--status-processing)' : 'var(--status-completed)' }}>
                                {!bounce.available ? 'INSUFFICIENT_DATA' : bounce.candidateCount === 1 ? 'GOOD' : 'PARTIAL'}
                            </span>
                        </div>
                        <div style={{ display: 'flex', gap: '16px', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                            <span>Available: {bounce.available ? 'true' : 'false'}</span>
                            <span>Candidates: {bounce.candidateCount}</span>
                            {bounce.strongestConfidence && <span>MaxConf: {bounce.strongestConfidence.toFixed(2)}</span>}
                        </div>
                    </div>
                )}

                {/* Impact Quality */}
                {impact && (
                    <div style={{ padding: '12px', background: 'var(--bg-dark)', borderRadius: '8px', border: '1px solid var(--border-light)' }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '8px' }}>
                            <Target size={18} color="var(--status-completed)" />
                            <span style={{ fontWeight: 500 }}>Impact</span>
                            <span style={{ marginLeft: 'auto', fontSize: '0.8rem', padding: '2px 8px', borderRadius: '12px', background: !impact.available ? 'rgba(239, 68, 68, 0.2)' : impact.candidateCount > 1 ? 'rgba(251, 191, 36, 0.2)' : 'rgba(16, 185, 129, 0.2)', color: !impact.available ? 'var(--status-failed)' : impact.candidateCount > 1 ? 'var(--status-processing)' : 'var(--status-completed)' }}>
                                {!impact.available ? 'INSUFFICIENT_DATA' : impact.candidateCount === 1 ? 'GOOD' : 'PARTIAL'}
                            </span>
                        </div>
                        <div style={{ display: 'flex', gap: '16px', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                            <span>Available: {impact.available ? 'true' : 'false'}</span>
                            <span>Candidates: {impact.candidateCount}</span>
                        </div>
                    </div>
                )}
                
                {/* Projection Quality */}
                {projection && (
                    <div style={{ padding: '12px', background: 'var(--bg-dark)', borderRadius: '8px', border: '1px solid var(--border-light)' }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '8px' }}>
                            <Shield size={18} color="var(--accent-primary)" />
                            <span style={{ fontWeight: 500 }}>Projection</span>
                            <span style={{ marginLeft: 'auto', fontSize: '0.8rem', padding: '2px 8px', borderRadius: '12px', background: !projection.available ? 'rgba(239, 68, 68, 0.2)' : 'rgba(16, 185, 129, 0.2)', color: !projection.available ? 'var(--status-failed)' : 'var(--status-completed)' }}>
                                {projection.available ? 'GOOD' : 'INSUFFICIENT_DATA'}
                            </span>
                        </div>
                        <div style={{ display: 'flex', gap: '16px', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                            <span>Available: {projection.available ? 'true' : 'false'}</span>
                            <span>WicketIntersect: {projection.wicketIntersectionAvailable ? 'true' : 'false'}</span>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};
