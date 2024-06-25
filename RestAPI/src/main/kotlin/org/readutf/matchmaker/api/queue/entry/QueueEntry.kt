package org.readutf.matchmaker.api.queue.entry

import java.time.LocalDateTime
import java.util.UUID

abstract class QueueEntry(var playerIds: List<UUID>, val joinedAt: LocalDateTime = LocalDateTime.now()) {

    fun size(): Int {
        return playerIds.size
    }

    companion object {
        fun from(playerIds: List<UUID>, joinedAt: LocalDateTime = LocalDateTime.now()) = DefaultQueueEntry(playerIds, joinedAt)
    }

    class DefaultQueueEntry(playerIds: List<UUID>, joinedAt: LocalDateTime = LocalDateTime.now()) : QueueEntry(playerIds)



}
