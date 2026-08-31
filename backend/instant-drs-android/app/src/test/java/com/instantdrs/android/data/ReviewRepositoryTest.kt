package com.instantdrs.android.data

import com.instantdrs.android.model.*
import com.instantdrs.android.network.ApiService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import retrofit2.Response

class ReviewRepositoryTest {

    @Mock
    lateinit var apiService: ApiService

    private lateinit var reviewRepository: ReviewRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        reviewRepository = ReviewRepository(apiService)
    }

    @Test
    fun `getReview success returns review data`() = runBlocking {
        val review = DrsReviewResponse(
            gameId = 1L,
            processingJobId = 1L,
            analysisJobId = 1L,
            analysisStatus = "COMPLETED",
            analysisCreatedAt = "2023-10-10",
            analysisStartedAt = null,
            analysisCompletedAt = null,
            analysisErrorMessage = null,
            totalFrames = 100,
            processedFrames = 100,
            progressPercent = 100.0,
            detectedBallFrameCount = 90,
            trajectoryPointCount = 80,
            cricketEventCount = 2,
            lbwAnalysisStatus = "COMPLETED",
            lbwConfidence = 0.95,
            decisionStatus = "READY",
            decision = "OUT",
            reasonCode = "EVIDENCE_COMPLETE",
            drsConfidence = 0.98,
            replay = null,
            timeline = emptyList()
        )
        
        `when`(apiService.getReview(1L, 1L, 1L)).thenReturn(Response.success(review))

        val result = reviewRepository.getReview(1L, 1L, 1L)
        
        assertTrue(result.isSuccess)
        assertEquals(review, result.getOrNull())
    }

    @Test
    fun `getReview failure returns failure`() = runBlocking {
        `when`(apiService.getReview(1L, 1L, 1L))
            .thenReturn(Response.error(404, okhttp3.ResponseBody.create(null, "")))

        val result = reviewRepository.getReview(1L, 1L, 1L)
        
        assertTrue(result.isFailure)
    }

    @Test
    fun `getPipelineStatus success`() = runBlocking {
        val status = VideoPipelineStatusResponse(
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
        `when`(apiService.getPipelineStatus(1L, 1L, 1L)).thenReturn(Response.success(status))

        val result = reviewRepository.getPipelineStatus(1L, 1L, 1L)
        
        assertTrue(result.isSuccess)
        assertEquals(status, result.getOrNull())
    }
}
