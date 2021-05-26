package com.santaistiger.gomourcustomerapp.ui.viewmodel

/**
 * Created by Jieun Park.
 */

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.santaistiger.gomourcustomerapp.data.repository.Repository
import com.santaistiger.gomourcustomerapp.data.repository.RepositoryImpl

class WaitMatchViewModel : ViewModel() {
    private val TAG = "WaitMatchViewModel"
    private val repository: Repository = RepositoryImpl

    // 주문 취소 버튼 눌리는지 여부 표시
    private val _eventCancelOrder = MutableLiveData<Boolean>()
    val eventCancelOrder: LiveData<Boolean>
        get() = _eventCancelOrder

    // 현재 주문이 realtime database에 존재하는지 여부 표시
    private val _isCurrentOrderExist = MutableLiveData<Boolean>()
    val isCurrentOrderExist: LiveData<Boolean>
        get() = _isCurrentOrderExist

    init {
        _eventCancelOrder.value = false
        _isCurrentOrderExist.value = true
    }

    /**
     * 주문 취소 버튼 동작 정의
     * 주문 취소 버튼이 눌리면 _eventCancelOrder 값을 true로 변경한다.
     */
    fun onCancelClicked() {
        _eventCancelOrder.value = true
    }

    /**
     * 현재 주문이 realtime database에 존재하는지 확인한다.
     * 만약 현재 주문이 realtime database에 존재하지 않을 경우 _isCurrentOrderExist 값을 false로 변경한다.
     */
    fun checkDatabase(orderId: String) {
        val currentOrder = repository.readCurrentOrder(orderId)

        currentOrder.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                // 현재 주문이 realtime database에 존재할 경우
                if (snapshot.exists()) {
                    Log.d(TAG, "current order is in realtime database")
                }

                // 현재 주문이 realtime database에 존재하지 않을 경우
                else {
                    Log.d(TAG, "current order is not in realtime database")
                    _isCurrentOrderExist.value = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", error.toException())
            }
        })
    }
}
