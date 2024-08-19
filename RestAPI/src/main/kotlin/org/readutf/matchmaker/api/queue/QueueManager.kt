package org.readutf.matchmaker.api.queue

import io.github.oshai.kotlinlogging.KotlinLogging
import org.readutf.matchmaker.api.game.GameManager
import org.readutf.matchmaker.api.queue.queues.UnratedQueue
import org.readutf.matchmaker.shared.entry.QueueEntry
import org.readutf.matchmaker.shared.result.Result
import org.readutf.matchmaker.shared.settings.QueueSettings
import java.util.concurrent.*

class QueueManager(
    val gameManager: GameManager,
) {
    private val logger = KotlinLogging.logger {}
    private val queues = mutableMapOf<String, Queue>()
    private val queueExecutor = mutableMapOf<Queue, ScheduledExecutorService>()
    private val queueCreators = mutableMapOf<String, QueueHandler<*>>()

    init {
        logger.info { "Initializing QueueManager" }

        registerQueueHandler("unrated", UnratedQueue.UnratedQueueHandler())
    }

    fun joinQueue(
        queue: Queue,
        queueEntry: QueueEntry,
    ): CompletableFuture<Result<Boolean>> =
        supplyOnQueue(queue) {
            queue.addToQueue(queueEntry)
        }

    fun leaveQueue(
        queue: Queue,
        queueEntry: QueueEntry,
    ) = getExecutor(queue).submit {
        queue.removeFromQueue(queueEntry)
    }

    internal fun tickQueue(queue: Queue) =
        runOnQueue(queue) {
            /**
             * Continue to tick until the queue is unable to produce
             * a result or if the queue produces an error
             */
            while (true) {
                val result = queue.tick()
                if (result.isError()) break

                // Process queue result submits a task to seperate thread
                gameManager.processQueueResult(result.get())
            }
        }

    /**
     * Remove all queue entries linked to a specific sessionId
     *
     * (Used on socket disconnect)
     */
    fun invalidateSession(sessionId: String) {
        for (queue in queues.values) {
            supplyOnQueue(queue) { queue.invalidateSession(sessionId) }
        }
    }

    private fun <T : Queue> registerQueueHandler(
        handlerName: String,
        queueHandler: QueueHandler<T>,
    ) {
        require(!queueCreators.containsKey(handlerName)) { "Queue creator already exists" }

        queueCreators[handlerName] = queueHandler

        queueHandler.getQueueStore().loadQueues().forEach { queue: Queue ->
            registerQueue(queue.getSettings().queueName, queue)
        }
    }

    fun registerQueue(
        name: String,
        queue: Queue,
    ): Queue {
        require(!queues.containsKey(name)) { "Queue already exists" }

        synchronized(queues) {
            queues[name] = queue
        }

        getExecutor(queue).scheduleAtFixedRate(
            // command =
            QueueTickTask(this, queue),
            // initialDelay =
            0,
            // period =
            100,
            // unit =
            TimeUnit.MILLISECONDS,
        )

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
    fun <T> supplyOnQueue(
        queue: Queue,
        runnable: () -> T,
    ): CompletableFuture<T> = CompletableFuture.supplyAsync(runnable, getExecutor(queue))

    /*
     * Run a task on a specific queue
     * @return a future that will be completed when the task is done
     */
    fun runOnQueue(
        queue: Queue,
        runnable: () -> Unit,
    ) {
        CompletableFuture.runAsync(runnable, getExecutor(queue))
    }

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
    private fun getExecutor(queue: Queue): ScheduledExecutorService =
        queueExecutor.getOrPut(queue) { Executors.newSingleThreadScheduledExecutor() }

    /**
     * @return the settings of all queues which includes custom properties
     */
    fun getQueues(): Collection<QueueSettings> = queues.values.map { it.getSettings() }

    fun getInternalQueues() = queues.values.toList()

    /**
     * @return the names of all queue creators
     */
    fun getQueueCreators(): List<String> = queueCreators.keys.toList()

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
