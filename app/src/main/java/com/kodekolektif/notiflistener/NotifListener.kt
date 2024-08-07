package com.kodekolektif.notiflistener

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import com.kodekolektif.notiflistener.data.datasource.local.database.AppDatabase
import com.kodekolektif.notiflistener.data.datasource.local.database.DatabaseInstance
import com.kodekolektif.notiflistener.data.datasource.local.entities.NotifEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotifListener: NotificationListenerService() {

    private lateinit var db: AppDatabase

    override fun onCreate() {
        super.onCreate()
        db = DatabaseInstance.getDatabase(this)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {

        val pkgName = sbn!!.packageName as String

        if(pkgName != "id.dana") return
        if(pkgName == this.packageName) return

        val key = sbn.key
        val extras: Bundle? = sbn.notification?.extras
        val title = extras?.getString("android.title")
        val text = extras?.getString("android.text")

        val nameAndNominal = extractInfo(text ?: "")

        val notification = NotifEntity(
            packageName = pkgName,
            title = title ?: "",
            body = text ?: "",
            name = nameAndNominal.first,
            nominal = nameAndNominal.second
        )

        CoroutineScope(Dispatchers.IO).launch {
            db.notificationDao().insertNotification(notification)
            if (key != null) {
                removeNotifications(key)
            }
        }

        // Broadcast the notification data
        val intent = Intent("com.kodekolektif.notiflistener.NOTIFICATION_LISTENER")
        intent.putExtra("pkgName", pkgName)
        intent.putExtra("title", title)
        intent.putExtra("text", text)
        sendBroadcast(intent)

        if (!isAppInForeground()) {
            sendNotification(pkgName, title, text)
        }

        super.onNotificationPosted(sbn)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun isAppInForeground(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        for (appProcess in appProcesses) {
            if (appProcess.importance == android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return appProcess.processName == packageName
            }
        }
        return false
    }

    private fun sendNotification(pkgName: String?, title: String?, text: String?) {
        val channelId = "notif_listener_channel"
        val notificationId = 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notification Listener"
            val descriptionText = "Channel for notification listener"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }

    private  fun removeNotifications(key: String) {
        cancelNotification(key)
    }

    private fun extractInfo(description: String): Pair<String?, Int?> {
        // Adjust regex to capture names with potential lowercase letters
        val nameRegex = Regex("""Hei, ([A-Za-z\s]+) baru""")
        // Adjust regex to handle comma as a decimal separator and optional dots
        val priceRegex = Regex("""Rp([0-9,.]+)""")

        val nameMatch = nameRegex.find(description)
        val priceMatch = priceRegex.find(description)

        val name = nameMatch?.groups?.get(1)?.value?.trim()
        val price = priceMatch?.groups?.get(1)?.value
            ?.replace(".", "")?.replace(",", "")?.toIntOrNull()

        return Pair(name, price)
    }
}
