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
import androidx.navigation.fragment.findNavController
import com.santaistiger.gomourcustomerapp.R
import com.santaistiger.gomourcustomerapp.databinding.FragmentDoOrderBinding
import com.santaistiger.gomourcustomerapp.ui.viewmodel.DoOrderViewModel
import kotlinx.android.synthetic.main.activity_base.*

private const val TAG = "DoOrderFragment"
private const val MAX_SIZE = 4

class DoOrderFragment : Fragment() {

    private lateinit var binding: FragmentDoOrderBinding
    private val viewModel: DoOrderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        setToolbar()

        init(inflater, container)
        addDestinationClickListener()
        addOrderRequestObserver()

        return binding.root
    }

    private fun setToolbar() {
        requireActivity().apply {
            toolbar.visibility = View.VISIBLE     // 툴바 보이도록 설정
            toolbar_title.setText(R.string.toolbar_title_do_order)     // 툴바 타이틀 변경
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)  // 스와이프 활성화
        }
    }

    /**
     * viewModel의 orderRequest를 관찰하다가
     * orderRequest의 값이 null이 아니면 waitMatchFragment로 이동
     */
    private fun addOrderRequestObserver() {
        viewModel.orderRequest.observe(viewLifecycleOwner, { orderRequest ->
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
    private fun addDestinationClickListener() {
        binding.cvDestination.binding.ibAddItem.setOnClickListener { addStore() }
        binding.cvDestination.binding.tvStoreAddress.setOnClickListener { searchPlace() }
    }

    private fun addStore() {
        if (viewModel.storeList.size < 3) {
            viewModel.addStore()
        } else {
            showDenyDialog()
        }
    }

    private fun showDenyDialog() {
        AlertDialog.Builder(context)
            .setMessage("주문 장소는 최대 3곳까지 가능합니다.")
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