package com.santaistiger.gomourcustomerapp.data.network.orderrequest

import android.util.Log
import android.view.View
import androidx.navigation.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.santaistiger.gomourcustomerapp.data.model.OrderRequest
import com.santaistiger.gomourcustomerapp.ui.waitmatch.WaitMatchFragmentDirections

private const val TAG: String = "FirebaseApiService"
private const val ORDER_REQUEST_TABLE = "order_request"

object FirebaseApi {
    private val database = Firebase.database
    private val orderRequestTable = database.getReference(ORDER_REQUEST_TABLE)

    fun write(key: String, orderRequest: OrderRequest) {
        orderRequestTable.child(key).setValue(orderRequest)
    }

    // realtime database에 현재 주문이 존재하는지 확인
    fun getCurrentOrder(orderId: String): Query {
        val currentOrder = orderRequestTable.orderByKey().equalTo(orderId)

        return currentOrder
    }

    // realtime database에서 주문 삭제
    fun deleteCurrentOrder(orderId: String) {
        orderRequestTable.child(orderId).removeValue()
    }

}