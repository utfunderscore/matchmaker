package org.readutf.matchmaker.wrapper

import org.readutf.matchmaker.shared.result.GameResult
import org.readutf.matchmaker.shared.result.QueueTickData

interface QueueListener {
    fun onQueueSuccess(
        queue: Queue,
        queueResult: QueueTickData,
        gameResult: GameResult,
    )

    fun onMatchMakerError(
        queue: Queue,
        failureReason: String,
    )
}
