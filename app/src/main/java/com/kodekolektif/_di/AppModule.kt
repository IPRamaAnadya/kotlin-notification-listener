package com.kodekolektif._di

import android.content.Context
import com.kodekolektif._core.network.ApiClient
import com.kodekolektif._core.database.DatabaseInstance
import com.kodekolektif._core.manager.DeviceInfoManager
import com.kodekolektif._core.manager.PermissionManager
import com.kodekolektif._core.network.ErrorHandler
import com.kodekolektif._core.utils.Constant
import com.kodekolektif.auth.data.datasource.remote.api.AuthApiServices
import com.kodekolektif.auth.data.repository.AuthRepository
import com.kodekolektif.auth.data.repository.AuthRepositoryImpl
import com.kodekolektif.auth.domain.usecase.AuthUsecase
import com.kodekolektif.auth.presentation.viewmodel.AuthViewModel
import com.kodekolektif.notiflistener.data.datasource.remote.api.NotificationApiServices
import com.kodekolektif.notiflistener.data.repository.NotifRepository
import com.kodekolektif.notiflistener.data.repository.NotifRepositoryImpl
import com.kodekolektif.notiflistener.domain.usecase.NotifUsecase
import com.kodekolektif.notiflistener.presentation.viewmodel.NotifViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit


val managerModule = module {
    single {
        DeviceInfoManager(get())
    }
}

val networkModule = module {
    single { ApiClient.init(androidContext()) }
    single { get<Retrofit>().create(NotificationApiServices::class.java) }
    single { get<Retrofit>().create(AuthApiServices::class.java) }
    single<ErrorHandler> { ErrorHandler() }
}

val databaseModule = module {
    single { DatabaseInstance.getDatabase(get()) }
}

val repositoryModule = module {
    single<AuthRepository>{ AuthRepositoryImpl(get(), get()) }
    single<NotifRepository> { NotifRepositoryImpl(get()) }
}

val usecaseModule = module {
    single { NotifUsecase(get()) }
    single { AuthUsecase(get()) }
}

val viewModelModule = module {
    viewModel { NotifViewModel(get()) }
    viewModel { AuthViewModel(get(), get()) }
}

val libraryModule = module {
    single { androidContext().getSharedPreferences(Constant.sharedPref, Context.MODE_PRIVATE) }
    single { PermissionManager() }
}



