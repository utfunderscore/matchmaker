package org.readutf.matchmaker.api.queue.queues

import com.alibaba.fastjson2.annotation.JSONField
import io.javalin.http.Context
import org.readutf.matchmaker.api.queue.Queue
import org.readutf.matchmaker.api.queue.QueueHandler
import org.readutf.matchmaker.api.queue.matchmaker.UnratedMatchmaker
import org.readutf.matchmaker.shared.result.QueueResult
import org.readutf.matchmaker.api.queue.store.QueueStore
import org.readutf.matchmaker.api.queue.store.impl.UnratedQueueStore
import org.readutf.matchmaker.shared.entry.QueueEntry
import org.readutf.matchmaker.shared.result.QueueResultType
import org.readutf.matchmaker.shared.settings.UnratedQueueSettings
import java.util.*


class UnratedQueue(@JSONField(serialize = false) val queueSettings: UnratedQueueSettings) : Queue {

    private val queue = mutableListOf<QueueEntry>()
    private val playerTracker = mutableMapOf<UUID, QueueEntry>()
    private val matchmaker = UnratedMatchmaker(queueSettings.teamSize, queueSettings.numberOfTeams)

    override fun isInQueue(uuid: UUID): Boolean {
        return playerTracker.containsKey(uuid)
    }

    override fun addToQueue(queueEntry: QueueEntry) {
        queueEntry.playerIds.any { playerTracker.containsKey(it) }.let { it ->
            if (it) return
            queue.add(queueEntry)
            queueEntry.playerIds.forEach { playerTracker[it] = queueEntry }
        }
    }

    override fun tick(): QueueResult {
        return try {
            matchmaker.buildTeams(queue)
        } catch (e: Exception) {
            QueueResult(resultType = QueueResultType.UNHANDLED_ERROR, emptyList(),  e.message ?: "An error occurred while building teams")
        }
    }

    override fun removeFromQueue(queueEntry: QueueEntry) {
        queueEntry.playerIds.any { playerTracker.containsKey(it) }.let { it ->
            if (!it) return
            queue.remove(queueEntry)
            queueEntry.playerIds.forEach { playerTracker.remove(it) }
        }
    }

    override fun getSettings(): UnratedQueueSettings {
        return queueSettings
    }

    class UnratedQueueHandler : QueueHandler<UnratedQueue> {

        override fun createQueue(queueName: String, context: Context): UnratedQueue {

            val teamSize = context.queryParam("teamSize")
                ?: throw IllegalArgumentException("teamSize is required")

            val numberOfTeams = context.queryParam("numberOfTeams")
                ?: throw IllegalArgumentException("numberOfTeams is required")

            return UnratedQueue(UnratedQueueSettings(queueName, teamSize.toInt(), numberOfTeams.toInt()))
        }

        override fun getQueueStore(): QueueStore<UnratedQueue> {
            return UnratedQueueStore()
        }

        override fun cast(queue: Queue): UnratedQueue {
            return queue as UnratedQueue
        }


    }

}