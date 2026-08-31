export interface DrsTimelineEventDto {
    frameNumber: number;
    timestampSeconds: number;
    eventType: string;
    label: string;
    confidence: number | null;
}

export interface DrsReplayResponse {
    replayJobId: number;
    status: string;
    available: boolean;
    outputEndpoint: string | null;
    createdAt: string;
    completedAt: string | null;
    errorMessage: string | null;
}

export interface DrsReviewResponse {
    gameId: number;
    processingJobId: number;
    analysisJobId: number;
    
    analysisStatus: string;
    analysisCreatedAt: string;
    analysisStartedAt: string | null;
    analysisCompletedAt: string | null;
    analysisErrorMessage: string | null;
    
    totalFrames: number | null;
    processedFrames: number | null;
    progressPercent: number | null;
    detectedBallFrameCount: number | null;
    trajectoryPointCount: number | null;
    cricketEventCount: number | null;
    lbwAnalysisStatus: string | null;
    lbwConfidence: number | null;
    
    decisionStatus: string;
    decision: string | null;
    reasonCode: string | null;
    drsConfidence: number | null;
    
    replay: DrsReplayResponse | null;
    timeline: DrsTimelineEventDto[] | null;
}

export interface VideoPipelineHealthResponse {
    processingJobsQueued: number;
    processingJobsProcessing: number;
    processingJobsCompleted: number;
    processingJobsFailed: number;
    
    analysisJobsQueued: number;
    analysisJobsProcessing: number;
    analysisJobsCompleted: number;
    analysisJobsFailed: number;
    
    replayJobsQueued: number;
    replayJobsProcessing: number;
    replayJobsCompleted: number;
    replayJobsFailed: number;
    
    ffmpegConfigured: boolean;
    storageRootAvailable: boolean;
}

export interface VideoPipelineStatusResponse {
    processingStatus: string;
    analysisStatus: string;
    totalFrames: number | null;
    processedFrames: number | null;
    progressPercent: number | null;
    cricketEventsAvailable: boolean;
    lbwAnalysisAvailable: boolean;
    drsDecisionAvailable: boolean;
    replayAvailable: boolean;
    replayStatus: string;
    overallPipelineReady: boolean;
}

export interface VideoAnalysisJobSummaryDto {
    jobId: number;
    status: string;
    errorMessage: string | null;
}

export interface VideoFrameAnalysisSummaryDto {
    totalFrames: number;
    processedFrames: number;
    progressPercent: number;
}

export interface VideoBallTrackPointDto {
    frameNumber: number;
    timestampSeconds: number;
    x: number;
    y: number;
    radius: number;
    confidence: number;
    discontinuity: boolean;
}

export interface VideoBallTrackingSummaryDto {
    totalDetectedFrames: number;
    discontinuities: number;
    averageConfidence: number;
    points: VideoBallTrackPointDto[];
}

export interface VideoTrajectorySegmentDto {
    segmentNumber: number;
    startFrame: number;
    endFrame: number;
    startX: number;
    startY: number;
    endX: number;
    endY: number;
    averageVelocity: number;
}

export interface VideoTrajectorySummaryDto {
      pointCount: number;
      points: any[];
      eventCount: number;
      events: any[];
  }

export interface VideoCricketEventDto {
    eventType: string;
    startFrameNumber: number;
    endFrameNumber: number | null;
    startTimestampSeconds: number;
    endTimestampSeconds: number | null;
    confidence: number | null;
}

export interface VideoCricketEventsSummaryDto {
      eventCount: number;
      events: VideoCricketEventDto[];
  }

export interface VideoLbwEvidenceDto {
    evidenceType: string;
    frameNumber: number;
    description: string;
    x: number | null;
    y: number | null;
}

export interface VideoLbwAnalysisSummaryDto {
      analysisCount: number;
      analyses: any[];
  }

export interface VideoDrsDecisionDto {
    decision: string;
    status: string;
    reasonCode: string | null;
    confidence: number | null;
}

export interface VideoAnalysisResultResponse {
    analysisJob: VideoAnalysisJobSummaryDto;
    frameAnalysis: VideoFrameAnalysisSummaryDto;
    ballTracking: VideoBallTrackingSummaryDto;
    trajectory: VideoTrajectorySummaryDto;
    cricketEvents: VideoCricketEventsSummaryDto;
    lbwAnalysis: VideoLbwAnalysisSummaryDto;
    drsDecisions: VideoDrsDecisionDto[];
}

export type EvidenceReadiness = 'READY' | 'PARTIAL' | 'INSUFFICIENT_DATA';

export type EvidenceReasonCode = 
    | 'TRACKING_INSUFFICIENT'
    | 'TRAJECTORY_INSUFFICIENT'
    | 'BOUNCE_EVIDENCE_MISSING'
    | 'IMPACT_EVIDENCE_MISSING'
    | 'PROJECTION_INSUFFICIENT'
    | 'WICKET_INTERSECTION_UNAVAILABLE'
    | 'LOW_EVIDENCE_CONFIDENCE'
    | 'MULTIPLE_CONFLICTING_CANDIDATES'
    | 'EVIDENCE_COMPLETE'
    | string;

export interface TrackingQualityDto {
    totalPoints: number;
    validPoints: number;
    missingPoints: number;
    minConfidence: number | null;
    maxConfidence: number | null;
    avgConfidence: number | null;
}

export interface TrajectoryQualityDto {
    totalPoints: number;
    validDerivatives: number;
    invalidDerivatives: number;
    trajectorySegmentCount: number;
    minSpeed: number | null;
    maxSpeed: number | null;
    avgSpeed: number | null;
}

export interface BounceQualityDto {
    available: boolean;
    candidateCount: number;
    strongestConfidence: number | null;
    candidateFrameNumbers: number[];
}

export interface ImpactQualityDto {
    available: boolean;
    candidateCount: number;
    strongestConfidence: number | null;
    candidateFrameNumbers: number[];
}

export interface ProjectionQualityDto {
    available: boolean;
    impactPositionX: number | null;
    impactPositionY: number | null;
    wicketIntersectionAvailable: boolean;
    confidence: number | null;
}

export interface DrsDecisionQualityDto {
    decision: string | null;
    reasonCode: string | null;
    confidence: number | null;
}

export interface EvidenceQualityResponse {
    analysisJobId: number;
    readiness: EvidenceReadiness;
    reasonCodes: EvidenceReasonCode[];
    overallEvidenceQuality: number | null;
    tracking: TrackingQualityDto;
    trajectory: TrajectoryQualityDto;
    bounce: BounceQualityDto;
    impact: ImpactQualityDto;
    projection: ProjectionQualityDto;
    drsDecision: DrsDecisionQualityDto;
}
