package com.santaistiger.gomourcustomerapp.ui.view

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.santaistiger.gomourcustomerapp.R
import com.santaistiger.gomourcustomerapp.data.repository.Repository
import com.santaistiger.gomourcustomerapp.data.repository.RepositoryImpl
import com.santaistiger.gomourcustomerapp.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoadingFragment: Fragment() {
    private val repository: Repository = RepositoryImpl

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setToolbar()

        startLoading()
        return inflater.inflate(R.layout.fragment_loading, container, false)
    }

    private fun setToolbar() {
        requireActivity().apply {
            toolbar.visibility = View.GONE	// 툴바 숨기기
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED) // 스와이프 비활성화
        }
    }

    // 2초 대기
    private fun startLoading() {
        CoroutineScope(Dispatchers.IO).launch {
            auth().join()
        }
    }

    private fun auth() = CoroutineScope(Dispatchers.IO).launch {
        val auto = requireActivity().getSharedPreferences("auto", Context.MODE_PRIVATE)
        val loginEmail = auto.getString("email", null)
        val loginPwd = auto.getString("password", null)
        if (loginEmail != null && loginPwd != null) {
            Firebase.auth?.signInWithEmailAndPassword(loginEmail, loginPwd)?.addOnSuccessListener {
                findNavController().navigate(R.id.action_loadingFragment_to_doOrderFragment)
                (activity as BaseActivity).setNavigationDrawerHeader()  // 네비게이션 드로어 헤더 설정
            }
        }
        else {
            // 저장된 로그인 정보가 없을 시 로그인 페이지로 이동
            findNavController().navigate(R.id.action_loadingFragment_to_loginFragment)
        }
    }

//    // 로그인 확인
//    private fun auth() = CoroutineScope(Dispatchers.IO).launch {
//        var auth = Firebase.auth
//        val auto = requireActivity().getSharedPreferences("auto", Context.MODE_PRIVATE)
//        val loginEmail = auto.getString("email", null)
//        val loginPwd = auto.getString("password", null)
//        if (loginEmail != null && loginPwd != null) {
//            auth = Firebase.auth
//            auth?.signInWithEmailAndPassword(loginEmail, loginPwd)?.addOnSuccessListener {
//                findNavController().navigate(R.id.action_loadingFragment_to_doOrderFragment)
//                (activity as BaseActivity).setNavigationDrawerHeader()  // 네비게이션 드로어 헤더 설정
//            }
//        }
//        else {
//            // 저장된 로그인 정보가 없을 시 로그인 페이지로 이동
//            findNavController().navigate(R.id.action_loadingFragment_to_loginFragment)
//        }
//    }


}