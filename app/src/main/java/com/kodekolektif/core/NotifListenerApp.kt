package com.kodekolektif.core

import android.app.Application
import android.content.Intent
import com.kodekolektif.notiflistener.BuildConfig
import com.kodekolektif.notiflistener.di.apiModule
import com.kodekolektif.notiflistener.di.globalModule
import com.kodekolektif.notiflistener.di.databaseModule
import com.kodekolektif.notiflistener.services.DataSyncService
import com.kodekolektif.notiflistener.services.MyNotifListenerServices
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class NotifListenerApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@NotifListenerApp)
            modules(listOf(globalModule, databaseModule, apiModule))
        }

        val syncService = Intent(this, DataSyncService::class.java)
        val listenerService = Intent(this, MyNotifListenerServices::class.java)

        if (!BuildConfig.DEBUG) {
            stopService(syncService)
            startService(syncService)
        }

        stopService(listenerService)
        startService(listenerService)
    }
}