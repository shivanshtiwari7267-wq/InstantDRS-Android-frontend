package com.instantdrs.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.instantdrs.android.data.AuthRepository
import com.instantdrs.android.model.AuthState
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    authRepository: AuthRepository,
    onLogout: () -> Unit,
    onNavigateToGames: () -> Unit
) {
    var healthStatus by remember { mutableStateOf("Checking backend connection...") }
    var authState by remember { mutableStateOf<AuthState>(AuthState.Unauthenticated) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val result = authRepository.verifyAuth()
        healthStatus = if (result.isSuccess) {
            "Backend Connected: Video Pipeline Ready"
        } else {
            // Note: If this fails due to 401/403, the interceptor will emit an event and navigate out.
            // If it's a network issue, we just show a message.
            "Backend Connection Error: ${result.exceptionOrNull()?.message}"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("InstantDRS Dashboard", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))
        Text(healthStatus, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(32.dp))
        
        if (authState is AuthState.LoggingOut) {
            CircularProgressIndicator()
        } else {
            Button(onClick = onNavigateToGames) {
                Text("Games")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                authState = AuthState.LoggingOut
                coroutineScope.launch {
                    authRepository.logout()
                    onLogout()
                }
            }) {
                Text("Logout")
            }
        }
    }
}
