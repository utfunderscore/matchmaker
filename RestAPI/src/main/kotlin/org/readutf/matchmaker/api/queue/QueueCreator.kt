package org.readutf.matchmaker.api.queue

import io.javalin.http.Context

interface QueueCreator<T : Queue> {

    fun createQueue(queueName: String, context: Context): T

}