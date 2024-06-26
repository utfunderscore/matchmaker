package org.readutf.matchmaker.shared.entry

import java.time.LocalDateTime
import java.util.UUID

open class QueueEntry(var playerIds: List<UUID>, val joinedAt: LocalDateTime = LocalDateTime.now()) {

    fun size(): Int {
        return playerIds.size
    }



}
