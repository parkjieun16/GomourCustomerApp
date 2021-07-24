package com.santaistiger.gomourcustomerapp.ui.view

/**
 * Created by Jangeunhye
 */
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.santaistiger.gomourcustomerapp.R
import com.santaistiger.gomourcustomerapp.data.model.Customer
import com.santaistiger.gomourcustomerapp.data.repository.Repository
import com.santaistiger.gomourcustomerapp.data.repository.RepositoryImpl
import com.santaistiger.gomourcustomerapp.databinding.FragmentJoinBinding
import com.santaistiger.gomourcustomerapp.ui.base.BaseActivity
import com.santaistiger.gomourcustomerapp.ui.customview.RoundedAlertDialog
import com.santaistiger.gomourcustomerapp.ui.viewmodel.JoinViewModel
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.fragment_join.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.regex.Pattern


class JoinFragment : Fragment() {
    private var auth: FirebaseAuth? = null
    private lateinit var binding: FragmentJoinBinding
    private lateinit var viewModel: JoinViewModel
    private val repository: Repository = RepositoryImpl
    var isUniqueEmail = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // 툴바 설정
        (requireActivity() as BaseActivity).setToolbar(
            requireContext(), false, null, false
        )

        auth = Firebase.auth
        binding = DataBindingUtil.inflate<FragmentJoinBinding>(
            inflater,
            R.layout.fragment_join, container, false
        )
        viewModel = ViewModelProvider(this).get(JoinViewModel::class.java)

        binding.apply {

            // 비어 있지 않을 때 이메일 중복확인 버튼 나오게하기
            emailEditText.addTextChangedListener(emailChangeWatcher)

            // 비어 있지 않을 때 비밀번호 감지하여 판단
            passwordCheckEditText.addTextChangedListener(passwordCheckChangeWatcher)
            passwordEditText.addTextChangedListener(passwordChangeWatcher)

            // 가입버튼을 위해 비어있는지 확인
            emailEditText.addTextChangedListener(mTextWatcher)
            nameEditText.addTextChangedListener(mTextWatcher)
            passwordCheckEditText.addTextChangedListener(mTextWatcher)
            passwordEditText.addTextChangedListener(mTextWatcher)
            PhoneEditText.addTextChangedListener(mTextWatcher)
        }


