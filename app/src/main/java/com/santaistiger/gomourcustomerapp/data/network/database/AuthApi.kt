package com.santaistiger.gomourcustomerapp.data.network.database

import android.content.Context
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.santaistiger.gomourcustomerapp.R
import com.santaistiger.gomourcustomerapp.data.repository.Repository
import com.santaistiger.gomourcustomerapp.data.repository.RepositoryImpl
import com.santaistiger.gomourcustomerapp.ui.base.BaseActivity
import kotlinx.coroutines.tasks.await

object AuthApi {

    // 로그인
    suspend fun login(firebaseAuth: FirebaseAuth, email:String, password:String): AuthResult? {
        return try{
            val data = firebaseAuth
                .signInWithEmailAndPassword(email,password)
                .await()
            return data
        }catch (e : Exception){
            return null
        }
    }

    //customerUid 가져오기
    fun readUid() =  Firebase.auth.uid
}