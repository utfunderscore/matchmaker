package org.readutf.matchmaker.api.queue

import io.github.oshai.kotlinlogging.KotlinLogging
import io.javalin.Javalin
import org.readutf.matchmaker.api.utils.ApiResponse

class QueueManager(private var javalin: Javalin) {

    private val logger = KotlinLogging.logger {}
    private val queues = mutableMapOf<String, Queue<*>>()

    fun <E : Queue<*>> registerQueueCreator(name: String, queueCreator: QueueCreator<E>) {

        javalin.put("/queue/$name") {
            val queueName = it.queryParam("name") ?: throw IllegalArgumentException("Missing query parameter 'name'")
            if(queues.containsKey(queueName)) {
                it.json(ApiResponse.failure("Queue already exists"))
                return@put
            }

            val queue = queueCreator.createQueue(queueName, it)
            registerQueue(queueName, queue)

            it.json(ApiResponse.success(queue))
        }

        logger.info { "Endpoint registered: (PUT) /queue/$name" }

    }

    private fun registerQueue(name: String, queue: Queue<*>): Queue<*> {
        if(queues.containsKey(name)) {
            throw IllegalArgumentException("Queue already exists")
        }

        synchronized(queues) {
            queues[name] = queue
        }

        javalin.ws("/queue/join/$name") { ws ->
            ws.onConnect { session ->
                val queue = queues[name] ?: throw IllegalArgumentException("Queue does not exist")
            }
        }

        return queue
    }

    fun getQueue(queueName: String): Queue<*>? {
        return queues[queueName];
    }

}