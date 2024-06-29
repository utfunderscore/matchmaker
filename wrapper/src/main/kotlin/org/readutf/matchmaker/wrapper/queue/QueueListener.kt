package org.readutf.matchmaker.wrapper.queue

import org.readutf.matchmaker.shared.entry.QueueEntry

interface QueueListener {

    /**
     * Called when a player successfully joins a queue
     */
    fun queueJoin(queueEntry: QueueEntry)

    /**
     * Called when a player leaves a queue
     * Occurs when a result is found or the player leaves the queue
     */
    fun queueLeave(queueEntry: QueueEntry)

    fun error(queueEntry: QueueEntry)

}