package com.santaistiger.gomourcustomerapp.ui.join

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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.santaistiger.gomourcustomerapp.R
import com.santaistiger.gomourcustomerapp.data.model.Customer
import com.santaistiger.gomourcustomerapp.databinding.FragmentJoinBinding
import kotlinx.android.synthetic.main.activity_base.*
import java.util.regex.Pattern

class JoinFragment : Fragment() {
    private var auth: FirebaseAuth? = null
    private lateinit var binding: FragmentJoinBinding
    private lateinit var viewModel: JoinViewModel
    val db = Firebase.firestore

    // 모든 영역이 채워져있는지 있는지 감지
    private val mTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
        override fun afterTextChanged(editable: Editable) {
            checkFieldsForEmptyValues()
        }
    }

    //이메일 변경될 때 마다 인식
    private val emailChageWatcher = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            binding.emailCheckButton.setEnabled(true)
        }
    }

    //비밀번호 변경될때마다 인식
    private val passwordChageWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s != null) {
                passwordCheck(s)
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setToolbar()
        auth = Firebase.auth
        binding = DataBindingUtil.inflate<FragmentJoinBinding>(inflater,
            R.layout.fragment_join,container,false)
        viewModel = ViewModelProvider(this).get(JoinViewModel::class.java)

        // 비어 있지 않을 때 이메일 중복확인 버튼 나오게하기
        binding.emailEditText.addTextChangedListener(emailChageWatcher)

        // 비어 있지 않을 때 비밀번호 감지하여 판단
        binding.passwordCheckEditText.addTextChangedListener(passwordChageWatcher)

        //비어 있지 않을 때 판단
        binding.emailEditText.addTextChangedListener(mTextWatcher)
        binding.nameEditText.addTextChangedListener(mTextWatcher)
        binding.passwordCheckEditText.addTextChangedListener(mTextWatcher)
        binding.passwordEditText.addTextChangedListener(mTextWatcher)
        binding.PhoneEditText.addTextChangedListener(mTextWatcher)

        //이메일 중복 버튼
        binding.emailCheckButton.setOnClickListener {
            emailDuplicateCheck(binding.emailEditText.text.toString())
        }

        //회원가입버튼
        binding.signUpButton.setOnClickListener{
            val email:String = binding.emailEditText.text.toString()
            val password:String =binding.passwordEditText.text.toString()
            val passwordCheck: String  = binding.passwordCheckEditText.text.toString()
            val name: String = binding.nameEditText.text.toString()
            val phone = binding.PhoneEditText.text.toString()
            //uid는 회원가입완료 후 생성됨. 일단은 임시로 ..!
            val uid = ""
            var customer = Customer(email, password, name, phone, uid )
            Log.d("Test","EamilValieCheckh"+emailValidCheck().toString())
            Log.d("Test", passwordCheck(passwordCheck).toString())

            if(emailValidCheck() && passwordCheck(passwordCheck)&& emailDuplicateCheck(email)){
                createAccount(customer, binding.passwordCheckEditText.text.toString())
            }
            else{
                Toast.makeText(context,"정보를 다시 입력하세요", Toast.LENGTH_LONG).show()
            }
        }

        return binding.root
    }

    private fun setToolbar() {
        requireActivity().apply {
            toolbar.visibility = View.GONE	// 툴바 숨기기
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED) // 스와이프 비활성화
        }
    }

    //패스워드 제한 확인
    private fun passwordCheck(s: CharSequence):Boolean{
        val password = binding.passwordEditText.text.toString()
        val pwPattern = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$"

        if(password == s.toString()){
            if(Pattern.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@\$%^&*-]).{8,16}\$", password))
            {
                binding.passwordValid.visibility = View.VISIBLE
                binding.passwordValid.text = "사용할 수 있는 비밀번호입니다."
                return true
            }
            else{
                binding.passwordValid.visibility = View.VISIBLE
                binding.passwordValid.text = "비밀번호는 대,소문자,숫자,특수문자 포함 8~16자여야합니다."
                return false
            }
        }
        else{
            binding.passwordValid.visibility = View.VISIBLE
            binding.passwordValid.text = "비밀번호가 같지 않습니다."
            return false
        }
    }



    //모든 영역 널이 아닌지 확인
    fun checkFieldsForEmptyValues() {
        val signUpButton = binding.signUpButton
        val email = binding.emailEditText.text.toString()
        val password: String = binding.passwordEditText.text.toString()
        val passwordCheck: String = binding.passwordCheckEditText.text.toString()
        val phone: String = binding.PhoneEditText.text.toString()

        if (email == "" || password == "" || passwordCheck=="" || phone=="") {
            signUpButton.isEnabled = false
        } else {
            signUpButton.isEnabled = true
        }
    }


    //이메일 형식 체크
    private fun emailValidCheck():Boolean{
        var returnvalue = true
        val email = binding.emailEditText.text .toString()
        //이메일 형식이 맞지 않을 경우
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailValid.visibility = View.VISIBLE
            binding.emailValid.text = "이메일 형식이 아닙니다."
            returnvalue = false
            Log.d("Test", "이메일형식이 맞지 않을경우" + returnvalue.toString())
        }
        //이메일 형식이 맞는 경우
        else{
            //단국이메일인경우
            if (email.contains("@dankook.ac.kr"))
            {
                binding.emailValid.visibility = View.VISIBLE
                binding.emailValid.text = "학교 이메일은 사용할 수 없습니다."
                returnvalue = false
                Log.d("Test", "단국 이메일일 경우" + returnvalue.toString())
            }
            else{
                Log.d("Test", "이메일 형식 오케이"+returnvalue.toString())
                returnvalue = true
            }

        }
        Log.d("Test", "마지막"+returnvalue.toString())
        return returnvalue
    }


    //이메일 중복 확인 버튼
    private fun emailDuplicateCheck(email:String):Boolean{
        var return_value = false
        if(emailValidCheck()){
            Firebase.firestore?.collection("customer").whereEqualTo("email",email).get()
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        for(dc in it.result!!.documents){
                            binding.emailValid.visibility = View.VISIBLE
                            binding.emailValid.text = "이미 사용 중인 이메일입니다."
                            return_value = false
                            Log.d("Test", "이미 사용중인 이메일입니다." + return_value.toString())
                        }
                        if (it.result!!.documents.isEmpty()){
                            binding.emailValid.visibility = View.VISIBLE
                            binding.emailValid.text = "사용할 수 있는 이메일입니다."
                            return_value = true
                            Log.d("Test", "사용가능 이메일입니다." + return_value.toString())
                        }
                    }
                }
            return return_value
        }
        else{
            Log.d("Test", "이메일 형식이 맞지않는걸?" + return_value.toString())
            return return_value
        }
    }


    //회원가입
    private fun createAccount(customer: Customer,passwordCheck:String){
        if (viewModel.Validation == true && passwordCheck(passwordCheck)){
            Log.d("Test","굳")
        }
        auth?.createUserWithEmailAndPassword(customer.email, customer.password)
            ?.addOnCompleteListener() {
                    task->
                if(task.isSuccessful){
                    Log.d("TEST","createUserWithEmail: success")
                    val user = auth!!.currentUser
                    customer.uid = user.uid
                    // Add a new document with a generated ID
                    db.collection("customer")
                        .document(customer.uid!!).set(customer)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(context,"firestore에 정보 전달 성공",Toast.LENGTH_LONG).show()
                            findNavController().navigate(R.id.action_joinFragment_to_doOrderFragment)
                        }
                        .addOnFailureListener { e ->
                            Log.w("TEST", "Error adding document", e)
                        }
                }
                else{
                    Log.w("TEST","createUserWithEmail: failure",task.exception )
                    Toast.makeText(context, "Authentication failed.", Toast.LENGTH_LONG).show()
                }
            }

    }






}