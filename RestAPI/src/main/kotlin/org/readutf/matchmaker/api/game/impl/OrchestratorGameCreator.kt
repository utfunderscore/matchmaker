package org.readutf.matchmaker.api.game.impl

import org.readutf.matchmaker.api.game.GameCreator
import org.readutf.matchmaker.shared.result.GameResult
import org.readutf.matchmaker.shared.result.Result
import org.readutf.orchestrator.wrapper.OrchestratorApi
import java.util.concurrent.CompletableFuture

class OrchestratorGameCreator(
    hostname: String,
    port: Int,
) : GameCreator {
    private val api = OrchestratorApi(hostname, port)

    override fun findGame(queueType: String): CompletableFuture<Result<GameResult>> {
        return api.requestGame(queueType).thenApply {
            val server = it.server ?: return@thenApply Result.error("Invalid server")

            Result.ok(GameResult(server.address.host, server.address.port, it.gameId!!))
        }
    }
}
