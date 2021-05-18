package com.santaistiger.gomourcustomerapp.ui.login

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.santaistiger.gomourcustomerapp.R
import com.santaistiger.gomourcustomerapp.databinding.FragmentLoginBinding
import kotlinx.android.synthetic.main.activity_base.*

/**
 *
 */
class LoginFragment : Fragment() {

    private var auth: FirebaseAuth? = null
    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setToolbar()

        auth = Firebase.auth
        binding = DataBindingUtil.inflate<FragmentLoginBinding>(inflater,R.layout.fragment_login,container,false)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)


        binding.emailLogin.addTextChangedListener(mTextWatcher) // 이메일 입력 감지
        binding.passwordLogin.addTextChangedListener(mTextWatcher) // 패스워드 입력 감지


        //로그인
        binding.loginButton.setOnClickListener{
            val email = binding.emailLogin.text.toString()
            val password = binding.passwordLogin.text.toString()
            signIn(email,password)
        }

        //회원가입 페이지로 이동
        binding.goSignUpPage.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_joinFragment)
        }


        return binding.root
    }

    private fun setToolbar() {
        requireActivity().apply {
            toolbar.visibility = View.GONE	// 툴바 숨기기
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED) // 스와이프 비활성화
        }
    }

    // 빈칸 감지
    private val mTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
        override fun afterTextChanged(editable: Editable) {
            checkFieldsForEmptyValues()
        }
    }

    // 이메일 패스워드 입력 유무에 따라 로그인 버튼 활성화
    fun checkFieldsForEmptyValues() {
        val loginButton = binding.loginButton
        val email = binding.emailLogin.text.toString()
        val password: String = binding.passwordLogin.text.toString()
        if (email == "" || password == "") {
            loginButton.isEnabled = false
        } else {
            loginButton.isEnabled = true
        }
    }

    // 로그인
    private fun signIn(email:String, password:String){
        // 단국이메일인 경우
        if ( email.contains("@dankook.ac.kr")){
            alertCancel()
        }
        else{
            auth?.signInWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(){ task ->
                    if(task.isSuccessful){
                        val user = auth!!.currentUser

                        // 자동로그인을 위해 shared preference에 정보 저장
                        val auto = this.requireActivity()
                            .getSharedPreferences("auto", Context.MODE_PRIVATE)
                        val autoLogin = auto.edit()
                        autoLogin.putString("email",email)
                        autoLogin.putString("password",password)
                        autoLogin.commit()

                        //로그인 후 주문하기 페이지로 이동
                        findNavController().navigate(R.id.action_loginFragment_to_doOrderFragment)
                    }
                }
                    // 로그인 실패시
                ?.addOnFailureListener {
                    alertCancel()
                }
        }
    }

    fun alertCancel() {
        AlertDialog.Builder(requireActivity())
            .setMessage("로그인에 실패하였습니다.")
            .setPositiveButton("확인",null)
            .create()
            .show()
    }





}