package com.kodekolektif.splash.presentation.page

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kodekolektif._core.manager.DeviceInfoManager
import com.kodekolektif._core.manager.PermissionManager
import com.kodekolektif.auth.presentation.page.LoginActivity
import com.kodekolektif.notiflistener.R
import com.kodekolektif.notiflistener.presentation.page.MainActivity
import com.kodekolektif.notiflistener.utils.DialogManager
import org.koin.android.ext.android.inject

class SplashActivity : AppCompatActivity() {

    // device info manager
    private val deviceInfoManager: DeviceInfoManager by inject()

    // shared preferences
    private val sharedPreferences: SharedPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // hide action bar
        supportActionBar?.hide()

        // request permission
        PermissionManager.requestPermissions(this, sharedPreferences)

        // get device info
        val deviceStatus = deviceInfoManager.deviceStatus()

        if (deviceStatus == 2) {
            Thread.sleep(2000)
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
                finish()
            }
        } else  if (deviceStatus == 1){
            DialogManager.showAlertDialog(this, "Penting!", "Status device pending. Silahkan hubungi admin agar device dapat menyingkronkan data.") {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            Thread.sleep(2000)
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }
}