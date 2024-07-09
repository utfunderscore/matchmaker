package org.readutf.matchmaker.api.config

import com.sksamuel.hoplite.ConfigAlias

data class EndpointConfig(
    @ConfigAlias("host") var hostaddress: String,
    var port: Int,
)

data class MainConfig(
    @ConfigAlias("endpoint") var endpointConfig: EndpointConfig,
)
