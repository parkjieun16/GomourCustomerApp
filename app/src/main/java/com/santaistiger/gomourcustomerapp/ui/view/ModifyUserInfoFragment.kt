package com.santaistiger.gomourcustomerapp.ui.view

/**
 * Created by Jangeunhye
 */

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
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
import com.santaistiger.gomourcustomerapp.data.repository.Repository
import com.santaistiger.gomourcustomerapp.data.repository.RepositoryImpl
import com.santaistiger.gomourcustomerapp.databinding.FragmentModifyUserInfoBinding
import com.santaistiger.gomourcustomerapp.ui.base.BaseActivity
import com.santaistiger.gomourcustomerapp.ui.customview.RoundedAlertDialog
import com.santaistiger.gomourcustomerapp.ui.viewmodel.ModifyUserInfoViewModel
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class ModifyUserInfoFragment : Fragment() {

    private var auth: FirebaseAuth? = null
    private lateinit var binding: FragmentModifyUserInfoBinding
    private lateinit var viewModel: ModifyUserInfoViewModel
    private val repository: Repository = RepositoryImpl


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // ?????? ??????
        (requireActivity() as BaseActivity).setToolbar(
            requireContext(),
            true,
            resources.getString(R.string.toolbar_title_modify_user_info),
            true
        )

        auth = Firebase.auth
        binding = DataBindingUtil.inflate<FragmentModifyUserInfoBinding>(
            inflater,
            R.layout.fragment_modify_user_info,
            container,
            false
        )
        viewModel = ViewModelProvider(this).get(ModifyUserInfoViewModel::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            val customer = async { repository.readCustomerInfo(repository.getUid()) }.await()!!
            displayCustomerInfo(customer)


            binding.apply {

                //?????? ?????? ?????? ??? ???????????? ???????????? ??????
                passwordModify.addTextChangedListener(passwordChangeWatcher)
                passwordCheckModify.addTextChangedListener(passwordCheckChangeWatcher)


                // ???????????? ????????? ????????? ?????????
                passwordModify.setOnFocusChangeListener { v, hasFocus ->
                    if (hasFocus) {
                        passwordModify.text.clear()
                    }
                }
                // ???????????? ????????? ????????? ?????????
                passwordCheckModify.setOnFocusChangeListener { v, hasFocus ->
                    if (hasFocus) {
                        passwordCheckModify.text.clear()
                    }
                }
            }
        }


        // ???????????? ?????? ?????? ???
        binding.modifyButton.setOnClickListener {
            val password = binding.passwordModify.text.toString()
            val passwordCheck = binding.passwordCheckModify.text.toString()
            if (password(password) && passwordEqual(passwordCheck)) {
                modifyUser()
            } else {
                Toast.makeText(context, R.string.confirm_fail, Toast.LENGTH_LONG).show()
            }
        }


        //?????? ?????? ?????? ???
        binding.withdrawalButton.setOnClickListener {
            showAlertDialog(resources.getString(R.string.withdrawal_dialog))
        }


        return binding.root
    }

    //???????????? ?????????????????? ??????
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

    //???????????? ?????? ?????????????????? ??????
    private val passwordCheckChangeWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s != null) {
                passwordEqual(s)
            }
        }
    }

    // ???????????? ?????? ??????
    private fun password(password: CharSequence): Boolean {
        val pwPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d~!@#\$%^&*()+|=]{8,16}$"
        if (Pattern.matches(pwPattern, password)) {
            binding.passwordModifyValid.visibility = View.GONE
            return true
        } else {
            // ???????????? ?????? ?????? ?????????
            passwordValidWrong()
            binding.passwordModifyValid.setText(R.string.password_form_info)
            return false
        }
    }

    private fun passwordEqual(passwordCheck: CharSequence): Boolean {
        val password = binding.passwordModify.text.toString()
        return if (password == passwordCheck.toString()) {
            passwordValidCorrect()
            true
        } else {
            // ???????????? ?????? ?????? ???
            passwordValidWrong()
            binding.passwordModifyValid.setText(R.string.password_different_info)
            false
        }
    }


    // ???????????? ?????? ???
    private fun modifyUser() {
        val currentUser = auth?.currentUser
        // Add a new document with a generated ID

        val password = binding.passwordModify.text.toString()
        val phone = binding.phoneModify.text.toString()


        //atuhenttication ???????????? ????????????
        repository.updateAuthPassword(password)

        //?????????????????? ???????????? ????????????
        repository.updateFireStorePassword(currentUser!!.uid, password)

        //?????????????????? ???????????? ????????????
        repository.updatePhone(currentUser.uid, phone)

        Toast.makeText(context, R.string.modify_success, Toast.LENGTH_SHORT).show()

        // ??????????????? ?????????
        val auto = this.requireActivity()
            .getSharedPreferences("auto", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = auto.edit()
        editor.clear()
        editor.apply()

        (activity as BaseActivity).setNavigationDrawerHeader()  // ??????????????? ????????? ?????? ??????
    }


    private fun showAlertDialog(msg: String) {
        RoundedAlertDialog()
            .setMessage(msg)
            .setPositiveButton(resources.getString(R.string.ok)) { withdrawal() }
            .setNegativeButton(resources.getString(R.string.cancel), null)
            .show(
                (requireActivity() as BaseActivity).supportFragmentManager,
                "rounded alert dialog"
            )
    }

    //??????

    @SuppressLint("RestrictedApi")
    private fun withdrawal() {

        // Authentication ??????
        repository.deleteAuthCustomer()

        // ????????? ????????? ??????
        repository.deleteFireStoreCustomer(repository.getUid())

        Toast.makeText(context, R.string.withdrawal_success, Toast.LENGTH_LONG).show()

        //??????????????? ??????
        val auto = this.requireActivity()
            .getSharedPreferences("auto", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = auto.edit()
        editor.clear()
        editor.apply()

        // ????????? ??????
        for (i in 1..findNavController().backStack.count()) {
            findNavController().popBackStack()
        }

        // ????????? ???????????? ??????
        findNavController().navigate(R.id.loginFragment)

    }


    // ???????????? ????????? ??? ?????? ??????
    private fun passwordValidWrong() {
        binding.passwordModifyValid.setTextColor(Color.parseColor("#FFF44336"))
        binding.passwordModifyValid.visibility = View.VISIBLE
    }

    // ???????????? ??????????????? ??? ??????
    private fun passwordValidCorrect() {
        binding.passwordModifyValid.setTextColor(Color.parseColor("#000000"))
        binding.passwordModifyValid.visibility = View.VISIBLE
        binding.passwordModifyValid.setText(R.string.password_available_info)
    }

    private fun displayCustomerInfo(customer: Customer) {
        binding.nameModify.setText(customer.name)
        binding.emailModify.setText(customer.email)
        binding.passwordModify.setText(customer.password)
        binding.passwordCheckModify.setText(customer.password)
        binding.phoneModify.setText(customer.phone)
    }
}