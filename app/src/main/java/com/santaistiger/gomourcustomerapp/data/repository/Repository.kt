package com.santaistiger.gomourcustomerapp.data.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.santaistiger.gomourcustomerapp.data.model.Customer
import com.santaistiger.gomourcustomerapp.data.model.Order
import com.santaistiger.gomourcustomerapp.data.model.OrderRequest
import com.santaistiger.gomourcustomerapp.data.model.Place
import com.google.firebase.database.Query
import net.daum.mf.map.api.MapPoint

interface Repository {
    suspend fun getDistance(start: String, goal: String): Int?
    suspend fun getDistance(start: String, goal: String, waypoints: String?): Int?
    suspend fun searchPlace(placeName: String, curMapPos: MapPoint.GeoCoordinate): List<Place>
    suspend fun readOrderDetail(orderId: String): Order?
    suspend fun readDeliveryManPhone(deliveryManUid: String): String?
    suspend fun readCustomerInfo(customerUid:String): Customer?
    suspend fun login(firebaseAuth: FirebaseAuth, email:String, password:String): AuthResult?
    suspend fun join(firebaseAuth:FirebaseAuth, email:String, password:String):AuthResult?
    suspend fun checkJoinable(email:String):Boolean
    fun updateAuthPassword(password:String)
    fun updateFireStorePassword(customerUid: String,password:String)
    fun updatePhone(customerUid: String,phone:String)
    fun writeOrderRequest(orderRequest: OrderRequest)
    fun deleteAuthCustomer()
    fun deleteFireStoreCustomer(customerUid: String)
    fun writeFireStoreCustomer(customer:Customer)
    fun writeAuthCustomer(email:String,password: String)
    fun getUid(): String
    suspend fun readDeliveryManAccount(deliveryManUid: String): String?
    fun readCurrentOrder(orderId: String): Query
    fun deleteCurrentOrder(orderId: String)
    fun readOrderList(customerUid: String): Query
}