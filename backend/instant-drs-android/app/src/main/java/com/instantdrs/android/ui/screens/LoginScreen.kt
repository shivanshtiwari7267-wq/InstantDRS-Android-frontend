package com.instantdrs.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.instantdrs.android.data.AuthRepository
import com.instantdrs.android.model.AuthState
import com.instantdrs.android.model.LoginRequest
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    authRepository: AuthRepository,
    onLoginSuccess: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var authState by remember { mutableStateOf<AuthState>(AuthState.Unauthenticated) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "InstantDRS Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (authState is AuthState.Authenticating) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    authState = AuthState.Authenticating
                    coroutineScope.launch {
                        val result = authRepository.login(LoginRequest(username, password))
                        if (result.isSuccess) {
                            authState = AuthState.Authenticated(result.getOrNull()?.username ?: username)
                            password = "" // clear password immediately
                            onLoginSuccess()
                        } else {
                            authState = AuthState.AuthenticationError(result.exceptionOrNull()?.message ?: "Unknown network error")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }
        }

        if (authState is AuthState.AuthenticationError) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = (authState as AuthState.AuthenticationError).message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
