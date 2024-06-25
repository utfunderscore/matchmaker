package org.readutf.matchmaker.api.queue

import io.javalin.http.Context
import org.readutf.matchmaker.api.queue.entry.QueueEntry

interface QueueCreator<T : Queue<*>> {

    fun createQueue(queueName: String, context: Context): T

}