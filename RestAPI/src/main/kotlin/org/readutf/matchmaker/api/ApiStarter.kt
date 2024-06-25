package org.readutf.matchmaker.api

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource
import io.github.oshai.kotlinlogging.KotlinLogging
import org.readutf.matchmaker.api.config.MainConfig

var logger = KotlinLogging.logger {}

fun main() {

    println(MatchmakerApi::class.java.getResource("/config.yml").readText())

    val configResult = ConfigLoaderBuilder
        .default()
        .addResourceSource("/config.yml")
        .build()
        .loadConfigOrThrow<MainConfig>()


    println(configResult)

}