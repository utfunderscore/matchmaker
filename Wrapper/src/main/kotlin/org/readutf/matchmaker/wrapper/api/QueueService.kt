package org.readutf.matchmaker.wrapper.api

import org.readutf.matchmaker.shared.response.ApiResponse
import org.readutf.matchmaker.shared.settings.QueueSettings
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface QueueService {

    @GET("/api/queue/list")
    fun list(): Call<ApiResponse<List<QueueSettings>>>

    @GET("/api/queue/{id}/create/")
    fun create(@Path("id") queueType: String, @Path("name") queueName: String): Call<ApiResponse<QueueSettings>>

    @PUT("/api/queue/join")
    fun join(@Query("name") queueName: String, @Query("players") players: List<List<UUID>>): Call<ApiResponse<Boolean>>

    @GET("/api/types")
    fun types(): Call<ApiResponse<List<String>>>


}