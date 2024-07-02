package org.readutf.matchmaker.shared.result.impl

import org.readutf.matchmaker.shared.entry.QueueEntry
import org.readutf.matchmaker.shared.result.QueueResult

class MatchMakerError(
    val queue: String,
    private val affectedTeams: List<QueueEntry>,
    val failureReason: String
) : QueueResult(queue) {

    override fun getAffectedSessions(): Collection<String> {
        return affectedTeams.map { it.sessionId }
    }

}