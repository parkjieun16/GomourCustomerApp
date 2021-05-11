package com.santaistiger.gomourcustomerapp.ui.orderdetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import com.santaistiger.gomourcustomerapp.R
import kotlinx.android.synthetic.main.activity_base.*

class OrderDetailFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setToolbar()

        // Inflate the layout for this fragment
        val args = OrderDetailFragmentArgs.fromBundle(requireArguments())
        Toast.makeText(context, "주문 번호 : ${args.orderId}", Toast.LENGTH_LONG).show()
        return inflater.inflate(R.layout.fragment_order_detail, container, false)
    }

    private fun setToolbar() {
        requireActivity().apply {
            toolbar.visibility = View.VISIBLE     // 툴바 보이도록 설정
            toolbar_title.setText("주문 조회")	    // 툴바 타이틀 변경
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED) // 스와이프 활성화

        }
    }
}