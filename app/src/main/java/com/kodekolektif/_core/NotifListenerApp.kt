package com.kodekolektif._core

import android.app.Application
import android.content.Intent
import com.kodekolektif._di.databaseModule
import com.kodekolektif._di.libraryModule
import com.kodekolektif._di.managerModule
import com.kodekolektif._di.networkModule
import com.kodekolektif._di.repositoryModule
import com.kodekolektif._di.usecaseModule
import com.kodekolektif._di.viewModelModule
import com.kodekolektif.notiflistener.BuildConfig
import com.kodekolektif.notiflistener.services.DataSyncService
import com.kodekolektif.notiflistener.services.NotifListenerServices
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class NotifListenerApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@NotifListenerApp)
            modules(
                listOf(
                    libraryModule,
                    managerModule,
                    networkModule,
                    databaseModule,
                    repositoryModule,
                    usecaseModule,
                    viewModelModule,
                )
            )
        }
    }
}