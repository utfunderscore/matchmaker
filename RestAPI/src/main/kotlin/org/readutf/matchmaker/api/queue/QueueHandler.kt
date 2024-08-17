package org.readutf.matchmaker.api.queue

import io.javalin.http.Context
import org.readutf.matchmaker.api.queue.store.QueueStore
import org.readutf.matchmaker.shared.result.Result

/**
 * Queue handlers are responsible for creating and managing queues.
 * Different queue types may require different parameters, settings
 * and other data to be created.
 *
 * The queue handler also defines how the queue is serialized and stored.
 * @see org.readutf.matchmaker.api.queue.queues.UnratedQueue.UnratedQueueHandler for an example
 */
interface QueueHandler<T : Queue> {
    fun createQueue(
        queueName: String,
        context: Context,
    ): Result<T>

    fun getQueueStore(): QueueStore<T>
}
