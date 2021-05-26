package com.santaistiger.gomourcustomerapp.data.network.database

import com.google.firebase.auth.AuthResult

data class AuthResponse(
    var authResult: AuthResult? = null,
    var exception: Exception? = null
)