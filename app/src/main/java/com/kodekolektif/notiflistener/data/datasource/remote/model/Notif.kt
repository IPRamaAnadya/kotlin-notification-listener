package com.kodekolektif.notiflistener.data.datasource.remote.model

import com.google.gson.annotations.SerializedName

data class Notif(
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String,
    @SerializedName("name") val name: String,
    @SerializedName("nominal") val nominal: Int,
)