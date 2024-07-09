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
import org.readutf.matchmaker.shared.result.QueueResult
import org.readutf.matchmaker.shared.result.impl.EmptyQueueResult
import org.readutf.matchmaker.shared.result.impl.MatchMakerError
import org.readutf.matchmaker.shared.result.impl.QueueSuccess
import org.readutf.matchmaker.shared.settings.UnratedQueueSettings
import panda.std.Result
import java.util.*

class UnratedQueue(
    @JSONField(serialize = false) val queueSettings: UnratedQueueSettings,
) : Queue {
    private val logger = KotlinLogging.logger { }

    private val queue = mutableListOf<QueueEntry>()
    private val playerTracker = mutableMapOf<UUID, QueueEntry>()
    private val matchmaker = UnratedMatchmaker(queueSettings.teamSize, queueSettings.numberOfTeams)

    override fun isInQueue(uuid: UUID): Boolean = playerTracker.containsKey(uuid)

    override fun addToQueue(queueEntry: QueueEntry): Result<Boolean, String> {
        if (queueEntry.playerIds.size > queueSettings.teamSize) return Result.error("Too many players in queue entry")
        if (queueEntry.playerIds.isEmpty()) return Result.error("No players in queue entry")

        if (queueEntry.playerIds.any { playerTracker.containsKey(it) }) {
            logger.info { "Player already in queue" }
            return Result.error("Player already in queue")
        }

        queue.add(queueEntry)
        queueEntry.playerIds.forEach { playerTracker[it] = queueEntry }
        logger.info { "Added player to queue" }
        return Result.ok(true)
    }

    override fun tick(): QueueResult {
        logger.info { "Ticking queue ${queueSettings.queueName}" }

        logger.info { "Before ${queue.size}" }

        val teams =
            try {
                matchmaker.buildTeams(queue)
            } catch (e: TeamBuildException) {
                logger.error(e) { "Error building teams" }
                return MatchMakerError(queueSettings.queueName, queue, e.message ?: "Unknown Error")
            }

        logger.info { "After ${queue.size}" }

        if (teams.isEmpty()) {
            return EmptyQueueResult(queueSettings.queueName)
        }

        for (team in teams) {
            for (queueEntry in team) {
                removeFromQueue(queueEntry)
            }
        }

        return QueueSuccess(queueSettings.queueName, teams)
    }

    override fun removeFromQueue(queueEntry: QueueEntry): Result<Unit, String> {
        if (queueEntry.playerIds.any { !playerTracker.containsKey(it) }) {
            return Result.error("Player not in queue")
        }
        queue.remove(queueEntry)
        queueEntry.playerIds.forEach { playerTracker.remove(it) }
        return Result.ok(Unit)
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
        ): Result<UnratedQueue, String> {
            val teamSize =
                context.queryParam("teamSize")
                    ?: return Result.error("Parameter 'teamSize' is required")

            val numberOfTeams =
                context.queryParam("numberOfTeams")
                    ?: return Result.error("Parameter 'numberOfTeams' is required")

            return Result.ok(UnratedQueue(UnratedQueueSettings(queueName, teamSize.toInt(), numberOfTeams.toInt())))
        }

        override fun getQueueStore(): QueueStore<UnratedQueue> = UnratedQueueStore()
    }
}
