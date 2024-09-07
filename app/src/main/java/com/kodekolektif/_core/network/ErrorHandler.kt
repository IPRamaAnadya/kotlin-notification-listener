package com.kodekolektif._core.network

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

class ErrorHandler {
    fun handleException(exception: Throwable): NetworkResult<Nothing> {
        return when (exception) {
            is HttpException -> {
                when (exception.code()) {
                    401 -> {
                        NetworkResult.Error(401, "Unauthorized")
                    }
                    403 -> {
                        NetworkResult.Error(403, "Forbidden")
                    }
                    404 -> {
                        NetworkResult.Error(404, "Not Found")
                    }
                    500 -> {
                        NetworkResult.Error(500, "Internal Server Error")
                    }
                    else -> {
                        NetworkResult.Error(exception.code(), exception.message())
                    }
                }
            }
            is SocketTimeoutException -> NetworkResult.TimeoutError
            is IOException -> {
                NetworkResult.NetworkError
            }
            else -> {
                NetworkResult.Error(0, exception.message ?: "Unknown error")
            }
        }
    }
}