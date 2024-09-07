package com.kodekolektif.auth.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kodekolektif._core.manager.DeviceInfoManager
import com.kodekolektif._core.network.NetworkResult
import com.kodekolektif.auth.data.datasource.remote.api.AuthApiServices
import com.kodekolektif.auth.data.datasource.remote.model.LoginRequestParams
import com.kodekolektif.auth.data.datasource.remote.model.LoginResponse
import com.kodekolektif.auth.data.repository.AuthRepository
import com.kodekolektif.auth.data.repository.AuthRepositoryImpl
import com.kodekolektif.auth.domain.usecase.AuthUsecase
import com.kodekolektif.notiflistener.data.repository.NotifRepository
import com.kodekolektif.notiflistener.domain.usecase.NotifUsecase
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authUsecase: AuthUsecase,
    private val deviceInfoManager: DeviceInfoManager
    ): ViewModel() {

    private val _loginState = MutableLiveData<NetworkResult<LoginResponse?>>()
    val loginState: LiveData<NetworkResult<LoginResponse?>> = _loginState

    fun login(deviceName: String) {
        _loginState.postValue(NetworkResult.Loading)

        val params = LoginRequestParams(
            deviceName = deviceName,
            serialNumber = deviceInfoManager.serialNumber(),
            deviceModel = deviceInfoManager.deviceModel(),
            deviceManufacture = deviceInfoManager.deviceManufacture(),
            deviceType = deviceInfoManager.deviceType()
        )
        viewModelScope.launch {
            val result = authUsecase.execute(params)
            deviceInfoManager.saveDeviceName(deviceName)
            _loginState.postValue(result)
        }
    }
}