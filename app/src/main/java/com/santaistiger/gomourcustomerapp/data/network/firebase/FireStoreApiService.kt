package com.santaistiger.gomourcustomerapp.data.network.firebase

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.santaistiger.gomourcustomerapp.data.model.DeliveryMan
import kotlinx.coroutines.tasks.await
import java.lang.Exception

object FireStoreApi{
    private val database = Firebase.firestore
    private val customerTable = database.collection("customer")
    private val deliveryManTable = database.collection("deliveryMan")

    suspend fun getDeliveryMan(deliveryManUid: String): DeliveryManResponse {
        val response = DeliveryManResponse()
        try {
            response.deliveryMan = deliveryManTable.document(deliveryManUid)
                .get().await().toObject(DeliveryMan::class.java)
        } catch (e: Exception) {
            response.exception = e
        }

        Log.i(com.santaistiger.gomourcustomerapp.data.repository.TAG, response.toString())
        return response
    }
}