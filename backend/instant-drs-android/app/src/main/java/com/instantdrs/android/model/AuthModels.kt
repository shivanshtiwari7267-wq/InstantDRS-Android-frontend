package com.instantdrs.android.model

data class AuthResponse(
    val token: String,
    val userId: Long,
    val username: String,
    val role: String
)

sealed class AuthState {
    object Unauthenticated : AuthState()
    object RestoringSession : AuthState()
    object Authenticating : AuthState()
    data class Authenticated(val username: String) : AuthState()
    data class AuthenticationError(val message: String) : AuthState()
    object LoggingOut : AuthState()
}

data class LoginRequest(
    val username: String,
    val password: String
)
