package org.readutf.matchmaker.api.queue

import org.readutf.matchmaker.shared.result.QueueResult
import org.readutf.matchmaker.shared.entry.QueueEntry
import org.readutf.matchmaker.shared.settings.QueueSettings
import java.util.UUID

interface Queue {

    fun isInQueue(uuid: UUID): Boolean

    fun addToQueue(queueEntry: QueueEntry)

    fun tick(): QueueResult;

    fun removeFromQueue(queueEntry: QueueEntry)

    fun getSettings(): QueueSettings

    fun getPlayersInQueue(): List<QueueEntry>

}