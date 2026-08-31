package com.example.instantdrs_android.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/" // Localhost for Android Emulator

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request()
            android.util.Log.d("DRS_DEBUG", "--> ${request.method()} ${request.url()}")
            try {
                val response = chain.proceed(request)
                val responseBody = response.body()
                val source = responseBody?.source()
                source?.request(Long.MAX_VALUE) // Buffer the entire body.
                val buffer = source?.buffer
                val responseBodyString = buffer?.clone()?.readString(java.nio.charset.Charset.forName("UTF-8"))
                
                android.util.Log.d("DRS_DEBUG", "<-- ${response.code()} ${response.request().url()}")
                android.util.Log.d("DRS_DEBUG", "HTTP BODY: $responseBodyString")
                response
            } catch (e: Exception) {
                android.util.Log.d("DRS_DEBUG", "<-- HTTP FAILED: $e")
                throw e
            }
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val drsApiService: DrsApiService = retrofit.create(DrsApiService::class.java)
}
