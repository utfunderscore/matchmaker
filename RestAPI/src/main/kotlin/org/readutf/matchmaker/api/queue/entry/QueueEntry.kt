package org.readutf.matchmaker.api.queue.entry

import io.javalin.websocket.WsContext
import java.time.LocalDateTime
import java.util.UUID

abstract class QueueEntry(var wsContext: WsContext, var playerIds: List<UUID>, val joinedAt: LocalDateTime = LocalDateTime.now()) {

    fun size(): Int {
        return playerIds.size
    }

    fun notifyEntry(data: Any) {
        wsContext.send(data)
    }

    class DefaultQueueEntry(wsContext: WsContext, playerIds: List<UUID>, joinedAt: LocalDateTime = LocalDateTime.now()) : QueueEntry(wsContext, playerIds)



}
