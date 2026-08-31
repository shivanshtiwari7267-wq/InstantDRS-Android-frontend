package com.instantdrs.android

import com.instantdrs.android.model.AuthState
import com.instantdrs.android.model.LoginRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthModelsTest {
    @Test
    fun testLoginRequest() {
        val req = LoginRequest("test", "pass")
        assertEquals("test", req.username)
        assertEquals("pass", req.password)
    }

    @Test
    fun testAuthState() {
        val state: AuthState = AuthState.Authenticated("user")
        assertTrue(state is AuthState.Authenticated)
        assertEquals("user", (state as AuthState.Authenticated).username)
    }
}
