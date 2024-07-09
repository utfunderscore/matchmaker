package org.readutf.matchmaker.api.utils

import org.readutf.matchmaker.shared.entry.QueueEntry

object QueueEntryUtils {
    fun getTotalPlayers(queue: List<QueueEntry>): Int = queue.sumOf { queue.size }
}
