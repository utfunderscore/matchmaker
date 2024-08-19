package org.readutf.matchmaker.api.queue.queues

import com.alibaba.fastjson2.annotation.JSONField
import io.github.oshai.kotlinlogging.KotlinLogging
import io.javalin.http.Context
import org.readutf.matchmaker.api.queue.Queue
import org.readutf.matchmaker.api.queue.QueueHandler
import org.readutf.matchmaker.api.queue.exception.TeamBuildException
import org.readutf.matchmaker.api.queue.matchmaker.UnratedMatchmaker
import org.readutf.matchmaker.api.queue.store.QueueStore
import org.readutf.matchmaker.api.queue.store.impl.UnratedQueueStore
import org.readutf.matchmaker.shared.entry.QueueEntry
import org.readutf.matchmaker.shared.result.QueueTickData
import org.readutf.matchmaker.shared.result.Result
import org.readutf.matchmaker.shared.result.Result.Companion.error
import org.readutf.matchmaker.shared.result.Result.Companion.ok
import org.readutf.matchmaker.shared.settings.UnratedQueueSettings
import java.util.*

class UnratedQueue(
    @JSONField(serialize = false) val queueSettings: UnratedQueueSettings,
) : Queue {
    private val logger = KotlinLogging.logger { }

    private val queue = mutableListOf<QueueEntry>()
    private val playerTracker = mutableMapOf<UUID, QueueEntry>()
    private val matchmaker = UnratedMatchmaker(queueSettings.teamSize, queueSettings.numberOfTeams)

    override fun isInQueue(uuid: UUID): Boolean = playerTracker.containsKey(uuid)

    override fun addToQueue(queueEntry: QueueEntry): Result<Boolean> {
        if (queueEntry.playerIds.size > queueSettings.teamSize) return error("Too many players in queue entry")
        if (queueEntry.playerIds.isEmpty()) return error("No players in queue entry")

        if (queueEntry.playerIds.any { playerTracker.containsKey(it) }) {
            logger.info { "Player already in queue" }
            return error("Player already in queue")
        }

        queue.add(queueEntry)
        queueEntry.playerIds.forEach { playerTracker[it] = queueEntry }
        logger.info { "Added player to queue" }

        println("in Queue: $queue")

        return ok(true)
    }

    override fun tick(): Result<QueueTickData> {
        logger.info { "Ticking queue ${queueSettings.queueName}" }

        logger.info { "Before ${queue.size}" }

        val teams =
            try {
                matchmaker.buildTeams(queue)
            } catch (e: TeamBuildException) {
                logger.error(e) { "Error building teams" }
                return error(e.message ?: "null")
            }

        for (team in teams) {
            for (queueEntry in team) {
                removeFromQueue(queueEntry)
            }
        }

        logger.info { "After ${queue.size}" }

        if (teams.isEmpty()) {
            return error("No teams available")
        }

        return ok(QueueTickData(queueSettings.queueName, teams))
    }

    override fun removeFromQueue(queueEntry: QueueEntry): Result<Unit> {
        queue.remove(queueEntry)
        queueEntry.playerIds.forEach { playerTracker.remove(it) }
        return ok(Unit)
    }

    override fun invalidateSession(sessionId: String) {
        logger.info { "Invalidating session $sessionId" }
        queue.filter { it.sessionId == sessionId }.forEach { removeFromQueue(it) }
    }

    override fun getSettings(): UnratedQueueSettings = queueSettings

    override fun getPlayersInQueue(): List<QueueEntry> = queue

    class UnratedQueueHandler : QueueHandler<UnratedQueue> {
        override fun createQueue(
            queueName: String,
            context: Context,
        ): Result<UnratedQueue> {
            val teamSize =
                context.queryParam("teamSize")
                    ?: return error("Parameter 'teamSize' is required")

            val numberOfTeams =
                context.queryParam("numberOfTeams")
                    ?: return error("Parameter 'numberOfTeams' is required")

            return ok(UnratedQueue(UnratedQueueSettings(queueName, teamSize.toInt(), numberOfTeams.toInt())))
        }

        override fun getQueueStore(): QueueStore<UnratedQueue> = UnratedQueueStore()
    }
}
