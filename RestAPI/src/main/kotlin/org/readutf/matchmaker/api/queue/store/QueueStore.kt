package org.readutf.matchmaker.api.queue.store

import org.readutf.matchmaker.api.queue.Queue

interface QueueStore {

    fun loadQueues(): List<Queue>

    fun saveQueues(queues: List<Queue>)

}