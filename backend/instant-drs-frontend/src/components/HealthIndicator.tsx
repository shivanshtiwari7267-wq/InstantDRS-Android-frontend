import React, { useEffect, useState } from 'react';
import { api } from '../services/api';
import type { VideoPipelineHealthResponse } from '../types';

export const HealthIndicator: React.FC = () => {
    const [health, setHealth] = useState<VideoPipelineHealthResponse | null>(null);
    const [error, setError] = useState(false);

    useEffect(() => {
        api.getHealth()
            .then(data => {
                setHealth(data);
                setError(false);
            })
            .catch(() => {
                setError(true);
            });
    }, []);

    const isHealthy = health && health.ffmpegConfigured && health.storageRootAvailable;

    return (
        <div className="health-indicator">
            <div className={`health-dot ${error ? 'error' : isHealthy ? 'healthy' : 'degraded'}`}></div>
            <span>{error ? 'Backend Offline' : isHealthy ? 'Pipeline Healthy' : 'Pipeline Degraded'}</span>
        </div>
    );
};
