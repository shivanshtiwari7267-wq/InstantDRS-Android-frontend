package com.instantdrs.android.data

import com.instantdrs.android.model.DrsReplayResponse
import com.instantdrs.android.model.DrsReviewResponse
import com.instantdrs.android.model.DrsTimelineEventDto
import com.instantdrs.android.model.EvidenceQualityResponse
import com.instantdrs.android.model.VideoAnalysisResultResponse
import com.instantdrs.android.model.VideoPipelineStatusResponse
import com.instantdrs.android.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReviewRepository(private val apiService: ApiService) {

    suspend fun getReview(gameId: Long, processingJobId: Long, analysisJobId: Long): Result<DrsReviewResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getReview(gameId, processingJobId, analysisJobId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch review: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getResult(gameId: Long, processingJobId: Long, analysisJobId: Long): Result<VideoAnalysisResultResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getResult(gameId, processingJobId, analysisJobId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch result: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getTimeline(gameId: Long, processingJobId: Long, analysisJobId: Long): Result<List<DrsTimelineEventDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getTimeline(gameId, processingJobId, analysisJobId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch timeline: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getReplay(gameId: Long, processingJobId: Long, analysisJobId: Long): Result<DrsReplayResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getReplay(gameId, processingJobId, analysisJobId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch replay: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getPipelineStatus(gameId: Long, processingJobId: Long, analysisJobId: Long): Result<VideoPipelineStatusResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPipelineStatus(gameId, processingJobId, analysisJobId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch pipeline status: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getEvidenceQuality(gameId: Long, processingJobId: Long, analysisJobId: Long): Result<EvidenceQualityResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getEvidenceQuality(gameId, processingJobId, analysisJobId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch evidence quality: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
