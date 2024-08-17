package org.readutf.matchmaker.api.endpoint

import io.javalin.Javalin
import io.javalin.community.routing.annotations.AnnotatedRouting.Annotated
import org.readutf.matchmaker.api.config.EndpointConfig
import org.readutf.matchmaker.api.logger
import org.readutf.matchmaker.api.queue.socket.QueueSocketManager
import org.readutf.matchmaker.api.utils.FastJsonMapper
import org.readutf.matchmaker.shared.response.ApiResponse
import java.util.concurrent.CompletableFuture

class EndpointManager(
    private var endpointConfig: EndpointConfig,
    private var queueSocketManager: QueueSocketManager,
    vararg endpoints: Any,
) {
    private var javalin: Javalin =
        Javalin
            .createAndStart { config ->
                // Register config for javalin

                config.jetty.defaultHost = endpointConfig.hostaddress
                config.jetty.defaultPort = endpointConfig.port

                config.jsonMapper(FastJsonMapper)
                config.bundledPlugins.enableCors { cors -> cors.addRule { it.anyHost() } }
                config.bundledPlugins.enableDevLogging()
                config.router.mount(Annotated) { routing ->

                    routing.registerResultHandler(ApiResponse::class.java) { ctx, result -> ctx.json(result) }
                    routing.registerResultHandler(CompletableFuture::class.java) { ctx, result -> ctx.future { result } }

                    endpoints.forEach { routing.registerEndpoints(it) }
                }

                config.pvt.internalRouter.allHttpHandlers().forEach { parsedEndpoint ->
                    val endpoint = parsedEndpoint.endpoint
                    println("Registered ${endpoint.method.name} endpoint '${endpoint.path}")
                }
            }.exception(Exception::class.java) { e, ctx ->
                ctx.json(ApiResponse.failure<Boolean>(e.message ?: "An error occurred"))
                logger.error(e) { "An exception occurred on path ${ctx.contextPath()}" }
            }.ws("/api/queue/notify") { ws ->
                ws.onConnect(queueSocketManager::onSocketJoin)
                ws.onClose(queueSocketManager::onSocketLeave)
            }

    fun stop() {
        javalin.stop()
    }
}
