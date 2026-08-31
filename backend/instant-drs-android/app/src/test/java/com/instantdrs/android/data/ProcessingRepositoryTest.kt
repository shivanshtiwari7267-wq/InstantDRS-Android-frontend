package com.instantdrs.android.data

import com.instantdrs.android.model.VideoAnalysisJobResponse
import com.instantdrs.android.model.VideoPipelineStatusResponse
import com.instantdrs.android.model.VideoProcessingJobResponse
import com.instantdrs.android.network.ApiService
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import retrofit2.Response

class ProcessingRepositoryTest {

    private lateinit var apiService: ApiService
    private lateinit var repository: ProcessingRepository

    @Before
    fun setup() {
        apiService = mock(ApiService::class.java)
        repository = ProcessingRepository(apiService)
    }

    @Test
    fun `getProcessingJob returns job when successful`() = runTest {
        val expectedJob = VideoProcessingJobResponse(id = 1L, status = "COMPLETED", errorMessage = null)
        `when`(apiService.getProcessingJob(1L, 1L)).thenReturn(Response.success(expectedJob))

        val result = repository.getProcessingJob(1L, 1L)

        assertTrue(result.isSuccess)
        assertEquals("COMPLETED", result.getOrNull()?.status)
    }

    @Test
    fun `getProcessingJob returns failure on error`() = runTest {
        val errorResponse = Response.error<VideoProcessingJobResponse>(404, "Not Found".toResponseBody(null))
        `when`(apiService.getProcessingJob(1L, 1L)).thenReturn(errorResponse)

        val result = repository.getProcessingJob(1L, 1L)

        assertTrue(result.isFailure)
    }

    @Test
    fun `createAnalysisJob returns job when successful`() = runTest {
        val expectedJob = VideoAnalysisJobResponse(id = 2L, processingJobId = 1L, status = "QUEUED", errorMessage = null)
        `when`(apiService.createAnalysisJob(1L, 1L)).thenReturn(Response.success(expectedJob))

        val result = repository.createAnalysisJob(1L, 1L)

        assertTrue(result.isSuccess)
        assertEquals(2L, result.getOrNull()?.id)
    }

    @Test
    fun `getPipelineStatus returns status when successful`() = runTest {
        val expectedStatus = VideoPipelineStatusResponse(
            processingStatus = "COMPLETED",
            analysisStatus = "COMPLETED",
            totalFrames = 100,
            processedFrames = 100,
            progressPercent = 100.0,
            cricketEventsAvailable = true,
            lbwAnalysisAvailable = true,
            drsDecisionAvailable = true,
            replayAvailable = true,
            replayStatus = "COMPLETED",
            overallPipelineReady = true
        )
        `when`(apiService.getPipelineStatus(1L, 1L, 2L)).thenReturn(Response.success(expectedStatus))

        val result = repository.getPipelineStatus(1L, 1L, 2L)

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.overallPipelineReady == true)
    }
}
