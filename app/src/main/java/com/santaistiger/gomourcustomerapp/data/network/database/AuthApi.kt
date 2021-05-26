package com.santaistiger.gomourcustomerapp.data.network.database

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object AuthApi {
    // 로그인
    suspend fun login(firebaseAuth: FirebaseAuth, email: String, password: String): AuthResponse {
        val authResponse = AuthResponse()

        try {
            authResponse.authResult = firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()
        } catch (e: Exception) {
            authResponse.exception = e
        }

        return authResponse
    }

    //회원가입
    suspend fun join(firebaseAuth:FirebaseAuth, email:String, password:String):AuthResponse{
        val authResponse = AuthResponse()
        try{
            authResponse.authResult = firebaseAuth
                .createUserWithEmailAndPassword(email,password)
                .await()
        } catch (e:java.lang.Exception){
            authResponse.exception = e
        }
        return authResponse
    }

    fun writeAuthCustomer(email:String,password: String){
        Firebase.auth.createUserWithEmailAndPassword(email, password)
    }

    fun updateAuthPassword(password:String){
        Firebase.auth.currentUser.updatePassword(password)
    }

    fun deleteAuthCustomer(){
        Firebase.auth.currentUser.delete()
    }

    //customerUid 가져오기
    fun readUid() = Firebase.auth.uid

}