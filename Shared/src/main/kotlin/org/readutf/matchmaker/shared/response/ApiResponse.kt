package org.readutf.matchmaker.shared.response

class ApiResponse<T>(var success: Boolean, var failureReason: String?, var response: T?) {

    companion object {

        fun <T> success(data: T): ApiResponse<T> {
            return ApiResponse(true, null, data)
        }

        fun <T> failure(reason: String): ApiResponse<T> {
            return ApiResponse(false, reason, null)
        }

    }

}