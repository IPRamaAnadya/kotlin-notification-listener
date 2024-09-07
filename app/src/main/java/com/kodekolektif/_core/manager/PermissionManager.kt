package com.kodekolektif._core.manager

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.PowerManager
import android.provider.Settings
import android.text.TextUtils
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.core.app.NotificationManagerCompat
import com.kodekolektif._core.library.AutoStart.Autostart

class PermissionManager {

    companion object {
        // Request all permissions
        @JvmStatic
        fun requestPermissions(context: Context, sharedPreferences: SharedPreferences) {
            checkAndRequestNotificationPermissions(context)
            checkAndRequestBatterySaverMode(context)
            checkAutoStart(context)
        }

        // Check and request notification permissions
        @JvmStatic
        fun checkAndRequestNotificationPermissions(context: Context) {
            if (!isNotificationListenerEnabled(context)) {
                openNotificationListenerSettings(context)
            }

            if (!isNotificationsEnabled(context)) {
                openAppNotificationSettings(context)
            }
        }

        // Check if notification listener is enabled
        @JvmStatic
        private fun isNotificationListenerEnabled(context: Context): Boolean {
            val enabledListeners = Settings.Secure.getString(
                context.contentResolver, "enabled_notification_listeners"
            )
            return !TextUtils.isEmpty(enabledListeners) && enabledListeners.contains(context.packageName)
        }

        // Check if notifications are enabled
        @JvmStatic
        private fun isNotificationsEnabled(context: Context): Boolean {
            return NotificationManagerCompat.from(context).areNotificationsEnabled()
        }

        // Open notification listener settings
        @JvmStatic
        private fun openNotificationListenerSettings(context: Context) {
            context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }

        // Open app notification settings
        @JvmStatic
        private fun openAppNotificationSettings(context: Context) {
            context.startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            })
        }

        // Check battery saver mode and request permission if needed
        @JvmStatic
        fun checkAndRequestBatterySaverMode(context: Context) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            if (powerManager.isPowerSaveMode) {
                val intent = Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS)
                context.startActivity(intent)
            }
        }

        // Check and handle auto start
        @JvmStatic
        fun checkAutoStart(context: Context) {
            val enabled: Boolean = Autostart.isAutoStartEnabled(context)
            val state = Autostart.getAutoStartState(context)

            if (state == Autostart.State.DISABLED) {
                showAutoStartDisabledAlert(context)
            }
        }

        @JvmStatic
        private fun showAutoStartDisabledAlert(context: Context) {
            AlertDialog.Builder(context).apply {
                setTitle("Aktifkan Auto Start")
                setMessage("Auto start dinonaktifkan. Harap aktifkan di pengaturan agar aplikasi berfungsi dengan baik.")
                setPositiveButton("Buka Pengaturan") { dialog, _ ->
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

        // Show permission dialog for first-time setup
        @JvmStatic
        fun showPermissionDialog(context: Context, sharedPreferences: SharedPreferences) {

            val doNotShowAgain = sharedPreferences.getBoolean("do_not_show_again", false)
            if (doNotShowAgain) {
                return
            }

            val checkbox = CheckBox(context).apply {
                text = "jangan tampilkan pesan ini lagi"
            }

            val layout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(50, 40, 50, 10)
                addView(checkbox)
            }

            AlertDialog.Builder(context)
                .setTitle("Mengizinkan ulang")
                .setMessage("Harap izinkan ulang izin dari service notifikasi.")
                .setView(layout)
                .setPositiveButton("OK") { _, _ ->
                    if (checkbox.isChecked) {
                        with(sharedPreferences.edit()) {
                            putBoolean("do_not_show_again", true)
                            apply()
                        }
                    }
                    openNotificationListenerSettings(context)
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    if (checkbox.isChecked) {
                        with(sharedPreferences.edit()) {
                            putBoolean("do_not_show_again", true)
                            apply()
                        }
                    }
                    dialog.dismiss()
                }
                .show()
        }
    }
}
