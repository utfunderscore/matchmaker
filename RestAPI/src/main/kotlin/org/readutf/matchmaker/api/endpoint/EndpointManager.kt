package org.readutf.matchmaker.api.endpoint

import io.javalin.Javalin
import org.readutf.matchmaker.api.config.EndpointConfig

class EndpointManager(private var endpointConfig: EndpointConfig) {

    var javalin: Javalin = Javalin.create { config ->
        config.bundledPlugins.enableCors { cors ->
            cors.addRule { it.anyHost() }
        }

    }

    fun start() {
        javalin.start(endpointConfig.hostaddress, endpointConfig.port)
    }

}