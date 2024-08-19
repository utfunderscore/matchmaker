package org.readutf.matchmaker.api.game

import org.readutf.matchmaker.api.queue.socket.QueueSocketManager
import org.readutf.matchmaker.shared.TypedJson
import org.readutf.matchmaker.shared.result.MatchMakerResult
import org.readutf.matchmaker.shared.result.QueueTickData
import java.util.*

class GameManager(
    private val socketManager: QueueSocketManager,
    private val gameCreator: GameCreator,
) {
    fun processQueueResult(queueTickResult: QueueTickData) {
        val findGame = gameCreator.findGame(queueTickResult.queueName)
//        val findGame =
//            CompletableFuture.completedFuture(
//                Result.ok(GameResult("", 1, UUID.randomUUID())),
//            )

        findGame.thenAccept { gameResult ->

            val notification =
                if (gameResult.isError()) {
                    TypedJson(MatchMakerResult.failure(queueTickResult.queueName, "Could not find game."))
                } else {
                    TypedJson(
                        MatchMakerResult
                            .success(queueTickResult.queueName, queueTickResult, gameResult.get()),
                    )
                }

            for (affectedSocket in queueTickResult.getAffectedSockets()) {
                socketManager.notify(affectedSocket, notification)
            }
        }
    }
}
