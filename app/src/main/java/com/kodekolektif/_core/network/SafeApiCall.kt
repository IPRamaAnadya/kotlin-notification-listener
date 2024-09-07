package com.kodekolektif._core.network

import okio.IOException
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

// create for handle api call
open class SafeApiCall {
    suspend fun <T> execute(
        apiCall: suspend () -> Response<T>
    ): NetworkResult<T> {
        return try {
            val response = apiCall.invoke()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    NetworkResult.Success(body)
                } else {
                    NetworkResult.Error(response.code(), response.message())
                }
            } else {
                NetworkResult.Error(response.code(), response.message())
            }
        } catch (e: SocketTimeoutException) {
            NetworkResult.TimeoutError
        } catch (e: UnknownHostException) {
            NetworkResult.Error(0, e.message ?: "Unknown Host")
        } catch (e: IOException) {
            NetworkResult.TimeoutError
        } catch (e: Exception) {
            NetworkResult.Error(0, e.message ?: "Unknown Error")
        }
    }
}