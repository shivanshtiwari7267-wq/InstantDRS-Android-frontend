package com.instantdrs.android.ui.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.instantdrs.android.data.UploadRepository
import com.instantdrs.android.model.VideoProcessingJobResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

sealed class CameraState {
    object Idle : CameraState()
    object StartingSession : CameraState()
    object SessionStarted : CameraState()
    object Recording : CameraState()
    data class RecordingSaved(val file: File) : CameraState()
    object Uploading : CameraState()
    data class UploadSuccess(val processingJobId: Long) : CameraState()
    data class Error(val message: String) : CameraState()
}

class CameraCaptureViewModel(
    private val repository: UploadRepository,
    private val gameId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow<CameraState>(CameraState.Idle)
    val uiState: StateFlow<CameraState> = _uiState.asStateFlow()

    fun startSession() {
        _uiState.value = CameraState.StartingSession
        viewModelScope.launch {
            val result = repository.startRecording(gameId)
            if (result.isSuccess) {
                _uiState.value = CameraState.SessionStarted
            } else {
                _uiState.value = CameraState.Error(result.exceptionOrNull()?.message ?: "Failed to start session")
            }
        }
    }

    fun markRecordingStarted() {
        _uiState.value = CameraState.Recording
    }

    fun onRecordingSaved(file: File) {
        _uiState.value = CameraState.RecordingSaved(file)
    }

    fun onRecordingError(message: String) {
        _uiState.value = CameraState.Error(message)
    }

    fun discardRecording(file: File) {
        if (file.exists()) {
            file.delete()
        }
        _uiState.value = CameraState.Idle
    }

    fun uploadRecording(file: File) {
        _uiState.value = CameraState.Uploading
        viewModelScope.launch {
            val uploadResult = repository.uploadRecording(gameId, file)
            if (uploadResult.isFailure) {
                _uiState.value = CameraState.Error(uploadResult.exceptionOrNull()?.message ?: "Upload failed")
                return@launch
            }

            // Stop session
            val stopResult = repository.stopSession(gameId)
            if (stopResult.isFailure) {
                _uiState.value = CameraState.Error(stopResult.exceptionOrNull()?.message ?: "Failed to stop session")
                return@launch
            }

            // Create Processing Job
            val jobResult = repository.createProcessingJob(gameId)
            if (jobResult.isFailure) {
                _uiState.value = CameraState.Error(jobResult.exceptionOrNull()?.message ?: "Failed to create processing job")
                return@launch
            }

            val job = jobResult.getOrNull()
            if (job != null) {
                _uiState.value = CameraState.UploadSuccess(job.id)
            } else {
                _uiState.value = CameraState.Error("Invalid job response")
            }
        }
    }
}

class CameraCaptureViewModelFactory(
    private val repository: UploadRepository,
    private val gameId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CameraCaptureViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CameraCaptureViewModel(repository, gameId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
