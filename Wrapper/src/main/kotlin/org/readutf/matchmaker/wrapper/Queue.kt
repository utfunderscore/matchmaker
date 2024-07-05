package org.readutf.matchmaker.wrapper

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.readutf.matchmaker.shared.entry.QueueEntry
import org.readutf.matchmaker.shared.settings.QueueSettings
import org.readutf.matchmaker.wrapper.api.QueueService
import java.util.*
import java.util.function.Supplier

class Queue(private val sessionId: String, queueSettings: QueueSettings, private val queueService: QueueService) {

    private val queueName = queueSettings.queueName

    private val exceptionMap = mutableMapOf<String, Supplier<Exception>>(

        "Queue $queueName not found" to Supplier { QueueNotFoundException() },
        "Invalid player teams" to Supplier { InvalidTeamsException() },

    )

    @Throws(Exception::class)
    fun join(players: List<List<UUID>>): Deferred<Unit> = runBlocking {
        async {
            val joinResult = queueService.join(queueName, players.map { QueueEntry(sessionId = sessionId, playerIds = it) })

            if(!joinResult.success) throw Exception("Failed to join queue")

            return@async
        }
    }


}