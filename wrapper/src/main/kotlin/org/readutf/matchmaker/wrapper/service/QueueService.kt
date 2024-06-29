package org.readutf.matchmaker.wrapper.service

import org.readutf.matchmaker.shared.response.ApiResponse
import org.readutf.matchmaker.shared.settings.UnratedQueueSettings
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

/**
 * Service for creating queues
 *
 * Any added queues and their creator should be referenced here to assist with the creation of queues
 */
interface QueueService {

    /**
     * Create an unrated queue
     */
    @PUT("/queue/create/unrated")
    fun createUnratedQueue(
        @Query("name") queueName: String,
        teamSize: Int,
        numberOfTeams: Int,
    ): Call<ApiResponse<UnratedQueueSettings>>


    @PUT("/queue/create/{queueType}")
    fun createUnknownQueue(
        @Path("queueType") queueType: String,
        @Query("name") queueName: String,
        @QueryMap params: Map<String, String>,
    ): Call<Map<String, Any>>

}