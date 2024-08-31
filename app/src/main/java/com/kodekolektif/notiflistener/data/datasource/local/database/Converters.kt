package com.kodekolektif.notiflistener.data.datasource.local.database

import androidx.room.TypeConverter
import com.kodekolektif.notiflistener.data.datasource.local.entities.NotificationStatus
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromNotificationStatus(status: NotificationStatus): Int {
        return status.value
    }

    @TypeConverter
    fun toNotificationStatus(value: Int): NotificationStatus {
        return NotificationStatus.fromInt(value)
    }

    // Converts UUID to String for database storage
    @TypeConverter
    fun fromUUID(uuid: UUID): String {
        return uuid.toString()
    }

    // Converts String back to UUID for use in the application
    @TypeConverter
    fun toUUID(uuid: String): UUID {
        return UUID.fromString(uuid)
    }
}