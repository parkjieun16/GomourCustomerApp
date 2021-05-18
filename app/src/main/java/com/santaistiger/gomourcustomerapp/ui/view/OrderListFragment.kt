package com.santaistiger.gomourcustomerapp.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.santaistiger.gomourcustomerapp.R
import com.santaistiger.gomourcustomerapp.data.network.database.RealtimeApi
import com.santaistiger.gomourcustomerapp.data.repository.Repository
import com.santaistiger.gomourcustomerapp.data.repository.RepositoryImpl
import com.santaistiger.gomourcustomerapp.databinding.FragmentOrderListBinding
import com.santaistiger.gomourcustomerapp.ui.adapter.OrderListAdapter
import com.santaistiger.gomourcustomerapp.ui.viewmodel.OrderListViewModel
import kotlinx.android.synthetic.main.activity_base.*

class OrderListFragment : Fragment() {

    private val TAG = "OrderListFragment"

    private lateinit var binding: FragmentOrderListBinding
    private lateinit var viewModel: OrderListViewModel

    private val repository: Repository = RepositoryImpl

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setToolbar()

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_order_list, container, false
        )
        viewModel = ViewModelProvider(this).get(OrderListViewModel::class.java)
        binding.orderListViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // 리사이클러뷰 어댑터 생성 후 설정
        val adapter = OrderListAdapter(context)
        val customerUid = repository.getUid()
        val emptyTextView = binding.emptyNoticeString
        RealtimeApi.readOrderList(customerUid, adapter, emptyTextView)
        binding.orderList.adapter = adapter

        return binding.root
    }

    private fun setToolbar() {
        requireActivity().apply {
            toolbar.visibility = View.VISIBLE     // 툴바 보이도록 설정
            toolbar_title.text = "주문 목록"    // 툴바 타이틀 변경
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)  // 스와이프 활성화
        }
    }
}