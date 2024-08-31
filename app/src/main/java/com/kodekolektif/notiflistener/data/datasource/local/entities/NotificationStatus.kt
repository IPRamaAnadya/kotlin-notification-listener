package com.kodekolektif.notiflistener.data.datasource.local.entities


enum class NotificationStatus(val value: Int) {
    NOT_SEND(1),
    SUCCESS(2),
    FAILED(3),
    WAITING(4);

    companion object {
        fun fromInt(value: Int): NotificationStatus {
            return values().first { it.value == value }
        }
    }
}