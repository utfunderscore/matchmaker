package org.readutf.matchmaker.wrapper.socket

import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter
import com.neovisionaries.ws.client.WebSocketFactory
import io.github.oshai.kotlinlogging.KotlinLogging
import org.readutf.matchmaker.shared.TypedJson
import org.readutf.matchmaker.shared.result.QueueResult
import org.readutf.matchmaker.wrapper.QueueManager

class SocketClient(hostName: String, port: Int, val queueManager: QueueManager) : WebSocketAdapter() {

    private val logger = KotlinLogging.logger {  }

    init {

        val socketFactory = WebSocketFactory()
        socketFactory.setSocketTimeout(5000)
        val webSocket = socketFactory.createSocket("ws://$hostName:$port/api/queue/notify")
        webSocket.addListener(this)

        webSocket.connect()

    }

    override fun onTextMessage(websocket: WebSocket?, text: String?) {

        if(text == null) {
            logger.warn { "Received invalid websocket text" }
            return
        }

        val typedData: TypedJson

        try {
            typedData = TypedJson.fromString(text)
        } catch (e: Exception) {
            logger.error(e) { "Failed to parse matchmaker notification" }
            return
        }

        when (typedData.data) {

            is QueueResult -> {
                queueManager.handleQueueResultAsync(typedData.data as QueueResult)
            }

            else -> {
                logger.warn { "Unsupported event received." }
            }
        }
    }

}