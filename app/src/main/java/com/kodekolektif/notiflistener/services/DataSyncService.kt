package com.kodekolektif.notiflistener.services

import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.util.Log
import com.kodekolektif.notiflistener.data.datasource.local.dao.NotifDao
import com.kodekolektif._core.database.DatabaseInstance
import com.kodekolektif._core.manager.DeviceInfoManager
import com.kodekolektif._core.network.ApiClient
import com.kodekolektif._core.utils.Constant
import com.kodekolektif.notiflistener.data.datasource.local.entities.NotifEntity
import com.kodekolektif.notiflistener.data.datasource.local.entities.NotificationStatus
import com.kodekolektif.notiflistener.data.datasource.remote.api.NotificationApiServices
import com.kodekolektif.notiflistener.utils.AppNotificationManager
import kotlinx.coroutines.*

class DataSyncService : Service() {

    private val delayMillis = 15000L

    private lateinit var notifDao: NotifDao
    private lateinit var apiService: NotificationApiServices
    private lateinit var notificationManager: AppNotificationManager
    private lateinit var deviceInfoManager: DeviceInfoManager
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        Log.e(className, "Service berjalan")

        // Initialize the DAO
        notifDao = DatabaseInstance.getDatabase(this).notificationDao()

        // Create the API service
        apiService = ApiClient.init(this).create(NotificationApiServices::class.java)

        // Create the notification manager
        notificationManager = AppNotificationManager(this)
        notificationManager.createNotificationChannel()

        // Create the shared preferences
        sharedPreferences = getSharedPreferences(Constant.sharedPref, MODE_PRIVATE)

        // Create the device info manager
        deviceInfoManager = DeviceInfoManager(sharedPreferences)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        notificationManager.sendNotification(1, "Data Sync Service", "Synchronizing data with server...")

        // Start the data synchronization process
        CoroutineScope(Dispatchers.IO).launch {
            syncDataContinuously()
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private suspend fun syncDataContinuously() {
        while (true) {
            val dataToSync = getDataFromRoom()
            if (dataToSync.isNotEmpty()) {
                val synchronizedData = sendDataToServer(dataToSync)
                Log.e(className, "Data yang berhasil disingkronkan: ${synchronizedData.size}")
                if (synchronizedData.isNotEmpty()) {
                    updateSyncDatas(synchronizedData)
                } else {
                    Log.e(className, "Tidak ada data yang tersingkronisasi")
                }
            } else{
                Log.e(className, "Tidak ada data yang harus disingkronkan")
            }
            delay(delayMillis)
        }
    }

    private suspend fun getDataFromRoom(): List<NotifEntity> {
        val newNotif = notifDao.getNotificationsByStatus(status = NotificationStatus.NOT_SEND)
        val notYetValidateNotif = notifDao.getNotificationsByStatus(status = NotificationStatus.WAITING)

        return newNotif + notYetValidateNotif
    }

    private suspend fun sendDataToServer(data: List<NotifEntity>): List<NotifEntity> {
        try {
            val response =  apiService.syncData(data,
                deviceName = deviceInfoManager.deviceName(),
                deviceSerialNumber = deviceInfoManager.serialNumber()
            )
            if (response.isSuccessful) {
                deviceInfoManager.saveDeviceStatus(2)
                return response.body() ?: emptyList()
            } else {
                Log.e(className, response.errorBody()?.string() ?: "Error")
                return emptyList()
            }
        } catch (e: java.lang.Exception) {
            Log.e(className, e.localizedMessage)
            return  emptyList()
        }
    }

    private  suspend fun updateSyncDatas(datas: List<NotifEntity>) {
        datas.forEach {
            Log.e(className, "Update status notifikasi: ${it.uuid} ke ${it.status}")
            notifDao.updateNotificationStatusByUuid(it.uuid.toString(), NotificationStatus.fromInt(it.status))
        }

        val intent = Intent(ACTION_SYNC_SUCCESS)
        sendBroadcast(intent)
    }

    companion object {
        const val ACTION_SYNC_SUCCESS = "com.kodekolektif.notiflistener.ACTION_SYNC_SUCCESS"
        const val className = "DataSyncService"
    }
}
