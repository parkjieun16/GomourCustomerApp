package com.santaistiger.gomourcustomerapp.data.network.database

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object AuthApi {
    fun readUid() = Firebase.auth.uid
}