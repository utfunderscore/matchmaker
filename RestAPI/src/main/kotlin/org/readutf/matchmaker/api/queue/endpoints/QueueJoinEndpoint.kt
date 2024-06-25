package org.readutf.matchmaker.api.queue.endpoints

import com.alibaba.fastjson2.JSON
import io.github.oshai.kotlinlogging.KotlinLogging
import io.javalin.http.Context
import io.javalin.websocket.WsConfig
import io.javalin.websocket.WsConnectHandler
import io.javalin.websocket.WsMessageHandler
import org.readutf.matchmaker.api.queue.QueueManager
import org.readutf.matchmaker.api.queue.entry.QueueEntry
import org.readutf.matchmaker.api.utils.ApiResponse
import java.util.function.Consumer

class QueueJoinEndpoint(queueManager: QueueManager, queueName: String) : WsConfig() {

    private val logger = KotlinLogging.logger {}

    init {

        onMessage {
            val message = it.message()

            try {
                var queueEntry = JSON.parseObject(message, QueueEntry::class.java)

                queueManager.getQueue(queueName) ?: {
                    logger.error { "Queue has been removed but endpoint not removed." }
                    throw IllegalArgumentException("Queue does not exist")
                }

            } catch (e: Exception) {
                it.send(ApiResponse.failure("Invalid Queue Entry"))
                it.closeSession()
                return@onMessage
            }

        }

    }

}