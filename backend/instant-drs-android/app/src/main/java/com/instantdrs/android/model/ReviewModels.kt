package com.instantdrs.android.model

data class DrsTimelineEventDto(
    val frameNumber: Int,
    val timestampSeconds: Double,
    val eventType: String,
    val label: String,
    val confidence: Double?
)

data class VideoProcessingJobResponse(
    val id: Long,
    val status: String,
    val errorMessage: String?
)

data class VideoAnalysisJobResponse(
    val id: Long,
    val processingJobId: Long,
    val status: String,
    val errorMessage: String?
)

data class DrsReplayResponse(
    val replayJobId: Long,
    val status: String,
    val available: Boolean,
    val outputEndpoint: String?,
    val createdAt: String,
    val completedAt: String?,
    val errorMessage: String?
)

data class DrsReviewResponse(
    val gameId: Long,
    val processingJobId: Long,
    val analysisJobId: Long,
    
    val analysisStatus: String,
    val analysisCreatedAt: String,
    val analysisStartedAt: String?,
    val analysisCompletedAt: String?,
    val analysisErrorMessage: String?,
    
    val totalFrames: Int?,
    val processedFrames: Int?,
    val progressPercent: Double?,
    val detectedBallFrameCount: Int?,
    val trajectoryPointCount: Int?,
    val cricketEventCount: Int?,
    val lbwAnalysisStatus: String?,
    val lbwConfidence: Double?,
    
    val decisionStatus: String,
    val decision: String?,
    val reasonCode: String?,
    val drsConfidence: Double?,
    
    val replay: DrsReplayResponse?,
    val timeline: List<DrsTimelineEventDto>?,
    val ruleName: String?
)

data class VideoPipelineStatusResponse(
    val processingStatus: String,
    val analysisStatus: String,
    val totalFrames: Int?,
    val processedFrames: Int?,
    val progressPercent: Double?,
    val cricketEventsAvailable: Boolean,
    val lbwAnalysisAvailable: Boolean,
    val drsDecisionAvailable: Boolean,
    val replayAvailable: Boolean,
    val replayStatus: String,
    val overallPipelineReady: Boolean
)

data class VideoAnalysisJobSummaryDto(
    val jobId: Long,
    val status: String,
    val errorMessage: String?
)

data class VideoFrameAnalysisSummaryDto(
    val totalFrames: Int,
    val processedFrames: Int,
    val progressPercent: Double
)

data class VideoBallTrackPointDto(
    val frameNumber: Int,
    val timestampSeconds: Double,
    val x: Double,
    val y: Double,
    val radius: Double,
    val confidence: Double,
    val discontinuity: Boolean
)

data class VideoBallTrackingSummaryDto(
    val totalDetectedFrames: Int,
    val discontinuities: Int,
    val averageConfidence: Double,
    val points: List<VideoBallTrackPointDto>
)

data class VideoTrajectorySegmentDto(
    val segmentNumber: Int,
    val startFrame: Int,
    val endFrame: Int,
    val startX: Double,
    val startY: Double,
    val endX: Double,
    val endY: Double,
    val averageVelocity: Double
)

data class VideoTrajectorySummaryDto(
    val pointCount: Int,
    val points: List<Any>,
    val eventCount: Int,
    val events: List<Any>
)

data class VideoCricketEventDto(
    val eventType: String,
    val startFrameNumber: Int,
    val endFrameNumber: Int?,
    val startTimestampSeconds: Double,
    val endTimestampSeconds: Double?,
    val confidence: Double?
)

data class VideoCricketEventsSummaryDto(
    val eventCount: Int,
    val events: List<VideoCricketEventDto>
)

data class VideoLbwEvidenceDto(
    val evidenceType: String,
    val frameNumber: Int,
    val description: String,
    val x: Double?,
    val y: Double?
)

data class VideoLbwAnalysisSummaryDto(
    val analysisCount: Int,
    val analyses: List<Any>
)

data class VideoDrsDecisionDto(
    val decision: String,
    val status: String,
    val reasonCode: String?,
    val confidence: Double?
)

data class VideoAnalysisResultResponse(
    val analysisJob: VideoAnalysisJobSummaryDto,
    val frameAnalysis: VideoFrameAnalysisSummaryDto,
    val ballTracking: VideoBallTrackingSummaryDto,
    val trajectory: VideoTrajectorySummaryDto,
    val cricketEvents: VideoCricketEventsSummaryDto,
    val lbwAnalysis: VideoLbwAnalysisSummaryDto,
    val drsDecisions: List<VideoDrsDecisionDto>
)

data class TrackingQualityDto(
    val totalPoints: Int,
    val validPoints: Int,
    val missingPoints: Int,
    val minConfidence: Double?,
    val maxConfidence: Double?,
    val avgConfidence: Double?
)

data class TrajectoryQualityDto(
    val totalPoints: Int,
    val validDerivatives: Int,
    val invalidDerivatives: Int,
    val trajectorySegmentCount: Int,
    val minSpeed: Double?,
    val maxSpeed: Double?,
    val avgSpeed: Double?
)

data class BounceQualityDto(
    val available: Boolean,
    val candidateCount: Int,
    val strongestConfidence: Double?,
    val candidateFrameNumbers: List<Int>
)

data class ImpactQualityDto(
    val available: Boolean,
    val candidateCount: Int,
    val strongestConfidence: Double?,
    val candidateFrameNumbers: List<Int>
)

data class ProjectionQualityDto(
    val available: Boolean,
    val impactPositionX: Double?,
    val impactPositionY: Double?,
    val wicketIntersectionAvailable: Boolean,
    val confidence: Double?
)

data class DrsDecisionQualityDto(
    val decision: String?,
    val reasonCode: String?,
    val confidence: Double?
)

data class EvidenceQualityResponse(
    val analysisJobId: Long,
    val readiness: String,
    val reasonCodes: List<String>,
    val overallEvidenceQuality: Double?,
    val tracking: TrackingQualityDto,
    val trajectory: TrajectoryQualityDto,
    val bounce: BounceQualityDto,
    val impact: ImpactQualityDto,
    val projection: ProjectionQualityDto,
    val drsDecision: DrsDecisionQualityDto
)
