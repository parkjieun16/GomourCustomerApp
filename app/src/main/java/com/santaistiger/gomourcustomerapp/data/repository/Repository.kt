package com.santaistiger.gomourcustomerapp.data.repository

import com.google.firebase.database.DatabaseReference
import com.santaistiger.gomourcustomerapp.data.model.Order
import com.santaistiger.gomourcustomerapp.data.model.OrderRequest
import com.santaistiger.gomourcustomerapp.data.model.Place
import net.daum.mf.map.api.MapPoint

interface Repository {
    suspend fun getDistance(start: String, goal: String): Int?
    suspend fun getDistance(start: String, goal: String, waypoints: String?): Int?
    suspend fun searchPlace(placeName: String, curMapPos: MapPoint.GeoCoordinate): List<Place>
    suspend fun readOrderDetail(orderId: String): Order?
    suspend fun readDeliveryManPhone(deliveryManUid: String): String?
    suspend fun writeOrderRequest(orderRequest: OrderRequest)
    suspend fun readDeliveryManAccount(deliveryManUid: String): String?
    fun getUid(): String
}