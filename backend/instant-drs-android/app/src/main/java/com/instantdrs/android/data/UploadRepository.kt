package com.instantdrs.android.data

import com.instantdrs.android.model.GameSessionResponse
import com.instantdrs.android.model.VideoProcessingJobResponse
import com.instantdrs.android.network.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UploadRepository(private val apiService: ApiService) {

    suspend fun startRecording(gameId: Long): Result<Any> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.startRecording(gameId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to start recording: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun stopSession(gameId: Long): Result<Any> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.stopSession(gameId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to stop session: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadRecording(gameId: Long, file: File): Result<GameSessionResponse> = withContext(Dispatchers.IO) {
        try {
            val requestFile = file.asRequestBody("video/mp4".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            val response = apiService.uploadRecording(gameId, body)
            
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Upload failed: ${response.code()} - ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createProcessingJob(gameId: Long): Result<VideoProcessingJobResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createProcessingJob(gameId)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed to create processing job: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
