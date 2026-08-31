package com.instantdrs.android.data

import com.instantdrs.android.model.GameCreateRequest
import com.instantdrs.android.model.GameResponse
import com.instantdrs.android.model.GameRuleSelectionRequest
import com.instantdrs.android.model.GameSessionResponse
import com.instantdrs.android.model.RuleResponse
import com.instantdrs.android.network.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameRepository(private val apiService: ApiService) {

    // Simple in-memory cache to support the list UI since the backend lacks a list endpoint
    private val _cachedGames = MutableStateFlow<List<GameResponse>>(emptyList())
    val cachedGames = _cachedGames.asStateFlow()

    suspend fun createGame(name: String): Result<GameResponse> {
        return try {
            val response = apiService.createGame(GameCreateRequest(name))
            if (response.isSuccessful && response.body() != null) {
                val game = response.body()!!
                val currentList = _cachedGames.value.toMutableList()
                currentList.add(0, game) // Add to top
                _cachedGames.value = currentList
                Result.success(game)
            } else {
                Result.failure(Exception("Failed to create game: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGame(gameId: Long): Result<GameResponse> {
        return try {
            val response = apiService.getGame(gameId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Game not found: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGameRules(gameId: Long): Result<List<RuleResponse>> {
        return try {
            val response = apiService.getGameRules(gameId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get rules: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setGameRules(gameId: Long, ruleIds: List<Long>): Result<Unit> {
        return try {
            val response = apiService.setGameRules(gameId, GameRuleSelectionRequest(ruleIds))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to set rules: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createSession(gameId: Long): Result<GameSessionResponse> {
        return try {
            val response = apiService.createSession(gameId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create session: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSession(gameId: Long): Result<GameSessionResponse> {
        return try {
            val response = apiService.getSession(gameId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get session: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
