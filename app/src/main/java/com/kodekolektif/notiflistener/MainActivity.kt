package com.kodekolektif.notiflistener

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kodekolektif.notiflistener.data.datasource.local.dao.NotifDao
import com.kodekolektif.notiflistener.data.datasource.local.database.AppDatabase
import com.kodekolektif.notiflistener.data.datasource.local.database.DatabaseInstance
import com.kodekolektif.notiflistener.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var notificationReceiver: BroadcastReceiver
    private lateinit var notificationDao: NotifDao
    private lateinit var binding: ActivityMainBinding
    private lateinit var notifAdapter: NotifAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeDatabase()
        setupRecyclerView()
        setupNotificationReceiver()
        checkPermissions()
        loadNotifications()
    }

    private fun initializeDatabase() {
        val db: AppDatabase = DatabaseInstance.getDatabase(this)
        notificationDao = db.notificationDao()
    }

    private fun setupRecyclerView() {
        notifAdapter = NotifAdapter(emptyList())
        binding.notifList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = notifAdapter
        }
    }

    private fun loadNotifications() {
        lifecycleScope.launch {
            val notifications = withContext(Dispatchers.IO) {
                notificationDao.getAllNotificationsWithNonNullNameAndNominal()
            }
            notifAdapter = NotifAdapter(notifications)
            binding.notifList.adapter = notifAdapter
        }
    }

    private fun setupNotificationReceiver() {
        notificationReceiver = createNotificationReceiver()
        val filter = IntentFilter("com.kodekolektif.notiflistener.NOTIFICATION_LISTENER")
        registerReceiver(notificationReceiver, filter)
    }

    private fun createNotificationReceiver(): BroadcastReceiver {
        return object : BroadcastReceiver() {
            @SuppressLint("SetTextI18n")
            override fun onReceive(context: Context?, intent: Intent?) {
                loadNotifications()
            }
        }
    }

    private fun checkPermissions() {
        if (!isNotificationListenerEnabled(this)) {
            openNotificationListenerSettings()
        }

        if (!isNotificationsEnabled(this)) {
            openAppNotificationSettings()
        }
    }

    private fun isNotificationListenerEnabled(context: Context): Boolean {
        val enabledListeners = Settings.Secure.getString(
            context.contentResolver, "enabled_notification_listeners"
        )
        return !TextUtils.isEmpty(enabledListeners) && enabledListeners.contains(context.packageName)
    }

    private fun isNotificationsEnabled(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    private fun openNotificationListenerSettings() {
        startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }

    private fun openAppNotificationSettings() {
        startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(notificationReceiver)
    }
}
