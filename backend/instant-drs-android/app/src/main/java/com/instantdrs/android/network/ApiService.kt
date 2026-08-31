package com.instantdrs.android.network

import com.instantdrs.android.model.AuthResponse
import com.instantdrs.android.model.LoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    // Using video pipeline health as authenticated dashboard verification
    @GET("health/video-pipeline")
    suspend fun checkHealth(): Response<Any>

    // Games
    @POST("games")
    suspend fun createGame(@Body request: com.instantdrs.android.model.GameCreateRequest): Response<com.instantdrs.android.model.GameResponse>

    @GET("games/{gameId}")
    suspend fun getGame(@retrofit2.http.Path("gameId") gameId: Long): Response<com.instantdrs.android.model.GameResponse>

    @GET("games/{gameId}/rules")
    suspend fun getGameRules(@retrofit2.http.Path("gameId") gameId: Long): Response<List<com.instantdrs.android.model.RuleResponse>>

    @PUT("games/{gameId}/rules")
    suspend fun setGameRules(
        @retrofit2.http.Path("gameId") gameId: Long,
        @Body request: com.instantdrs.android.model.GameRuleSelectionRequest
    ): Response<Any>

    // Game Sessions
    @POST("games/{gameId}/session")
    suspend fun createSession(@retrofit2.http.Path("gameId") gameId: Long): Response<com.instantdrs.android.model.GameSessionResponse>

    @GET("games/{gameId}/session")
    suspend fun getSession(@retrofit2.http.Path("gameId") gameId: Long): Response<com.instantdrs.android.model.GameSessionResponse>

    @retrofit2.http.PUT("games/{gameId}/session/recording")
    suspend fun startRecording(@retrofit2.http.Path("gameId") gameId: Long): Response<Any>

    @retrofit2.http.PUT("games/{gameId}/session/stop")
    suspend fun stopSession(@retrofit2.http.Path("gameId") gameId: Long): Response<Any>

    @retrofit2.http.Multipart
    @POST("games/{gameId}/session/recording/upload")
    suspend fun uploadRecording(
        @retrofit2.http.Path("gameId") gameId: Long,
        @retrofit2.http.Part file: okhttp3.MultipartBody.Part
    ): Response<com.instantdrs.android.model.GameSessionResponse>

    @POST("games/{gameId}/session/processing")
    suspend fun createProcessingJob(@retrofit2.http.Path("gameId") gameId: Long): Response<com.instantdrs.android.model.VideoProcessingJobResponse>

    @GET("games/{gameId}/session/processing/{processingJobId}")
    suspend fun getProcessingJob(
        @retrofit2.http.Path("gameId") gameId: Long,
        @retrofit2.http.Path("processingJobId") processingJobId: Long
    ): Response<com.instantdrs.android.model.VideoProcessingJobResponse>

    @POST("games/{gameId}/session/processing/{processingJobId}/analysis")
    suspend fun createAnalysisJob(
        @retrofit2.http.Path("gameId") gameId: Long,
        @retrofit2.http.Path("processingJobId") processingJobId: Long
    ): Response<com.instantdrs.android.model.VideoAnalysisJobResponse>

    @GET("games/{gameId}/session/processing/{processingJobId}/analysis")
    suspend fun getAnalysisJobs(
        @retrofit2.http.Path("gameId") gameId: Long,
        @retrofit2.http.Path("processingJobId") processingJobId: Long
    ): Response<List<com.instantdrs.android.model.VideoAnalysisJobResponse>>

    // Review Endpoints
    @GET("games/{gameId}/session/processing/{processingJobId}/analysis/{analysisJobId}/review")
    suspend fun getReview(
        @retrofit2.http.Path("gameId") gameId: Long,
        @retrofit2.http.Path("processingJobId") processingJobId: Long,
        @retrofit2.http.Path("analysisJobId") analysisJobId: Long
    ): Response<com.instantdrs.android.model.DrsReviewResponse>

    @GET("games/{gameId}/session/processing/{processingJobId}/analysis/{analysisJobId}/result")
    suspend fun getResult(
        @retrofit2.http.Path("gameId") gameId: Long,
        @retrofit2.http.Path("processingJobId") processingJobId: Long,
        @retrofit2.http.Path("analysisJobId") analysisJobId: Long
    ): Response<com.instantdrs.android.model.VideoAnalysisResultResponse>

    @GET("games/{gameId}/session/processing/{processingJobId}/analysis/{analysisJobId}/timeline")
    suspend fun getTimeline(
        @retrofit2.http.Path("gameId") gameId: Long,
        @retrofit2.http.Path("processingJobId") processingJobId: Long,
        @retrofit2.http.Path("analysisJobId") analysisJobId: Long
    ): Response<List<com.instantdrs.android.model.DrsTimelineEventDto>>

    @GET("games/{gameId}/session/processing/{processingJobId}/analysis/{analysisJobId}/review/replay")
    suspend fun getReplay(
        @retrofit2.http.Path("gameId") gameId: Long,
        @retrofit2.http.Path("processingJobId") processingJobId: Long,
        @retrofit2.http.Path("analysisJobId") analysisJobId: Long
    ): Response<com.instantdrs.android.model.DrsReplayResponse>

    @GET("games/{gameId}/session/processing/{processingJobId}/analysis/{analysisJobId}/pipeline-status")
    suspend fun getPipelineStatus(
        @retrofit2.http.Path("gameId") gameId: Long,
        @retrofit2.http.Path("processingJobId") processingJobId: Long,
        @retrofit2.http.Path("analysisJobId") analysisJobId: Long
    ): Response<com.instantdrs.android.model.VideoPipelineStatusResponse>

    @GET("games/{gameId}/session/processing/{processingJobId}/analysis/{analysisJobId}/evidence-quality")
    suspend fun getEvidenceQuality(
        @retrofit2.http.Path("gameId") gameId: Long,
        @retrofit2.http.Path("processingJobId") processingJobId: Long,
        @retrofit2.http.Path("analysisJobId") analysisJobId: Long
    ): Response<com.instantdrs.android.model.EvidenceQualityResponse>
}
