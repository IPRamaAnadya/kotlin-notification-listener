package com.kodekolektif._core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kodekolektif.notiflistener.data.datasource.local.dao.NotifDao
import com.kodekolektif.notiflistener.data.datasource.local.entities.NotifEntity

@Database(entities = [NotifEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotifDao
}

object DatabaseInstance {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "notifications"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}