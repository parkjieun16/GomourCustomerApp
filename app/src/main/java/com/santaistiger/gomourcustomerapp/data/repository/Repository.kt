package com.santaistiger.gomourcustomerapp.data.repository

import android.widget.TextView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.santaistiger.gomourcustomerapp.data.model.Order
import com.santaistiger.gomourcustomerapp.data.model.OrderRequest
import com.santaistiger.gomourcustomerapp.data.model.Place
import com.santaistiger.gomourcustomerapp.ui.adapter.OrderListAdapter
import net.daum.mf.map.api.MapPoint

interface Repository {
    suspend fun getDistance(start: String, goal: String): Int?
    suspend fun getDistance(start: String, goal: String, waypoints: String?): Int?
    suspend fun searchPlace(placeName: String, curMapPos: MapPoint.GeoCoordinate): List<Place>
    suspend fun readOrderDetail(orderId: String): Order?
    suspend fun readDeliveryManPhone(deliveryManUid: String): String?
    fun writeOrderRequest(orderRequest: OrderRequest)
    fun getUid(): String
    fun readCurrentOrder(orderId: String): Query
    fun deleteCurrentOrder(orderId: String)
    fun readOrderList(customerUid: String): Query
}