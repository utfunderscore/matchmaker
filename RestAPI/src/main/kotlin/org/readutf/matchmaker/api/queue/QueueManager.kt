package org.readutf.matchmaker.api.queue

import io.javalin.Javalin
import org.readutf.matchmaker.api.queue.entry.QueueEntry

class QueueManager(private var javalin: Javalin) {

    private val queues = mutableMapOf<String, Queue<QueueEntry>>()

    fun registerQueueCreator(name: String, queueCreator: QueueCreator) {

        javalin.put("/queue/$name") {
            val queue = queueCreator.createQueue(it)
            queues[name] = queue
            it.json(queue)
        }
    }

}