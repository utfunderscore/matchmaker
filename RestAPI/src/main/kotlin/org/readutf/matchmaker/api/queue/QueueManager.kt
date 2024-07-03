package org.readutf.matchmaker.api.queue

import io.github.oshai.kotlinlogging.KotlinLogging
import org.readutf.matchmaker.api.queue.queues.UnratedQueue
import org.readutf.matchmaker.api.queue.socket.QueueSocketManager
import org.readutf.matchmaker.shared.TypedJson
import org.readutf.matchmaker.shared.entry.QueueEntry
import org.readutf.matchmaker.shared.result.impl.EmptyQueueResult
import org.readutf.matchmaker.shared.settings.QueueSettings
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QueueManager(val socketManager: QueueSocketManager) {

    private val logger = KotlinLogging.logger {}
    private val queues = mutableMapOf<String, Queue>()
    private val queueExecutor = mutableMapOf<Queue, ExecutorService>()
    private val queueCreators = mutableMapOf<String, QueueHandler<*>>()

    init {
        logger.info { "Initializing QueueManager" }

        registerQueueHandler("unrated", UnratedQueue.UnratedQueueHandler())
    }


    fun joinQueue(queue: Queue, queueEntry: QueueEntry): CompletableFuture<Void> {
        return CompletableFuture.allOf(
            /*Add player to queue*/
            runOnQueue(queue) { queue.addToQueue(queueEntry) },
            /*Tick the queue to produce a result*/
            handleTick(queue)
        )
    }

    fun leaveQueue(queue: Queue, queueEntry: QueueEntry) = getExecutor(queue).submit {
        queue.removeFromQueue(queueEntry)
    }

    private fun handleTick(queue: Queue) = runOnQueue(queue) {

        val result = queue.tick()

        if (result is EmptyQueueResult) return@runOnQueue

        result.getAffectedSessions()
            .distinct()
            .forEach { socketManager.notify(it, TypedJson(result)) }
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

    /**
     * Get a queue handler by its name
     */
    fun getQueueHandler(name: String): QueueHandler<*>? = queueCreators[name]

    /**
     * Run a task on a specific queue
     * @return a future that will be completed when the task is done
     */
    fun <T> runOnQueue(queue: Queue, runnable: () -> T): CompletableFuture<T> =
        CompletableFuture.supplyAsync(runnable, getExecutor(queue))

    /**
     * Get a queue by its name
     * @return the queue or null if it does not exist
     */
    fun getQueue(queueName: String): Queue? = queues[queueName]

    /**
     * Each queue has its own executor to prevent blocking the main thread,
     * and ensuring that all operations are thread-safe
     *
     * @return the executor for the given queue
     */
    private fun getExecutor(queue: Queue): ExecutorService =
        queueExecutor.getOrDefault(queue, Executors.newSingleThreadExecutor())

    /**
     * @return the settings of all queues which includes custom properties
     */
    fun getQueues(): Collection<QueueSettings> {
        return queues.values.map { it.getSettings() }
    }

    /**
     * @return the names of all queue creators
     */
    fun getQueueCreators(): List<String> {
        return queueCreators.keys.toList()
    }

    /**
     * Stop the queue manager and its subcomponents
     * This will empty all active queues, and save all queues to disk
     */
    fun stop() {

        queueCreators.values.forEach { queueHandler ->
            queueHandler.getQueueStore().saveQueues(queues.values.toList())
        }

    }

}