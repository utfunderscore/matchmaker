package org.readutf.matchmaker.api.queue

import org.readutf.matchmaker.shared.result.QueueResult
import org.readutf.matchmaker.shared.entry.QueueEntry

interface Matchmaker<T : QueueEntry> {

    fun buildTeams(queue: List<T>): List<List<T>>

}