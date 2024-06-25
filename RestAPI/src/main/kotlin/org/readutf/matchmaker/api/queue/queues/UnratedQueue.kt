package org.readutf.matchmaker.api.queue.queues

import io.javalin.http.Context
import org.readutf.matchmaker.api.queue.Queue
import org.readutf.matchmaker.api.queue.QueueCreator
import org.readutf.matchmaker.api.queue.entry.QueueEntry
import org.readutf.matchmaker.api.queue.matchmaker.UnratedMatchmaker
import org.readutf.matchmaker.api.queue.result.QueueResult
import java.util.*


class UnratedQueue(teamSize: Int, numberOfTeams: Int) : Queue<QueueEntry> {

    private val queue = mutableListOf<QueueEntry>()
    private val playerTracker = mutableMapOf<UUID, QueueEntry>()
    private val matchmaker = UnratedMatchmaker(teamSize, numberOfTeams)

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
        return matchmaker.buildTeams(queue)
    }

    override fun removeFromQueue(queueEntry: QueueEntry) {
        queueEntry.playerIds.any { playerTracker.containsKey(it) }.let { it ->
            if (!it) return
            queue.remove(queueEntry)
            queueEntry.playerIds.forEach { playerTracker.remove(it) }
        }
    }

    class UnratedQueueCreator : QueueCreator {

        override fun createQueue(context: Context): Queue<QueueEntry> {

            val teamSize = context.queryParam("teamSize")
                ?: throw IllegalArgumentException("teamSize is required")
            val numberOfTeams = context.queryParam("numberOfTeams")
                ?: throw IllegalArgumentException("numberOfTeams is required")

            return UnratedQueue(teamSize = teamSize.toInt(), numberOfTeams = numberOfTeams.toInt())
        }

    }

}