package com.instantdrs.android.data

import com.instantdrs.android.model.VideoAnalysisJobResponse
import com.instantdrs.android.model.VideoPipelineStatusResponse
import com.instantdrs.android.model.VideoProcessingJobResponse
import com.instantdrs.android.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProcessingRepository(private val apiService: ApiService) {

    suspend fun getProcessingJob(gameId: Long, processingJobId: Long): Result<VideoProcessingJobResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getProcessingJob(gameId, processingJobId)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed to get processing job: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createAnalysisJob(gameId: Long, processingJobId: Long): Result<VideoAnalysisJobResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createAnalysisJob(gameId, processingJobId)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed to create analysis job: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAnalysisJobs(gameId: Long, processingJobId: Long): Result<List<VideoAnalysisJobResponse>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getAnalysisJobs(gameId, processingJobId)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed to get analysis jobs: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPipelineStatus(gameId: Long, processingJobId: Long, analysisJobId: Long): Result<VideoPipelineStatusResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getPipelineStatus(gameId, processingJobId, analysisJobId)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed to get pipeline status: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
