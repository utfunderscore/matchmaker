package org.readutf.matchmaker.shared.result

import java.util.UUID

data class GameResult(
    val hostAddress: String,
    val port: Int,
    val gameId: UUID,
)
