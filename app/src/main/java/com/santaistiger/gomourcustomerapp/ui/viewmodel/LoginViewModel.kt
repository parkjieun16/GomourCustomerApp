package com.santaistiger.gomourcustomerapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.santaistiger.gomourcustomerapp.R
import com.santaistiger.gomourcustomerapp.data.repository.Repository
import com.santaistiger.gomourcustomerapp.data.repository.RepositoryImpl
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class LoginViewModel:ViewModel() {
    private var repository: Repository = RepositoryImpl
    private var auth = Firebase.auth

    var email = String()
    var password = String()
    var login_state = MutableLiveData<Boolean>()

    fun login() {
        viewModelScope.async{
            login_state.value = repository.login(auth!!,email,password)
        }
    }

}