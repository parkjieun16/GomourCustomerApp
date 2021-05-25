package com.santaistiger.gomourcustomerapp.data.network.database

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.santaistiger.gomourcustomerapp.data.model.DeliveryMan
import kotlinx.coroutines.tasks.await


object FireStoreApi {
    private const val TAG = "FireStoreApiService"
    private const val CUSTOMER_TABLE = "customer"
    private const val DELIVERY_MAN_TABLE = "deliveryMan"

    private val database = Firebase.firestore
    private val customerTable = database.collection(CUSTOMER_TABLE)
    private val deliveryManTable = database.collection(DELIVERY_MAN_TABLE)

    suspend fun getDeliveryMan(deliveryManUid: String): DeliveryManResponse {
        val response = DeliveryManResponse()
        try {
            response.deliveryMan = deliveryManTable.document(deliveryManUid)
                .get().await().toObject(DeliveryMan::class.java)
        } catch (e: Exception) {
            response.exception = e
        }

        Log.i(TAG, response.toString())
        return response
    }
}