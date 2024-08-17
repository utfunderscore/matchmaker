package org.readutf.matchmaker.api.queue

import org.readutf.matchmaker.shared.entry.QueueEntry
import org.readutf.matchmaker.shared.result.QueueTickData
import org.readutf.matchmaker.shared.result.Result
import org.readutf.matchmaker.shared.settings.QueueSettings
import java.util.UUID

interface Queue {
    fun isInQueue(uuid: UUID): Boolean

    fun addToQueue(queueEntry: QueueEntry): Result<Boolean>

    fun tick(): Result<QueueTickData>

    fun removeFromQueue(queueEntry: QueueEntry): Result<Unit>

    fun invalidateSession(sessionId: String)

    fun getSettings(): QueueSettings

    fun getPlayersInQueue(): List<QueueEntry>
}
