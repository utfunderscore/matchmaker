package org.readutf.matchmaker.wrapper

import org.readutf.matchmaker.shared.settings.QueueSettings
import org.readutf.matchmaker.wrapper.api.QueueService
import java.util.*
import java.util.function.Supplier

class Queue(private val queueSettings: QueueSettings, private val queueService: QueueService) {

    private val queueName = queueSettings.queueName

    private val exceptionMap = mutableMapOf<String, Supplier<Exception>>(

        "Queue $queueName not found" to Supplier { QueueNotFoundException() },
        "Invalid player teams" to Supplier { InvalidTeamsException() },

    )

    @Throws(Exception::class)
    fun join(players: List<List<UUID>>): Boolean {

        val joinHandle = queueService.join(queueName, players).execute()

        if (!joinHandle.isSuccessful || joinHandle.body() == null) throw ServiceUnreachableException()

        val joinResponse = joinHandle.body()!!

        if (joinResponse.success) return true

        exceptionMap.getOrDefault(joinResponse.failureReason, Supplier { Exception(joinResponse.failureReason) }).get()
            .let { throw it }
    }


}