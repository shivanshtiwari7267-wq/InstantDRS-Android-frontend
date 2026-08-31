package com.instantdrs.android.ui.processing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.instantdrs.android.data.ProcessingRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

sealed class ProcessingState {
    object Initializing : ProcessingState()
    object ProcessingQueued : ProcessingState()
    object Processing : ProcessingState()
    object AnalysisProcessing : ProcessingState()
    data class ReviewReady(val analysisJobId: Long) : ProcessingState()
    data class Failed(val message: String) : ProcessingState()
}

class VideoProcessingViewModel(
    private val repository: ProcessingRepository,
    private val gameId: Long,
    private val processingJobId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProcessingState>(ProcessingState.Initializing)
    val uiState: StateFlow<ProcessingState> = _uiState.asStateFlow()

    private var pollingJob: Job? = null
    private var analysisJobId: Long? = null

    init {
        startPolling()
    }

    fun startPolling() {
        if (pollingJob?.isActive == true) return

        pollingJob = viewModelScope.launch {
            while (isActive) {
                if (analysisJobId == null) {
                    val result = repository.getProcessingJob(gameId, processingJobId)
                    if (result.isSuccess) {
                        val job = result.getOrNull()
                        when (job?.status) {
                            "QUEUED" -> _uiState.value = ProcessingState.ProcessingQueued
                            "PROCESSING" -> _uiState.value = ProcessingState.Processing
                            "COMPLETED" -> {
                                _uiState.value = ProcessingState.AnalysisProcessing
                                createAnalysisJob()
                            }
                            "FAILED" -> {
                                _uiState.value = ProcessingState.Failed(job.errorMessage ?: "Processing Failed")
                                pollingJob?.cancel()
                            }
                            else -> _uiState.value = ProcessingState.ProcessingQueued
                        }
                    } else {
                        _uiState.value = ProcessingState.Failed(result.exceptionOrNull()?.message ?: "Network error fetching processing job")
                        pollingJob?.cancel()
                    }
                } else {
                    val aId = analysisJobId!!
                    val result = repository.getPipelineStatus(gameId, processingJobId, aId)
                    if (result.isSuccess) {
                        val status = result.getOrNull()
                        if (status?.analysisStatus == "FAILED") {
                            _uiState.value = ProcessingState.Failed("Analysis Failed")
                            pollingJob?.cancel()
                        } else if (status?.overallPipelineReady == true) {
                            _uiState.value = ProcessingState.ReviewReady(aId)
                            pollingJob?.cancel()
                        } else {
                            _uiState.value = ProcessingState.AnalysisProcessing
                        }
                    } else {
                        _uiState.value = ProcessingState.Failed(result.exceptionOrNull()?.message ?: "Network error fetching analysis status")
                        pollingJob?.cancel()
                    }
                }
                
                if (isActive) delay(3000)
            }
        }
    }

    private suspend fun createAnalysisJob() {
        val result = repository.createAnalysisJob(gameId, processingJobId)
        if (result.isSuccess) {
            val job = result.getOrNull()
            if (job != null) {
                analysisJobId = job.id
            } else {
                _uiState.value = ProcessingState.Failed("Invalid analysis job response")
                pollingJob?.cancel()
            }
        } else {
            if (result.exceptionOrNull()?.message?.contains("409") == true || result.exceptionOrNull()?.message?.contains("Conflict") == true) {
                val existingJobsResult = repository.getAnalysisJobs(gameId, processingJobId)
                if (existingJobsResult.isSuccess) {
                    val jobs = existingJobsResult.getOrNull()
                    if (!jobs.isNullOrEmpty()) {
                        analysisJobId = jobs.first().id
                        return
                    }
                }
                _uiState.value = ProcessingState.Failed("Analysis job already exists but failed to retrieve.")
            } else {
                _uiState.value = ProcessingState.Failed(result.exceptionOrNull()?.message ?: "Failed to create analysis job")
            }
            pollingJob?.cancel()
        }
    }

    fun manualRefresh() {
        pollingJob?.cancel()
        startPolling()
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}

class VideoProcessingViewModelFactory(
    private val repository: ProcessingRepository,
    private val gameId: Long,
    private val processingJobId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VideoProcessingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VideoProcessingViewModel(repository, gameId, processingJobId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
