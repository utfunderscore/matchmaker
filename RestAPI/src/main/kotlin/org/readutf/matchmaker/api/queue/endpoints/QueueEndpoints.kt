package org.readutf.matchmaker.api.queue.endpoints

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.TypeReference
import io.github.oshai.kotlinlogging.KotlinLogging
import io.javalin.community.routing.annotations.Endpoints
import io.javalin.community.routing.annotations.Get
import io.javalin.community.routing.annotations.Post
import io.javalin.community.routing.annotations.Put
import io.javalin.http.Context
import org.readutf.matchmaker.api.queue.QueueManager
import org.readutf.matchmaker.shared.entry.QueueEntry
import org.readutf.matchmaker.shared.response.ApiResponse
import org.readutf.matchmaker.shared.settings.QueueSettings

@Endpoints("/api/queue")
class QueueEndpoints(private var queueManager: QueueManager) {

    val logger = KotlinLogging.logger { }

    @Get
    fun getQueue(ctx: Context): ApiResponse<QueueSettings> {

        val queueName = ctx.queryParam("queueName") ?: return ApiResponse.failure("Missing 'queueName' parameter")

        val queue = queueManager.getQueue(queueName)

        return if (queue == null)
            ApiResponse.failure("Queue not found")
        else
            ApiResponse.success(queue.getSettings())

    }

    @Get("/list")
    fun list(): ApiResponse<List<QueueSettings>> = ApiResponse.success(queueManager.getQueues().toList())

    @Put("{id}/create/")
    fun create(ctx: Context): ApiResponse<QueueSettings> {
        val id = ctx.pathParam("id")

        val queueName = ctx.queryParam("name") ?: return ApiResponse.failure("Missing query parameter 'name'")
        if (queueManager.getQueue(queueName) != null) return ApiResponse.failure("Queue $queueName already exists")
        val queueCreator = queueManager.getQueueHandler(id) ?: return ApiResponse.failure("Queue creator $id not found")
        val queue = queueCreator.createQueue(queueName, ctx)

        if (queue.isErr) return ApiResponse.failure(queue.error)

        queueManager.registerQueue(queueName, queue.get())

        return ApiResponse.success(queue.get().getSettings())
    }

    @Post("/join")
    fun join(ctx: Context) {
        val queueName = ctx.queryParam("name") ?: run {
            ctx.json(ApiResponse.failure<Boolean>("Missing query parameter 'name'"))
            return
        }
        val playerTeamsString = ctx.body()

        val queue = queueManager.getQueue(queueName)

        if (queue == null) {
            ctx.json(ApiResponse.failure<Boolean>("Queue not found"))
            return
        }


        val queueEntry = JSON.parseObject(
            playerTeamsString,
            object : TypeReference<QueueEntry>() {})

        if (queueEntry == null) {
            ctx.json(ApiResponse.failure<Boolean>("Invalid player teams"))
            return
        }

        val addToQueueResult = queueManager.joinQueue(queue, queueEntry)
        queueManager.tickQueue(queue)

        ctx.future {
            addToQueueResult.thenAccept { result ->
                result.peek { _ ->
                    ctx.json(ApiResponse.success(true))
                }.onError { error ->
                    ctx.json(ApiResponse.failure<Boolean>(error))
                }
            }
        }
    }

    @Get("/types")
    fun listCreators(): ApiResponse<List<String>> {
        return ApiResponse.success(queueManager.getQueueCreators())
    }

}