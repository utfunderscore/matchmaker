package org.readutf.matchmaker.api.queue.result

import org.readutf.matchmaker.api.queue.entry.QueueEntry

class QueueResult(var teams: List<List<QueueEntry>>? = null, var error: String? = null) {

    companion object {

        fun success(teams: List<List<QueueEntry>>): QueueResult {
            return QueueResult(teams = teams)
        }

        fun error(error: String): QueueResult {
            return QueueResult(error = error)
        }

    }

}