package org.readutf.matchmaker.shared.entry

import java.time.LocalDateTime
import java.util.UUID

open class QueueEntry(val sessionId: String, val playerIds: List<UUID>, private val joinedAt: LocalDateTime = LocalDateTime.now()) {

    fun size(): Int {
        return playerIds.size
    }

    override fun toString(): String {
        return "QueueEntry(sessionId='$sessionId', playerIds=$playerIds, joinedAt=$joinedAt)"
    }


}
