package org.readutf.matchmaker.shared.result.impl

import org.readutf.matchmaker.shared.entry.QueueEntry
import org.readutf.matchmaker.shared.result.QueueResult

class QueueSuccess(val queue: String, val teams: List<List<QueueEntry>>) : QueueResult(queue) {
}