package org.readutf.matchmaker.wrapper

import org.readutf.matchmaker.shared.result.QueueResult
import java.util.UUID

interface QueueListener {

    fun onQueueResult(queue: Queue, teams: List<List<UUID>>)

    fun onQueueFailure(queue: Queue, failureReason: String)

}