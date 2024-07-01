package org.readutf.matchmaker.shared.result

import org.readutf.matchmaker.shared.entry.QueueEntry

data class QueueResult(
    val queueName: String,
    val resultType: QueueResultType,
    val teams: List<List<QueueEntry>>,
    val message: String
) {
    companion object {
        fun empty(queueName: String): QueueResult {
            return QueueResult(queueName, QueueResultType.NEUTRAL, emptyList(), "")
        }
    }
}

