import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { ReplayPlayer } from './ReplayPlayer';
import type { DrsReplayResponse } from '../types';

describe('ReplayPlayer Component', () => {
    it('renders checking status when replay is null', () => {
        render(<ReplayPlayer replay={null} />);
        expect(screen.getByText('Checking replay status...')).toBeInTheDocument();
    });

    it('renders generation failed state correctly', () => {
        const mockReplay: DrsReplayResponse = {
            replayJobId: 1,
            status: 'FAILED',
            available: false,
            outputEndpoint: null,
            createdAt: '',
            completedAt: '',
            errorMessage: 'FFmpeg processing failed'
        };
        
        render(<ReplayPlayer replay={mockReplay} />);
        expect(screen.getByText('Replay Generation Failed')).toBeInTheDocument();
        expect(screen.getByText('FFmpeg processing failed')).toBeInTheDocument();
    });

    it('renders processing state when unavailable', () => {
        const mockReplay: DrsReplayResponse = {
            replayJobId: 1,
            status: 'PROCESSING',
            available: false,
            outputEndpoint: null,
            createdAt: '',
            completedAt: null,
            errorMessage: null
        };
        
        render(<ReplayPlayer replay={mockReplay} />);
        expect(screen.getByText('Replay is being generated.')).toBeInTheDocument();
    });

    it('renders video player when available', () => {
        const mockReplay: DrsReplayResponse = {
            replayJobId: 1,
            status: 'COMPLETED',
            available: true,
            outputEndpoint: '/replays/1/output.mp4',
            createdAt: '',
            completedAt: '',
            errorMessage: null
        };
        
        render(<ReplayPlayer replay={mockReplay} />);
        expect(screen.getByText('DRS Replay')).toBeInTheDocument();
        
        // Since we are not actually rendering a visible video element role, let's just use querySelector
        const videoEl = document.querySelector('video');
        expect(videoEl).toBeInTheDocument();
        expect(videoEl?.src).toContain('/replays/1/output.mp4');
    });
});
