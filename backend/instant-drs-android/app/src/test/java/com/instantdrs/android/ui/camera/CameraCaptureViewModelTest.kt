package com.instantdrs.android.ui.camera

import com.instantdrs.android.data.UploadRepository
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
import java.io.File
import com.instantdrs.android.model.GameSessionResponse
import com.instantdrs.android.entity.SessionStatus

@OptIn(ExperimentalCoroutinesApi::class)
class CameraCaptureViewModelTest {

    private lateinit var repository: UploadRepository
    private lateinit var viewModel: CameraCaptureViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock(UploadRepository::class.java)
        viewModel = CameraCaptureViewModel(repository, 1L)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `startSession updates state to SessionStarted on success`() = runTest {
        `when`(repository.startRecording(1L)).thenReturn(Result.success(Any()))

        viewModel.startSession()
        advanceUntilIdle()

        assertEquals(CameraState.SessionStarted, viewModel.uiState.value)
    }

    @Test
    fun `startSession updates state to Error on failure`() = runTest {
        `when`(repository.startRecording(1L)).thenReturn(Result.failure(Exception("API Error")))

        viewModel.startSession()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is CameraState.Error)
        assertEquals("API Error", (state as CameraState.Error).message)
    }

    @Test
    fun `uploadRecording cascades success and returns UploadSuccess`() = runTest {
        val file = File("test.mp4")
        
        `when`(repository.uploadRecording(eq(1L), any())).thenReturn(Result.success(
            GameSessionResponse(1L, 1L, SessionStatus.RECORDING, null, null, null, "test.mp4", null)
        ))
        `when`(repository.stopSession(1L)).thenReturn(Result.success(Any()))
        `when`(repository.createProcessingJob(1L)).thenReturn(Result.success(
            VideoProcessingJobResponse(100L, "QUEUED", null)
        ))

        viewModel.uploadRecording(file)
        
        // Initial state should be Uploading
        assertEquals(CameraState.Uploading, viewModel.uiState.value)
        
        advanceUntilIdle()

        // Final state should be UploadSuccess
        val finalState = viewModel.uiState.value
        assertTrue(finalState is CameraState.UploadSuccess)
        assertEquals(100L, (finalState as CameraState.UploadSuccess).processingJobId)
    }

    @Test
    fun `uploadRecording handles upload failure`() = runTest {
        val file = File("test.mp4")
        
        `when`(repository.uploadRecording(eq(1L), any())).thenReturn(Result.failure(Exception("Upload Failed")))

        viewModel.uploadRecording(file)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is CameraState.Error)
        assertEquals("Upload Failed", (state as CameraState.Error).message)
    }
}
