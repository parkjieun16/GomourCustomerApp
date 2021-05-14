package com.santaistiger.gomourcustomerapp.data.repository

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.santaistiger.gomourcustomerapp.data.model.Order
import com.santaistiger.gomourcustomerapp.data.model.OrderRequest
import com.santaistiger.gomourcustomerapp.data.model.Place
import com.santaistiger.gomourcustomerapp.data.network.firebase.FireStoreApi
import com.santaistiger.gomourcustomerapp.data.network.firebase.FirebaseApi
import com.santaistiger.gomourcustomerapp.data.network.firebase.FirebaseApi.write
import com.santaistiger.gomourcustomerapp.data.network.map.KakaoMapApi
import com.santaistiger.gomourcustomerapp.data.network.map.NaverMapApi
import com.santaistiger.gomourcustomerapp.data.network.map.asDomainModel
import net.daum.mf.map.api.MapPoint

val TAG = "RepositoryImpl"
object RepositoryImpl : Repository {

    /**
     * 출발지, 목적지, 경유지를 매개변수로 받아서
     * 네이버 Directions API를 이용해서 최적 경로의 거리(단위: m)를 가져오는 함수
     */
    override suspend fun getDistance(start: String, goal: String): Int? {
        return getDistance(start, goal, null)
    }

    override suspend fun getDistance(start: String, goal: String, waypoints: String?): Int? {
        val jsonResponse =
                NaverMapApi.retrofitService.getDirections(start, goal, waypoints)

        return jsonResponse.route["traoptimal"]?.get(0)?.summary?.distance?.toInt()
    }

    override suspend fun getOrderDetail(orderId: String): Order? {
        val response =  FirebaseApi.getOrderDetail(orderId)
        return response.order
    }

    /**
     * 찾고싶은 장소명 또는 키워드로 장소를 찾는 함수
     * 현재의 지도의 중심점에서 거리순으로 15개의 Place를 리턴함
     */
    override suspend fun searchPlace(placeName: String, curMapPos: MapPoint.GeoCoordinate): List<Place> {
        val kakaoMapProperty =
                KakaoMapApi.retrofitService.searchPlaces(placeName, curMapPos.longitude, curMapPos.latitude)

        return kakaoMapProperty.documents.asDomainModel()
    }

    /**
     * orderRequest를 받아서 Firebase Realtime Database의 order_reqeust 테이블에 write
     * 이때, data key는 orderRequest의 orderId를 사용
     */
    override fun writeOrderRequest(orderRequest: OrderRequest) {
        val key = orderRequest.orderId
        write(key, orderRequest)
    }

    override suspend fun getDeliveryManPhone(deliveryManUid: String): String? {
        val response = FireStoreApi.getDeliveryMan(deliveryManUid)
        val deliveryMan = response.deliveryMan
        return deliveryMan?.phone
    }
}