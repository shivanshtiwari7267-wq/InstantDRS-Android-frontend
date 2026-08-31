import type {
    DrsReviewResponse,
    VideoAnalysisResultResponse,
    DrsTimelineEventDto,
    DrsReplayResponse,
    VideoPipelineStatusResponse,
    VideoPipelineHealthResponse,
    EvidenceQualityResponse
} from '../types';

export class ApiError extends Error {
    public status: number;
    constructor(message: string, status: number) {
        super(message);
        this.status = status;
        this.name = 'ApiError';
    }
}

async function handleResponse<T>(response: Response): Promise<T> {
    if (!response.ok) {
        if (response.status === 401 || response.status === 403) {
            window.dispatchEvent(new Event('auth-error'));
        }
        throw new ApiError(`HTTP error! status: ${response.status}`, response.status);
    }
    const text = await response.text();
    return text ? JSON.parse(text) : {} as T;
}

const API_BASE = import.meta.env.VITE_API_BASE_URL || '';

const getHeaders = () => {
    const token = localStorage.getItem('token');
    const headers: Record<string, string> = {
        'Content-Type': 'application/json'
    };
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    return headers;
};

export const api = {
    login: async (username: string, password: string): Promise<any> => {
        const response = await fetch(`${API_BASE}/api/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });
        return handleResponse<any>(response);
    },

    getReview: async (gameId: number, processingJobId: number, analysisJobId: number): Promise<DrsReviewResponse> => {
        const response = await fetch(`${API_BASE}/api/games/${gameId}/session/processing/${processingJobId}/analysis/${analysisJobId}/review`, { headers: getHeaders() });
        return handleResponse<DrsReviewResponse>(response);
    },

    getResult: async (gameId: number, processingJobId: number, analysisJobId: number): Promise<VideoAnalysisResultResponse> => {
        const response = await fetch(`${API_BASE}/api/games/${gameId}/session/processing/${processingJobId}/analysis/${analysisJobId}/result`, { headers: getHeaders() });
        return handleResponse<VideoAnalysisResultResponse>(response);
    },

    getTimeline: async (gameId: number, processingJobId: number, analysisJobId: number): Promise<DrsTimelineEventDto[]> => {
        const response = await fetch(`${API_BASE}/api/games/${gameId}/session/processing/${processingJobId}/analysis/${analysisJobId}/timeline`, { headers: getHeaders() });
        return handleResponse<DrsTimelineEventDto[]>(response);
    },

    getReplay: async (gameId: number, processingJobId: number, analysisJobId: number): Promise<DrsReplayResponse> => {
        const response = await fetch(`${API_BASE}/api/games/${gameId}/session/processing/${processingJobId}/analysis/${analysisJobId}/review/replay`, { headers: getHeaders() });
        return handleResponse<DrsReplayResponse>(response);
    },

    getPipelineStatus: async (gameId: number, processingJobId: number, analysisJobId: number): Promise<VideoPipelineStatusResponse> => {
        const response = await fetch(`${API_BASE}/api/games/${gameId}/session/processing/${processingJobId}/analysis/${analysisJobId}/pipeline-status`, { headers: getHeaders() });
        return handleResponse<VideoPipelineStatusResponse>(response);
    },

    getHealth: async (): Promise<VideoPipelineHealthResponse> => {
        const response = await fetch(`${API_BASE}/api/health/video-pipeline`, { headers: getHeaders() });
        return handleResponse<VideoPipelineHealthResponse>(response);
    },

    getEvidenceQuality: async (gameId: number, processingJobId: number, analysisJobId: number): Promise<EvidenceQualityResponse> => {
        const response = await fetch(`${API_BASE}/api/games/${gameId}/session/processing/${processingJobId}/analysis/${analysisJobId}/evidence-quality`, { headers: getHeaders() });
        return handleResponse<EvidenceQualityResponse>(response);
    }
};
