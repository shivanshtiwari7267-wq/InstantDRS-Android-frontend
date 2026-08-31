package com.instantdrs.android.ui.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.instantdrs.android.data.ReviewRepository
import com.instantdrs.android.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DrsReviewState {
    object Loading : DrsReviewState()
    data class Success(
        val review: DrsReviewResponse,
        val result: VideoAnalysisResultResponse?,
        val timeline: List<DrsTimelineEventDto>?,
        val replay: DrsReplayResponse?,
        val pipelineStatus: VideoPipelineStatusResponse?,
        val evidenceQuality: EvidenceQualityResponse?
    ) : DrsReviewState()
    data class Error(val message: String) : DrsReviewState()
}

class DrsReviewViewModel(
    private val repository: ReviewRepository,
    private val gameId: Long,
    private val processingJobId: Long,
    private val analysisJobId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow<DrsReviewState>(DrsReviewState.Loading)
    val uiState: StateFlow<DrsReviewState> = _uiState.asStateFlow()

    init {
        loadReviewData()
    }

    fun loadReviewData() {
        _uiState.value = DrsReviewState.Loading
        viewModelScope.launch {
            val reviewResult = repository.getReview(gameId, processingJobId, analysisJobId)
            
            if (reviewResult.isFailure) {
                val error = reviewResult.exceptionOrNull()
                val errorMsg = error?.message ?: ""
                val displayMsg = when {
                    errorMsg.contains("404") -> "This review is not available."
                    errorMsg.contains("401") -> "Authentication has expired."
                    errorMsg.contains("403") -> "You do not have permission to view this review."
                    else -> "Unable to load the review. Check the connection and try again."
                }
                _uiState.value = DrsReviewState.Error(displayMsg)
                return@launch
            }
            
            val review = reviewResult.getOrThrow()
            
            // Fetch optional dimensions concurrently or sequentially.
            // Failing to fetch these should not crash the screen if review was successful.
            val resultResponse = repository.getResult(gameId, processingJobId, analysisJobId).getOrNull()
            val timelineResponse = repository.getTimeline(gameId, processingJobId, analysisJobId).getOrNull()
            val replayResponse = repository.getReplay(gameId, processingJobId, analysisJobId).getOrNull()
            val pipelineResponse = repository.getPipelineStatus(gameId, processingJobId, analysisJobId).getOrNull()
            val qualityResponse = repository.getEvidenceQuality(gameId, processingJobId, analysisJobId).getOrNull()

            _uiState.value = DrsReviewState.Success(
                review = review,
                result = resultResponse,
                timeline = timelineResponse,
                replay = replayResponse,
                pipelineStatus = pipelineResponse,
                evidenceQuality = qualityResponse
            )
        }
    }
}

class DrsReviewViewModelFactory(
    private val repository: ReviewRepository,
    private val gameId: Long,
    private val processingJobId: Long,
    private val analysisJobId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DrsReviewViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DrsReviewViewModel(repository, gameId, processingJobId, analysisJobId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
