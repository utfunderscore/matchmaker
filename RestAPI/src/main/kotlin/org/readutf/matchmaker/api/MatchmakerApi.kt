package org.readutf.matchmaker.api

import com.alibaba.fastjson2.JSON
import org.readutf.matchmaker.api.config.MainConfig
import org.readutf.matchmaker.api.endpoint.EndpointManager
import org.readutf.matchmaker.api.queue.QueueManager
import org.readutf.matchmaker.api.queue.queues.UnratedQueue

class MatchmakerApi(mainConfig: MainConfig) {

    private val endpointManager = EndpointManager(mainConfig.endpointConfig)
    private val queueManager = QueueManager(endpointManager.javalin)

    init {

        //Register creator for unrated queue
        queueManager.registerQueueCreator("unrated", UnratedQueue.UnratedQueueCreator())

        endpointManager.start()
    }

}