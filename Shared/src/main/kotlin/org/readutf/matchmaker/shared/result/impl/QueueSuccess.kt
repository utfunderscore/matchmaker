package org.readutf.matchmaker.shared.result.impl

import org.readutf.matchmaker.shared.entry.QueueEntry
import org.readutf.matchmaker.shared.result.QueueResult

class QueueSuccess(val queue: String, val queueEntries: List<List<QueueEntry>>) : QueueResult(queue) {

    override fun getAffectedSessions(): Collection<String> {
        return queueEntries.flatten().map { it.sessionId }
    }


}