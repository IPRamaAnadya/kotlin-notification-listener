package com.kodekolektif.monitoringservices.presentation.page

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kodekolektif.notiflistener.R
import com.kodekolektif.notiflistener.databinding.ActivityMonitoringServicesBinding
import com.kodekolektif.notiflistener.services.DataCleanupService
import com.kodekolektif.notiflistener.services.DataSyncService
import com.kodekolektif.notiflistener.services.NotifListenerServices
import com.kodekolektif.notiflistener.utils.DialogManager
import com.kodekolektif.notiflistener.utils.AppNotificationManager as AppNotificationManager1

class MonitoringServicesActivity : AppCompatActivity() {

    companion object {
        private val TAG = MonitoringServicesActivity::class.java.simpleName
    }

    private  lateinit var binding: ActivityMonitoringServicesBinding
    private val notificationReceiver = createNotificationReceiver()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMonitoringServicesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initView()
    }

    private fun  initView() {
        supportActionBar?.title = "Monitoring Services"

        checkServiceStatus()

        setActionService1()
        setActionService2()
        setActionService3()

        setupReceivers()
    }

    private fun setupReceivers() {
        val notificationFilter = IntentFilter("com.kodekolektif.notiflistener.NOTIFICATION_LISTENER_TEST")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(notificationReceiver, notificationFilter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(notificationReceiver, notificationFilter)
        }
    }

    // set action for service 1
    private fun setActionService1() {
        binding.btnStartService.setOnClickListener {
            startService(Intent(this, NotifListenerServices::class.java))
            checkServiceStatus()
        }

        binding.btnStopService.setOnClickListener {
            stopService(Intent(this, NotifListenerServices::class.java))
            checkServiceStatus()
        }

        binding.btnTest.setOnClickListener {
            sendNotification()
        }
    }

    // set action for service 2
    private fun setActionService2() {
        binding.btnStartService2.setOnClickListener {
            startService(Intent(this, DataSyncService::class.java))
            checkServiceStatus()
        }

        binding.btnStopService2.setOnClickListener {
            stopService(Intent(this, DataSyncService::class.java))
            checkServiceStatus()
        }

        binding.btnTest2.setOnClickListener {
            DialogManager.showAlertDialog(this, "Alert!", "Fitur belum tersedia")
        }
    }

    // set action for service 3
    private fun setActionService3() {
        binding.btnStartService3.setOnClickListener {
            startService(Intent(this, DataCleanupService::class.java))
            checkServiceStatus()
        }

        binding.btnStopService3.setOnClickListener {
            stopService(Intent(this, DataCleanupService::class.java))
            checkServiceStatus()
        }

        binding.btnTest3.setOnClickListener {
            DialogManager.showAlertDialog(this, "Alert!", "Fitur belum tersedia")
        }
    }

    // check the status of the service
    private fun checkServiceStatus() {
        if (isServiceRunning(this, DataSyncService::class.java)) {
            binding.tvServiceStatus2.text = "Service is running"
        } else {
            binding.tvServiceStatus2.text = "Service is not running"
        }

        if (isServiceRunning(this, DataCleanupService::class.java)) {
            binding.tvServiceStatus3.text = "Service is running"
        } else {
            binding.tvServiceStatus3.text = "Service is not running"
        }

        if (isServiceRunning(this, NotifListenerServices::class.java)) {
            binding.tvServiceStatus.text = "Service is running"
        } else {
            binding.tvServiceStatus.text = "Service is not running"
        }
    }


    // check if the service is running
    fun isServiceRunning(context: Context, serviceClass: Class<out Service>): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    // send notification using notification manager
    private fun sendNotification() {
        AppNotificationManager1(this).sendNotification(1, "Test Notif Listener", "Test Notif Listener")
    }

    private fun createNotificationReceiver(): BroadcastReceiver {
        return object : BroadcastReceiver() {
            @SuppressLint("SetTextI18n")
            override fun onReceive(context: Context?, intent: Intent?) {
                DialogManager.showAlertDialog(this@MonitoringServicesActivity, "Alert!", "Notification Collector Services Worked")
            }
        }
    }


}