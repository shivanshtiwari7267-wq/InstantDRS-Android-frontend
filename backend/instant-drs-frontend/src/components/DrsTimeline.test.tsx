import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { DrsTimeline } from './DrsTimeline';
import type { DrsTimelineEventDto } from '../types';

describe('DrsTimeline Component', () => {
    it('renders loading state when timeline is null', () => {
        render(<DrsTimeline timeline={null} />);
        expect(screen.getByText('Loading timeline...')).toBeInTheDocument();
    });

    it('renders empty state when timeline has no events', () => {
        render(<DrsTimeline timeline={[]} />);
        expect(screen.getByText('No events detected yet.')).toBeInTheDocument();
    });

    it('renders timeline events correctly', () => {
        const mockTimeline: DrsTimelineEventDto[] = [
            { frameNumber: 10, timestampSeconds: 0.33, eventType: 'DELIVERY_SEGMENT', label: 'Delivery Start', confidence: null },
            { frameNumber: 30, timestampSeconds: 1.0, eventType: 'BALL_BOUNCE_CANDIDATE', label: 'Bounce Detected', confidence: 0.95 }
        ];

        render(<DrsTimeline timeline={mockTimeline} />);
        
        expect(screen.getByText('Delivery Start')).toBeInTheDocument();
        expect(screen.getByText('Type: DELIVERY_SEGMENT')).toBeInTheDocument();
        
        expect(screen.getByText('Bounce Detected')).toBeInTheDocument();
        expect(screen.getByText('Type: BALL_BOUNCE_CANDIDATE • Confidence: 95.0%')).toBeInTheDocument();
    });
});
