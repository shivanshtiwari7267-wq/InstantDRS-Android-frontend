package com.example.instantdrs_android.data.remote

data class DrsReviewWorkflowResponse(
    val reviewId: String,
    val status: String,
    val progressPercent: Double,
    val result: DrsReviewResponse?,
    val errorMessage: String?,
    val hasReplay: Boolean? = false,
    val isFallback: Boolean? = false,
    val evidenceVideoUrl: String? = null
)

data class DrsReviewResponse(
    val gameId: Long,
    val processingJobId: Long,
    val analysisJobId: Long,
    val analysisStatus: String,
    val decisionStatus: String?,
    val decision: String?,
    val reasonCode: String?,
    val drsConfidence: Double?
)
