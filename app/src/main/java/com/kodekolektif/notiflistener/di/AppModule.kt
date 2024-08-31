package com.kodekolektif.notiflistener.di

import androidx.room.Room
import com.kodekolektif.notiflistener.data.datasource.local.database.AppDatabase
import com.kodekolektif.notiflistener.data.datasource.remote.api.ApiService
import com.kodekolektif.notiflistener.data.repository.NotifRepository
import com.kodekolektif.notiflistener.data.repository.NotifRepositoryImpl
import com.kodekolektif.notiflistener.domain.usecase.NotifUsecase
import com.kodekolektif.notiflistener.presentation.viewmodel.NotifViewModel
import com.kodekolektif.notiflistener.utils.Constant
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val globalModule = module {

    single<NotifRepository> { NotifRepositoryImpl(get()) }

    single { NotifUsecase(get()) }

    viewModel { NotifViewModel(get()) }
}

val databaseModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "notifications")
            .build()
    }
}

val apiModule = module {
    single {
        Retrofit.Builder()
            .baseUrl(Constant.apiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}