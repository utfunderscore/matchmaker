package org.readutf.matchmaker.wrapper

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.readutf.matchmaker.shared.result.QueueResult
import org.readutf.matchmaker.shared.result.impl.MatchMakerError
import org.readutf.matchmaker.shared.result.impl.QueueSuccess
import org.readutf.matchmaker.wrapper.api.QueueService
import org.readutf.matchmaker.wrapper.socket.SocketClient
import org.readutf.matchmaker.wrapper.utils.FastJsonConvertorFactory
import retrofit2.Retrofit

class QueueManager private constructor(hostname: String, port: Int, private val queueListener: QueueListener) {

    private val logger = KotlinLogging.logger { }

    private val queues = mutableMapOf<String, Queue>()

    private val retrofit = Retrofit.Builder()
        .client(
            OkHttpClient.Builder()
//                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
        )
        .baseUrl("http://$hostname:$port/")
        .addConverterFactory(FastJsonConvertorFactory())
        .build()

    private val socketClient = SocketClient(hostname, port, this)
    private val queueService = retrofit.create(QueueService::class.java)
    lateinit var socketId: String

    private suspend fun init() {

        socketId = socketClient.sessionIdFuture.join()

        runBlocking {
            val execute = queueService.list()

            execute.response?.forEach {
                queues[it.queueName] = Queue(socketId, it, queueService)
            }
        }

    }

    fun createQueue(queueType: String, queueName: String): Deferred<Queue> = runBlocking {
        return@runBlocking async {
            val apiResponse = queueService.create(queueType, queueName)

            if (!apiResponse.success || apiResponse.response == null)
                throw Exception(apiResponse.failureReason ?: "Failed to create queue")

            val queueSettings = apiResponse.response!!

            val queue = Queue(socketId, queueSettings, queueService)
            queues[queueSettings.queueName] = queue
            return@async queue;
        }

    }

    fun handleQueueResultAsync(queueResult: QueueResult) = runBlocking { launch { handleQueueResult(queueResult) } }


    private suspend fun handleQueueResult(queueResult: QueueResult) {
        val queue = getQueueOrFetch(queueResult.queueName)

        if (queue == null) {
            logger.warn { "Received information about a queue that doesnt exist" }
            return
        }

        when (queueResult) {

            is QueueSuccess -> {
                queueListener.onQueueSuccess(queue, queueResult.queueEntries)
            }

            is MatchMakerError -> {
                queueListener.onMatchMakerError(queue, queueResult.failureReason)
            }
        }
    }

    fun getQueue(queueName: String): Queue? {
        return queues[queueName]
    }

    private suspend fun getQueueOrFetch(queueName: String): Queue? {
        val queueResult = queueService.getQueue(queueName)

        if (queueResult.success && queueResult.response != null) {
            return Queue(socketId, queueResult.response!!, queueService)
        }

        return null

    }

    companion object {

        suspend fun create(hostName: String, port: Int, queueListener: QueueListener): QueueManager {
            return QueueManager(hostName, port, queueListener).also { it.init() }
        }

    }

}