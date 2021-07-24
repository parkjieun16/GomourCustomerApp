package com.santaistiger.gomourcustomerapp.ui.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.kakao.util.maps.helper.Utility
import com.pedro.library.AutoPermissions
import com.santaistiger.gomourcustomerapp.R
import com.santaistiger.gomourcustomerapp.data.model.Customer
import com.santaistiger.gomourcustomerapp.data.repository.Repository
import com.santaistiger.gomourcustomerapp.data.repository.RepositoryImpl
import com.santaistiger.gomourcustomerapp.databinding.ActivityBaseBinding
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.nav_header.view.*
import java.util.*

class BaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    companion object {
        private val TAG = BaseActivity::class.java.canonicalName
    }

    private lateinit var binding: ActivityBaseBinding
    private val repository: Repository = RepositoryImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.activity_base,
            null,
            false
        )

        Log.d(TAG, "hash key: ${Utility.getKeyHash(this)}")
        setContentView(binding.root)

        // Auto permission
        AutoPermissions.loadAllPermissions(this, 1)

        setToolbar() // 툴바 설정
        navigation_view.setNavigationItemSelectedListener(this)     // navigation 리스너 설정
    }

    // 툴바 설정
    private fun setToolbar() {
        setSupportActionBar(toolbar)    // 툴바를 액티비티의 앱바로 지정
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)   // 툴바 홈버튼 활성화
            setHomeAsUpIndicator(R.drawable.hamburger_btn)     // 홈버튼 이미지 변경
            setDisplayShowTitleEnabled(false)     // 툴바에 앱 타이틀 보이지 않도록 설정
        }
    }

    /** 각 fragment의 툴바를 설정하는 함수 */
    fun setToolbar(context: Context, isVisible: Boolean, title: String?, isSwapable: Boolean) {
        context.apply {
            val swapable = when (isSwapable) {
                true -> DrawerLayout.LOCK_MODE_UNLOCKED
                false -> DrawerLayout.LOCK_MODE_LOCKED_CLOSED
            }

            toolbar.visibility = when (isVisible) {
                true -> View.VISIBLE
                false -> View.GONE
            }
            binding.toolbarTitle.text = title ?: String()
            binding.drawerLayout.setDrawerLockMode(swapable)
        }
    }


    // 네비게이션 드로어 헤더에 현재 로그인한 회원 정보 설정
    fun setNavigationDrawerHeader() {
        val tmpUid = repository.getUid()
        Log.d(TAG, "uid: $tmpUid")
        val header = navigation_view.getHeaderView(0)
        val docRef = Firebase.firestore.collection("customer").document(tmpUid)
        docRef.get().addOnSuccessListener { documentSnapshot ->
            val currentUserInfo = documentSnapshot.toObject<Customer>()
            if (currentUserInfo != null) {
                header.apply {
                    user_name_string.text = currentUserInfo.name
                    user_phone_num_string.text =
                        PhoneNumberUtils.formatNumber(
                            currentUserInfo.phone,
                            Locale.getDefault().country
                        )
                    user_email_string.text = currentUserInfo.email
                }
            }
        }
    }

    // 메뉴 클릭 시 동작 정의
    @SuppressLint("RestrictedApi")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val navController = nav_host_fragment.findNavController()

        // 회원 정보 변경 이외의 메뉴 클릭 시 백스택 제거하여 취소 버튼 누르면 바로 앱 종료되도록
        if (item.itemId != R.id.modifyUserInfoFragment) {
            for (i in 1..navController.backStack.count()) {
                navController.popBackStack()
            }
        }

        when (item.itemId) {
            // 주문하기 클릭 시
            R.id.doOrderFragment -> navController.navigate(R.id.doOrderFragment)

            // 주문 목록 클릭 시
            R.id.orderListFragment -> navController.navigate(R.id.orderListFragment)

            // 회원 정보 변경 클릭 시
            R.id.modifyUserInfoFragment -> navController.navigate(R.id.modifyUserInfoFragment)

            // 로그아웃 클릭 시
            R.id.logout -> {

                // 현재 사용자 가져와서 로그아웃
                var auth = Firebase.auth
                auth?.signOut()

                // 자동로그인 삭제
                val auto = getSharedPreferences("auto", Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = auto.edit()
                editor.clear()
                editor.commit()

                // 로그인 화면으로 이동
                navController.navigate(R.id.loginFragment)
            }
        }
        drawer_layout.closeDrawers()
        return false
    }

    // 툴바의 홈버튼 누르면 네비게이션 드로어 열리도록 설정
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawer_layout.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 뒤로가기 버튼 정의
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }
}