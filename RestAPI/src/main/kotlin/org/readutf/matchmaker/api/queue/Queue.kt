package org.readutf.matchmaker.api.queue

import org.readutf.matchmaker.api.queue.entry.QueueEntry
import org.readutf.matchmaker.api.queue.result.QueueResult
import java.util.UUID

interface Queue<T : QueueEntry> {

    fun isInQueue(uuid: UUID): Boolean

    fun addToQueue(queueEntry: T)

    fun tick(): QueueResult;

    fun removeFromQueue(queueEntry: T)

}