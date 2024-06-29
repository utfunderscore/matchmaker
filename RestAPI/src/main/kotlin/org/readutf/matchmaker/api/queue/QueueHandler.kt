package org.readutf.matchmaker.api.queue

import io.javalin.http.Context
import org.readutf.matchmaker.api.queue.store.QueueStore

interface QueueHandler<T : Queue> {

    fun createQueue(queueName: String, context: Context): T

    fun getQueueStore(): QueueStore

}