package org.readutf.matchmaker.wrapper

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.readutf.matchmaker.shared.result.MatchMakerResult
import org.readutf.matchmaker.shared.result.Result
import org.readutf.matchmaker.wrapper.api.QueueService
import org.readutf.matchmaker.wrapper.socket.SocketClient
import org.readutf.matchmaker.wrapper.utils.FastJsonConvertorFactory
import retrofit2.Retrofit

class QueueManager private constructor(
    hostname: String,
    port: Int,
    private val queueListener: QueueListener,
) {
    private val logger = KotlinLogging.logger { }

    private val queues = mutableMapOf<String, Queue>()

    private val retrofit =
        Retrofit
            .Builder()
            .client(
                OkHttpClient
                    .Builder()
//                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build(),
            ).baseUrl("http://$hostname:$port/")
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

    fun createQueue(
        queueType: String,
        queueName: String,
    ): Deferred<Result<Queue>> =
        runBlocking {
            async {
                val apiResponse = queueService.create(queueType, queueName)

                if (!apiResponse.success || apiResponse.response == null) {
                    if (apiResponse.failureReason.equals("Queue test already exists", ignoreCase = true)) {
                        return@async getQueueOrFetch(queueName)?.let { Result.ok(it) } ?: Result.error("Failed to create queue")
                    }
                    return@async Result.error(apiResponse.failureReason ?: "Failed to create queue")
                }

                val queueSettings = apiResponse.response!!

                val queue = Queue(socketId, queueSettings, queueService)
                queues[queueSettings.queueName] = queue
                return@async Result.ok(queue)
            }
        }

    fun handleQueueResultAsync(matchMakerResult: MatchMakerResult) = runBlocking { launch { handleQueueResult(matchMakerResult) } }

    private suspend fun handleQueueResult(result: MatchMakerResult) {
        val queue = getQueueOrFetch(result.queueId)

        if (queue == null) {
            logger.warn { "Received information about a queue that doesnt exist" }
            return
        }

        when (result.isSuccess()) {
            true -> {
                queueListener.onQueueSuccess(queue, result.queueResult!!, result.gameResult!!)
            }

            false -> {
                queueListener.onMatchMakerError(queue, result.failureReason!!)
            }
        }
    }

    fun getQueue(queueName: String): Queue? = queues[queueName]

    private suspend fun getQueueOrFetch(queueName: String): Queue? {
        val queueResult = queueService.getQueue(queueName)

        if (queueResult.success && queueResult.response != null) {
            return Queue(socketId, queueResult.response!!, queueService)
        }

        return null
    }

    companion object {
        suspend fun create(
            hostName: String,
            port: Int,
            queueListener: QueueListener,
        ): QueueManager = QueueManager(hostName, port, queueListener).also { it.init() }
    }
}
