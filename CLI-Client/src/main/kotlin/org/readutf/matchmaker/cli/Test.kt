package org.readutf.matchmaker.cli

import org.readutf.matchmaker.shared.result.QueueResult
import org.readutf.matchmaker.wrapper.QueueListener
import org.readutf.matchmaker.wrapper.QueueManager

fun main() {

    val queueManager = QueueManager("localhost", 8280, object : QueueListener {
        override fun onQueueResult(queueResult: QueueResult) {

        }
    })

    val queue = queueManager.getQueue("test")

    if (queue == null) {
        println("Queue not found")
        return
    }


}