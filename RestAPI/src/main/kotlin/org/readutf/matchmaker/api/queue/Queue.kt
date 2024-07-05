package org.readutf.matchmaker.api.queue

import org.readutf.matchmaker.shared.result.QueueResult
import org.readutf.matchmaker.shared.entry.QueueEntry
import org.readutf.matchmaker.shared.settings.QueueSettings
import panda.std.Result
import java.util.UUID

interface Queue {

    fun isInQueue(uuid: UUID): Boolean

    fun addToQueue(queueEntry: QueueEntry): Result<Boolean, String>

    fun tick(): QueueResult;

    fun removeFromQueue(queueEntry: QueueEntry): Result<Unit, String>

    fun invalidateSession(sessionId: String)

    fun getSettings(): QueueSettings

    fun getPlayersInQueue(): List<QueueEntry>

}