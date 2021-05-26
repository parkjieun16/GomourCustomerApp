package com.santaistiger.gomourcustomerapp.data.network.database

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object AuthApi {

    fun login(email:String, password:String){
        Firebase.auth.signInWithEmailAndPassword(email,password)
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