package com.kodekolektif.auth.data.datasource.remote.model

import com.google.gson.annotations.SerializedName


data class LoginResponse(
    @SerializedName("user") val user: User,
    @SerializedName("device") val device: Device
)

data class User(
    @SerializedName("name") val name: String,
    @SerializedName("status") val status: Int
)

data class Device(
    @SerializedName("device_name") val deviceName: String,
    @SerializedName("serial_number") val serialNumber: String,
    @SerializedName("device_model") val deviceModel: String,
    @SerializedName("device_manufacture") val deviceManufacture: String,
    @SerializedName("device_type") val deviceType: String,
    @SerializedName("imei") val imei: String? = null
)