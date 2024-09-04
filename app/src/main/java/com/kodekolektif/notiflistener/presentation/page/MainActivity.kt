package com.kodekolektif.notiflistener.presentation.page

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.*
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.kodekolektif.notiflistener.presentation.adapter.NotifAdapter
import com.kodekolektif.notiflistener.data.datasource.local.dao.NotifDao
import com.kodekolektif.notiflistener.data.datasource.local.database.AppDatabase
import com.kodekolektif.notiflistener.data.datasource.local.database.DatabaseInstance
import com.kodekolektif.notiflistener.data.datasource.local.entities.NotifEntity
import com.kodekolektif.notiflistener.data.datasource.remote.model.Notif
import com.kodekolektif.notiflistener.databinding.ActivityMainBinding
import com.kodekolektif.notiflistener.presentation.viewmodel.NotifViewModel
import com.kodekolektif.notiflistener.utils.AutoStart.Autostart
import com.kodekolektif.notiflistener.utils.Constant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.observeOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var notificationReceiver: BroadcastReceiver
    private lateinit var notificationDao: NotifDao
    private lateinit var binding: ActivityMainBinding
    private lateinit var notifAdapter: NotifAdapter
    private lateinit var sharedPreferences: SharedPreferences

    private val notifViewModel: NotifViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeDatabase()
        setupRecyclerView()
        checkAndRequestBatterySaverMode(this)
        setupNotificationReceiver()
        checkPermissions()
        loadNotifications()
        checkAutoStart(this)

        binding.deleteAllNotif.setOnClickListener {
            deleteAllNotif()
        }

        sharedPreferences = getSharedPreferences(Constant.sharedPref, Context.MODE_PRIVATE)

        if (!sharedPreferences.getBoolean("do_not_show_again", false)) {
            showPermissionDialog()
        }
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

    fun checkAndRequestBatterySaverMode(context: Context) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

        if (powerManager.isPowerSaveMode) {
            // Notify the user that the battery saver mode is on
            // Optionally, you can show a dialog to inform the user and redirect them to the settings
            val intent = Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS)
            context.startActivity(intent)
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

    private  fun deleteAllNotif() {
        lifecycleScope.launch {
            notificationDao.deleteAllNotifications()
            val notifications = withContext(Dispatchers.IO) {
                notificationDao.getAllNotificationsWithNonNullNameAndNominal()
            }
            notifAdapter = NotifAdapter(notifications)
            binding.notifList.adapter = notifAdapter
        }
    }

    private fun showPermissionDialog() {
        val checkbox = CheckBox(this).apply {
            text = "jangan tampilkan pesan ini lagi"
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
            addView(checkbox)
        }

        AlertDialog.Builder(this)
            .setTitle("Mengizinkan ulang")
            .setMessage("Karena terdapat masalah di sistem android (beberapa devices), harap izinkan ulang izin dari service notifikasi.")
            .setView(layout)
            .setPositiveButton("OK") { _, _ ->
                if (checkbox.isChecked) {
                    // Save preference to not show the dialog again
                    with(sharedPreferences.edit()) {
                        putBoolean("do_not_show_again", true)
                        apply()
                    }
                }
                openNotificationListenerSettings()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    fun checkAutoStart(applicationContext: Context) {
        val enabled: Boolean = Autostart.isAutoStartEnabled(applicationContext)
        val state = Autostart.getAutoStartState(applicationContext)

        if (state == Autostart.State.DISABLED) {
            // Show an alert dialog to the user
            showAutoStartDisabledAlert(applicationContext)
        } else if (state == Autostart.State.ENABLED) {
            // Auto start is enabled, proceed with your logic
        }
    }

    fun showAutoStartDisabledAlert(context: Context) {
        AlertDialog.Builder(context).apply {
            setTitle("Aktifkan Auto Start")
            setMessage("Auto start dinonaktifkan. Harap aktifkan di pengaturan agar aplikasi berfungsi dengan baik.")
            setPositiveButton("Buka Pengaturan") { dialog, _ ->
                // Redirect the user to the Auto Start settings
                val intent = Intent(Settings.ACTION_SETTINGS)
                context.startActivity(intent)
                dialog.dismiss()
            }
            setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(notificationReceiver)
    }
}
