package org.readutf.matchmaker.api.queue.socket

import io.github.oshai.kotlinlogging.KotlinLogging
import io.javalin.websocket.WsConnectContext
import io.javalin.websocket.WsContext
import org.readutf.matchmaker.shared.TypedJson

class QueueSocketManager {

    private val logger = KotlinLogging.logger {}
    private val activeSockets = mutableListOf<WsContext>()

    fun onSocketJoin(wsConnectContext: WsConnectContext) {
        activeSockets.add(wsConnectContext)
    }

    fun onSocketLeave(wsContext: WsContext) {
        activeSockets.remove(wsContext)
    }

    fun notify(data: Any) {
        activeSockets.forEach { it.send(TypedJson(data)) }
        logger.info { "Notified ${activeSockets.size} sockets $data" }
    }



}