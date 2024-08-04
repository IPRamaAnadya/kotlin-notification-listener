package com.kodekolektif.notiflistener.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kodekolektif.notiflistener.data.datasource.local.entities.NotifEntity

@Dao
interface NotifDao {
    @Insert
    suspend fun insertNotification(notification: NotifEntity)

    @Query("SELECT * FROM notif ORDER BY created_at DESC")
    suspend fun getAllNotifications(): List<NotifEntity>
}