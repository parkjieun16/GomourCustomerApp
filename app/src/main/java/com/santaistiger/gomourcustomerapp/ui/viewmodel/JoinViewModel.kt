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
import com.santaistiger.gomourcustomerapp.data.model.Customer
import com.santaistiger.gomourcustomerapp.data.repository.Repository
import com.santaistiger.gomourcustomerapp.data.repository.RepositoryImpl
import kotlinx.coroutines.launch

class JoinViewModel: ViewModel() {
    private var repository: Repository = RepositoryImpl
    private var auth = Firebase.auth

    var email = String()
    var password = String()
    val joinInfo = MutableLiveData<AuthResult>()

    fun join() =
        viewModelScope.launch { joinInfo.value = repository.join(auth!!, email, password) }

    suspend fun duplicateCheck(): Boolean {
        return repository.checkJoinable(email)
    }
}