        // 이메일 중복 버튼
        binding.emailCheckButton.setOnClickListener {
            viewModel.email = binding.emailEditText.text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                val deferredCheckResult = async { viewModel.duplicateCheck() }
                val checkResult: Boolean = deferredCheckResult.await()

                launch(Dispatchers.Main) {
                    if (checkResult) emailValidCorrect() //사용 가능함
                    else emailValidWrong() //이미 사용하고 있음
                }
            }
        }

        // 회원가입버튼
        binding.signUpButton.setOnClickListener {
            val email: String = binding.emailEditText.text.toString()
            val password: String = binding.passwordEditText.text.toString()
            val passwordCheck: String = binding.passwordCheckEditText.text.toString()
            val name: String = binding.nameEditText.text.toString()
            val phone = binding.PhoneEditText.text.toString()

            val customer = Customer(email, password, name, phone, null)
            if (isUniqueEmail) {
                if (password(password) && passwordEqual(passwordCheck)) {
                    createAccount(customer)
                }
            } else {
                showAlertDialog(resources.getString(R.string.join_email_check_info))
            }
        }
        return binding.root
    }


    // 모든 영역이 채워져있는지 있는지 감지
    private val mTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
        override fun afterTextChanged(editable: Editable) {
            checkFieldsForEmptyValues()
        }
    }

    // 이메일 변경될 때 마다 인식
    private val emailChangeWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {
            isUniqueEmail = false
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            emailValidCheck()
        }
    }

    // 비밀번호 변경될때마다 인식
    private val passwordChangeWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s != null) {
                password(s)
            }
        }
    }


    // 비밀번호 체크 변경될때마다 인식
    private val passwordCheckChangeWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s != null) {
                passwordEqual(s)
            }
        }
    }

    // 패스워드 체크 제한
    private fun password(password: CharSequence): Boolean {
        val pwPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d~!@#\$%^&*()+|=]{8,16}\$"

        return if (Pattern.matches(pwPattern, password)) {
            binding.passwordValid.visibility = View.GONE
            true
        } else {
            // 비밀번호 형식 맞지 않을떄
            binding.passwordValid.setTextColor(Color.parseColor("#FFF44336"))
            binding.passwordValid.visibility = View.VISIBLE
            binding.passwordValid.setText(R.string.password_form_info)
            false
        }
    }

    private fun passwordEqual(passwordCheck: CharSequence): Boolean {
        val password = binding.passwordEditText.text.toString()
        return if (password == passwordCheck.toString()) {
            binding.passwordValid.setTextColor(Color.parseColor("#000000"))
            binding.passwordValid.visibility = View.VISIBLE
            binding.passwordValid.setText(R.string.password_available_info)
            true

        } // 비밀번호 같지 않을 때
        else {
            binding.passwordValid.setTextColor(Color.parseColor("#FFF44336"))
            binding.passwordValid.visibility = View.VISIBLE
            binding.passwordValid.setText(R.string.password_different_info)
            false
        }
    }


    // 모든 영역 널이 아닌지 확인
    fun checkFieldsForEmptyValues() {
        val signUpButton = binding.signUpButton
        val email = binding.emailEditText.text.toString()
        val password: String = binding.passwordEditText.text.toString()
        val passwordCheck: String = binding.passwordCheckEditText.text.toString()
        val phone: String = binding.PhoneEditText.text.toString()

        signUpButton.isEnabled =
            !(email == "" || password == "" || passwordCheck == "" || phone == "")
    }


    // 이메일 형식 체크
    private fun emailValidCheck(): Boolean {
        val email = binding.emailEditText.text.toString()

        // 이메일 형식이 맞지 않을 경우
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailValid.visibility = View.VISIBLE
            binding.emailValid.setTextColor(Color.parseColor("#FFF44336"))
            binding.emailValid.setText(R.string.join_email_form_info)
            emailCheckButton.isEnabled = false
            return false
        }
        // 이메일 형식이 맞는 경우
        else {
            binding.emailValid.visibility = View.GONE

            return if (email.contains(R.string.dankook_domain.toString())) { //단국대학교 이메일인경우
                binding.emailValid.visibility = View.VISIBLE
                binding.emailValid.setTextColor(Color.parseColor("#FFF44336"))
                binding.emailValid.setText(R.string.dankook_domain_info)
                emailCheckButton.isEnabled = false
                false

            } //사용가능
            else {
                binding.emailValid.visibility = View.GONE
                emailCheckButton.isEnabled = true
                true
            }
        }
    }


    // 사용중인 이메일
    private fun emailValidWrong() {
        binding.emailValid.visibility = View.VISIBLE
        binding.emailValid.setTextColor(Color.parseColor("#FFF44336")) //레드
        binding.emailValid.setText(R.string.join_email_duplicate_info)
        Toast.makeText(context, R.string.confirm_fail, Toast.LENGTH_LONG).show()

    }

    // 사용 가능한 이메일
    private fun emailValidCorrect() {
        binding.emailValid.visibility = View.VISIBLE
        binding.emailValid.setTextColor(Color.parseColor("#000000"))
        binding.emailValid.setText(R.string.join_email_available_info)
        isUniqueEmail = true
    }

    //회원가입
    private fun createAccount(customer: Customer) {
        viewModel.email = binding.emailEditText.text.toString()
        viewModel.password = binding.passwordCheckEditText.text.toString()
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.join().join()
            if (viewModel.joinInfo.value != null) {

                // 회원가입
                customer.uid = repository.getUid()
                repository.writeFireStoreCustomer(customer)

                // 자동로그인에 저장
                val auto = requireActivity()
                    .getSharedPreferences("auto", Context.MODE_PRIVATE)
                val autoLogin = auto.edit()
                autoLogin.putString("email", customer.email)
                autoLogin.putString("password", customer.password)
                autoLogin.apply()
                findNavController().navigate(R.id.action_joinFragment_to_doOrderFragment)
                (requireActivity() as BaseActivity).setNavigationDrawerHeader()
            } else {
                launch(Dispatchers.Main) {
                    Toast.makeText(context, R.string.join_fail_info, Toast.LENGTH_LONG).show()
                }
            }
        }
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