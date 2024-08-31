package com.kodekolektif.notiflistener.data.datasource.remote.api

import com.kodekolektif.notiflistener.data.datasource.local.entities.NotifEntity
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("transaction")
    suspend fun syncData(@Body data: List<NotifEntity>): List<NotifEntity>
}