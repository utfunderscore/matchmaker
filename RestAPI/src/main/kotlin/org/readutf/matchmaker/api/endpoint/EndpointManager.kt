package org.readutf.matchmaker.api.endpoint

import io.javalin.Javalin
import org.readutf.matchmaker.api.config.EndpointConfig
import org.readutf.matchmaker.api.utils.ApiResponse
import org.readutf.matchmaker.api.utils.FastJsonMapper

class EndpointManager(private var endpointConfig: EndpointConfig) {

    var javalin: Javalin = Javalin.create { config ->
        config.jsonMapper(FastJsonMapper)
        config.bundledPlugins.enableCors { cors ->
            cors.addRule { it.anyHost() }
        }
    }.exception(Exception::class.java) { e, ctx ->
        ctx.json(ApiResponse.failure(e.message ?: "An error occurred"))
    }

    fun start() {
        javalin.start(endpointConfig.hostaddress, endpointConfig.port)
    }

}