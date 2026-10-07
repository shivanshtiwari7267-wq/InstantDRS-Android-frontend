package com.example.instantdrs_android.data.remote

data class DrsReviewWorkflowResponse(
    val reviewId: String,
    val status: String,
    val progressPercent: Double?,
    val decisionStatus: String?,
    val decision: String?,
    val confidence: Double?,
    val evidenceAvailable: Boolean?,
    val evidenceType: String?,
    val evidenceVideoUrl: String?,
    val result: DrsReviewResponse?,
    val errorMessage: String?
)

data class DrsReviewResponse(
    val gameId: Long,
    val processingJobId: Long,
    val analysisJobId: Long,
    val analysisStatus: String,
    val decisionStatus: String?,
    val decision: String?,
    val reasonCode: String?,
    val drsConfidence: Double?,
    val replay: DrsReplayResponse?
)

data class DrsReplayResponse(
    val status: String?,
    val available: Boolean?,
    val outputEndpoint: String?
)
