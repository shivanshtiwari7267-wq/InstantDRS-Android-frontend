import { render, screen, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { DrsReviewDashboard } from './DrsReviewDashboard';
import { api } from '../services/api';
import type { DrsReviewResponse, VideoPipelineStatusResponse } from '../types';

vi.mock('../services/api');

describe('DrsReviewDashboard', () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it('displays polling/processing state', async () => {
        vi.mocked(api.getReview).mockResolvedValueOnce({
            analysisStatus: 'PROCESSING',
            decisionStatus: 'PROCESSING'
        } as unknown as DrsReviewResponse);
        
        vi.mocked(api.getPipelineStatus).mockResolvedValueOnce({
            processingStatus: 'PROCESSING',
            analysisStatus: 'PROCESSING'
        } as unknown as VideoPipelineStatusResponse);
        
        vi.mocked(api.getHealth).mockResolvedValueOnce({} as any);
        
        vi.mocked(api.getResult).mockRejectedValueOnce({ status: 404 });
        vi.mocked(api.getEvidenceQuality).mockRejectedValueOnce({ status: 404 });

        render(<DrsReviewDashboard />);

        await waitFor(() => {
            expect(screen.getByText('PENDING')).toBeInTheDocument();
        });
    });

    it('displays error state when backend is unavailable', async () => {
        vi.mocked(api.getReview).mockRejectedValueOnce({ status: 500, message: 'Server error' });
        vi.mocked(api.getPipelineStatus).mockRejectedValueOnce({ status: 500 });
        vi.mocked(api.getResult).mockRejectedValueOnce({ status: 500 });
        vi.mocked(api.getEvidenceQuality).mockRejectedValueOnce({ status: 500 });
        vi.mocked(api.getHealth).mockRejectedValueOnce({ status: 500 });

        render(<DrsReviewDashboard />);

        await waitFor(() => {
            expect(screen.getByText('Connection Error')).toBeInTheDocument();
            expect(screen.getByText('Server error')).toBeInTheDocument();
        });
    });
});
