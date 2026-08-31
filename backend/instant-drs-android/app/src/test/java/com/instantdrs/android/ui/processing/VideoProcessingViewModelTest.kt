package com.instantdrs.android.ui.processing

import com.instantdrs.android.data.ProcessingRepository
import com.instantdrs.android.model.VideoAnalysisJobResponse
import com.instantdrs.android.model.VideoPipelineStatusResponse
import com.instantdrs.android.model.VideoProcessingJobResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

@OptIn(ExperimentalCoroutinesApi::class)
class VideoProcessingViewModelTest {

    private lateinit var repository: ProcessingRepository
    private lateinit var viewModel: VideoProcessingViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock(ProcessingRepository::class.java)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `polls processing job until COMPLETED, creates analysis job, polls analysis until ready`() = runTest {
        val queuedJob = VideoProcessingJobResponse(id = 1L, status = "QUEUED", errorMessage = null)
        val completedJob = VideoProcessingJobResponse(id = 1L, status = "COMPLETED", errorMessage = null)
        val analysisJob = VideoAnalysisJobResponse(id = 2L, processingJobId = 1L, status = "QUEUED", errorMessage = null)
        
        val pipelineProcessing = VideoPipelineStatusResponse(
            "COMPLETED", "PROCESSING", 100, 50, 50.0, false, false, false, false, "NONE", false
        )
        val pipelineReady = VideoPipelineStatusResponse(
            "COMPLETED", "COMPLETED", 100, 100, 100.0, true, true, true, true, "COMPLETED", true
        )

        // First poll: QUEUED
        `when`(repository.getProcessingJob(1L, 1L)).thenReturn(Result.success(queuedJob))
        viewModel = VideoProcessingViewModel(repository, 1L, 1L)
        advanceTimeBy(100) // Let initial startPolling run
        assertEquals(ProcessingState.ProcessingQueued, viewModel.uiState.value)

        // Second poll: COMPLETED (triggers createAnalysisJob)
        `when`(repository.getProcessingJob(1L, 1L)).thenReturn(Result.success(completedJob))
        `when`(repository.createAnalysisJob(1L, 1L)).thenReturn(Result.success(analysisJob))
        advanceTimeBy(3000)
        
        // Third poll: AnalysisProcessing
        `when`(repository.getPipelineStatus(1L, 1L, 2L)).thenReturn(Result.success(pipelineProcessing))
        advanceTimeBy(3000)
        assertEquals(ProcessingState.AnalysisProcessing, viewModel.uiState.value)

        // Fourth poll: ReviewReady
        `when`(repository.getPipelineStatus(1L, 1L, 2L)).thenReturn(Result.success(pipelineReady))
        advanceTimeBy(3000)
        
        val finalState = viewModel.uiState.value
        assertTrue(finalState is ProcessingState.ReviewReady)
        assertEquals(2L, (finalState as ProcessingState.ReviewReady).analysisJobId)
    }

    @Test
    fun `fails gracefully when processing job fails`() = runTest {
        val failedJob = VideoProcessingJobResponse(id = 1L, status = "FAILED", errorMessage = "Bad video")
        
        `when`(repository.getProcessingJob(1L, 1L)).thenReturn(Result.success(failedJob))
        viewModel = VideoProcessingViewModel(repository, 1L, 1L)
        advanceTimeBy(100)
        
        val state = viewModel.uiState.value
        assertTrue(state is ProcessingState.Failed)
        assertEquals("Bad video", (state as ProcessingState.Failed).message)
    }

    @Test
    fun `handles 409 Conflict by fetching existing analysis job`() = runTest {
        val completedJob = VideoProcessingJobResponse(id = 1L, status = "COMPLETED", errorMessage = null)
        val analysisJob = VideoAnalysisJobResponse(id = 2L, processingJobId = 1L, status = "QUEUED", errorMessage = null)
        val pipelineReady = VideoPipelineStatusResponse(
            "COMPLETED", "COMPLETED", 100, 100, 100.0, true, true, true, true, "COMPLETED", true
        )
        
        `when`(repository.getProcessingJob(1L, 1L)).thenReturn(Result.success(completedJob))
        `when`(repository.createAnalysisJob(1L, 1L)).thenReturn(Result.failure(Exception("409 Conflict")))
        `when`(repository.getAnalysisJobs(1L, 1L)).thenReturn(Result.success(listOf(analysisJob)))
        `when`(repository.getPipelineStatus(1L, 1L, 2L)).thenReturn(Result.success(pipelineReady))

        viewModel = VideoProcessingViewModel(repository, 1L, 1L)
        advanceTimeBy(100) // Initial poll hits COMPLETED -> creates analysis -> hits 409 -> gets existing -> uses id 2L
        
        advanceTimeBy(3000) // Next poll queries pipeline status with id 2L
        val finalState = viewModel.uiState.value
        assertTrue(finalState is ProcessingState.ReviewReady)
    }
}
