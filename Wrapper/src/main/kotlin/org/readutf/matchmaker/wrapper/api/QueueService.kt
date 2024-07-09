package org.readutf.matchmaker.wrapper.api

import org.readutf.matchmaker.shared.entry.QueueEntry
import org.readutf.matchmaker.shared.response.ApiResponse
import org.readutf.matchmaker.shared.settings.QueueSettings
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface QueueService {
    @GET("/api/queue/list")
    suspend fun list(): ApiResponse<List<QueueSettings>>

    @GET("/api/queue/{id}/create/")
    suspend fun create(
        @Path("id") queueType: String,
        @Path("name") queueName: String,
    ): ApiResponse<QueueSettings>

    @POST("/api/queue/join")
    suspend fun join(
        @Query("name") queueName: String,
        @Body team: QueueEntry,
    ): ApiResponse<Boolean>

    @GET("/api/queue")
    suspend fun getQueue(
        @Query("queueName") queueName: String,
    ): ApiResponse<QueueSettings>

    @GET("/api/types")
    suspend fun types(): ApiResponse<List<String>>
}
