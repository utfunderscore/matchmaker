package org.readutf.matchmaker.wrapper

import org.readutf.matchmaker.shared.result.QueueResult
import org.readutf.matchmaker.shared.result.QueueResultType
import org.readutf.matchmaker.wrapper.api.QueueService
import org.readutf.matchmaker.wrapper.socket.SocketClient
import org.readutf.matchmaker.wrapper.utils.FastJsonConvertorFactory
import retrofit2.Retrofit

class QueueManager(hostname: String, port: Int, val queueListener: QueueListener) {

    private val queues = mutableMapOf<String, Queue>()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://$hostname:$port/")
        .addConverterFactory(FastJsonConvertorFactory())
        .build()

    private val socketClient = SocketClient(hostname, port, this)

    private val queueService = retrofit.create(QueueService::class.java)

    init {

        val execute = queueService.list().execute()

        execute.body()?.response?.forEach {
            queues[it.queueName] = Queue(it, queueService)
        }

    }

    fun createQueue(queueType: String, queueName: String): Queue {
        val create = queueService.create(queueType, queueName)

        val execute = create.execute()
        if (!execute.isSuccessful) throw Exception("Failed to create queue")
        val apiResponse = execute.body()

        if (apiResponse?.response == null || !apiResponse.success)
            throw Exception(apiResponse?.failureReason ?: "Failed to create queue")

        val queueSettings = apiResponse.response!!

        val queue = Queue(queueSettings, queueService)
        queues[queueSettings.queueName] = queue
        return queue;
    }

    fun handleQueueResult(queueResult: QueueResult) {

        if(queueResult.resultType == QueueResultType.SUCCESS) {
            queueListener.onQueueResult(qu)
        }


    }

    fun getQueue(queueName: String): Queue? {
        return queues[queueName]
    }

    fun fetchQueue()

}