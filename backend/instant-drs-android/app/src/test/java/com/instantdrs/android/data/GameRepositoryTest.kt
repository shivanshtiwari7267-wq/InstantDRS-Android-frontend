package com.instantdrs.android.data

import com.instantdrs.android.model.*
import com.instantdrs.android.network.ApiService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import retrofit2.Response

class GameRepositoryTest {

    @Mock
    lateinit var apiService: ApiService

    private lateinit var gameRepository: GameRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        gameRepository = GameRepository(apiService)
    }

    @Test
    fun `createGame success updates cached games and returns game`() = runBlocking {
        val game = GameResponse(1L, "Test Game", GameStatus.CREATED, "2023-10-10")
        `when`(apiService.createGame(any(GameCreateRequest::class.java))).thenReturn(Response.success(game))

        val result = gameRepository.createGame("Test Game")
        
        assertTrue(result.isSuccess)
        assertEquals(game, result.getOrNull())
        assertTrue(gameRepository.cachedGames.value.contains(game))
    }

    @Test
    fun `createGame failure returns failure`() = runBlocking {
        `when`(apiService.createGame(any(GameCreateRequest::class.java)))
            .thenReturn(Response.error(400, okhttp3.ResponseBody.create(null, "")))

        val result = gameRepository.createGame("Invalid Game")
        
        assertTrue(result.isFailure)
        assertTrue(gameRepository.cachedGames.value.isEmpty())
    }

    @Test
    fun `getGame success`() = runBlocking {
        val game = GameResponse(1L, "Test Game", GameStatus.CREATED, "2023-10-10")
        `when`(apiService.getGame(1L)).thenReturn(Response.success(game))

        val result = gameRepository.getGame(1L)
        
        assertTrue(result.isSuccess)
        assertEquals(game, result.getOrNull())
    }

    @Test
    fun `getGame 404 failure`() = runBlocking {
        `when`(apiService.getGame(999L))
            .thenReturn(Response.error(404, okhttp3.ResponseBody.create(null, "")))

        val result = gameRepository.getGame(999L)
        
        assertTrue(result.isFailure)
    }

    @Test
    fun `setGameRules success`() = runBlocking {
        `when`(apiService.setGameRules(eq(1L), any(GameRuleSelectionRequest::class.java)))
            .thenReturn(Response.success(Any()))

        val result = gameRepository.setGameRules(1L, listOf(1L, 2L))
        
        assertTrue(result.isSuccess)
    }

    @Test
    fun `createSession success`() = runBlocking {
        val session = GameSessionResponse(1L, 1L, SessionStatus.CREATED, null, null, null, null, null)
        `when`(apiService.createSession(1L)).thenReturn(Response.success(session))

        val result = gameRepository.createSession(1L)
        
        assertTrue(result.isSuccess)
        assertEquals(session, result.getOrNull())
    }

    @Test
    fun `getSession success`() = runBlocking {
        val session = GameSessionResponse(1L, 1L, SessionStatus.CREATED, null, null, null, null, null)
        `when`(apiService.getSession(1L)).thenReturn(Response.success(session))

        val result = gameRepository.getSession(1L)
        
        assertTrue(result.isSuccess)
        assertEquals(session, result.getOrNull())
    }
}
