import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { DecisionCard } from './DecisionCard';
import type { DrsReviewResponse } from '../types';

describe('DecisionCard Component', () => {
    it('renders loading state when review is null', () => {
        render(<DecisionCard review={null} />);
        expect(screen.getByText('Loading...')).toBeInTheDocument();
    });

    it('renders OUT decision correctly', () => {
        const mockReview: DrsReviewResponse = {
            gameId: 1, processingJobId: 1, analysisJobId: 1,
            analysisStatus: 'COMPLETED', analysisCreatedAt: '', analysisStartedAt: null, analysisCompletedAt: null, analysisErrorMessage: null,
            totalFrames: 100, processedFrames: 100, progressPercent: 100,
            detectedBallFrameCount: 50, trajectoryPointCount: 40, cricketEventCount: 5,
            lbwAnalysisStatus: 'COMPLETED', lbwConfidence: 0.9,
            decisionStatus: 'COMPLETED', decision: 'OUT', reasonCode: 'WICKET_HITTING', drsConfidence: 0.95,
            replay: null, timeline: null
        };
        
        render(<DecisionCard review={mockReview} />);
        
        const badge = screen.getByText('OUT');
        expect(badge).toBeInTheDocument();
        expect(badge).toHaveClass('out');
        expect(screen.getByText('WICKET HITTING')).toBeInTheDocument();
    });

    it('renders NOT_OUT decision correctly', () => {
        const mockReview: DrsReviewResponse = {
            gameId: 1, processingJobId: 1, analysisJobId: 1,
            analysisStatus: 'COMPLETED', analysisCreatedAt: '', analysisStartedAt: null, analysisCompletedAt: null, analysisErrorMessage: null,
            totalFrames: 100, processedFrames: 100, progressPercent: 100,
            detectedBallFrameCount: 50, trajectoryPointCount: 40, cricketEventCount: 5,
            lbwAnalysisStatus: 'COMPLETED', lbwConfidence: 0.9,
            decisionStatus: 'COMPLETED', decision: 'NOT_OUT', reasonCode: 'PITCHED_OUTSIDE_LEG', drsConfidence: 0.85,
            replay: null, timeline: null
        };
        
        render(<DecisionCard review={mockReview} />);
        
        const badge = screen.getByText('NOT_OUT');
        expect(badge).toBeInTheDocument();
        expect(badge).toHaveClass('not-out');
        expect(screen.getByText('PITCHED OUTSIDE LEG')).toBeInTheDocument();
    });

    it('renders INSUFFICIENT_DATA decision correctly', () => {
        const mockReview: DrsReviewResponse = {
            gameId: 1, processingJobId: 1, analysisJobId: 1,
            analysisStatus: 'COMPLETED', analysisCreatedAt: '', analysisStartedAt: null, analysisCompletedAt: null, analysisErrorMessage: null,
            totalFrames: 10, processedFrames: 10, progressPercent: 100,
            detectedBallFrameCount: 0, trajectoryPointCount: 0, cricketEventCount: 0,
            lbwAnalysisStatus: 'INSUFFICIENT_DATA', lbwConfidence: null,
            decisionStatus: 'COMPLETED', decision: 'INSUFFICIENT_DATA', reasonCode: 'NO_BALL_DETECTED', drsConfidence: null,
            replay: null, timeline: null
        };
        
        render(<DecisionCard review={mockReview} />);
        
        const badge = screen.getByText('INSUFFICIENT_DATA');
        expect(badge).toBeInTheDocument();
        expect(screen.getByText('NO BALL DETECTED')).toBeInTheDocument();
    });

    it('renders PROCESSING state correctly', () => {
        const mockReview: DrsReviewResponse = {
            gameId: 1, processingJobId: 1, analysisJobId: 1,
            analysisStatus: 'PROCESSING', analysisCreatedAt: '', analysisStartedAt: null, analysisCompletedAt: null, analysisErrorMessage: null,
            totalFrames: 100, processedFrames: 50, progressPercent: 50,
            detectedBallFrameCount: null, trajectoryPointCount: null, cricketEventCount: null,
            lbwAnalysisStatus: null, lbwConfidence: null,
            decisionStatus: 'PROCESSING', decision: null, reasonCode: null, drsConfidence: null,
            replay: null, timeline: null
        };
        
        render(<DecisionCard review={mockReview} />);
        
        const badge = screen.getByText('PENDING');
        expect(badge).toBeInTheDocument();
        expect(badge).toHaveClass('pending');
    });

    it('renders FAILED state correctly', () => {
        const mockReview: DrsReviewResponse = {
            gameId: 1, processingJobId: 1, analysisJobId: 1,
            analysisStatus: 'FAILED', analysisCreatedAt: '', analysisStartedAt: null, analysisCompletedAt: null, analysisErrorMessage: 'Frame extraction failed',
            totalFrames: null, processedFrames: null, progressPercent: null,
            detectedBallFrameCount: null, trajectoryPointCount: null, cricketEventCount: null,
            lbwAnalysisStatus: null, lbwConfidence: null,
            decisionStatus: 'FAILED', decision: null, reasonCode: null, drsConfidence: null,
            replay: null, timeline: null
        };
        
        render(<DecisionCard review={mockReview} />);
        
        const badge = screen.getByText('FAILED');
        expect(badge).toBeInTheDocument();
        expect(badge).toHaveClass('failed');
        expect(screen.getByText('Error: Frame extraction failed')).toBeInTheDocument();
    });
});
