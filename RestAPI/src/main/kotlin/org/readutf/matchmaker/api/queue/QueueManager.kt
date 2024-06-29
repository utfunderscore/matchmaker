package org.readutf.matchmaker.api.queue

import io.github.oshai.kotlinlogging.KotlinLogging

class QueueManager {

    private val logger = KotlinLogging.logger {}
    private val queues = mutableMapOf<String, Queue>()
    private val queueCreators = mutableMapOf<String, QueueCreator<*>>()

    fun registerQueueCreator(name: String, queueCreator: QueueCreator<*>) {

        if (queueCreators.containsKey(name)) {
            throw IllegalArgumentException("Queue creator already exists")
        }

        synchronized(queueCreators) {
            queueCreators[name] = queueCreator
        }
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

    fun getQueueCreator(name: String): QueueCreator<*>? {
        println(queueCreators.keys)
        return queueCreators[name]
    }

    fun getQueue(queueName: String): Queue? {
        return queues[queueName];
    }

    fun getQueues(): List<Queue> {
        return queues.values.toList()
    }

}