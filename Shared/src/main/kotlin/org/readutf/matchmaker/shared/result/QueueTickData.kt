package org.readutf.matchmaker.shared.result

import org.readutf.matchmaker.shared.entry.QueueEntry

data class QueueTickData(
    val queueName: String,
    val teams: List<List<QueueEntry>>,
) {
    fun getAffectedSockets() = teams.flatten().map { it.sessionId }.distinct()
}
