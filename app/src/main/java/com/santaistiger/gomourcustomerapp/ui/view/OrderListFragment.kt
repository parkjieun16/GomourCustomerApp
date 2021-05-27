package com.santaistiger.gomourcustomerapp.ui.view

/**
 * Created by Jieun Park.
 */

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.santaistiger.gomourcustomerapp.R
import com.santaistiger.gomourcustomerapp.data.model.Order
import com.santaistiger.gomourcustomerapp.data.repository.Repository
import com.santaistiger.gomourcustomerapp.data.repository.RepositoryImpl
import com.santaistiger.gomourcustomerapp.databinding.FragmentOrderListBinding
import com.santaistiger.gomourcustomerapp.ui.adapter.OrderListAdapter
import com.santaistiger.gomourcustomerapp.ui.base.BaseActivity
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

        (requireActivity() as BaseActivity).setToolbar(
            requireContext(), true, resources.getString(R.string.toolbar_title_order_list), true
        )

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_order_list, container, false
        )
        viewModel = ViewModelProvider(this).get(OrderListViewModel::class.java)
        binding.orderListViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // 리사이클러뷰 어댑터 생성 후 설정
        val adapter = OrderListAdapter(context)
        val customerUid = repository.getUid()
        val emptyNoticeTextView = binding.emptyNoticeString

        /**
         * realtime database에 주문 내역이 존재하는지 확인
         * 주문 내역이 존재하면 주문 내역을 최근 날짜 순으로 배열하면 어댑터의 주문 목록 리스트에 넣어준다.
         * 주문 내역이 존재하지 않으면 빈 리싸이클러뷰 안내 문구를 표시한다.
         */
        viewModel.getOrderList(customerUid)

        viewModel.orders.observe(viewLifecycleOwner, Observer<ArrayList<Order>> { orders ->
            if (!orders.isNullOrEmpty()) {
                // 최근 날짜 순으로 주문 목록 재배열 후 adapter의 orders에 할당
                adapter.orderList = orders.asReversed()
                adapter.notifyDataSetChanged()
                emptyNoticeTextView.visibility = View.GONE     // 빈 리싸이클러뷰 안내 문구 숨김
            } else {
                emptyNoticeTextView.visibility = View.VISIBLE  // 빈 리싸이클러뷰 안내 문구 표시
            }
        })

        binding.orderList.adapter = adapter

        return binding.root
    }
}