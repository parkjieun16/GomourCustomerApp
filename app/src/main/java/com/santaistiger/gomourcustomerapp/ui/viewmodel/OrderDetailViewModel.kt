package com.santaistiger.gomourcustomerapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.santaistiger.gomourcustomerapp.data.model.Order
import com.santaistiger.gomourcustomerapp.data.repository.Repository
import com.santaistiger.gomourcustomerapp.data.repository.RepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



class OrderDetailViewModel(orderId: String) : ViewModel() {
    companion object {
        private val TAG = "OrderDetailViewModel"
    }

    val order: MutableLiveData<Order> = liveData(Dispatchers.IO) {
        emit(repository.readOrderDetail(orderId))
    } as MutableLiveData<Order>

    val isCallBtnClick = MutableLiveData<Boolean>()
    val isTextBtnClick = MutableLiveData<Boolean>()

    private val repository: Repository = RepositoryImpl

    fun onCallBtnClick() {
        isCallBtnClick.value = true
    }

    fun doneCallBtnClick() {
        isCallBtnClick.value = false
    }

    fun onTextBtnClick() {
        isTextBtnClick.value = true
    }

    fun doneTextBtnClick() {
        isTextBtnClick.value = false
    }

    fun getDeliveryManUid() = order.value!!.deliveryManUid!!
}