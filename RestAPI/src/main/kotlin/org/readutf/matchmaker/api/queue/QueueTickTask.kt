package org.readutf.matchmaker.api.queue

import org.readutf.matchmaker.api.logger

class QueueTickTask(
    val queueManager: QueueManager,
    val queue: Queue,
) : Runnable {
    override fun run() {
        if (queue.getPlayersInQueue().isEmpty()) return

        logger.info { "Ticking queue" }

        queueManager.tickQueue(queue)
    }
}
