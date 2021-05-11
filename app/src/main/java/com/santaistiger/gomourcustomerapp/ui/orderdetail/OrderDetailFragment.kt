package com.santaistiger.gomourcustomerapp.ui.orderdetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.santaistiger.gomourcustomerapp.R

class OrderDetailFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val args = OrderDetailFragmentArgs.fromBundle(requireArguments())
        Toast.makeText(context, "주문 번호 : ${args.orderId}", Toast.LENGTH_LONG).show()
        return inflater.inflate(R.layout.fragment_order_detail, container, false)
    }
}