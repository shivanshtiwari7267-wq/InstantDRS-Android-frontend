package com.instantdrs.android.data

import com.instantdrs.android.model.AuthResponse
import com.instantdrs.android.model.LoginRequest
import com.instantdrs.android.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val apiService: ApiService, private val tokenManager: TokenManager) {
    suspend fun login(request: LoginRequest): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.login(request)
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                tokenManager.saveToken(authResponse.token)
                Result.success(authResponse)
            } else {
                Result.failure(Exception("Login failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyAuth(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            if (tokenManager.getToken() == null) {
                return@withContext Result.failure(Exception("No token found"))
            }
            val response = apiService.checkHealth()
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                tokenManager.clearToken()
                Result.failure(Exception("Invalid token"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        tokenManager.clearToken()
    }
}
