package org.readutf.matchmaker.api

import org.readutf.matchmaker.api.config.MainConfig
import org.readutf.matchmaker.api.endpoint.EndpointManager
import org.readutf.matchmaker.api.queue.QueueManager
import org.readutf.matchmaker.api.queue.endpoints.QueueEndpoints
import org.readutf.matchmaker.api.queue.socket.QueueSocketManager

class MatchmakerApi(mainConfig: MainConfig) {

    private val queueSocketManager = QueueSocketManager()
    private val queueManager = QueueManager(queueSocketManager)
    private val endpointManager = EndpointManager(
        mainConfig.endpointConfig,
        queueSocketManager,
        QueueEndpoints(queueManager)
    )

    init {
        Runtime.getRuntime().addShutdownHook(Thread { this.stop() })
    }

    private fun stop() {
        logger.info { "Matchmaker shutting down..." }

        endpointManager.stop()
        queueManager.stop()

        logger.info { "Matchmaker stopped." }
    }

}