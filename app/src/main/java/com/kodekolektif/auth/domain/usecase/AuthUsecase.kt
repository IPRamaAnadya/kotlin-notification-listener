package com.kodekolektif.auth.domain.usecase

import com.kodekolektif._core.network.NetworkResult
import com.kodekolektif.auth.data.datasource.remote.model.LoginRequestParams
import com.kodekolektif.auth.data.datasource.remote.model.LoginResponse
import com.kodekolektif.auth.data.repository.AuthRepository

class AuthUsecase(private val repository: AuthRepository)  {
    suspend fun execute(params: LoginRequestParams): NetworkResult<LoginResponse?> {
        return repository.login(params)
    }

}