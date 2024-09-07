package com.kodekolektif.auth.data.datasource.remote.api

import com.kodekolektif.auth.data.datasource.remote.model.LoginRequestParams
import com.kodekolektif.auth.data.datasource.remote.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiServices {
    @POST("login")
    suspend fun login(@Body data: LoginRequestParams): Response<LoginResponse>
}