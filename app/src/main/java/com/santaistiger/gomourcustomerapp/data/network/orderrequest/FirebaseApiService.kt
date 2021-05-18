package com.santaistiger.gomourcustomerapp.data.network.orderrequest

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.navigation.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.santaistiger.gomourcustomerapp.data.model.Order
import com.santaistiger.gomourcustomerapp.data.model.OrderRequest
import com.santaistiger.gomourcustomerapp.ui.orderlist.OrderListAdapter
import com.santaistiger.gomourcustomerapp.ui.waitmatch.WaitMatchFragmentDirections

private const val TAG: String = "FirebaseApiService"
private const val ORDER_REQUEST_TABLE = "order_request"
private const val ORDER_TABLE = "order"

object FirebaseApi {
    private val database = Firebase.database
    private val orderRequestTable = database.getReference(ORDER_REQUEST_TABLE)
    private val orderTable = database.getReference(ORDER_TABLE)

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

    // 소비자의 주문 목록 받아오기
    fun getOrders(customerUid: String, adapter: OrderListAdapter, textView: TextView) {
        val orders = mutableListOf<Order>()
        val ordersReference = orderTable.orderByChild("customerUid").equalTo(customerUid)

        val ordersListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                orders.clear()
                adapter.orders.clear()

                for (messageSnapshot in dataSnapshot.children) {
                    val order: Order? = messageSnapshot.getValue(Order::class.java)
                    if (order != null) {
                        orders.add(order)
                    }
                }

                // 날짜 역순으로 재배열 후 adapter의 orders에 할당
                adapter.orders = orders.asReversed()
                Log.d(TAG, "orders was changed")
                adapter.notifyDataSetChanged()

                // 주문 내역이 없을 경우 안내 문구 표시
                if (adapter.orders.count() == 0) {
                    textView.visibility = View.VISIBLE
                } else {
                    textView.visibility = View.GONE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        ordersReference.addValueEventListener(ordersListener)
    }

}