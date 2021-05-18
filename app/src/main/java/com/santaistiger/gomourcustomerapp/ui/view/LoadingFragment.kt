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
import com.santaistiger.gomourcustomerapp.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_base.*

class LoadingFragment: Fragment() {

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
        val handler = Handler()
        handler.postDelayed(Runnable {
            auth()

        }, 2000)
    }

    // 로그인 확인
    private fun auth() {

        var auth = Firebase.auth
        val auto = this.requireActivity().getSharedPreferences("auto", Context.MODE_PRIVATE)
        val loginEmail = auto.getString("email", null)
        val loginPwd = auto.getString("password", null)
        if (loginEmail != null && loginPwd != null) {
            auth = Firebase.auth
            auth?.signInWithEmailAndPassword(loginEmail, loginPwd)?.addOnSuccessListener {
                findNavController().navigate(R.id.action_loadingFragment_to_doOrderFragment)
                Toast.makeText(context, "안녕", Toast.LENGTH_LONG).show()
                (activity as BaseActivity).setNavigationDrawerHeader()
            }
        }
        else {
            // 저장된 로그인 정보가 없을 시 로그인 페이지로 이동
            findNavController().navigate(R.id.action_loadingFragment_to_loginFragment)
        }

    }


}