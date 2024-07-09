package org.readutf.matchmaker.api.queue.store.impl

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.TypeReference
import io.github.oshai.kotlinlogging.KotlinLogging
import org.readutf.matchmaker.api.queue.Queue
import org.readutf.matchmaker.api.queue.queues.UnratedQueue
import org.readutf.matchmaker.api.queue.store.QueueStore
import org.readutf.matchmaker.shared.settings.UnratedQueueSettings
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class UnratedQueueStore : QueueStore<UnratedQueue> {
    private val logger = KotlinLogging.logger { }

    private var currentVersion: String = "1.0.0"

    private val file = File(File(System.getProperty("user.dir")), "unrated-queues.json")

    override fun loadQueues(): List<UnratedQueue> {
        logger.info { "Loading Unrated queues..." }

        val start = System.currentTimeMillis()

        if (!file.exists()) {
            file.createNewFile()
            file.writeText(JSON.toJSONString(VersionedData(currentVersion, emptyList<UnratedQueueSettings>())))
            return emptyList()
        }

        val versionedData =
            JSON.parseObject<VersionedData<List<UnratedQueueSettings>>>(
                FileInputStream(file),
                object : TypeReference<VersionedData<List<UnratedQueueSettings>>>() {}.type,
            )
        if (versionedData.version != currentVersion) {
            logger.warn { "Version mismatch, expected $currentVersion but got ${versionedData.version}" }
        }
        val loadedQueues = versionedData.data.map { queueSettings -> UnratedQueue(queueSettings) }

        val timeTaken = System.currentTimeMillis() - start

        logger.info { "Loaded ${loadedQueues.size} Unrated Queue(s) in ${timeTaken}ms" }

        return loadedQueues
    }

    override fun saveQueues(queues: List<Queue>) {
        writeToFile(
            file,
            VersionedData(currentVersion, queues.filterIsInstance<UnratedQueue>().map { queue -> queue.queueSettings }),
        )
    }

    private fun <T> writeToFile(
        file: File,
        any: T,
    ): T {
        FileOutputStream(file).use {
            JSON.writeTo(it, any)
        }
        return any
    }

    data class VersionedData<T>(
        val version: String,
        val data: T,
    )
}
