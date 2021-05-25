package com.santaistiger.gomourcustomerapp.data.network.database

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
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

    //customerUid 가져오기
    fun readUid() = Firebase.auth.uid
}