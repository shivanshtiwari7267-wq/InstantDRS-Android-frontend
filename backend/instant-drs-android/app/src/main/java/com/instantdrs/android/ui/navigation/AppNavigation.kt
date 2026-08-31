package com.instantdrs.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.instantdrs.android.data.AuthRepository
import com.instantdrs.android.data.TokenManager
import com.instantdrs.android.network.ApiClient
import com.instantdrs.android.ui.screens.HomeScreen
import com.instantdrs.android.ui.screens.LoginScreen
import com.instantdrs.android.data.GameRepository
import com.instantdrs.android.ui.screens.GamesScreen
import com.instantdrs.android.ui.screens.CreateGameScreen
import com.instantdrs.android.ui.screens.GameDetailsScreen
import com.instantdrs.android.ui.screens.GameSessionScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    
    val tokenManager = remember { TokenManager(context) }
    val apiService = remember { ApiClient.getService(tokenManager) }
    val authRepository = remember { AuthRepository(apiService, tokenManager) }
    val gameRepository = remember { GameRepository(apiService) }

    val startDestination = if (tokenManager.getToken() != null) "home" else "login"

    LaunchedEffect(Unit) {
        tokenManager.sessionExpiredEvent.collect {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                authRepository = authRepository,
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            HomeScreen(
                authRepository = authRepository,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onNavigateToGames = {
                    navController.navigate("games")
                }
            )
        }
        composable("games") {
            GamesScreen(
                gameRepository = gameRepository,
                onNavigateToCreate = { navController.navigate("create_game") },
                onNavigateToGameDetails = { gameId -> navController.navigate("game_details/$gameId") },
                onBack = { navController.popBackStack() }
            )
        }
        composable("create_game") {
            CreateGameScreen(
                gameRepository = gameRepository,
                onGameCreated = { gameId ->
                    navController.navigate("game_details/$gameId") {
                        popUpTo("games")
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            "game_details/{gameId}",
            arguments = listOf(navArgument("gameId") { type = NavType.LongType })
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getLong("gameId") ?: return@composable
            GameDetailsScreen(
                gameId = gameId,
                gameRepository = gameRepository,
                onNavigateToSession = { gid -> navController.navigate("game_session/$gid") },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            "game_session/{gameId}",
            arguments = listOf(navArgument("gameId") { type = NavType.LongType })
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getLong("gameId") ?: return@composable
            GameSessionScreen(
                gameId = gameId,
                gameRepository = gameRepository,
                onNavigateToReview = { gId, pId, aId -> 
                    navController.navigate("drs_review/$gId/$pId/$aId") 
                },
                onNavigateToCamera = { gid -> navController.navigate("camera_capture/$gid") },
                onNavigateToProcessing = { gId, pId -> navController.navigate("video_processing/$gId/$pId") },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            "camera_capture/{gameId}",
            arguments = listOf(navArgument("gameId") { type = NavType.LongType })
        ) { backStackEntry ->
            val argsGameId = backStackEntry.arguments?.getLong("gameId") ?: return@composable
            val uploadRepository = androidx.compose.runtime.remember {
                com.instantdrs.android.data.UploadRepository(apiService)
            }
            val viewModel: com.instantdrs.android.ui.camera.CameraCaptureViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = com.instantdrs.android.ui.camera.CameraCaptureViewModelFactory(uploadRepository, argsGameId)
            )
            com.instantdrs.android.ui.camera.CameraCaptureScreen(
                gameId = argsGameId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProcessing = { gId, pId -> 
                    navController.navigate("video_processing/$gId/$pId") {
                        popUpTo("game_session/$gId")
                    }
                }
            )
        }
        composable(
            "video_processing/{gameId}/{processingJobId}",
            arguments = listOf(
                navArgument("gameId") { type = NavType.LongType },
                navArgument("processingJobId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val argsGameId = backStackEntry.arguments?.getLong("gameId") ?: return@composable
            val pId = backStackEntry.arguments?.getLong("processingJobId") ?: return@composable
            
            val processingRepository = androidx.compose.runtime.remember {
                com.instantdrs.android.data.ProcessingRepository(apiService)
            }
            
            val viewModel: com.instantdrs.android.ui.processing.VideoProcessingViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = com.instantdrs.android.ui.processing.VideoProcessingViewModelFactory(processingRepository, argsGameId, pId)
            )
            
            com.instantdrs.android.ui.processing.VideoProcessingScreen(
                gameId = argsGameId,
                processingJobId = pId,
                viewModel = viewModel,
                onNavigateToReview = { gId, pJobId, aId -> 
                    navController.navigate("drs_review/$gId/$pJobId/$aId") {
                        popUpTo("game_session/$gId")
                    }
                },
                onNavigateBack = { 
                    navController.popBackStack("game_session/$argsGameId", inclusive = false)
                }
            )
        }
        composable(
            "drs_review/{gameId}/{processingJobId}/{analysisJobId}",
            arguments = listOf(
                navArgument("gameId") { type = NavType.LongType },
                navArgument("processingJobId") { type = NavType.LongType },
                navArgument("analysisJobId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val argsGameId = backStackEntry.arguments?.getLong("gameId") ?: return@composable
            val pId = backStackEntry.arguments?.getLong("processingJobId") ?: return@composable
            val aId = backStackEntry.arguments?.getLong("analysisJobId") ?: return@composable
            
            val reviewRepository = androidx.compose.runtime.remember {
                com.instantdrs.android.data.ReviewRepository(apiService)
            }
            
            val viewModel: com.instantdrs.android.ui.review.DrsReviewViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = com.instantdrs.android.ui.review.DrsReviewViewModelFactory(reviewRepository, argsGameId, pId, aId)
            )
            
            com.instantdrs.android.ui.review.DrsReviewScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
