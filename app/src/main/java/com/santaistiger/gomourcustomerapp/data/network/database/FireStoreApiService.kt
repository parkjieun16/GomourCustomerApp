package com.santaistiger.gomourcustomerapp.data.network.database

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.santaistiger.gomourcustomerapp.data.model.Customer
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
    // customer정보 얻기
    suspend fun getCustomer(customerUid: String): CustomerResponse {
        val response = CustomerResponse()
        try {
            response.customer = customerTable.document(customerUid)
                .get().await().toObject(Customer::class.java)
        } catch (e: Exception) {
            response.exception = e
        }

        Log.i(com.santaistiger.gomourcustomerapp.data.repository.TAG, response.toString())
        return response
    }

    suspend fun checkJoinable(email:String): Boolean{
        return customerTable.whereEqualTo("email",email).get().await().isEmpty
    }


    fun updateFireStorePassword(customerUid: String,password:String){
        customerTable.document(customerUid).update("password",password)
    }

    fun updatePhone(customerUid: String,phone:String){
        customerTable.document(customerUid).update("phone",phone)
    }

    fun deleteFireStoreCustomer(customerUid: String){
        customerTable.document(customerUid).delete()
    }

    fun writeFireStoreCustomer(customer:Customer){
        customerTable.document(customer.uid!!).set(customer)
    }
}