package com.santaistiger.gomourcustomerapp.ui.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
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
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.santaistiger.gomourcustomerapp.R
import com.santaistiger.gomourcustomerapp.data.model.Customer
import com.santaistiger.gomourcustomerapp.databinding.FragmentModifyUserInfoBinding
import com.santaistiger.gomourcustomerapp.ui.base.BaseActivity
import com.santaistiger.gomourcustomerapp.ui.viewmodel.ModifyUserInfoViewModel
import kotlinx.android.synthetic.main.activity_base.*
import java.util.regex.Pattern

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
class ModifyUserInfoFragment : Fragment() {

    private var auth: FirebaseAuth? = null
    private lateinit var binding: FragmentModifyUserInfoBinding
    private lateinit var viewModel: ModifyUserInfoViewModel
    val db = Firebase.firestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setToolbar()

        auth = Firebase.auth
        binding = DataBindingUtil.inflate<FragmentModifyUserInfoBinding>(inflater,R.layout.fragment_modify_user_info,container,false)
        viewModel = ViewModelProvider(this).get(ModifyUserInfoViewModel::class.java)
        val currentUser = auth?.currentUser
        if (currentUser != null) {
            val docRef = db.collection("customer").document(currentUser.uid)
            docRef.get().addOnSuccessListener { documentSnapshot ->
                val data = documentSnapshot.toObject<Customer>()
                if (data != null) {
                    binding.nameModify.text = data.name
                    binding.emailModify.text = data.email
                    binding.passwordModify.setText(data.password)
                    binding.passwordCheckModify.setText(data.password)
                    binding.phoneModify.setText(data.phone)
                }
            }


            //비어 있지 않을 때 비밀번호 감지하여 판단
            binding.passwordCheckModify.addTextChangedListener(passwordCheckChangeWatcher)
            binding.passwordModify.addTextChangedListener(passwordChangeWatcher)

            // 변경완료 버튼 클릭 시
            binding.modifyButton.setOnClickListener{
                val password:String =binding.passwordModify.text.toString()
                val passwordCheck: String  = binding.passwordCheckModify.text.toString()

                if (passwordCheck(passwordCheck) && password(password)){
                    modifyUser()
                }
                else{
                    Toast.makeText(context,"정보를 다시 입력하세요", Toast.LENGTH_LONG).show()
                }
            }

            //탈퇴 버튼 클릭 시
            binding.withdrawalButton.setOnClickListener {
                alertCancel()
            }
        }
        return binding.root
    }


    private fun setToolbar() {
        requireActivity().apply {
            toolbar.visibility = View.VISIBLE     // 툴바 보이도록 설정
            toolbar_title.setText("회원 정보 변경")     // 툴바 타이틀 변경
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)  // 스와이프 활성화
        }
    }


    //비밀번호 변경될때마다 인식
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

    //비밀번호 체크 변경될때마다 인식
    private val passwordCheckChangeWatcher = object : TextWatcher {
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


    //패스워드 제한
    private fun password(password: CharSequence):Boolean{
        val passwordCheck = binding.passwordCheckModify.text.toString()
        val pwPattern = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@\$%^&*-]).{8,16}\$"
        if (Pattern.matches(pwPattern, password)){
            if(password== passwordCheck){
                binding.passwordModifyValid.visibility = View.VISIBLE
                binding.passwordModifyValid.text = "사용할 수 있는 비밀번호입니다."
            }
            else{
                binding.passwordModifyValid.visibility = View.VISIBLE
                binding.passwordModifyValid.text = "비밀번호가 같지 않습니다."

            }

            return true
        }
        else{
            binding.passwordModifyValid.visibility = View.VISIBLE
            binding.passwordModifyValid.text = "비밀번호는 대,소문자,숫자,특수문자 포함 8~16자여야합니다."
            return false
        }
    }

    //패스워드 체크 제한 확인
    private fun passwordCheck(s: CharSequence):Boolean{
        val password = binding.passwordModify.text.toString()
        val pwPattern = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@\$%^&*-]).{8,16}\$"
        if(password == s.toString()) {
            if(Pattern.matches(pwPattern, password))
            {
                binding.passwordModifyValid.visibility = View.VISIBLE
                binding.passwordModifyValid.text = "사용할 수 있는 비밀번호입니다."
                return true
            }
            else{
                binding.passwordModifyValid.visibility = View.VISIBLE
                binding.passwordModifyValid.text = "비밀번호는 대,소문자,숫자,특수문자 포함 8~16자여야합니다."
                return false
            }
        }
        else{
            binding.passwordModifyValid.visibility = View.VISIBLE
            binding.passwordModifyValid.text = "비밀번호가 같지 않습니다."
            return false
        }
    }

    // 회원정보 변경 시
    private fun modifyUser(){
        val currentUser = auth?.currentUser
        // Add a new document with a generated ID

        val password = binding.passwordModify.text.toString()
        val phone = binding.phoneModify.text.toString()

        if (currentUser != null) {
            currentUser.updatePassword(password)
                .addOnSuccessListener { Toast.makeText(context,"비밀번호 변경 성공",Toast.LENGTH_LONG).show() }
                .addOnFailureListener {  Toast.makeText(context,"비밀번호 변경 실패",Toast.LENGTH_LONG).show()}

            db.collection("customer")
                .document(currentUser.uid!!)
                .update("password",password)
                .addOnSuccessListener { documentReference ->

                }
                .addOnFailureListener { e ->
                    Log.w("TEST", "Error adding document", e)
                }

            db.collection("customer")
                .document(currentUser.uid!!)
                .update("phone",phone)
                .addOnSuccessListener { documentReference ->
                    val auto = this.requireActivity()
                        .getSharedPreferences("auto", Context.MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = auto.edit()
                    editor.clear()
                    editor.commit()
                    //findNavController().navigate(R.id.action_modifyUserInfoFragment_to_loginFragment)
                }
                .addOnFailureListener { e ->
                    Log.w("TEST", "Error adding document", e)
                }
        }
        else{
            findNavController().navigate(R.id.action_modifyUserInfoFragment_to_loginFragment)
        }

        (activity as BaseActivity).setNavigationDrawerHeader()  // 네비게이션 드로어 헤더 설정
    }




    fun alertCancel() {
        AlertDialog.Builder(requireActivity())
            .setMessage("정말 탈퇴하시겠습니까?")
            .setPositiveButton("예") { _, _ ->
                //탈퇴
                withdrawal()
            }
            .setNegativeButton("아니오", null)
            .create()
            .show()
    }

    //탈퇴
    @SuppressLint("RestrictedApi")
    private fun withdrawal(){
        var currentUser = Firebase.auth.currentUser
        var uid = currentUser.uid
        currentUser.delete()
            .addOnFailureListener {
                Toast.makeText(context,"탈퇴 실패",Toast.LENGTH_LONG).show()
            }
            .addOnSuccessListener {
            }

        db.collection("customer").document(uid)
            .delete()
            .addOnSuccessListener {

                Toast.makeText(context,"탈퇴 성공", Toast.LENGTH_LONG).show()
                //자동로그인 삭제
                val auto = this.requireActivity()
                    .getSharedPreferences("auto", Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = auto.edit()
                editor.clear()
                editor.commit()

                // 백스택 제거
                for (i in 1..findNavController().backStack.count()) {
                    findNavController().popBackStack()
                }

                // 로그인 페이지로 이동
                findNavController().navigate(R.id.loginFragment)
            }
            .addOnFailureListener {Toast.makeText(context,"탈퇴 실패",Toast.LENGTH_LONG).show() }
    }
}