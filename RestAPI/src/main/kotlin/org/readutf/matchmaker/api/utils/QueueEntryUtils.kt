package org.readutf.matchmaker.api.utils

import org.readutf.matchmaker.api.queue.entry.QueueEntry

object QueueEntryUtils {

    fun getNumberInQueue(queue: List<QueueEntry>): Int {
        return queue.sumOf { queue.size }
    }

}