package com.santaistiger.gomourcustomerapp.data.network.database

import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.santaistiger.gomourcustomerapp.data.model.Order
import com.santaistiger.gomourcustomerapp.data.model.OrderRequest
import com.santaistiger.gomourcustomerapp.ui.adapter.OrderListAdapter
import kotlinx.coroutines.tasks.await


object RealtimeApi {
    private const val TAG = "RealtimeApiService"
    private const val ORDER_REQUEST_TABLE = "order_request"
    private const val ORDER_TABLE = "order"

    private val database = Firebase.database
    private val orderRequestTable = database.getReference(ORDER_REQUEST_TABLE)
    private val orderTable = database.getReference(ORDER_TABLE)

    fun writeRequest(key: String, orderRequest: OrderRequest) {
        orderRequestTable.child(key).setValue(orderRequest)
    }

    suspend fun readOrderDetail(orderId: String): OrderResponse {
        val response = OrderResponse()
        try {
            response.order = orderTable.child(orderId)
                .get().await().getValue(Order::class.java)

        } catch (e: Exception) {
            response.exception = e
            e.printStackTrace()
        }

        return response
    }

    // realtime database의 order_request 테이블에서 인자로 받은 주문 번호에 해당하는 주문 정보를 받아와 해당 값을 반환한다.
    fun readCurrentOrder(orderId: String): Query {
        val currentOrder = orderRequestTable.orderByKey().equalTo(orderId)

        return currentOrder
    }

    // realtime database에서 인자로 받은 주문 번호에 해당하는 주문 정보를 삭제한다.
    fun deleteCurrentOrder(orderId: String) {
        orderRequestTable.child(orderId).removeValue()
    }

    // realtime database의 order 테이블에 있는 소비자의 주문 목록을 받아와 해당 값을 반환한다.
    fun readOrderList(customerUid: String): Query {
        val orderList = orderTable.orderByChild("customerUid").equalTo(customerUid)

        return orderList
    }


}