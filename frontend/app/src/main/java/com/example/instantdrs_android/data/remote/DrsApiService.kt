package com.example.instantdrs_android.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface DrsApiService {

    @Multipart
    @POST("api/drs/reviews")
    suspend fun createReview(
        @Part file: MultipartBody.Part,
        @Part("sportType") sportType: RequestBody,
        @Part("ruleName") ruleName: RequestBody
    ): DrsReviewWorkflowResponse

    @GET("api/drs/reviews/{reviewId}")
    suspend fun getReviewStatus(
        @Path("reviewId") reviewId: String
    ): DrsReviewWorkflowResponse
}
