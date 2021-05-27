package com.santaistiger.gomourcustomerapp.ui.view

/**
 * Created by Jieun Park.
 */

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.santaistiger.gomourcustomerapp.R
import com.santaistiger.gomourcustomerapp.data.repository.Repository
import com.santaistiger.gomourcustomerapp.data.repository.RepositoryImpl
import com.santaistiger.gomourcustomerapp.databinding.FragmentWaitMatchBinding
import com.santaistiger.gomourcustomerapp.ui.base.BaseActivity
import com.santaistiger.gomourcustomerapp.ui.customview.RoundedAlertDialog
import com.santaistiger.gomourcustomerapp.ui.viewmodel.WaitMatchViewModel
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
    private val repository: Repository = RepositoryImpl
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

        // 툴바 설정
        (requireActivity() as BaseActivity).setToolbar(
            requireContext(), false, null, false)

        // 로딩 gif 이미지 설정
        Glide.with(this).load(R.raw.wait_match_loading).into(binding.loadingImage)

        // 주문하기 화면에서 받아온 현재 주문 번호를 orderId에 저장
        val args = WaitMatchFragmentArgs.fromBundle(requireArguments())
        orderId = args.orderId

        // 주문 취소 버튼 누른 경우 다이얼로그 출력
        viewModel.eventCancelOrder.observe(viewLifecycleOwner, Observer<Boolean> { it ->
            if (it) {
                alertCancel()
            }
        })

        // 현재 주문이 realtime database의 order_request 테이블에 존재하는지 검사
        // 만약 존재하지 않을 경우(매칭이 완료되어 order_request 테이블에서 삭제된 경우) 주문 정보 전달하며 주문 상세 페이지로 이동
        viewModel.checkDatabase(orderId)
        viewModel.isCurrentOrderExist.observe(viewLifecycleOwner, Observer<Boolean> { it ->
            if (!it) {
                view?.findNavController()
                    ?.navigate(
                        WaitMatchFragmentDirections.actionWaitMatchFragmentToOrderDetailFragment(
                            orderId
                        )
                    )
            }
        })

        return binding.root
    }

    /**
     * 주문 취소하기
     * 주문하기 페이지로 이동하고 현재 주문 정보를 realtime database의 order_request 테이블에서 삭제한다.
     */
    fun cancelOrder() {
        // 주문하기 페이지로 이동
        view?.findNavController()?.navigate(R.id.action_waitMatchFragment_to_doOrderFragment)

        // realtime database에서 현재 주문 정보 삭제
        repository.deleteCurrentOrder(orderId)
    }

    // 주문 취소 확인 다이얼로그
    fun alertCancel() {
        RoundedAlertDialog()
            .setMessage("주문을 취소하시겠습니까?")
            .setPositiveButton("예") { cancelOrder() }
            .setNegativeButton("아니오", null)
            .show((activity as BaseActivity).supportFragmentManager, "rounded alert dialog")

    }

    /**
     * 뒤로 가기 버튼 클릭 동작 정의
     * 뒤로 가기 버튼을 누그면 주문을 취소할지 확인하는 경고창을 띄운다.
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
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