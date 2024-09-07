package com.kodekolektif.auth.data.repository

import com.kodekolektif._core.network.ErrorHandler
import com.kodekolektif._core.network.NetworkResult
import com.kodekolektif.auth.data.datasource.remote.api.AuthApiServices
import com.kodekolektif.auth.data.datasource.remote.model.LoginRequestParams
import com.kodekolektif.auth.data.datasource.remote.model.LoginResponse
import retrofit2.HttpException


interface AuthRepository {
    suspend fun login(params: LoginRequestParams): NetworkResult<LoginResponse?>
}


class AuthRepositoryImpl(
    private val apiService: AuthApiServices,
    private val errorHandler: ErrorHandler) : AuthRepository {

    override suspend fun login(params: LoginRequestParams): NetworkResult<LoginResponse?> {
        return try {
            val response = apiService.login(params)
            if (response.isSuccessful) {
                NetworkResult.Success(response.body())
            } else {
                errorHandler.handleException(HttpException(response))
            }
        } catch (e: Exception) {
            errorHandler.handleException(e)
        }
    }
}