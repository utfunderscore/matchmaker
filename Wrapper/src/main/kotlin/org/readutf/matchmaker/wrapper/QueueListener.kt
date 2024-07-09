package org.readutf.matchmaker.wrapper

import org.readutf.matchmaker.shared.entry.QueueEntry

interface QueueListener {
    fun onQueueSuccess(
        queue: Queue,
        teams: List<List<QueueEntry>>,
    )

    fun onMatchMakerError(
        queue: Queue,
        failureReason: String,
    )
}
