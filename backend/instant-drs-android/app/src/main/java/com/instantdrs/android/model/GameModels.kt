package com.instantdrs.android.model

enum class GameStatus {
    CREATED, ACTIVE, COMPLETED
}

data class GameResponse(
    val id: Long,
    val name: String,
    val status: GameStatus,
    val createdAt: String?
)

data class GameCreateRequest(
    val name: String
)

data class GameRuleSelectionRequest(
    val ruleIds: List<Long>
)

data class RuleResponse(
    val id: Long,
    val ruleType: String,
    val name: String,
    val description: String
)

enum class SessionStatus {
    CREATED, RECORDING, STOPPED, COMPLETED
}

data class VideoMetadataResponse(
    val duration: Double?,
    val width: Int?,
    val height: Int?,
    val fps: Double?,
    val frameCount: Long?,
    val codec: String?,
    val fileSize: Long?
)

data class GameSessionResponse(
    val id: Long,
    val gameId: Long,
    val status: SessionStatus,
    val startedAt: String?,
    val endedAt: String?,
    val recordingMetadata: String?,
    val recordingFile: String?,
    val videoMetadata: VideoMetadataResponse?
)
