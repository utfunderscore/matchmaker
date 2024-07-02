package org.readutf.matchmaker.shared.response

class ApiResponse<T>(val success: Boolean, var failureReason: String?, val response: T?) {

    companion object {

        fun <T> success(data: T): ApiResponse<T> {
            return ApiResponse(true, null, data)
        }

        fun <T> failure(reason: String): ApiResponse<T> {
            return ApiResponse(false, reason, null)
        }

    }

}