package org.readutf.matchmaker.cli

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.TypeReference
import io.github.oshai.kotlinlogging.KotlinLogging
import org.readutf.matchmaker.shared.entry.QueueEntry
import org.readutf.matchmaker.shared.result.QueueResult
import org.readutf.matchmaker.wrapper.Queue
import org.readutf.matchmaker.wrapper.QueueListener
import org.readutf.matchmaker.wrapper.QueueManager
import java.util.*

val logger = KotlinLogging.logger {  }

fun main() {

//    println(
//        JSON.parseObject("[[e59b259b-030c-40c1-98aa-8e0c469d6f70], [7b75fb9b-b602-401e-8ce2-64e60c9baae1]]",
//            object : TypeReference<List<List<UUID>>>() {})
//    )
//
//    println(JSON.toJSONString(listOf(listOf(UUID.randomUUID()), listOf(UUID.randomUUID()))))

    val queueManager = QueueManager("localhost", 8280, SimpleQueueListener())

    val queue = queueManager.getQueue("test")

    if (queue == null) {
        println("Queue not found")
        return
    }

    queue.join(listOf(listOf(UUID.randomUUID()), listOf(UUID.randomUUID()))).invokeOnCompletion {

        logger.info { "Joined queue" }

    }

}

class SimpleQueueListener : QueueListener {

    override fun onQueueSuccess(queue: Queue, teams: List<List<QueueEntry>>) {
        println("Success ! $teams")
    }

    override fun onMatchMakerError(queue: Queue, failureReason: String) {
        println("ERROR! $failureReason")
    }

}