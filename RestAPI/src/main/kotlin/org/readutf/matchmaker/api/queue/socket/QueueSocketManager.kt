package org.readutf.matchmaker.api.queue.socket

import io.github.oshai.kotlinlogging.KotlinLogging
import io.javalin.websocket.WsConnectContext
import io.javalin.websocket.WsContext
import org.readutf.matchmaker.shared.TypedJson

class QueueSocketManager {

    private val logger = KotlinLogging.logger {}
    private val activeSockets = mutableMapOf<String, WsContext>()

    fun onSocketJoin(wsConnectContext: WsConnectContext) {
        val sessionId = wsConnectContext.sessionId()
        activeSockets[sessionId] = wsConnectContext

        wsConnectContext.send(wsConnectContext.sessionId())
    }

    fun onSocketLeave(wsContext: WsContext) {
        activeSockets.remove(wsContext.sessionId())
    }

    fun notify(sessionId: String, typedJson: TypedJson) {

        logger.info { "Notifying session $sessionId with $typedJson" }
        activeSockets[sessionId]?.run {
            send(typedJson)
        }

    }



}