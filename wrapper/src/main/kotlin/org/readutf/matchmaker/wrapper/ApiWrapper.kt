package org.readutf.matchmaker.wrapper

import org.readutf.matchmaker.wrapper.service.QueueService
import org.readutf.matchmaker.wrapper.utils.FastJson2ConvertorFactory
import retrofit2.Retrofit

class ApiWrapper(baseUrl: String) {

    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(FastJson2ConvertorFactory())
        .build()

    fun createQueueService(): QueueService {
        return retrofit.create(QueueService::class.java)
    }
}