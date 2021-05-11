package com.santaistiger.gomourcustomerapp.data.repository

import com.santaistiger.gomourcustomerapp.data.model.OrderRequest
import com.santaistiger.gomourcustomerapp.data.model.Place
import net.daum.mf.map.api.MapPoint

interface Repository {
    suspend fun getDistance(start: String, goal: String): Int?
    suspend fun getDistance(start: String, goal: String, waypoints: String?): Int?
    suspend fun searchPlace(placeName: String, curMapPos: MapPoint.GeoCoordinate): List<Place>
    fun writeOrderRequest(orderRequest: OrderRequest)
}