package com.santaistiger.gomourcustomerapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WaitMatchViewModel : ViewModel() {

    // 주문 취소 버튼 눌리는지 여부
    private val _eventCancelOrder = MutableLiveData<Boolean>()
    val eventCancelOrder: LiveData<Boolean>
        get() = _eventCancelOrder

    init {
        _eventCancelOrder.value = false
    }

    fun onCancelClicked() {
        _eventCancelOrder.value = true
    }
}