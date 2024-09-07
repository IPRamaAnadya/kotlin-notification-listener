package com.kodekolektif.notiflistener.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kodekolektif.notiflistener.data.datasource.local.entities.NotifEntity
import com.kodekolektif.notiflistener.data.datasource.local.entities.NotificationStatus
import java.util.UUID

@Dao
interface NotifDao {
    @Insert
    suspend fun insertNotification(notification: NotifEntity)

    @Query("SELECT * FROM notif ORDER BY created_at DESC")
    suspend fun getAllNotifications(): List<NotifEntity>

    @Query("SELECT * FROM notif WHERE name IS NOT NULL AND nominal IS NOT NULL AND nominal != 0 ORDER BY created_at DESC")
    suspend fun getAllNotificationsWithNonNullNameAndNominal(): List<NotifEntity>

    @Query("SELECT * FROM notif WHERE name IS NOT NULL AND nominal IS NOT NULL AND nominal != 0 AND validated_at IS NULL ORDER BY created_at ASC")
    suspend fun getNotificationsWithNullValidateAt(): List<NotifEntity>

    @Query("SELECT * FROM notif WHERE status = :status AND name IS NOT NULL AND nominal IS NOT NULL AND nominal != 0 ORDER BY created_at DESC LIMIT 5")
    suspend fun getNotificationsByStatus(status: NotificationStatus): List<NotifEntity>

    @Query("DELETE FROM notif")
    suspend fun deleteAllNotifications()

    @Query("DELETE FROM notif WHERE uuid IN (:uuids)")
    suspend fun deleteNotificationsByUuids(uuids: List<UUID>)

    @Query("UPDATE notif SET status = :status, validated_at = CURRENT_TIMESTAMP WHERE uuid = :uuid")
    suspend fun updateNotificationStatusByUuid(uuid: String, status: NotificationStatus)

    @Query("SELECT uuid FROM notif WHERE status NOT IN (1, 4) ORDER BY created_at DESC LIMIT 50")
    suspend fun getUuidsForNotificationsToDelete(): List<UUID>
}
