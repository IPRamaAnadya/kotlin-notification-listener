package com.kodekolektif._core.manager

import android.content.SharedPreferences
import java.util.UUID

class DeviceInfoManager(private  val sharedPreferences: SharedPreferences) {

    fun deviceName(): String {
        return sharedPreferences.getString("device_name", null) ?: "Unknown Device"
    }

    fun saveDeviceName(deviceName: String) {
        sharedPreferences.edit().putString("device_name", deviceName).apply()
    }

    fun deviceStatus(): Int {
        return sharedPreferences.getInt("device_status", 0)
    }

    fun saveDeviceStatus(deviceStatus: Int) {
        sharedPreferences.edit().putInt("device_status", deviceStatus).apply()
    }

    fun serialNumber(): String {
        val serialNumber = sharedPreferences.getString("serial_number", null)
        if (serialNumber == null) {
            val uuid = UUID.randomUUID().toString()
            sharedPreferences.edit().putString("serial_number", uuid).apply()
            return uuid
        }
        return serialNumber
    }

    fun deviceModel(): String {
        return android.os.Build.MODEL
    }

    fun deviceManufacture(): String {
        return android.os.Build.MANUFACTURER
    }

    fun deviceType(): String {
        return android.os.Build.DEVICE
    }
}