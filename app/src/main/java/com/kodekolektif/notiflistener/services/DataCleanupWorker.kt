package com.kodekolektif.notiflistener.services

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.kodekolektif.notiflistener.data.datasource.local.dao.NotifDao
import com.kodekolektif._core.database.DatabaseInstance
import com.kodekolektif.notiflistener.services.DataSyncService.Companion.className
import kotlinx.coroutines.delay

class DataCleanupService : Service() {


    private lateinit var notifDao: NotifDao

    private val TAG = "DataCleanupService"
    private val delayMillis = 10000L

    override fun onCreate() {
        super.onCreate()
        Log.e(TAG, "DataCleanupService created")

        notifDao = DatabaseInstance.getDatabase(this).notificationDao()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Perform cleanup operation
        CoroutineScope(Dispatchers.IO).launch {
            performCleanup()
        }
        return START_STICKY
    }

    private suspend fun performCleanup() {
        while (true) {
            val uuidsToDelete = notifDao.getUuidsForNotificationsToDelete()
            if (uuidsToDelete.isNotEmpty()) {
                notifDao.deleteNotificationsByUuids(uuidsToDelete)
                Log.e(TAG, "Menghapus ${uuidsToDelete.size} data notifikasi")
            } else {
                Log.e(TAG, "Tidak ada data yang dihapus")
            }

            delay(delayMillis)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
