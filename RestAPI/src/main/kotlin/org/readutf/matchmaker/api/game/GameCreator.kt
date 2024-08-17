package org.readutf.matchmaker.api.game

import org.readutf.matchmaker.shared.result.GameResult
import org.readutf.matchmaker.shared.result.Result
import java.util.concurrent.CompletableFuture

interface GameCreator {
    fun findGame(queueType: String): CompletableFuture<Result<GameResult>>
}
