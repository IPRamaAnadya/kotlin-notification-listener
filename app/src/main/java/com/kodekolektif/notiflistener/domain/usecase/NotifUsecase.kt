package com.kodekolektif.notiflistener.domain.usecase

import com.kodekolektif.notiflistener.data.datasource.local.entities.NotifEntity
import com.kodekolektif.notiflistener.data.repository.NotifRepository

class NotifUsecase(private val repo: NotifRepository) {
    suspend fun execute(notif: List<NotifEntity>): List<NotifEntity> {
        return repo.syncData(notif)
    }
}
