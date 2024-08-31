package com.kodekolektif.notiflistener.data.repository

import com.kodekolektif.notiflistener.data.datasource.local.entities.NotifEntity
import com.kodekolektif.notiflistener.data.datasource.remote.api.ApiService

interface NotifRepository {
    suspend fun syncData(notif: List<NotifEntity>): List<NotifEntity>
}

class NotifRepositoryImpl(private val apiService: ApiService) : NotifRepository {
    override suspend fun syncData(notifs: List<NotifEntity>): List<NotifEntity> {
        return apiService.syncData(notifs)
    }
}