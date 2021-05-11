package com.santaistiger.gomourcustomerapp.ui.doorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.santaistiger.gomourcustomerapp.R
import com.santaistiger.gomourcustomerapp.databinding.FragmentDoOrderBinding
import kotlinx.android.synthetic.main.activity_base.*
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

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
            toolbar_title.setText("주문하기")     // 툴바 타이틀 변경
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
                        DoOrderFragmentDirections.actionDoOrderFragmentToWaitMatchFragment(orderRequest.orderId))
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
                false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    /**
     * DestinationView의 button, textView에 clickListener 설정
     */
    private fun addDestinationClickListener() {
        binding.cvDestination.binding.ibAddItem.setOnClickListener { viewModel.addStore() }
        binding.cvDestination.binding.tvStoreAddress.setOnClickListener { searchPlace() }
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