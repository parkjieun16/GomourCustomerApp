package com.santaistiger.gomourcustomerapp.data.network.database

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.santaistiger.gomourcustomerapp.data.model.DeliveryMan
import kotlinx.coroutines.tasks.await
import java.lang.Exception

private const val TAG = "FireStoreApiService"

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

        Log.i(TAG, response.toString())
        return response
    }
}