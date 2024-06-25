package org.readutf.matchmaker.api

import com.alibaba.fastjson2.JSON
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import com.sksamuel.hoplite.addResourceSource
import io.github.oshai.kotlinlogging.KotlinLogging
import org.readutf.matchmaker.api.config.MainConfig
import org.readutf.matchmaker.api.queue.queues.UnratedQueue
import java.io.File
import java.nio.file.Files

var logger = KotlinLogging.logger {}

/**
 * Application entry point, loads the config and starts the API.
 */
fun main() {

    val baseDir = File(System.getProperty("user.dir"))

    val externalConfig = copyDefaultConfig(baseDir)
    val configResult = loadMainConfig(externalConfig)

    MatchmakerApi(configResult)
}

/**
 * Loads the config from the config.yml file,
 * or the default config.yml resource if the file doesn't exist.
 *
 * @param externalConfig The external config file.
 * @return The loaded config.
 */
private fun loadMainConfig(externalConfig: File): MainConfig {
    val configResult = ConfigLoaderBuilder
        .default()
        .addFileSource(externalConfig)
        .addResourceSource("/config.yml")
        .build()
        .loadConfigOrThrow<MainConfig>()
    return configResult
}

/**
 * Copies the default config from resources to the base directory.
 */
private fun copyDefaultConfig(baseDir: File): File {
    val externalConfig = File(baseDir, "config.yml")
    if (!externalConfig.exists()) {
        logger.info { "Config.yml not found, copying defaults." }

        val defaultConfigResource = MatchmakerApi::class.java.getResourceAsStream("/config.yml")
            ?: throw IllegalStateException("Default config not found.")

        Files.copy(defaultConfigResource, externalConfig.toPath())
    }
    return externalConfig
}