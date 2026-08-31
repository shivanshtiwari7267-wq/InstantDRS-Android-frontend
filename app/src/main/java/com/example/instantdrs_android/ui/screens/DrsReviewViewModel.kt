package com.example.instantdrs_android.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instantdrs_android.data.remote.DrsReviewResponse
import com.example.instantdrs_android.data.remote.DrsReviewWorkflowResponse
import com.example.instantdrs_android.data.remote.RetrofitClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import android.util.Log

enum class DrsReviewState {
    IDLE, UPLOADING, PROCESSING, ANALYZING, COMPLETED, INVALID_VIDEO, FALLBACK_REPLAY, FAILED
}

class DrsReviewViewModel : ViewModel() {
    private val apiService = RetrofitClient.drsApiService

    private val _uiState = MutableStateFlow(DrsReviewState.IDLE)
    val uiState: StateFlow<DrsReviewState> = _uiState

    private val _progressPercent = MutableStateFlow(0.0)
    val progressPercent: StateFlow<Double> = _progressPercent

    private val _drsResult = MutableStateFlow<DrsReviewResponse?>(null)
    val drsResult: StateFlow<DrsReviewResponse?> = _drsResult
    
    private val _evidenceVideoUrl = MutableStateFlow<String?>(null)
    val evidenceVideoUrl: StateFlow<String?> = _evidenceVideoUrl
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun startDrsReview(videoPath: String, sportType: String, ruleName: String) {
        viewModelScope.launch {
            _uiState.value = DrsReviewState.UPLOADING
            _errorMessage.value = null
            
            try {
                val file = File(videoPath)
                Log.d("DRS_DEBUG", "START_REVIEW_CALLED")
                Log.d("DRS_DEBUG", "VIDEO_PATH: $videoPath")
                Log.d("DRS_DEBUG", "VIDEO_EXISTS: ${file.exists()}")
                Log.d("DRS_DEBUG", "VIDEO_SIZE: ${file.length()}")
                Log.d("DRS_DEBUG", "SPORT: $sportType")
                Log.d("DRS_DEBUG", "RULE: $ruleName")

                val mediaTypeVideo = MediaType.parse("video/mp4")
                val requestFile = RequestBody.create(mediaTypeVideo, file)
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                
                val mediaTypeText = MediaType.parse("text/plain")
                val sportTypeBody = RequestBody.create(mediaTypeText, sportType.uppercase())
                val ruleNameBody = RequestBody.create(mediaTypeText, ruleName)

                Log.d("DRS_DEBUG", "REQUEST_STARTED")
                val initialResponse = apiService.createReview(body, sportTypeBody, ruleNameBody)
                
                Log.d("DRS_DEBUG", "INITIAL_RESPONSE: $initialResponse")

                if (initialResponse.status == "FAILED") {
                    Log.d("DRS_DEBUG", "UPLOAD HTTP STATUS = FAILED_CUSTOM")
                    _uiState.value = DrsReviewState.FAILED
                    _errorMessage.value = initialResponse.errorMessage ?: "Upload failed"
                    return@launch
                }
                
                Log.d("DRS_DEBUG", "REVIEW_ID: ${initialResponse.reviewId}")
                pollReviewStatus(initialResponse.reviewId)
                
            } catch (e: Exception) {
                Log.d("DRS_DEBUG", "UPLOAD HTTP ERROR = ${e.message}")
                _uiState.value = DrsReviewState.FAILED
                _errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    private suspend fun pollReviewStatus(reviewId: String) {
        while (_uiState.value != DrsReviewState.COMPLETED && _uiState.value != DrsReviewState.FAILED && _uiState.value != DrsReviewState.INVALID_VIDEO && _uiState.value != DrsReviewState.FALLBACK_REPLAY) {
            try {
                val response = apiService.getReviewStatus(reviewId)
                Log.d("DRS_DEBUG", "POLL_STATUS: ${response.status}")
                Log.d("DRS_DEBUG", "POLL_PROGRESS: ${response.progressPercent}")
                Log.d("DRS_DEBUG", "reviewId: ${response.reviewId}, status: ${response.status}, hasReplay: ${response.hasReplay}, isFallback: ${response.isFallback}, evidenceVideoUrl: ${response.evidenceVideoUrl}, result: ${response.result}, decision: ${response.result?.decision}, confidence: ${response.result?.drsConfidence}, errorMessage: ${response.errorMessage}")
                
                if (response.isFallback == true && response.hasReplay == true && response.evidenceVideoUrl != null) {
                    Log.d("DRS_DEBUG", "FALLBACK_REPLAY RESPONSE = $response")
                    _uiState.value = DrsReviewState.FALLBACK_REPLAY
                    _evidenceVideoUrl.value = response.evidenceVideoUrl
                    _progressPercent.value = 100.0
                    return
                }

                when (response.status) {
                    "PROCESSING" -> {
                        Log.d("DRS_DEBUG", "PROCESSING RESPONSE = $response")
                        _uiState.value = DrsReviewState.PROCESSING
                        _progressPercent.value = response.progressPercent ?: 0.0
                    }
                    "ANALYZING" -> {
                        Log.d("DRS_DEBUG", "ANALYSIS RESPONSE = $response")
                        _uiState.value = DrsReviewState.ANALYZING
                        _progressPercent.value = response.progressPercent ?: 0.0
                    }
                    "COMPLETED" -> {
                        Log.d("DRS_DEBUG", "REVIEW RESPONSE = $response")
                        Log.d("DRS_DEBUG", "REVIEW_ID: ${response.result?.analysisJobId}")
                        Log.d("DRS_DEBUG", "DECISION: ${response.result?.decision}")
                        Log.d("DRS_DEBUG", "CONFIDENCE: ${response.result?.drsConfidence}")
                        Log.d("DRS_DEBUG", "POLL_STATUS: ${response.status}")

                        _uiState.value = DrsReviewState.COMPLETED
                        _progressPercent.value = 100.0
                        _drsResult.value = response.result
                        return
                    }
                    "INVALID_VIDEO" -> {
                        Log.d("DRS_DEBUG", "INVALID_VIDEO RESPONSE = $response")
                        _uiState.value = DrsReviewState.INVALID_VIDEO
                        _errorMessage.value = response.errorMessage ?: "No valid cricket gameplay was detected."
                        return
                    }
                    "FAILED" -> {
                        Log.d("DRS_DEBUG", "FAILED RESPONSE = $response")
                        _uiState.value = DrsReviewState.FAILED
                        _errorMessage.value = response.errorMessage ?: "Processing failed on backend."
                        return
                    }
                }
                
                delay(3000) // Poll every 3 seconds
            } catch (e: Exception) {
                Log.d("DRS_DEBUG", "POLL HTTP ERROR = ${e.message}")
                _uiState.value = DrsReviewState.FAILED
                _errorMessage.value = "Network Error while polling: ${e.message}"
                return
            }
        }
    }
    
    fun resetState() {
        _uiState.value = DrsReviewState.IDLE
        _progressPercent.value = 0.0
        _drsResult.value = null
        _evidenceVideoUrl.value = null
        _errorMessage.value = null
    }
}
