package com.santaistiger.gomourcustomerapp.ui.waitmatch

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.santaistiger.gomourcustomerapp.R
import com.santaistiger.gomourcustomerapp.data.network.orderrequest.FirebaseApi
import com.santaistiger.gomourcustomerapp.databinding.FragmentWaitMatchBinding
import kotlinx.android.synthetic.main.activity_base.*

/**
 * 배달원 매칭을 기다리는 화면
 *
 * 주문 취소 버튼을 누르면 주문하기 화면으로 돌아간다. 기존의 데이터는 사라진다.
 * 배달원이 매칭되면 ( = 데이터베이스 서버의 'request_order' 테이블에서 해당 주문이 사라지면)
 * 주문 조회창으로 넘어간다. 이때 orderId를 인자로 넘긴다.
 *
 */
class WaitMatchFragment : Fragment() {

    private val TAG = "WaitMatchFragment"

    private lateinit var binding: FragmentWaitMatchBinding
    private lateinit var viewModel: WaitMatchViewModel
    private lateinit var callback: OnBackPressedCallback
    private lateinit var orderId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_wait_match, container, false
        )
        viewModel = ViewModelProvider(this).get(WaitMatchViewModel::class.java)
        binding.waitMatchViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setToolbar()

        // 로딩 gif 이미지 설정
        Glide.with(this).load(R.raw.wait_match_loading).into(binding.loadingImage)

        // 주문하기 화면에서 받아온 현재 주문 번호 orderId에 저장
        val args = WaitMatchFragmentArgs.fromBundle(requireArguments())
        orderId = args.orderId

        // 주문 취소 버튼 누른 경우
        viewModel.eventCancelOrder.observe(viewLifecycleOwner, Observer<Boolean> { it ->
            if (it) {
                alertCancel()
            }
        })

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        checkDatabase(orderId)      // 데이터베이스에 현재 주문이 존재하는지 체크
    }

    private fun setToolbar() {
        requireActivity().apply {
            toolbar.visibility = View.GONE    // 툴바 숨기기
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED) // 스와이프 비활성화
        }
    }

    // 현재 주문이 realtime database에 존재하는지 확인
    fun checkDatabase(orderId: String) {
        val currentOrder = FirebaseApi.getCurrentOrder(orderId)

        currentOrder.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                // 현재 주문이 realtime database에 존재할 경우
                if (snapshot.exists()) {
                    Log.d(TAG, "current order is in realtime database")
                }

                // 현재 주문이 realtime database에 존재하지 않을 경우
                else {
                    Log.d(TAG, "current order is not in realtime database")
                    // 주문상세 페이지로 이동
                    view?.findNavController()
                        ?.navigate(
                            WaitMatchFragmentDirections.actionWaitMatchFragmentToOrderDetailFragment(orderId)
                        )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", error.toException())
            }
        })
    }

    // 주문하기 페이지로 이동 후 realtime database에서 현재 주문 정보 삭제
    fun cancelOrder() {
        // 주문하기 페이지로 이동
        view?.findNavController()?.navigate(R.id.action_waitMatchFragment_to_doOrderFragment)

        // realtime database에서 현재 주문 정보 삭제
        FirebaseApi.deleteCurrentOrder(orderId)
    }

    // 주문 취소 경고창
    fun alertCancel() {
        AlertDialog.Builder(requireActivity())
            .setMessage("주문을 취소하시겠습니까?")
            .setPositiveButton("예") { _, _ ->
                cancelOrder()
            }
            .setNegativeButton("아니오", null)
            .create()
            .show()
    }

    // 뒤로 가기 버튼 누른 경우 주문을 취소할지 확인하는 경고창 띄우기
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d(TAG, "backPressed")
                alertCancel()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}