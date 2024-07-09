package org.readutf.matchmaker.api

import org.readutf.matchmaker.api.config.MainConfig
import org.readutf.matchmaker.api.endpoint.EndpointManager
import org.readutf.matchmaker.api.queue.QueueManager
import org.readutf.matchmaker.api.queue.commands.QueueCommand
import org.readutf.matchmaker.api.queue.endpoints.QueueEndpoints
import org.readutf.matchmaker.api.queue.socket.QueueSocketManager
import revxrsal.commands.cli.ConsoleCommandHandler

class MatchmakerApi(
    mainConfig: MainConfig,
) {
    private val queueSocketManager = QueueSocketManager(this)
    val queueManager = QueueManager(queueSocketManager)
    private val endpointManager =
        EndpointManager(
            mainConfig.endpointConfig,
            queueSocketManager,
            QueueEndpoints(queueManager),
        )
    private val commandThread = Thread()
    private val commandManager: ConsoleCommandHandler = ConsoleCommandHandler.create()

    init {
        Runtime.getRuntime().addShutdownHook(Thread { this.stop() })

        commandManager.register(QueueCommand(queueManager))

        // Start command polling
        commandThread.run { commandManager.pollInput() }
    }

    private fun stop() {
        logger.info { "Matchmaker shutting down..." }

        endpointManager.stop()
        queueManager.stop()
        commandThread.interrupt()

        logger.info { "Matchmaker stopped." }
    }
}
