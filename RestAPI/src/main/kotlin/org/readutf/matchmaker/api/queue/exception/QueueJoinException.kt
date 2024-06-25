package org.readutf.matchmaker.api.queue.exception

class QueueJoinException(reason: String) : Exception(reason) {

    constructor(reason: String, throwable: Throwable) : this(reason) {
        initCause(throwable)
    }

}