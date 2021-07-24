/**
 * created by Kang Gumsil
 */

package com.santaistiger.gomourcustomerapp.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.santaistiger.gomourcustomerapp.R
import com.santaistiger.gomourcustomerapp.databinding.FragmentDoOrderBinding
import com.santaistiger.gomourcustomerapp.ui.base.BaseActivity
import com.santaistiger.gomourcustomerapp.ui.customview.RoundedAlertDialog
import com.santaistiger.gomourcustomerapp.ui.viewmodel.DoOrderViewModel


class DoOrderFragment : Fragment() {
    companion object {
        private val TAG = this::class.java.canonicalName
        private const val MAX_SIZE = 3
    }

    private lateinit var binding: FragmentDoOrderBinding
    private val viewModel: DoOrderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        init(inflater, container)
        addObserver()
        addClickListener()

        return binding.root
    }

    /** binding 설정 및 툴바 설정 */
    private fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_do_order,
            container,
            false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // 툴바 설정
        (requireActivity() as BaseActivity).setToolbar(
            requireContext(), true, resources.getString(R.string.do_order), true
        )
    }

    private fun addObserver() {
        addOrderRequestObserver()
        addExceptionObserver()
    }

    private fun addClickListener() {
        // 주문하기 버튼 클릭 시 입력창 검사하고 주문 생성하도록 설정
        binding.btnDoOrder.setOnClickListener { onDoOrderBtnClick() }

        // 배달 장소 입력 창의 + 버튼 클릭 시 주문 장소 및 메뉴 입력창이 추가되도록 설정
        binding.cvDestination.binding.ibAddItem.setOnClickListener { appendStore() }

        // 배달 장소 입력 창 클릭 시 장소를 선택할 수 있도록 SearchPlaceFragment로 이동하도록 설정
        binding.cvDestination.binding.tvStoreAddress.setOnClickListener { searchPlace() }

        // 배달료 창의 ↻ 버튼 클릭 시 배달료를 갱신하도록 설정
        binding.cvPrice.binding.ibRefresh.setOnClickListener { viewModel.getDeliveryCharge() }

    }

    /**
     * DoOrderViewModel의 orderRequest에 값이 설정되면 기사 매칭 화면 (WaitMatchFragment)로 이동
     * 이때 navigate args로 orderId를 전달한다.
     */
    private fun addOrderRequestObserver() {
        viewModel.orderRequest.observe(viewLifecycleOwner, Observer { orderRequest ->
            if (orderRequest != null) {
                findNavController().navigate(
                    DoOrderFragmentDirections.actionDoOrderFragmentToWaitMatchFragment(orderRequest.orderId)
                )
                viewModel.doneNavigateWaitMatch()
            }
        })
    }

    /** DoOrderViewModel의 exception에 값이 설정되면 경고 다이얼로그를 띄운다. */
    private fun addExceptionObserver() {
        viewModel.exception.observe(viewLifecycleOwner) { e ->
            if (e != null) {
                binding.ivLoading.visibility = View.GONE
                binding.btnDoOrder.isClickable = true
                showAlertDialog(e.message!!)
                viewModel.doneExceptionProcess()
                view
            }
        }
    }

    private fun appendStore() {
        if (viewModel.storeList.size < MAX_SIZE) {
            viewModel.appendStore()
        } else {
            showAlertDialog(resources.getString(R.string.warning_limit_store_count_message))
        }
    }

    private fun showAlertDialog(msg: String) {
        RoundedAlertDialog()
            .setMessage(msg)
            .setPositiveButton(resources.getString(R.string.ok), null)
            .show(requireActivity().supportFragmentManager, "rounded alert dialog")
    }

    /** 장소를 선택할 수 있도록 지도 화면 (SearchFragment)로 이동하는 함수 */
    private fun searchPlace() {
        findNavController().navigate(
            DoOrderFragmentDirections.actionDoOrderFragmentToSearchPlaceFragment()
        )
    }

    /** '주문하기' 버튼 클릭시 수행*/
    private fun onDoOrderBtnClick() {
        binding.btnDoOrder.isClickable = false
        binding.ivLoading.apply {
            visibility = View.VISIBLE
            Glide.with(this)
                .load(R.raw.wait_match_loading)
                .into(this)
            bringToFront()
        }

        viewModel.createOrder()
    }
}