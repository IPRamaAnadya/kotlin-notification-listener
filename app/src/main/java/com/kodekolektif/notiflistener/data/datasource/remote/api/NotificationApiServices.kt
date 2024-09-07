package com.kodekolektif.notiflistener.data.datasource.remote.api

import com.kodekolektif.notiflistener.data.datasource.local.entities.NotifEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApiServices {

    @POST("transaction")
    suspend fun syncData(
        @Body data: List<NotifEntity>,
        @Header("device_name") deviceName: String? = null,
        @Header("device_serial_number") deviceSerialNumber: String? = null
    ): Response<List<NotifEntity>>
}
