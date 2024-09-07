package com.kodekolektif.notiflistener.data.repository

import com.kodekolektif.notiflistener.data.datasource.local.entities.NotifEntity
import com.kodekolektif.notiflistener.data.datasource.remote.api.NotificationApiServices

interface NotifRepository {
    suspend fun syncData(notif: List<NotifEntity>): List<NotifEntity>
}

class NotifRepositoryImpl(private val apiService: NotificationApiServices) : NotifRepository {
    override suspend fun syncData(notifs: List<NotifEntity>): List<NotifEntity> {
        val response = apiService.syncData(notifs)

        return if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            emptyList()
        }
    }
}