package com.kodekolektif.notiflistener.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.kodekolektif.notiflistener.data.datasource.local.entities.NotifEntity
import com.kodekolektif.notiflistener.domain.usecase.NotifUsecase
import com.kodekolektif._core.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NotifViewModel (private val usecase: NotifUsecase) : ViewModel() {

    private val _postState = MutableStateFlow<Resource<NotifEntity>>(Resource.Loading())
    val postState: StateFlow<Resource<NotifEntity>> get() = _postState
}