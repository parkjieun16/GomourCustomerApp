package com.santaistiger.gomourcustomerapp.ui.view

/**
 * Created by Jangeunhye
 */
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.santaistiger.gomourcustomerapp.R
import com.santaistiger.gomourcustomerapp.data.model.Place
import com.santaistiger.gomourcustomerapp.data.repository.Repository
import com.santaistiger.gomourcustomerapp.data.repository.RepositoryImpl
import com.santaistiger.gomourcustomerapp.databinding.FragmentLoginBinding
import com.santaistiger.gomourcustomerapp.ui.base.BaseActivity
import com.santaistiger.gomourcustomerapp.ui.customview.RoundedAlertDialog
import com.santaistiger.gomourcustomerapp.ui.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.coroutines.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint

class LoginFragment : Fragment() {

    private var auth: FirebaseAuth? = null
    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // 툴바 설정
        (requireActivity() as BaseActivity).setToolbar(
            requireContext(), false, null, false
        )

        auth = Firebase.auth
        binding = DataBindingUtil.inflate<FragmentLoginBinding>(
            inflater,
            R.layout.fragment_login,
            container,
            false
        )
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        binding.emailLogin.addTextChangedListener(mTextWatcher) // 이메일 입력 감지
        binding.passwordLogin.addTextChangedListener(mTextWatcher) // 패스워드 입력 감지

        setLoginBtnClickListener()

        //회원가입 페이지로 이동
        binding.goSignUpPage.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_joinFragment)
        }

        return binding.root
    }

    private fun setLoginBtnClickListener() {
        binding.loginButton.setOnClickListener {
            viewModel.email = binding.emailLogin.text.toString()
            viewModel.password = binding.passwordLogin.text.toString()

            // 단국대 이메일이 아니면 로그인
            if (viewModel.email.contains(R.string.dankook_domain.toString())) {
                showAlertDialog(resources.getString(R.string.login_fail_dialog))
            } else {
                login()
            }
        }
    }

    private fun login() {
        binding.loginButton.isClickable = false
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.login().join()
            if (viewModel.loginInfo.value != null) { // 로그인 성공 시
                findNavController().navigate(R.id.action_loginFragment_to_doOrderFragment)
                (requireActivity() as BaseActivity).setNavigationDrawerHeader()

                // 자동로그인을 위해 shared preference에 정보 저장
                setSharedPreference()

            } else { // 로그인 실패 시
                binding.loginButton.isClickable = true
                withContext(Dispatchers.Main) {
                    showAlertDialog(resources.getString(R.string.login_fail_dialog))
                }
            }
        }
    }

    private fun setSharedPreference() {
        val auto = requireActivity()
            .getSharedPreferences("auto", Context.MODE_PRIVATE)
        val autoLogin = auto.edit()
        autoLogin.putString("email", viewModel.email)
        autoLogin.putString("password", viewModel.password)
        autoLogin.commit()

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
        loginButton.isEnabled = !(email == "" || password == "")
    }

    private fun showAlertDialog(msg: String) {
        RoundedAlertDialog()
            .setMessage(msg)
            .setPositiveButton(resources.getString(R.string.ok), null)
            .show(
                (requireActivity() as BaseActivity).supportFragmentManager,
                "rounded alert dialog"
            )
    }
}