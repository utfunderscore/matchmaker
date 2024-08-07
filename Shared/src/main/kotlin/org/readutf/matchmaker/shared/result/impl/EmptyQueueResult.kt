package org.readutf.matchmaker.shared.result.impl

import org.readutf.matchmaker.shared.result.QueueResult

class EmptyQueueResult(
    name: String,
) : QueueResult(name) {
    override fun getAffectedSessions(): Collection<String> = emptyList()
}
