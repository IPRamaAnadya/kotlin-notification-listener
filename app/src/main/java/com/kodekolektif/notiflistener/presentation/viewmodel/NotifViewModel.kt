package com.kodekolektif.notiflistener.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kodekolektif.notiflistener.data.datasource.local.entities.NotifEntity
import com.kodekolektif.notiflistener.data.datasource.remote.model.Notif
import com.kodekolektif.notiflistener.domain.usecase.NotifUsecase
import com.kodekolektif.notiflistener.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotifViewModel (private val usecase: NotifUsecase) : ViewModel() {

    private val _postState = MutableStateFlow<Resource<NotifEntity>>(Resource.Loading())
    val postState: StateFlow<Resource<NotifEntity>> get() = _postState
}