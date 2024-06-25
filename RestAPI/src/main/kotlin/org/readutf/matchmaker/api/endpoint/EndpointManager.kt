package org.readutf.matchmaker.api.endpoint

import io.javalin.Javalin
import org.readutf.matchmaker.api.config.EndpointConfig

class EndpointManager(endpointConfig: EndpointConfig) {

    var javalin: Javalin = Javalin.create { config ->
        config.bundledPlugins.enableCors { cors ->
            cors.addRule { it.anyHost() }
        }

    }.start(endpointConfig.port)


}