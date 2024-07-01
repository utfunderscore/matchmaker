package org.readutf.matchmaker.api.queue.store

import org.readutf.matchmaker.api.queue.Queue

interface QueueStore<T : Queue> {

    fun loadQueues(): List<T>

    fun saveQueues(queues: List<Queue>)

}