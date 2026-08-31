package com.instantdrs.android

import com.instantdrs.android.model.AuthState
import org.junit.Assert.*
import org.junit.Test

class AuthLifecycleTest {
    
    @Test
    fun testUnauthenticatedState() {
        val state: AuthState = AuthState.Unauthenticated
        assertTrue(state is AuthState.Unauthenticated)
    }

    @Test
    fun testAuthenticationTransitions() {
        var state: AuthState = AuthState.Unauthenticated
        
        state = AuthState.Authenticating
        assertTrue(state is AuthState.Authenticating)
        
        state = AuthState.Authenticated("testuser")
        assertTrue(state is AuthState.Authenticated)
        assertEquals("testuser", (state as AuthState.Authenticated).username)
        
        state = AuthState.LoggingOut
        assertTrue(state is AuthState.LoggingOut)
        
        state = AuthState.Unauthenticated
        assertTrue(state is AuthState.Unauthenticated)
    }

    @Test
    fun testErrorState() {
        val state: AuthState = AuthState.AuthenticationError("Invalid credentials")
        assertTrue(state is AuthState.AuthenticationError)
        assertEquals("Invalid credentials", (state as AuthState.AuthenticationError).message)
    }
}
