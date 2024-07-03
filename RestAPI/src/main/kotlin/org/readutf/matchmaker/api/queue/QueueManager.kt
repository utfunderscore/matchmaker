package org.readutf.matchmaker.api.queue

import io.github.oshai.kotlinlogging.KotlinLogging
import org.readutf.matchmaker.api.queue.queues.UnratedQueue
import org.readutf.matchmaker.api.queue.socket.QueueSocketManager
import org.readutf.matchmaker.shared.TypedJson
import org.readutf.matchmaker.shared.entry.QueueEntry
import org.readutf.matchmaker.shared.result.impl.EmptyQueueResult
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QueueManager(val socketManager: QueueSocketManager) {

    private val logger = KotlinLogging.logger {}
    private val queues = mutableMapOf<String, Queue>()
    private val queueToCreator = mutableMapOf<Queue, QueueHandler<*>>()
    private val queueExecutor = mutableMapOf<Queue, ExecutorService>()
    private val queueCreators = mutableMapOf<String, QueueHandler<*>>()

    init {
        logger.info { "Initializing QueueManager" }

        registerQueueHandler("unrated", UnratedQueue.UnratedQueueHandler())
    }

    fun handleTick(queue: Queue) = getExecutor(queue).submit {

        val result = queue.tick()

        if (result is EmptyQueueResult) return@submit

        result.getAffectedSessions()
            .distinct()
            .forEach { socketManager.notify(it, TypedJson(result)) }
    }

    fun joinQueue(queue: Queue, queueEntry: QueueEntry) {
        TODO()
    }


    private fun <T : Queue> registerQueueHandler(name: String, queueHandler: QueueHandler<T>) {
        require(!queueCreators.containsKey(name)) { "Queue creator already exists" }

        queueCreators[name] = queueHandler

        queueHandler.getQueueStore().loadQueues().forEach { queue: Queue ->
            registerQueue(queue.getSettings().queueName, queue)
        }

        println("queues: $queues")

    }

    fun registerQueue(name: String, queue: Queue): Queue {
        require(!queues.containsKey(name)) { "Queue already exists" }

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

    private fun getExecutor(queue: Queue): ExecutorService {
        return queueExecutor.getOrDefault(queue, Executors.newSingleThreadExecutor())
    }

    fun getQueues(): List<Queue> {
        return queues.values.toList()
    }

    fun getQueueCreators(): List<String> {
        return queueCreators.keys.toList()
    }

    fun stop() {

        queueCreators.values.forEach { queueHandler ->
            queueHandler.getQueueStore().saveQueues(queues.values.toList())
        }

    }

}