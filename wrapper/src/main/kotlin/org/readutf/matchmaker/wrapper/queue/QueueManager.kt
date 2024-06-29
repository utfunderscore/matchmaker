package org.readutf.matchmaker.wrapper.queue

import org.readutf.matchmaker.shared.settings.UnratedQueueSettings
import org.readutf.matchmaker.wrapper.service.QueueService
import retrofit2.Retrofit

class QueueManager(retrofit: Retrofit) {

    val queueService = retrofit.create(QueueService::class.java)

    fun createUnratedQueue(queueName: String, teamSize: Int, numberOfTeams: Int): UnratedQueueSettings {
        val createUnratedQueue = queueService.createUnratedQueue(queueName, teamSize, numberOfTeams)
        val execute = createUnratedQueue.execute()
        return execute.body()?.response ?: throw IllegalStateException("Failed to create queue")
    }

    fun crateUnknownQueue(queueType: String, queueName: String, params: Map<String, String>) {
        queueService.createUnknownQueue(queueType, queueName, params)
    }

}