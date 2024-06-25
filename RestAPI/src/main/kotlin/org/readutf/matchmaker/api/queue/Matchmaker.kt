package org.readutf.matchmaker.api.queue

import org.readutf.matchmaker.api.queue.entry.QueueEntry
import org.readutf.matchmaker.api.queue.result.QueueResult

interface Matchmaker<T : QueueEntry> {

    fun buildTeams(queue: List<T>): QueueResult

}