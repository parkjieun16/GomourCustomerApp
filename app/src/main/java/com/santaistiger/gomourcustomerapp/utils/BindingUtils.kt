package com.santaistiger.gomourcustomerapp.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.*
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.santaistiger.gomourcustomerapp.data.model.Order
import com.santaistiger.gomourcustomerapp.data.model.Place
import com.santaistiger.gomourcustomerapp.data.model.Status
import com.santaistiger.gomourcustomerapp.data.model.Store
import com.santaistiger.gomourcustomerapp.ui.adapter.DoOrderStoreAdapter
import com.santaistiger.gomourcustomerapp.ui.adapter.OrderDetailStoreAdapter
import com.santaistiger.gomourcustomerapp.ui.customview.*
import java.text.NumberFormat
import java.text.SimpleDateFormat


object BindingUtils {

    @BindingAdapter("bind_do_store_list")
    @JvmStatic
    fun bindStoreList(recyclerView: RecyclerView, items: ObservableArrayList<Store>) {
        if (recyclerView.adapter == null) {
            recyclerView.layoutManager =
                LinearLayoutManager(recyclerView.context)
            recyclerView.adapter = DoOrderStoreAdapter()
        }
        (recyclerView.adapter as DoOrderStoreAdapter).items = items
        recyclerView.adapter?.notifyDataSetChanged()
    }

    @BindingAdapter("bind_do_destination")
    @JvmStatic
    fun setDestination(view: DoDestinationView, item: ObservableField<Place>) {
        if (view.binding.item != item.get()) {
            view.binding.item = item.get()
        }
    }

    @BindingAdapter("bind_do_message")
    @JvmStatic
    fun setMessage(view: DoMessageView, item: ObservableField<String>) {
        if (view.binding.message != item.get()) {
            view.binding.message = item.get()
            view.binding.etMessage.setSelection(item.get()?.length ?: 0)
        }
    }

    @InverseBindingAdapter(attribute = "bind_do_message", event = "bind_do_messageAttrChanged")
    @JvmStatic
    fun getMessage(view: DoMessageView): String {
        return view.getMessage()
    }

    @BindingAdapter("bind_do_messageAttrChanged")
    @JvmStatic
    fun setListener(view: DoMessageView, listener: InverseBindingListener?) {
        view.binding.etMessage.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                listener?.onChange()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    @BindingAdapter("bind_do_price")
    @JvmStatic
    fun setPrice(view: DoPriceView, item: ObservableInt) {
        if (view.binding.price != item.get()) {
            view.binding.price = item.get()
        }
    }

    @BindingAdapter("bind_detail_store_list")
    @JvmStatic
    fun bindDetailStoreList(recyclerView: RecyclerView, item: MutableLiveData<Order>) {
        if (item.value != null) {
            val order = item.value!!
            val stores = order.stores

            if (recyclerView.adapter == null) {
                recyclerView.layoutManager =
                    LinearLayoutManager(recyclerView.context)
                recyclerView.adapter = OrderDetailStoreAdapter()
            }
            (recyclerView.adapter as OrderDetailStoreAdapter).items = stores ?: ArrayList()
            recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    @BindingAdapter("bind_detail_delivery_time")
    @JvmStatic
    fun bindDetailDeliveryTime(view: TextView, item: MutableLiveData<Order>) {
        if (item.value != null) {
            val order = item.value!!
            view.text = if (order.status == Status.DELIVERY_COMPLETE) {
                SimpleDateFormat("yyyy-MM-dd (EEE) hh:mm 배달 완료").format(order.deliveryTime)
            } else {
                SimpleDateFormat("yyyy-MM-dd (EEE) hh:mm 도착 예정").format(order.deliveryTime)
            }
        }
    }

    @BindingAdapter("bind_detail_destination")
    @JvmStatic
    fun bindDetailDestination(view: DetailDestinationView, item: MutableLiveData<Order>) {
        if (item.value != null) {
            val order = item.value!!
            view.binding.tvStoreAddress.text = order.destination?.getDisplayName()

            when (order.status) {
                Status.PICKUP_COMPLETE -> {
                    setComplete(view.binding.tvPickupComplete)
                }

                Status.DELIVERY_COMPLETE -> {
                    setComplete(view.binding.tvPickupComplete)
                    setComplete(view.binding.tvDeliveryComplete)
                }
            }
        }
    }

    private fun setComplete(tv: AppCompatTextView) {
        tv.text = tv.hint.toString()
    }


    @BindingAdapter("bind_detail_message")
    @JvmStatic
    fun bindDetailMessage(view: DetailMessageView, item: MutableLiveData<Order>) {
        if (item.value != null) {
            view.binding.tvMessage.text = item.value!!.message
        }
    }

    @BindingAdapter("bind_detail_price")
    @JvmStatic
    fun bindDetailPrice(view: DetailPriceView, item: MutableLiveData<Order>) {
        if (item.value != null) {
            val order = item.value!!
            var price = order.deliveryCharge ?: 0
            for (store in order.stores!!) {
                price += store.cost ?: 0
            }
            view.binding.tvPrice.text = numberFormat.format(price) + " 원"
        }
    }

    @BindingAdapter("bind_detail_account_info")
    @JvmStatic
    fun bindDetailAccountInfo(view: DetailPriceView, item: ObservableField<String>) {
        view.binding.tvAccount.text = item.get()
    }


    @BindingAdapter("bind_detail_cost")
    @JvmStatic
    fun bindDetailCost(view: AppCompatTextView, item: Int?) {
        if (item != null) {
            view.text = numberFormat.format(item)
        }
    }

}

