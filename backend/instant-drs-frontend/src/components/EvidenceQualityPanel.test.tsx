import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { EvidenceQualityPanel } from './EvidenceQualityPanel';
import type { EvidenceQualityResponse } from '../types';

describe('EvidenceQualityPanel', () => {
    it('renders nothing when notFound is true', () => {
        const { container } = render(<EvidenceQualityPanel evidenceQuality={null} loading={false} error={null} notFound={true} />);
        expect(container.firstChild).toBeNull();
    });

    it('renders error state correctly', () => {
        render(<EvidenceQualityPanel evidenceQuality={null} loading={false} error="API Error" notFound={false} />);
        expect(screen.getByText('Evidence Quality unavailable. Unable to retrieve evidence-quality data.')).toBeInTheDocument();
    });

    it('renders loading state correctly', () => {
        render(<EvidenceQualityPanel evidenceQuality={null} loading={true} error={null} notFound={false} />);
        expect(screen.getByText('Waiting for analysis...')).toBeInTheDocument();
    });

    it('renders EvidenceQuality correctly for READY state', () => {
        const mockResponse: EvidenceQualityResponse = {
            analysisJobId: 1,
            readiness: 'READY',
            reasonCodes: ['EVIDENCE_COMPLETE'],
            overallEvidenceQuality: 1.0,
            tracking: { totalPoints: 50, validPoints: 49, missingPoints: 1, minConfidence: 0.8, maxConfidence: 0.9, avgConfidence: 0.85 },
            trajectory: { totalPoints: 50, validDerivatives: 45, invalidDerivatives: 5, trajectorySegmentCount: 1, minSpeed: 20, maxSpeed: 40, avgSpeed: 30 },
            bounce: { available: true, candidateCount: 1, strongestConfidence: 0.9, candidateFrameNumbers: [25] },
            impact: { available: true, candidateCount: 1, strongestConfidence: 0.9, candidateFrameNumbers: [45] },
            projection: { available: true, impactPositionX: 1, impactPositionY: 1, wicketIntersectionAvailable: true, confidence: 0.9 },
            drsDecision: { decision: 'OUT', reasonCode: 'IMPACT_IN_LINE', confidence: 0.9 }
        };

        render(<EvidenceQualityPanel evidenceQuality={mockResponse} loading={false} error={null} notFound={false} />);
        expect(screen.getByText('READY')).toBeInTheDocument();
        expect(screen.queryByText('Why?')).toBeNull(); // Shouldn't show Why if complete
        expect(screen.getByText('Tracking')).toBeInTheDocument();
        expect(screen.getByText('Trajectory')).toBeInTheDocument();
        expect(screen.getByText('Bounce')).toBeInTheDocument();
        expect(screen.getByText('Impact')).toBeInTheDocument();
        expect(screen.getByText('Projection')).toBeInTheDocument();
    });

    it('renders EvidenceQuality correctly for PARTIAL state with reasons', () => {
        const mockResponse: EvidenceQualityResponse = {
            analysisJobId: 1,
            readiness: 'PARTIAL',
            reasonCodes: ['MULTIPLE_CONFLICTING_CANDIDATES'],
            overallEvidenceQuality: 0.8,
            tracking: { totalPoints: 50, validPoints: 49, missingPoints: 1, minConfidence: 0.8, maxConfidence: 0.9, avgConfidence: 0.85 },
            trajectory: { totalPoints: 50, validDerivatives: 45, invalidDerivatives: 5, trajectorySegmentCount: 1, minSpeed: 20, maxSpeed: 40, avgSpeed: 30 },
            bounce: { available: true, candidateCount: 2, strongestConfidence: 0.9, candidateFrameNumbers: [25, 27] },
            impact: { available: true, candidateCount: 1, strongestConfidence: 0.9, candidateFrameNumbers: [45] },
            projection: { available: true, impactPositionX: 1, impactPositionY: 1, wicketIntersectionAvailable: true, confidence: 0.9 },
            drsDecision: { decision: 'INSUFFICIENT_DATA', reasonCode: null, confidence: 0.0 }
        };

        render(<EvidenceQualityPanel evidenceQuality={mockResponse} loading={false} error={null} notFound={false} />);
        expect(screen.getAllByText('PARTIAL')[0]).toBeInTheDocument();
        expect(screen.getByText('Why?')).toBeInTheDocument();
        expect(screen.getByText('Multiple conflicting candidates')).toBeInTheDocument();
        
        // Check partial bounce formatting
        expect(screen.getByText('Candidates: 2')).toBeInTheDocument();
    });

    it('renders safe fallback for unknown reason codes', () => {
        const mockResponse: EvidenceQualityResponse = {
            analysisJobId: 1,
            readiness: 'INSUFFICIENT_DATA',
            reasonCodes: ['SOME_UNKNOWN_CODE'],
            overallEvidenceQuality: 0.0,
            tracking: { totalPoints: 0, validPoints: 0, missingPoints: 0, minConfidence: null, maxConfidence: null, avgConfidence: null },
            trajectory: { totalPoints: 0, validDerivatives: 0, invalidDerivatives: 0, trajectorySegmentCount: 0, minSpeed: null, maxSpeed: null, avgSpeed: null },
            bounce: { available: false, candidateCount: 0, strongestConfidence: null, candidateFrameNumbers: [] },
            impact: { available: false, candidateCount: 0, strongestConfidence: null, candidateFrameNumbers: [] },
            projection: { available: false, impactPositionX: null, impactPositionY: null, wicketIntersectionAvailable: false, confidence: null },
            drsDecision: { decision: 'INSUFFICIENT_DATA', reasonCode: 'NO_TRACKING', confidence: 0.0 }
        };

        render(<EvidenceQualityPanel evidenceQuality={mockResponse} loading={false} error={null} notFound={false} />);
        expect(screen.getAllByText('INSUFFICIENT_DATA')[0]).toBeInTheDocument();
        expect(screen.getByText('Reason: SOME_UNKNOWN_CODE')).toBeInTheDocument();
    });

    it('does not crash if a dimension is missing from response', () => {
        // @ts-ignore - deliberately omitting fields to test safety
        const mockResponse: EvidenceQualityResponse = {
            analysisJobId: 1,
            readiness: 'INSUFFICIENT_DATA',
            reasonCodes: [],
            overallEvidenceQuality: null,
            // tracking missing entirely
            trajectory: { totalPoints: 0, validDerivatives: 0, invalidDerivatives: 0, trajectorySegmentCount: 0, minSpeed: null, maxSpeed: null, avgSpeed: null },
            // bounce missing entirely
            impact: { available: false, candidateCount: 0, strongestConfidence: null, candidateFrameNumbers: [] },
            projection: { available: false, impactPositionX: null, impactPositionY: null, wicketIntersectionAvailable: false, confidence: null },
            drsDecision: { decision: 'INSUFFICIENT_DATA', reasonCode: null, confidence: 0.0 }
        };

        render(<EvidenceQualityPanel evidenceQuality={mockResponse} loading={false} error={null} notFound={false} />);
        expect(screen.getAllByText('INSUFFICIENT_DATA')[0]).toBeInTheDocument();
        expect(screen.queryByText('Tracking')).toBeNull();
        expect(screen.queryByText('Bounce')).toBeNull();
        expect(screen.getByText('Trajectory')).toBeInTheDocument();
    });
});
