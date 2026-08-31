import React, { useEffect, useState, useRef } from 'react';
import { api } from '../services/api';
import type { 
    DrsReviewResponse, 
    VideoPipelineStatusResponse, 
    VideoAnalysisResultResponse,
    EvidenceQualityResponse
} from '../types';
import { HealthIndicator } from '../components/HealthIndicator';
import { DecisionCard } from '../components/DecisionCard';
import { PipelineStatus } from '../components/PipelineStatus';
import { AnalysisSummary } from '../components/AnalysisSummary';
import { DrsTimeline } from '../components/DrsTimeline';
import { ReplayPlayer } from '../components/ReplayPlayer';
import { EvidenceQualityPanel } from '../components/EvidenceQualityPanel';
import { Search } from 'lucide-react';

export const DrsReviewDashboard: React.FC = () => {
    // We would normally get these from URL params via React Router
    const [gameId, setGameId] = useState<number>(1);
    const [pId, setPId] = useState<number>(1);
    const [aId, setAId] = useState<number>(1);
    
    // Inputs for the search form
    const [inputGameId, setInputGameId] = useState<string>('1');
    const [inputPId, setInputPId] = useState<string>('1');
    const [inputAId, setInputAId] = useState<string>('1');

    const [review, setReview] = useState<DrsReviewResponse | null>(null);
    const [status, setStatus] = useState<VideoPipelineStatusResponse | null>(null);
    const [result, setResult] = useState<VideoAnalysisResultResponse | null>(null);
    const [evidenceQuality, setEvidenceQuality] = useState<EvidenceQualityResponse | null>(null);
    
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    const [evidenceQualityError, setEvidenceQualityError] = useState<string | null>(null);
    const [notFound, setNotFound] = useState<boolean>(false);
    const [isPolling, setIsPolling] = useState<boolean>(false);

    const pollingIntervalRef = useRef<number | null>(null);

    const fetchAllData = async () => {
        try {
            setError(null);
            setEvidenceQualityError(null);
            setNotFound(false);
            
            // Parallel fetches
            const [fetchedReview, fetchedStatus, fetchedResult, fetchedEvidence] = await Promise.all([
                api.getReview(gameId, pId, aId).catch(e => {
                    if (e.status === 404) setNotFound(true);
                    if (e.status === 409) return null; // Still processing
                    throw e;
                }),
                api.getPipelineStatus(gameId, pId, aId).catch(e => {
                    if (e.status === 404) return null;
                    throw e;
                }),
                api.getResult(gameId, pId, aId).catch(e => {
                    if (e.status === 404 || e.status === 409) return null;
                    throw e;
                }),
                api.getEvidenceQuality(gameId, pId, aId).catch(e => {
                    if (e.status === 404 || e.status === 409) return null;
                    setEvidenceQualityError('Failed to load evidence quality data.');
                    return null;
                })
            ]);

            if (fetchedReview) setReview(fetchedReview);
            if (fetchedStatus) setStatus(fetchedStatus);
            if (fetchedResult) setResult(fetchedResult);
            if (fetchedEvidence) setEvidenceQuality(fetchedEvidence);
            
            // Check terminal state
            const isCompleted = fetchedReview?.analysisStatus === 'COMPLETED' && fetchedReview?.replay?.status === 'COMPLETED';
            const isFailed = fetchedReview?.analysisStatus === 'FAILED' || fetchedStatus?.processingStatus === 'FAILED';
            
            if (isCompleted || isFailed) {
                stopPolling();
            } else if (!isPolling) {
                startPolling();
            }

        } catch (err: any) {
            console.error('Failed to fetch DRS data:', err);
            setError(err.message || 'Failed to load analysis data.');
            stopPolling();
        } finally {
            setLoading(false);
        }
    };

    const startPolling = () => {
        if (!isPolling) {
            setIsPolling(true);
            pollingIntervalRef.current = window.setInterval(fetchAllData, 2500);
        }
    };

    const stopPolling = () => {
        setIsPolling(false);
        if (pollingIntervalRef.current !== null) {
            clearInterval(pollingIntervalRef.current);
            pollingIntervalRef.current = null;
        }
    };

    useEffect(() => {
        setLoading(true);
        fetchAllData();
        return () => stopPolling();
    }, [gameId, pId, aId]);

    const handleSearch = (e: React.FormEvent) => {
        e.preventDefault();
        setGameId(parseInt(inputGameId, 10));
        setPId(parseInt(inputPId, 10));
        setAId(parseInt(inputAId, 10));
    };

    return (
        <div className="app-container">
            {/* Header */}
            <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '32px' }}>
                <div>
                    <h1 style={{ fontSize: '2rem', marginBottom: '4px' }}>InstantDRS <span style={{ color: 'var(--accent-primary)' }}>Review</span></h1>
                    <div style={{ color: 'var(--text-muted)' }}>
                        Game ID: {gameId} • Processing Job: {pId} • Analysis Job: {aId}
                    </div>
                </div>
                
                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end', gap: '16px' }}>
                    <HealthIndicator />
                    
                    <form onSubmit={handleSearch} style={{ display: 'flex', gap: '8px' }}>
                        <input type="number" value={inputGameId} onChange={e => setInputGameId(e.target.value)} placeholder="Game ID" style={{ width: '80px', padding: '6px', borderRadius: '4px', border: '1px solid var(--border-light)', background: 'var(--bg-dark)', color: 'white' }} />
                        <input type="number" value={inputPId} onChange={e => setInputPId(e.target.value)} placeholder="Process ID" style={{ width: '80px', padding: '6px', borderRadius: '4px', border: '1px solid var(--border-light)', background: 'var(--bg-dark)', color: 'white' }} />
                        <input type="number" value={inputAId} onChange={e => setInputAId(e.target.value)} placeholder="Analysis ID" style={{ width: '80px', padding: '6px', borderRadius: '4px', border: '1px solid var(--border-light)', background: 'var(--bg-dark)', color: 'white' }} />
                        <button type="submit" style={{ padding: '6px 12px', borderRadius: '4px', background: 'var(--accent-primary)', color: 'white', border: 'none', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '6px' }}>
                            <Search size={16} /> Load
                        </button>
                    </form>
                </div>
            </header>

            {/* Error States */}
            {notFound && (
                <div className="glass-panel" style={{ textAlign: 'center', padding: '64px 24px', border: '1px solid rgba(251, 191, 36, 0.3)' }}>
                    <h2 style={{ fontSize: '1.5rem', marginBottom: '8px' }}>Analysis Not Found</h2>
                    <p style={{ color: 'var(--text-muted)' }}>We couldn't find a DRS analysis for this job ID. Please check the IDs and try again.</p>
                </div>
            )}
            
            {error && !notFound && (
                <div className="glass-panel" style={{ border: '1px solid rgba(239, 68, 68, 0.3)' }}>
                    <h2 style={{ color: 'var(--status-failed)' }}>Connection Error</h2>
                    <p>{error}</p>
                    <button onClick={fetchAllData} style={{ marginTop: '16px', padding: '8px 16px', borderRadius: '6px', background: 'rgba(255,255,255,0.1)', color: 'white', border: 'none', cursor: 'pointer' }}>Retry Connection</button>
                </div>
            )}

            {/* Dashboard Content */}
            {!error && !notFound && (
                <div className="grid-layout">
                    {/* Top Row: Final Decision (Left) & Evidence Quality (Right) */}
                    <div className="col-span-12 lg:col-span-6" style={{ gridColumn: 'span 6' }}>
                        {loading && !review ? (
                            <DecisionCard review={null} />
                        ) : (
                            <DecisionCard review={review} />
                        )}
                    </div>
                    
                    <div className="col-span-12 lg:col-span-6" style={{ gridColumn: 'span 6' }}>
                        <EvidenceQualityPanel 
                            evidenceQuality={evidenceQuality} 
                            loading={loading} 
                            error={evidenceQualityError} 
                            notFound={notFound} 
                        />
                    </div>

                    {/* Middle Row: Replay & Pipeline Status */}
                    <div className="col-span-12 lg:col-span-8" style={{ gridColumn: 'span 8' }}>
                        {loading && !review ? (
                            <ReplayPlayer replay={null} />
                        ) : (
                            <ReplayPlayer replay={review?.replay || null} />
                        )}
                    </div>
                    
                    <div className="col-span-12 lg:col-span-4" style={{ gridColumn: 'span 4' }}>
                        <PipelineStatus status={status} />
                    </div>

                    {/* Bottom Row: Timeline and Detailed Summary */}
                    <div className="col-span-12 lg:col-span-6" style={{ gridColumn: 'span 6' }}>
                        <DrsTimeline timeline={review?.timeline || null} />
                    </div>
                    
                    <div className="col-span-12 lg:col-span-6" style={{ gridColumn: 'span 6' }}>
                        <AnalysisSummary result={result} />
                    </div>
                </div>
            )}
        </div>
    );
};
