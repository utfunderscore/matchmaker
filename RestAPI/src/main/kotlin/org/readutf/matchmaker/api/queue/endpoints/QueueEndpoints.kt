package org.readutf.matchmaker.api.queue.endpoints

import io.javalin.community.routing.annotations.Endpoints
import io.javalin.community.routing.annotations.Get
import io.javalin.community.routing.annotations.Param
import io.javalin.community.routing.annotations.Put
import io.javalin.http.Context
import org.readutf.matchmaker.api.queue.Queue
import org.readutf.matchmaker.api.queue.QueueManager
import org.readutf.matchmaker.shared.entry.QueueEntry
import org.readutf.matchmaker.shared.response.ApiResponse

@Endpoints("/api/queue")
class QueueEndpoints(private var queueManager: QueueManager) {

    @Get("/list")
    fun list(ctx: Context): ApiResponse<List<Queue>> {
        return ApiResponse.success(queueManager.getQueues())
    }

    @Put("/create/{id}")
    fun create(ctx: Context): ApiResponse<*> {
        val id = ctx.pathParam("id")

        val queueName = ctx.queryParam("name") ?: throw IllegalArgumentException("Missing query parameter 'name'")
        if (queueManager.getQueue(queueName) != null) return ApiResponse.failure("Queue already exists")
        val queueCreator = queueManager.getQueueCreator(id) ?: return ApiResponse.failure("Queue creator $id not found")
        val queue = queueCreator.createQueue(queueName, ctx)

        queueManager.registerQueue(queueName, queue)
        return ApiResponse.success(queue.getSettings())
    }

    @Put("/join/{name}")
    fun join(ctx: Context, @Param queueEntry: QueueEntry): ApiResponse<*> {
        val queueName = ctx.pathParam("name")
        val queue = queueManager.getQueue(queueName) ?: return ApiResponse.failure("Queue $queueName not found")

        try {
            queue.addToQueue(queueEntry)
            return ApiResponse.success(true)
        } catch (e: Exception) {
            return ApiResponse.failure(e.message ?: "An error occurred")
        }
    }

}