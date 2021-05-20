package com.santaistiger.gomourcustomerapp.ui.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.santaistiger.gomourcustomerapp.R
import com.santaistiger.gomourcustomerapp.databinding.FragmentDoOrderBinding
import com.santaistiger.gomourcustomerapp.ui.viewmodel.DoOrderViewModel
import kotlinx.android.synthetic.main.activity_base.*

class DoOrderFragment : Fragment() {
    companion object {
        private const val TAG = "DoOrderFragment"
        private const val MAX_SIZE = 3
    }

    private lateinit var binding: FragmentDoOrderBinding
    private val viewModel: DoOrderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        setToolbar()
        init(inflater, container)
        addObserver()
        addClickListener()

        return binding.root
    }

    private fun addObserver() {
        addOrderRequestObserver()
        addNotEnteredExceptionObserver()
    }

    private fun addNotEnteredExceptionObserver() {
        viewModel.notEnteredException.observe(viewLifecycleOwner, Observer { e ->
            if (e != null) {
                showDenyDialog(e.message!!)
                viewModel.doneNotEnteredExceptionProcess()
            }
        })
    }

    private fun setToolbar() {
        requireActivity().apply {
            toolbar.visibility = View.VISIBLE     // 툴바 보이도록 설정
            toolbar_title.text = "주문하기"     // 툴바 타이틀 변경
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)  // 스와이프 활성화
        }
    }

    /**
     * viewModel의 orderRequest를 관찰하다가
     * orderRequest의 값이 null이 아니면 waitMatchFragment로 이동
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

    /**
     * binding 설정
     */
    private fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_do_order,
            container,
            false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    /**
     * DestinationView의 button, textView에 clickListener 설정
     */
    private fun addClickListener() {
        binding.cvDestination.binding.ibAddItem.setOnClickListener { appendStore() }
        binding.cvDestination.binding.tvStoreAddress.setOnClickListener { searchPlace() }
        binding.cvPrice.binding.ibRefresh.setOnClickListener { viewModel.getDeliveryCharge() }
    }

    private fun appendStore() {
        if (viewModel.storeList.size < MAX_SIZE) {
            viewModel.appendStore()
        } else {
            showDenyDialog("주문 장소는 최대 3곳까지 가능합니다.")
        }
    }

    private fun showDenyDialog(msg: String) {
        AlertDialog.Builder(context)
            .setMessage(msg)
            .setNegativeButton("확인", null)
            .create()
            .show()
    }

    /**
     * 지도 화면 (SearchFragment)로 이동
     */
    private fun searchPlace() {
        findNavController().navigate(
            DoOrderFragmentDirections.actionDoOrderFragmentToSearchPlaceFragment()
        )
    }
}