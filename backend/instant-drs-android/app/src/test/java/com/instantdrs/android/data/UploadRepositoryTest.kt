package com.instantdrs.android.data

import com.instantdrs.android.model.GameSessionResponse
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
import java.io.File
import com.instantdrs.android.entity.SessionStatus

class UploadRepositoryTest {

    private lateinit var apiService: ApiService
    private lateinit var repository: UploadRepository

    @Before
    fun setup() {
        apiService = mock(ApiService::class.java)
        repository = UploadRepository(apiService)
    }

    @Test
    fun `startRecording returns success when API is successful`() = runTest {
        `when`(apiService.startRecording(1L)).thenReturn(Response.success(Any()))

        val result = repository.startRecording(1L)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `stopSession returns failure on HTTP error`() = runTest {
        val errorResponse = Response.error<Any>(400, "Bad Request".toResponseBody(null))
        `when`(apiService.stopSession(1L)).thenReturn(errorResponse)

        val result = repository.stopSession(1L)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("400") == true)
    }

    @Test
    fun `uploadRecording returns session response when successful`() = runTest {
        val mockFile = File("test.mp4")
        val expectedResponse = GameSessionResponse(
            id = 1L, gameId = 1L, status = SessionStatus.RECORDING,
            startedAt = null, endedAt = null, recordingMetadata = null,
            recordingFile = "test.mp4", videoMetadata = null
        )

        `when`(apiService.uploadRecording(eq(1L), any())).thenReturn(Response.success(expectedResponse))

        val result = repository.uploadRecording(1L, mockFile)

        assertTrue(result.isSuccess)
        assertEquals(expectedResponse, result.getOrNull())
    }

    @Test
    fun `createProcessingJob returns processing job when successful`() = runTest {
        val expectedJob = VideoProcessingJobResponse(id = 5L, status = "QUEUED", errorMessage = null)
        `when`(apiService.createProcessingJob(1L)).thenReturn(Response.success(expectedJob))

        val result = repository.createProcessingJob(1L)

        assertTrue(result.isSuccess)
        assertEquals(5L, result.getOrNull()?.id)
    }

    @Test
    fun `uploadRecording returns failure on 401 Unauthorized`() = runTest {
        val errorResponse = Response.error<GameSessionResponse>(401, "Unauthorized".toResponseBody(null))
        `when`(apiService.uploadRecording(eq(1L), any())).thenReturn(errorResponse)

        val result = repository.uploadRecording(1L, File("test.mp4"))

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("401") == true)
    }
}
