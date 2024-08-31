package com.kodekolektif.notiflistener.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.kodekolektif.notiflistener.R
import com.kodekolektif.notiflistener.data.datasource.local.dao.NotifDao
import com.kodekolektif.notiflistener.data.datasource.local.database.DatabaseInstance
import com.kodekolektif.notiflistener.data.datasource.local.entities.NotifEntity
import com.kodekolektif.notiflistener.data.datasource.local.entities.NotificationStatus
import com.kodekolektif.notiflistener.data.datasource.remote.api.ApiService
import com.kodekolektif.notiflistener.utils.Constant
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID
import java.util.concurrent.TimeUnit

class DataSyncService : Service() {

    private val channelId = "DataSyncServiceChannel"
    private val delayMillis = 1000L
    private  val timeOut: Long = 10

    private lateinit var notifDao: NotifDao
    private lateinit var apiService: ApiService

    override fun onCreate() {
        super.onCreate()

        Log.e(className, "Service berjalan")

        createNotificationChannel()

        // Initialize the DAO
        notifDao = DatabaseInstance.getDatabase(this).notificationDao()

        // Create a logging interceptor to log every request and response
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Set the desired logging level
        }

        val okHttpClient = OkHttpClient.Builder().apply {
            readTimeout(timeOut, TimeUnit.SECONDS)
            writeTimeout(timeOut, TimeUnit.SECONDS)
            connectTimeout(timeOut, TimeUnit.SECONDS)
            addInterceptor(loggingInterceptor)
            addInterceptor { chain ->
                var request = chain.request()
                request = request.newBuilder()
                    .build()
                val response = chain.proceed(request)
                response
            }
        }
            .build()

        // Initialize Retrofit with the OkHttpClient
        val retrofit = Retrofit.Builder()
            .baseUrl(Constant.apiUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create the API service
        apiService = retrofit.create(ApiService::class.java)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification())

        // Start the data synchronization process
        CoroutineScope(Dispatchers.IO).launch {
            syncDataContinuously()
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Data Sync Service")
            .setContentText("Synchronizing data with server...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            channelId,
            "Data Sync Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }

    private suspend fun syncDataContinuously() {
        while (true) {
            val dataToSync = getDataFromRoom()
            if (dataToSync.isNotEmpty()) {
                val synchronizedData = sendDataToServer(dataToSync)
                if (synchronizedData.isNotEmpty()) {
                    deleteSyncDatas(synchronizedData)
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
        return notifDao.getNotificationsByStatus(status = NotificationStatus.NOT_SEND)
    }

    private suspend fun sendDataToServer(data: List<NotifEntity>): List<NotifEntity> {
        try {
            return apiService.syncData(data)
        } catch (e: java.lang.Exception) {
            Log.e(className, e.localizedMessage)
            return  emptyList()
        }
    }

    private  suspend fun deleteSyncDatas(datas: List<NotifEntity>) {
        datas.forEach {
            notifDao.updateNotificationStatusByUuid(it.uuid.toString(), NotificationStatus.fromInt(2))
        }
    }

    companion object {
        const val className = "DataSyncService"
    }
}
