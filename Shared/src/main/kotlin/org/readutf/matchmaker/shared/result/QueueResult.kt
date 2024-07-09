package org.readutf.matchmaker.shared.result

abstract class QueueResult(
    val queueName: String,
) {
    abstract fun getAffectedSessions(): Collection<String>
}
