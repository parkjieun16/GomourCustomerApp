package com.santaistiger.gomourcustomerapp.ui.viewmodel
/**
 * Created by Jangeunhye
 */
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.santaistiger.gomourcustomerapp.data.repository.Repository
import com.santaistiger.gomourcustomerapp.data.repository.RepositoryImpl
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private var repository: Repository = RepositoryImpl
    private var auth = Firebase.auth

    var email = String()
    var password = String()
    val loginInfo = MutableLiveData<AuthResult>()

    fun login() =
        viewModelScope.launch { loginInfo.value = repository.login(auth!!, email, password) }


}