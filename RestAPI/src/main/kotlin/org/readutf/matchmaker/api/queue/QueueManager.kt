package org.readutf.matchmaker.api.queue

import io.github.oshai.kotlinlogging.KotlinLogging
import org.readutf.matchmaker.api.queue.queues.UnratedQueue

class QueueManager {

    private val logger = KotlinLogging.logger {}
    private val queues = mutableMapOf<String, Queue>()
    private val queueCreators = mutableMapOf<String, QueueHandler<*>>()

    init {
        logger.info { "Initializing QueueManager" }

        registerQueueHandler("unrated", UnratedQueue.UnratedQueueHandler())
    }

    fun <T : Queue> registerQueueHandler(name: String, queueHandler: QueueHandler<T>) {

        if (queueCreators.containsKey(name)) {
            throw IllegalArgumentException("Queue creator already exists")
        }

        queueCreators[name] = queueHandler

        queueHandler.getQueueStore().loadQueues().forEach { queue: Queue ->
            registerQueue(queue.getSettings().queueName, queue)
        }

        println("queues: $queues")

    }

    fun registerQueue(name: String, queue: Queue): Queue {
        if (queues.containsKey(name)) {
            throw IllegalArgumentException("Queue already exists")
        }

        synchronized(queues) {
            queues[name] = queue
        }

        return queue
    }

    fun getQueueCreator(name: String): QueueHandler<*>? {
        println(queueCreators.keys)
        return queueCreators[name]
    }

    fun getQueue(queueName: String): Queue? {
        return queues[queueName];
    }

    fun getQueues(): List<Queue> {
        return queues.values.toList()
    }

    fun stop() {

        queueCreators.values.forEach { queueHandler ->
            queueHandler.getQueueStore().saveQueues(queues.values.toList())
        }

    }

}