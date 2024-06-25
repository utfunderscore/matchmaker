package org.readutf.matchmaker.api.config

import com.sksamuel.hoplite.ConfigAlias

data class MainConfig(@ConfigAlias("endpoint") var endpointConfig: EndpointConfig)
