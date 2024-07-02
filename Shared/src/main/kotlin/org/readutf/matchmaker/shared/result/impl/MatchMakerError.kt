package org.readutf.matchmaker.shared.result.impl

import org.readutf.matchmaker.shared.result.QueueResult

class MatchMakerError(val queue: String, val failureReason: String) : QueueResult(queue) {
}