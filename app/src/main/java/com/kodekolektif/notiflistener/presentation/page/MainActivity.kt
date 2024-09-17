package com.kodekolektif.notiflistener.presentation.page

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.*
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kodekolektif.notiflistener.presentation.adapter.NotifAdapter
import com.kodekolektif.notiflistener.data.datasource.local.dao.NotifDao
import com.kodekolektif._core.database.AppDatabase
import com.kodekolektif._core.database.DatabaseInstance
import com.kodekolektif.notiflistener.databinding.ActivityMainBinding
import com.kodekolektif.notiflistener.presentation.viewmodel.NotifViewModel
import com.kodekolektif._core.library.AutoStart.Autostart
import com.kodekolektif._core.manager.PermissionManager
import com.kodekolektif.monitoringservices.presentation.page.MonitoringServicesActivity
import com.kodekolektif.notiflistener.BuildConfig
import com.kodekolektif.notiflistener.R
import com.kodekolektif.notiflistener.services.DataCleanupService
import com.kodekolektif.notiflistener.services.DataSyncService
import com.kodekolektif.notiflistener.services.NotifListenerServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var notifAdapter: NotifAdapter
    private lateinit var notificationDao: NotifDao

    private val notificationReceiver = createNotificationReceiver()
    private val syncReceiver = createSyncReceiver()

    private val sharedPreferences: SharedPreferences by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeDatabase()
        setupRecyclerView()
        loadNotifications()

        setupReceivers()
        startOrStopServices()

        PermissionManager.requestPermissions(this, sharedPreferences)
    }

    private fun initializeDatabase() {
        val db: AppDatabase = DatabaseInstance.getDatabase(this)
        notificationDao = db.notificationDao()
    }

    private fun setupRecyclerView() {
        notifAdapter = NotifAdapter(emptyList())
        binding.notifList.layoutManager = LinearLayoutManager(this)
        binding.notifList.adapter = notifAdapter
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

    private fun setupReceivers() {
        val notificationFilter = IntentFilter("com.kodekolektif.notiflistener.NOTIFICATION_LISTENER")
        val syncFilter = IntentFilter(DataSyncService.ACTION_SYNC_SUCCESS)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(notificationReceiver, notificationFilter, RECEIVER_EXPORTED)
            registerReceiver(syncReceiver, syncFilter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(notificationReceiver, notificationFilter)
            registerReceiver(syncReceiver, syncFilter)
        }
    }

    private fun startOrStopServices() {
        val syncService = Intent(this, DataSyncService::class.java)
        val listenerService = Intent(this, NotifListenerServices::class.java)
        val cleanUpService = Intent(this, DataCleanupService::class.java)

        stopService(syncService)
        startService(syncService)

        stopService(listenerService)
        startService(listenerService)

        stopService(cleanUpService)
        startService(cleanUpService)
    }

    private fun createNotificationReceiver(): BroadcastReceiver {
        return object : BroadcastReceiver() {
            @SuppressLint("SetTextI18n")
            override fun onReceive(context: Context?, intent: Intent?) {
                loadNotifications()
            }
        }
    }

    private fun createSyncReceiver(): BroadcastReceiver {
        return object : BroadcastReceiver() {
            @SuppressLint("SetTextI18n")
            override fun onReceive(context: Context?, intent: Intent?) {
                loadNotifications()
            }
        }
    }

    private fun deleteAllNotif() {
        lifecycleScope.launch {
            notificationDao.deleteAllNotifications()
            loadNotifications()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(notificationReceiver)
        unregisterReceiver(syncReceiver)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_monitoring -> {
                // Move to Monitoring page
                val intent = Intent(this, MonitoringServicesActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
