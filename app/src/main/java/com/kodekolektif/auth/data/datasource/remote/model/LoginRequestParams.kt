package com.kodekolektif.auth.data.datasource.remote.model

import com.google.gson.annotations.SerializedName

data class LoginRequestParams(
    @SerializedName("device_name") val deviceName: String,
    @SerializedName("serial_number") val serialNumber: String,
    @SerializedName("device_model") val deviceModel: String,
    @SerializedName("device_manufacture") val deviceManufacture: String,
    @SerializedName("device_type") val deviceType: String,
    @SerializedName("imei") val imei: String? = null
)