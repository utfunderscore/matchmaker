package org.readutf.matchmaker.shared.result

import org.readutf.matchmaker.shared.entry.QueueEntry

data class QueueResult(var teams: List<List<QueueEntry>>? = emptyList(), var error: String? = null, val empty: Boolean = false) {


    companion object {

        fun success(teams: List<List<QueueEntry>>): QueueResult {
            return QueueResult(teams = teams)
        }

        fun empty(): QueueResult {
            return QueueResult(empty = true)
        }

        fun error(error: String): QueueResult {
            return QueueResult(error = error)
        }

    }

}