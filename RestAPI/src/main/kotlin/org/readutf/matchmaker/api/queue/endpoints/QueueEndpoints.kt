package org.readutf.matchmaker.api.queue.endpoints

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.TypeReference
import io.javalin.community.routing.annotations.*
import io.javalin.http.Context
import org.readutf.matchmaker.api.logger
import org.readutf.matchmaker.api.queue.QueueManager
import org.readutf.matchmaker.shared.entry.QueueEntry
import org.readutf.matchmaker.shared.response.ApiResponse
import org.readutf.matchmaker.shared.settings.QueueSettings
import java.util.*

@Endpoints("/api/queue")
class QueueEndpoints(private var queueManager: QueueManager) {

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
    fun list(ctx: Context): ApiResponse<List<QueueSettings>> {
        return ApiResponse.success(queueManager.getQueues().map { it.getSettings() })
    }

    @Put("{id}/create/")
    fun create(ctx: Context): ApiResponse<QueueSettings> {
        val id = ctx.pathParam("id")

        val queueName = ctx.queryParam("name") ?: throw IllegalArgumentException("Missing query parameter 'name'")
        if (queueManager.getQueue(queueName) != null) return ApiResponse.failure("Queue already exists")
        val queueCreator = queueManager.getQueueCreator(id) ?: return ApiResponse.failure("Queue creator $id not found")
        val queue = queueCreator.createQueue(queueName, ctx)

        queueManager.registerQueue(queueName, queue)
        return ApiResponse.success(queue.getSettings())
    }

    @Post("/join")
    fun join(ctx: Context): ApiResponse<Boolean> {
        val queueName = ctx.queryParam("name") ?: throw IllegalArgumentException("Missing query parameter 'name'")
        val playerTeamsString = ctx.body()

        val queue = queueManager.getQueue(queueName) ?: return ApiResponse.failure("Queue $queueName not found")
        val queueEntries = JSON.parseObject(
            playerTeamsString,
            object : TypeReference<List<QueueEntry>>() {})

        if(queueEntries == null) return ApiResponse.failure("Invalid player teams")

        try {
            queueEntries.forEach { queue.addToQueue(it) }
            queueManager.handleTick(queue)

            return ApiResponse.success(true)
        } catch (e: Exception) {
            logger.error(e) { "An error occurred while adding players to queue $queueName" }
            return ApiResponse.failure(e.message ?: "An error occurred")
        }
    }

    @Get("/types")
    fun listCreators(ctx: Context): ApiResponse<List<String>> {
        return ApiResponse.success(queueManager.getQueueCreators())
    }

}