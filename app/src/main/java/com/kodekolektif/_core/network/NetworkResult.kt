package com.kodekolektif._core.network

sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val errorCode: Int, val errorMessage: String) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
    data object Empty : NetworkResult<Nothing>()
    data object NetworkError : NetworkResult<Nothing>()
    data object TimeoutError : NetworkResult<Nothing>()
}
