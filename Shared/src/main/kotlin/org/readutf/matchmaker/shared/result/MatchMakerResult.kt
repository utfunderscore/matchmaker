package org.readutf.matchmaker.shared.result

data class MatchMakerResult private constructor(
    val queueId: String,
    val queueResult: QueueTickData?,
    val gameResult: GameResult?,
    val failureReason: String?,
) {
    fun isSuccess() = failureReason == null

    companion object {
        fun success(
            queueId: String,
            queueResult: QueueTickData,
            gameResult: GameResult,
        ): MatchMakerResult =
            MatchMakerResult(
                queueId = queueId,
                queueResult = queueResult,
                gameResult = gameResult,
                failureReason = null,
            )

        fun failure(
            queueId: String,
            reason: String,
        ) = MatchMakerResult(
            queueId = queueId,
            queueResult = null,
            gameResult = null,
            failureReason = reason,
        )
    }
}
