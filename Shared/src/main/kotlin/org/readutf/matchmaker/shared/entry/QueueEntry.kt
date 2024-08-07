package org.readutf.matchmaker.shared.entry

import java.time.LocalDateTime
import java.util.UUID

open class QueueEntry(
    val entryId: UUID = UUID.randomUUID(),
    val sessionId: String,
    val playerIds: List<UUID>,
    val joinedAt: LocalDateTime = LocalDateTime.now(),
) {
    fun size(): Int = playerIds.size

    override fun toString(): String = "QueueEntry(sessionId='$sessionId', playerIds=$playerIds, joinedAt=$joinedAt)"
}
