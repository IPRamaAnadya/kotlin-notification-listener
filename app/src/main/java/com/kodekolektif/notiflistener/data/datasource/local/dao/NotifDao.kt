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

    @Query("SELECT * FROM notif WHERE name IS NOT NULL AND nominal IS NOT NULL AND nominal != 0 ORDER BY created_at DESC")
    suspend fun getAllNotificationsWithNonNullNameAndNominal(): List<NotifEntity>

    @Query("SELECT * FROM notif WHERE name IS NOT NULL AND nominal IS NOT NULL AND nominal != 0 AND validated_at NOT NULL ORDER BY created_at ASC")
    suspend fun getNotificationsWithNullValidateAt(): List<NotifEntity>
}